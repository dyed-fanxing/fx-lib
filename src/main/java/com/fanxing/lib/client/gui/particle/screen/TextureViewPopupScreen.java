package com.fanxing.lib.client.gui.particle.screen;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

public class TextureViewPopupScreen extends Screen {
    private final ResourceLocation texture;
    private int imgWidth, imgHeight;
    private final Screen parent;

    private float zoom = 1.0f;
    private float initMaxZoom = 1.0f;
    private float translateX = 0, translateY = 0;
    private boolean dragging = false;
    private double dragStartX, dragStartY;

    public TextureViewPopupScreen(ResourceLocation texture, Screen parent) {
        super(Component.translatable("texture_viewer.title"));
        this.texture = texture;
        this.parent = parent;
        loadImageSize();
    }

    private void loadImageSize() {
        var optResource = Minecraft.getInstance().getResourceManager().getResource(texture);
        if (optResource.isPresent()) {
            try (InputStream inputStream = optResource.get().open();
                 NativeImage nativeImage = NativeImage.read(inputStream)) {
                this.imgWidth = nativeImage.getWidth();
                this.imgHeight = nativeImage.getHeight();
            } catch (IOException e) {
                e.printStackTrace();
                setDefaultSize();
            }
        } else {
            setDefaultSize();
        }
    }

    private void setDefaultSize() {
        this.imgWidth = 256;
        this.imgHeight = 256;
    }

    @Override
    protected void init() {
        super.init();
        float scaleX = (float) width / imgWidth;
        float scaleY = (float) height / imgHeight;
        initMaxZoom = Math.min(scaleX, scaleY) * 0.9f;
        zoom = initMaxZoom;
        translateX = (width - imgWidth * zoom) / 2;
        translateY = (height - imgHeight * zoom) / 2;
        // 关闭按钮放在右下角
        int btnWidth = 100;
        int btnHeight = 20;
        int btnX = width - btnWidth - 10;
        int btnY = height - btnHeight - 10;
        addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, btn -> onClose())
                .bounds(btnX, btnY, btnWidth, btnHeight).build());
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        float delta = (float) (1 - scrollY * 0.1);
        float newZoom = zoom * delta;
        newZoom = Mth.clamp(newZoom, 0.1f, initMaxZoom);
        if (newZoom == zoom) return true;

        if (Screen.hasControlDown()) {
            float mouseImgX = (float) ((mouseX - translateX) / zoom);
            float mouseImgY = (float) ((mouseY - translateY) / zoom);
            zoom = newZoom;
            translateX = (float) (mouseX - mouseImgX * zoom);
            translateY = (float) (mouseY - mouseImgY * zoom);
        } else {
            float centerX = width / 2.0f;
            float centerY = height / 2.0f;
            float centerImgX = (centerX - translateX) / zoom;
            float centerImgY = (centerY - translateY) / zoom;
            zoom = newZoom;
            translateX = centerX - centerImgX * zoom;
            translateY = centerY - centerImgY * zoom;
        }
        clampTranslate();
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && Screen.hasShiftDown()) {
            dragging = true;
            dragStartX = mouseX;
            dragStartY = mouseY;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (dragging) {
            translateX += (float) (mouseX - dragStartX);
            translateY += (float) (mouseY - dragStartY);
            dragStartX = mouseX;
            dragStartY = mouseY;
            clampTranslate();
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (dragging) {
            dragging = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void clampTranslate() {
        float minX = width - imgWidth * zoom;
        float maxX = 0;
        float minY = height - imgHeight * zoom;
        float maxY = 0;
        if (imgWidth * zoom > width) {
            translateX = Mth.clamp(translateX, minX, maxX);
        }
        if (imgHeight * zoom > height) {
            translateY = Mth.clamp(translateY, minY, maxY);
        }
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // 先绘制按钮等控件
        super.render(graphics, mouseX, mouseY, partialTick);

        // 绘制纹理（已实现）
        RenderSystem.enableBlend();
        graphics.pose().pushPose();
        graphics.pose().translate(translateX, translateY, 0);
        graphics.pose().scale(zoom, zoom, 1.0f);
        graphics.blit(texture, 0, 0, 0, 0, imgWidth, imgHeight, imgWidth, imgHeight);
        graphics.pose().popPose();
        RenderSystem.disableBlend();

        // 关闭按钮参数（与 init 中一致）
        int btnWidth = 100;
        int btnHeight = 20;
        int btnX = width - btnWidth - 10;
        int btnY = height - btnHeight - 10;
        // 分辨率字符串
        String resolution = imgWidth + " x " + imgHeight;
        int resWidth = font.width(resolution);
        // 放在按钮左侧，间隔 10 像素
        int resX = btnX - resWidth - 10;
        int resY = btnY + (btnHeight - font.lineHeight) / 2; // 垂直居中
        // 如果超出左边界，则靠左显示（可选）
        if (resX < 10) resX = 10;
        graphics.drawString(font, resolution, resX, resY, 0xFFFFFF);

        // 操作提示（左下角，使用翻译键，样式不变）
        Component tip1 = Component.translatable("gui.fx_lib.transform.scale");
        Component tip2 = Component.translatable("gui.fx_lib.transform.scale_ctrl");
        Component tip3 = Component.translatable("gui.fx_lib.transform.drag_shift");
        String combined = tip1.getString() + "   " + tip2.getString() + "   " + tip3.getString();
        int tipX = 10;
        int tipY = height - font.lineHeight - 10;
        graphics.drawString(font, combined, tipX, tipY, 0xFFFFFF);
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(parent);
    }
}
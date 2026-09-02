package com.fanxing.lib.client.gui.particle;

import com.fanxing.lib.client.gui.Scheme;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetTooltipHolder;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TextureDisplayWidget extends AbstractWidget {
    private static final int TEXT_GAP = 4;

    private ResourceLocation texture;
    private String path;
    private int imgWidth = 0, imgHeight = 0;
    private Runnable callback;

    public TextureDisplayWidget(int x, int y, int width, int height, ResourceLocation initialTexture) {
        super(x, y, width, height, Component.empty());
        setTexture(initialTexture);
    }

    public void setTexture(@Nullable ResourceLocation texture) {
        this.texture = texture;
        if (texture != null) {
            this.path = texture.toString();
            loadImageSize();
        } else {
            this.path = "";
            this.imgWidth = this.imgHeight = -1;
        }
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
                this.imgWidth = this.imgHeight = -1;
            }
        } else {
            this.imgWidth = this.imgHeight = -1;
        }
    }

    public void setCallback(Runnable callback) {
        this.callback = callback;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int x = getX(), y = getY(), w = getWidth(), h = getHeight();
        Font font = Minecraft.getInstance().font;

        graphics.fill(x, y, x + w, y + h, Scheme.BG_COLOR_BLACK);
        graphics.renderOutline(x, y, w, h, isHoveredOrFocused() ? Scheme.BORDER_COLOR_HOVER : Scheme.BORDER_COLOR_NORMAL);
        int lineHeight = 8;
        if (texture == null) {
            String plus = "+";
            graphics.drawString(font, plus, x + (w - font.width(plus)) / 2, y + (h - lineHeight) / 2, 0xFFFFFF);
            return;
        }

        int iconSize = h - 2;
        int iconX = x + 1;
        int iconY = y + 1;

        RenderSystem.enableBlend();
        graphics.blit(texture, iconX, iconY, 0, 0, iconSize, iconSize, iconSize, iconSize);
        RenderSystem.disableBlend();

        int textX = iconX + iconSize + TEXT_GAP;
        int textWidth = (x + w) - textX - 4; // 右侧留边距
        if (textWidth < 10) textWidth = 10;

        int rowHeight = (h - 2) / 3; // 每行区域高度（包括内边距）
        int textAreaTop = y + 1; // 文本区域起始Y坐标（顶部有BORDER边距）

        // 路径文本处理（最多两行，同时记录是否被截断）
        List<String> pathLines = new ArrayList<>();
        String remaining = path;
        boolean pathClipped = false;
        int maxLines = 2;
        while (!remaining.isEmpty() && pathLines.size() < maxLines) {
            String line = font.plainSubstrByWidth(remaining, textWidth);
            if (line.isEmpty()) break;
            pathLines.add(line);
            remaining = remaining.substring(line.length());
        }
        if (!remaining.isEmpty() && pathLines.size() == maxLines) {
            String lastLine = pathLines.getLast();
            String suffix = "...";
            int suffixWidth = font.width(suffix);
            int availableWidth = textWidth - suffixWidth;
            if (availableWidth < 0) availableWidth = 0;
            String truncated = font.plainSubstrByWidth(lastLine, availableWidth) + suffix;
            pathLines.set(pathLines.size() - 1, truncated);
            pathClipped = true;
        } else if (!remaining.isEmpty()) {
            pathClipped = true;
        }

        // 绘制路径行
        for (int i = 0; i < pathLines.size(); i++) {
            int lineY = textAreaTop + i * rowHeight + (rowHeight - lineHeight) / 2;
            graphics.drawString(font, pathLines.get(i), textX, lineY, 0xFFFFFF);
        }

        // 分辨率第三行
        String resolution = (imgWidth > 0 && imgHeight > 0) ? (imgWidth + " x " + imgHeight) : "? x ?";
        int resY = textAreaTop + 2 * rowHeight + (rowHeight - lineHeight) / 2;
        graphics.drawString(font, resolution, textX, resY, 0xAAAAAA);

        // 删除按钮（位于第三行右侧，垂直居中）
        int delX = x + w - rowHeight - 2;
        int delY = textAreaTop + 2 * rowHeight;
        boolean overDel = mouseX >= delX && mouseX <= delX + rowHeight && mouseY >= delY && mouseY <= delY + rowHeight;
        int delColor = overDel ? 0xFFFF5555 : 0xFFFFFFFF;
        graphics.drawString(font, "✕", delX + (rowHeight - font.width("✕")) / 2, delY + (rowHeight - lineHeight) / 2, delColor);

        // 设置 Tooltip：如果路径被截断，显示完整路径
        if (pathClipped) {
            this.setTooltip(Tooltip.create(Component.literal(path)));
        } else {
            this.setTooltip(null);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        if (texture != null) {
            int x = getX(), w = getWidth(), h = getHeight();
            int rowHeight = (h - 2) / 3;
            int delX = x + w - rowHeight - 2;
            int delY = getY() + 1 + 2 * rowHeight;
            if (mouseX >= delX && mouseX <= delX + rowHeight && mouseY >= delY && mouseY <= delY + rowHeight) {
                setTexture(null);
                if (callback != null) callback.run();
                return;
            }
        }
        if (callback != null) callback.run();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, Component.literal(texture == null ? "未选择纹理" : path));
    }
}
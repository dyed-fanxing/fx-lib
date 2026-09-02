package com.fanxing.lib.client.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ParticlePreview extends AbstractWidget {
    private static final float MAX_ZOOM = 200F;
    private static final float MIN_ZOOM = 10F;
    private static final float SENSE = 0.01f;

    private String particleType;
    private float zoom = 75;
    private float yaw = 0.0f;
    private float pitch = 0.0f;
    private boolean dragging;
    private double dragStartX, dragStartY;
    private float startYaw, startPitch;
    private static final Quaternionf FIX = new Quaternionf().rotateZ((float) Math.PI);

    public ParticlePreview(int x, int y, int width, int height, String initialType) {
        super(x, y, width, height, Component.empty());
        this.particleType = initialType;
    }

    public void setParticleType(String type) {
        this.particleType = type;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // 绘制背景和边框
        graphics.fill(getX(), getY(), getX() + width, getY() + height, 0xFF222222);
        // 渲染粒子内容
        renderParticle(graphics, getX(), getY(), getX() + width, getY() + height, (int) zoom, 0.0625F, partialTick);
    }

    private void renderParticle(GuiGraphics graphics, int left, int top, int right, int bottom, int size, float yOffset, float partialTick) {
        float centerX = (left + right) / 2.0F;
        float centerY = (top + bottom) / 2.0F;

        Quaternionf yawRot = new Quaternionf().rotateY(-yaw);
        Quaternionf pitchRot = new Quaternionf().rotateX(pitch);
        Quaternionf finalRot = new Quaternionf().mul(FIX).mul(pitchRot).mul(yawRot);

        float scale = zoom / 75f;
        Vector3f offset = new Vector3f(0.0F, yOffset * scale, 0.0F);
        float scaledSize = size / scale;
        renderParticleEntity(graphics, centerX, centerY, scaledSize, offset, finalRot, partialTick);
    }

    private void renderParticleEntity(GuiGraphics graphics, float centerX, float centerY, float scale, Vector3f offset, Quaternionf rotation, float partialTick) {
        graphics.pose().pushPose();
        graphics.pose().translate(centerX, centerY, 0F);
        graphics.pose().scale(scale, scale, scale);
        graphics.pose().translate(offset.x, offset.y, offset.z);
        graphics.pose().mulPose(rotation);

        float rad = 1.0f;
        int steps = 60;
        for (int i = 0; i < steps; i++) {
            float angle = (float) (2 * Math.PI * i / steps);
            float x = (float) Math.cos(angle) * rad;
            float y = (float) Math.sin(angle) * rad;
            float hue = (i / (float) steps + System.currentTimeMillis() * 0.002f) % 1.0f;
            int color = java.awt.Color.HSBtoRGB(hue, 1f, 1f);
            graphics.fill((int)(x * 20 - 2), (int)(y * 20 - 2), (int)(x * 20 + 2), (int)(y * 20 + 2), color);
        }

        graphics.pose().popPose();

        // 显示文字提示
        graphics.drawString(Minecraft.getInstance().font,
                Component.literal(particleType), getX() + 5, getY() + 5, 0xFFFFFF);
        graphics.drawString(Minecraft.getInstance().font,
                Component.literal("拖拽旋转 | 滚轮缩放"), getX() + 5, getY() + getHeight() - 15, 0xAAAAAA);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isMouseOver(mouseX, mouseY)) {
            dragging = true;
            dragStartX = mouseX;
            dragStartY = mouseY;
            startYaw = yaw;
            startPitch = pitch;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        if (dragging) {
            float deltaX = (float) (dragX * SENSE);
            float deltaY = (float) (dragY * SENSE);
            yaw = startYaw + deltaX;
            pitch = Mth.clamp(startPitch + deltaY, -1.2f, 1.2f);
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (isMouseOver(mouseX, mouseY)) {
            zoom += (float) scrollY * 5;
            zoom = Mth.clamp(zoom, MIN_ZOOM, MAX_ZOOM);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {}
}
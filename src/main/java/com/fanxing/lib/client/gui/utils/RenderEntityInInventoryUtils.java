package com.fanxing.lib.client.gui.utils;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.joml.Quaternionf;

public class RenderEntityInInventoryUtils {
    /**
     * 基础渲染（固定旋转，支持缩放），任意 Entity
     * @param graphics GuiGraphics
     * @param centerX 渲染区域中心点 X 坐标
     * @param centerY 渲染区域中心点 Y 坐标
     * @param scale 缩放系数
     * @param rotation 旋转四元数
     * @param entity 要渲染的实体（任意 Entity）
     */
    public static void renderEntity(GuiGraphics graphics, float centerX, float centerY, float scale, Quaternionf rotation, Entity entity) {
        // 保存当前渲染状态
        RenderSystem.runAsFancy(() -> {
            graphics.pose().pushPose();
            graphics.pose().translate(centerX, centerY, 50.0);
            graphics.pose().scale(scale, scale, scale);
            graphics.pose().translate(0.0, -entity.getBbHeight() / 2.0, 0.0);
            graphics.pose().mulPose(rotation);

            Lighting.setupForEntityInInventory();
            EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
            dispatcher.setRenderShadow(false);
            dispatcher.render(entity, 0, 0, 0, 0, 1.0F, graphics.pose(), graphics.bufferSource(), LightTexture.FULL_BRIGHT);
            graphics.bufferSource().endBatch();
            dispatcher.setRenderShadow(true);
            graphics.pose().popPose();
            Lighting.setupFor3DItems();
        });
    }

    /**
     * 跟随鼠标渲染（类似原版背包界面），支持任意 Entity
     * 注意：原版 InventoryScreen 中的跟随鼠标方法只接受 LivingEntity，这里重新实现。
     * @param graphics GuiGraphics
     * @param left 区域左上角 X
     * @param top 区域左上角 Y
     * @param right 区域右下角 X
     * @param bottom 区域右下角 Y
     * @param baseScale 基础缩放（原版中为30）
     * @param entity 实体
     * @param mouseX 鼠标 X 坐标
     * @param mouseY 鼠标 Y 坐标
     * @param zoom 额外缩放系数
     */
    public static void renderEntityFollowMouse(GuiGraphics graphics, int left, int top, int right, int bottom,
                                               float baseScale, Entity entity, int mouseX, int mouseY, float zoom) {
        // 计算区域中心
        float centerX = (left + right) / 2.0F;
        float centerY = (top + bottom) / 2.0F;
        // 计算跟随鼠标的角度（与原版逻辑一致）
        float dx = mouseX - centerX;
        float dy = mouseY - centerY;
        float yaw = (float) Math.atan2(dx, 40.0) * (180F / (float) Math.PI);
        float pitch = (float) Math.atan2(-dy, 40.0) * (180F / (float) Math.PI);
        pitch = Mth.clamp(pitch, -20F, 20F);
        // 构建旋转四元数
        Quaternionf rotation = new Quaternionf().rotateY((float) Math.toRadians(yaw)).rotateX((float) Math.toRadians(pitch));
        float scale = baseScale * zoom;
        // 启用剪刀测试，限制渲染区域
        graphics.enableScissor(left, top, right, bottom);
        renderEntity(graphics, centerX, centerY, scale, rotation, entity);
        graphics.disableScissor();
    }

    /**
     * 自由旋转 + 缩放交互（调用者管理角度和缩放），支持任意 Entity
     * @param graphics GuiGraphics
     * @param centerX 预览区域中心X
     * @param centerY 预览区域中心Y
     * @param baseScale 基础缩放（如30）
     * @param entity 实体
     * @param yawDeg 当前偏航角（度），会被更新
     * @param pitchDeg 当前俯仰角（度），会被更新
     * @param zoom 当前额外缩放系数（例如由滚轮控制），会被更新
     * @param dragX 拖拽X增量
     * @param dragY 拖拽Y增量
     * @param isDragging 是否正在拖拽
     * @param scrollDelta 滚轮滚动量（正为放大，负为缩小）
     * @return 更新后的 [yawDeg, pitchDeg, zoom]
     */
    public static float[] renderEntityRotatable(GuiGraphics graphics, float centerX, float centerY, float baseScale,
                                                Entity entity,
                                                float yawDeg, float pitchDeg, float zoom,
                                                double dragX, double dragY, boolean isDragging,
                                                double scrollDelta) {
        // 滚轮缩放
        if (scrollDelta != 0) {
            zoom = (float) Mth.clamp(zoom + scrollDelta * 0.1, 0.3, 3.0);
        }
        // 拖拽旋转
        if (isDragging) {
            yawDeg += (float) (dragX * 0.5F);
            pitchDeg = Mth.clamp(pitchDeg - (float) dragY * 0.5F, -50F, 50F);
        }
        Quaternionf rotation = new Quaternionf().rotateY((float) Math.toRadians(yawDeg)).rotateX((float) Math.toRadians(pitchDeg));
        float scale = baseScale * zoom;
        renderEntity(graphics, centerX, centerY, scale, rotation, entity);
        return new float[]{yawDeg, pitchDeg, zoom};
    }

}

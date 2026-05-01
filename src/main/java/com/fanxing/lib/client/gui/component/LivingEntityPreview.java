package com.fanxing.lib.client.gui.component;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * 活体实体预览组件。
 */
public class LivingEntityPreview<T extends LivingEntity> extends EntityPreview<T> {

    public LivingEntityPreview(int x, int y, int width, int height, T entity) {
        super(x, y, width, height, entity);
    }

    @Override
    protected void renderFollowAngle(GuiGraphics graphics, float centerX, float centerY, int size, float yOffset, float yaw, float pitch, float partialTick) {
        float yBodyRot = entity.yBodyRot;
        float yRot = entity.getYRot();
        float xRot = entity.getXRot();
        float yHeadRotO = entity.yHeadRotO;
        float yHeadRot = entity.yHeadRot;


        entity.setXRot(pitch);
        entity.yBodyRot = yaw;
        entity.setYRot(yaw);
        entity.yHeadRot = entity.getYRot();
        entity.yHeadRotO = entity.getYRot();

        Quaternionf pitchRot = new Quaternionf().rotateX(pitch * Mth.DEG_TO_RAD);
        Quaternionf finalRot = new Quaternionf().rotateZ((float) Math.PI);
        finalRot = finalRot.mul(pitchRot);
        float scale = entity.getScale();
        Vector3f offset = new Vector3f(0.0F, entity.getBbHeight() / 2.0F + yOffset * scale, 0.0F);
        float scaledSize = size / scale;
        renderEntity(graphics, centerX, centerY, scaledSize, offset, finalRot, null, partialTick);

        entity.yBodyRot = yBodyRot;
        entity.setYRot(yRot);
        entity.setXRot(xRot);
        entity.yHeadRotO = yHeadRotO;
        entity.yHeadRot = yHeadRot;
    }

    @Override
    protected void renderDragRotation(GuiGraphics graphics, int left, int top, int right, int bottom, int size, float yOffset, float partialTick) {
        float centerX = (left + right) / 2.0F;
        float centerY = (top + bottom) / 2.0F;

        float oldYRot = entity.getYRot();
        float oldXRot = entity.getXRot();
        float oldYBodyRot = entity.yBodyRot;
        float oldYHeadRot = entity.yHeadRot;
        float oldYHeadRotO = entity.yHeadRotO;

        entity.setYRot(0);
        entity.setXRot(0);
        entity.yBodyRot = 0;
        entity.yHeadRot = 0;
        entity.yHeadRotO = 0;

        Quaternionf yawRot = new Quaternionf().rotateY(-yaw);
        Quaternionf pitchRot = new Quaternionf().rotateX(pitch);
        Quaternionf finalRot = new Quaternionf().mul(FIX).mul(pitchRot).mul(yawRot);

        float scale = entity.getScale();
        Vector3f offset = new Vector3f(0.0F, entity.getBbHeight() / 2.0F + yOffset * scale, 0.0F);
        float scaledSize = size / scale;
        renderEntity(graphics, centerX, centerY, scaledSize, offset, finalRot, null, partialTick);

        entity.setYRot(oldYRot);
        entity.setXRot(oldXRot);
        entity.yBodyRot = oldYBodyRot;
        entity.yHeadRot = oldYHeadRot;
        entity.yHeadRotO = oldYHeadRotO;
    }
}
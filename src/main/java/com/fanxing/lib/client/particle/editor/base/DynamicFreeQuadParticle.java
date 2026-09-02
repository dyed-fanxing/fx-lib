package com.fanxing.lib.client.particle.editor.base;

import com.fanxing.lib.client.particle.editor.strategy.SizeStrategy;
import com.fanxing.lib.client.render.geometry.QuadRenderer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;


/**
 * 策略配置的动态四边形粒子。
 * 位置、旋转、UV 策略由 AbstractParticle 提供。
 * 大小策略单独控制宽高。
 * @author dyed_fanxing
 * @since  2026/5/13 16:10
 */
public class DynamicFreeQuadParticle extends AbstractPropertyParticle {
    protected SizeStrategy sizeStrategy;
    public float lerpWidth = 1.0f;
    public float lerpHeight = 1.0f;

    public DynamicFreeQuadParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z, 0, 0, 0);
    }

    @Override
    public void tick() {
        super.tick(); // 调用位置、旋转、UV 策略的 tick
        if (sizeStrategy != null) sizeStrategy.tick(this);
    }


    @Override
    public void render(@NotNull VertexConsumer consumer, float cx, float cy, float cz, float partialTick) {

        float hl = lerpWidth * 0.5f;
        float hh = lerpHeight * 0.5f;
        // 旋转矩阵（使用基类的 rotation 四元数）
        float qx = rotation.x, qy = rotation.y, qz = rotation.z, qw = rotation.w;
        float xx = qx * qx, yy = qy * qy, zz = qz * qz, ww = qw * qw;
        float xy = qx * qy, xz = qx * qz, yz = qy * qz;
        float xw = qx * qw, yw = qy * qw, zw = qz * qw;

        float m00 = ww + xx - yy - zz;
        float m01 = 2 * (xy - zw);
        float m10 = 2 * (xy + zw);
        float m11 = ww - xx + yy - zz;
        float m20 = 2 * (xz - yw);
        float m21 = 2 * (yz + xw);

        float brx = Math.fma(m00, hl, m01 * -hh) + cx;
        float bry = Math.fma(m10, hl, m11 * -hh) + cy;
        float brz = Math.fma(m20, hl, m21 * -hh) + cz;

        float trx = Math.fma(m00, hl, m01 * hh) + cx;
        float try_ = Math.fma(m10, hl, m11 * hh) + cy;
        float trz = Math.fma(m20, hl, m21 * hh) + cz;

        float tlx = Math.fma(m00, -hl, m01 * hh) + cx;
        float tly = Math.fma(m10, -hl, m11 * hh) + cy;
        float tlz = Math.fma(m20, -hl, m21 * hh) + cz;

        float blx = Math.fma(m00, -hl, m01 * -hh) + cx;
        float bly = Math.fma(m10, -hl, m11 * -hh) + cy;
        float blz = Math.fma(m20, -hl, m21 * -hh) + cz;

        int r = (int) (rCol * 255);
        int g = (int) (gCol * 255);
        int b = (int) (bCol * 255);
        int a = (int) (alpha * 255);
        int light = getLightColor(partialTick);

        QuadRenderer.render(consumer,
                brx, bry, brz,
                trx, try_, trz,
                tlx, tly, tlz,
                blx, bly, blz,
                r, g, b, a, light,
                lerpU0, lerpV0, lerpU1, lerpV1);
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(float partialTick) {
        if (sizeStrategy != null) sizeStrategy.applyLerpScale(this, partialTick);
        calaLerpPosition(partialTick);
        float halfW = lerpWidth * 0.5f;
        float halfH = lerpHeight * 0.5f;
        return new AABB(lerpX - halfW, lerpY - halfH, lerpZ - halfW,
                lerpX + halfW, lerpY + halfH, lerpZ + halfW);
    }

    @Override
    protected void setSize(float width, float height) {
        super.setSize(width, height);
        this.lerpWidth = width;
        this.lerpHeight = height;
    }



    public void setSizeStrategy(SizeStrategy sizeStrategy) {
        this.sizeStrategy = sizeStrategy;
    }
    public SizeStrategy getSizeStrategy() {
        return sizeStrategy;
    }
}
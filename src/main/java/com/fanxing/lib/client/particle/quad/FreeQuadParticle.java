package com.fanxing.lib.client.particle.quad;


import com.fanxing.lib.client.particle.BaseParticle;
import com.fanxing.lib.client.particle.mesh.QuadParticleRenderer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;

/**
 * @author dyed_fanxing
 * @date 2026/5/7 17:49
 * 自由平面纹理粒子（自由旋转、独立纹理，支持 UV 滚动）
 * 需要配合自定义 ParticleRenderType（纹理需支持 REPEAT 模式）。
 */
public abstract class FreeQuadParticle extends BaseParticle {
    protected FloatUnaryOperator length = ONE;
    protected FloatUnaryOperator height = ONE;
    protected FloatUnaryOperator uMin = ZERO;
    protected FloatUnaryOperator vMin = ZERO;
    protected FloatUnaryOperator uMax = ONE;
    protected FloatUnaryOperator vMax = ONE;
    protected FloatUnaryOperator alphaFactory = ONE;


    public FreeQuadParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz,
                            FloatUnaryOperator length, FloatUnaryOperator height,
                            FloatUnaryOperator uMin, FloatUnaryOperator vMin,
                            FloatUnaryOperator uMax, FloatUnaryOperator vMax,
                            FloatUnaryOperator alphaFactory) {
        super(level, x, y, z, vx, vy, vz);
        this.length = length;
        this.height = height;
        this.uMin = uMin;
        this.vMin = vMin;
        this.uMax = uMax;
        this.vMax = vMax;
        this.alphaFactory = alphaFactory;
    }

    public FreeQuadParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z, 0, 0, 0);
    }


    @Override
    public void render(@NotNull VertexConsumer consumer, float cx, float cy, float cz, float partialTick) {
        float t = getProgress(partialTick);
        float hl = length.apply(t) * 0.5f;
        float hh = height.apply(t) * 0.5f;

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

        setAlpha(alphaFactory.apply(t));
        QuadParticleRenderer.render(consumer,
                brx, bry, brz, trx, try_, trz, tlx, tly, tlz, blx, bly, blz,
                (int) (rCol * 255), (int) (gCol * 255), (int) (bCol * 255), (int) (alpha * 255),
                LightTexture.FULL_BRIGHT,
                uMin.apply(t), vMin.apply(t), uMax.apply(t), vMax.apply(t));
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(float partialTick) {
        calLerpPos(partialTick);
        float t = getProgress(partialTick);
        float hl = length.apply(t) * 0.5f;
        float hh = height.apply(t) * 0.5f;
        float maxHalf = Mth.sqrt(hl * hl + hh * hh);
        return new AABB(lerpX - maxHalf, lerpY - maxHalf, lerpZ - maxHalf,
                lerpX + maxHalf, lerpY + maxHalf, lerpZ + maxHalf);
    }


    public void setSize(FloatUnaryOperator size) {
        this.length = size;this.height = size;
    }
    public void setSize(FloatUnaryOperator length, FloatUnaryOperator width) {
        this.length = width;this.height = length;
    }

    public void setUV(FloatUnaryOperator uMin, FloatUnaryOperator uMax, FloatUnaryOperator vMin, FloatUnaryOperator vMax) {
        this.uMin = uMin;this.uMax = uMax;this.vMin = vMin;this.vMax = vMax;
    }
    public void setUV(FloatUnaryOperator ...uv) {
        uMin = uv[0];vMin = uv[1];uMax = uv[2];vMax = uv[3];
    }

    public void setAlphaFactory(FloatUnaryOperator alphaFactory) {
        this.alphaFactory = alphaFactory;
    }

}
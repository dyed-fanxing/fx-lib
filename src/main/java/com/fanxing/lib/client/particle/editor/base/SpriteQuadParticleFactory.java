package com.fanxing.lib.client.particle.editor.base;

import com.fanxing.lib.client.render.geometry.QuadRenderer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;

/**
 * @author dyed_fanxing
 * @date 2026/5/7 17:33
 * 精灵平面粒子，从精灵纹理集里获取UV
 */
public abstract class SpriteQuadParticleFactory extends AbstractPropertyParticle {
    protected TextureAtlasSprite sprite;
    private FloatUnaryOperator height;
    private FloatUnaryOperator length;

    public SpriteQuadParticleFactory(ClientLevel level, double x, double y, double z, double vx, double vy, double vz) {
        super(level, x, y, z, vx, vy, vz);
    }


    @Override
    public void render(@NotNull VertexConsumer consumer, float cx, float cy, float cz, float partialTick) {
        float progress = getProgress(partialTick);
        float hl = length.apply(partialTick) * 0.5f;
        float hh = height.apply(partialTick) * 0.5f;

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

        float brx = org.joml.Math.fma(m00, hl, m01 * -hh) + cx;
        float bry = org.joml.Math.fma(m10, hl, m11 * -hh) + cy;
        float brz = org.joml.Math.fma(m20, hl, m21 * -hh) + cz;

        float trx = org.joml.Math.fma(m00, hl, m01 * hh) + cx;
        float try_ = org.joml.Math.fma(m10, hl, m11 * hh) + cy;
        float trz = org.joml.Math.fma(m20, hl, m21 * hh) + cz;

        float tlx = org.joml.Math.fma(m00, -hl, m01 * hh) + cx;
        float tly = org.joml.Math.fma(m10, -hl, m11 * hh) + cy;
        float tlz = org.joml.Math.fma(m20, -hl, m21 * hh) + cz;

        float blx = org.joml.Math.fma(m00, -hl, m01 * -hh) + cx;
        float bly = org.joml.Math.fma(m10, -hl, m11 * -hh) + cy;
        float blz = Math.fma(m20, -hl, m21 * -hh) + cz;

        QuadRenderer.render(consumer,
                brx, bry, brz, trx, try_, trz, tlx, tly, tlz, blx, bly, blz,
                rCol, gCol, (int) (bCol * 255), (int) (alpha * 255), getLightColor(partialTick),
                getU0(),getV0(),getU1(),getV1());
    }
    @Override
    public @NotNull AABB getRenderBoundingBox(float partialTick) {
        float t = getProgress(partialTick);
        float hl = length.apply(t) * 0.5f;
        float hh = height.apply(t) * 0.5f;
        float maxHalf = Mth.sqrt(hl * hl + hh * hh);
        return new AABB(lerpX - maxHalf, lerpY - maxHalf, lerpZ - maxHalf,
                lerpX + maxHalf, lerpY + maxHalf, lerpZ + maxHalf);
    }

    protected void setSize(FloatUnaryOperator length,FloatUnaryOperator height) {
        this.length = length;
        this.height = height;
    }
    protected void setSprite(TextureAtlasSprite p_108338_) {
        this.sprite = p_108338_;
    }

    protected float getU0() {return sprite.getU0();}
    protected float getU1() {
        return sprite.getU1();
    }

    protected float getV0() {
        return sprite.getV0();
    }

    protected float getV1() {
        return sprite.getV1();
    }

    public void pickSprite(SpriteSet spriteSet) {
        this.setSprite(spriteSet.get(this.random));
    }

    public void setSpriteFromAge(SpriteSet spriteSet) {
        if (!this.removed) {
            this.setSprite(spriteSet.get(this.age, this.lifetime));
        }
    }


}
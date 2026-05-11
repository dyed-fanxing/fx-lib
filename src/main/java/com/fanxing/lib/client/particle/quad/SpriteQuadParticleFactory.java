package com.fanxing.lib.client.particle.quad;

import com.fanxing.lib.client.particle.BaseParticle;
import com.fanxing.lib.client.particle.mesh.QuadParticleRenderer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * @author dyed_fanxing
 * @date 2026/5/7 17:33
 * 精灵平面粒子，从精灵纹理集里获取UV
 */
public abstract class SpriteQuadParticleFactory extends BaseParticle {
    protected TextureAtlasSprite sprite;
    private FloatUnaryOperator width;
    private FloatUnaryOperator length;

    public SpriteQuadParticleFactory(ClientLevel level, double x, double y, double z, double vx, double vy, double vz) {
        super(level, x, y, z, vx, vy, vz);
    }

    @Override
    public void render(@NotNull VertexConsumer consumer, Vector3f center, Quaternionf rotation, float partialTick) {
        float progress = getProgress(partialTick);
        float halfW = width.apply(progress) * 0.5f;
        float halfH = length.apply(progress) * 0.5f;
        // 局部坐标（未旋转）
        Vector3f br = new Vector3f(halfW, -halfH, 0);
        Vector3f tr = new Vector3f(halfW, halfH, 0);
        Vector3f tl = new Vector3f(-halfW, halfH, 0);
        Vector3f bl = new Vector3f(-halfW, -halfH, 0);
        // 应用旋转并平移到世界中心
        br.rotate(rotation).add(center);
        tr.rotate(rotation).add(center);
        tl.rotate(rotation).add(center);
        bl.rotate(rotation).add(center);
        QuadParticleRenderer.render(consumer,
                br.x(), br.y(), br.z(),
                tr.x(), tr.y(), tr.z(),
                tl.x(), tl.y(), tl.z(),
                bl.x(), bl.y(), bl.z(),
                (int) (rCol * 255), (int) (gCol * 255), (int) (bCol * 255), (int) (alpha * 255), getLightColor(partialTick),
                getU0(),getV0(),getU1(),getV1());
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(float partialTick) {
        Vector3f pos = getInterpolatedPos(partialTick);
        float progress = getProgress(partialTick);
        double cx = pos.x;
        double cy = pos.y;
        double cz = pos.z;
        float hw = width.apply(progress) * 0.5f;
        float hh = length.apply(progress) * 0.5f;
        return new AABB(cx - hw, cy - hh, cz - hw, cx + hw, cy + hh, cz + hw);
    }

    protected void setSize(FloatUnaryOperator width, FloatUnaryOperator length) {
        this.width = width;
        this.length = length;
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
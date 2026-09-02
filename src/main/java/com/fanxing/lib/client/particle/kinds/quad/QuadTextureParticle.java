package com.fanxing.lib.client.particle.kinds.quad;

import com.fanxing.lib.client.particle.AbstractParticle;
import com.fanxing.lib.client.particle.rendertypes.ParticleRenderTypesFxLib;
import com.fanxing.lib.client.render.geometry.QuadRenderer;
import com.fanxing.lib.client.render.instance.format.InstanceFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import java.nio.ByteBuffer;

/**
 * 四边形纹理粒子基类（物理驱动）。
 * 继承 {@link AbstractParticle}，位置由物理引擎驱动。
 * <p>
 * @author dyed_fanxing
 * @since 2026/7/3 00:00
 */
public abstract class QuadTextureParticle extends AbstractParticle {
    protected TextureAtlasSprite sprite;
    protected float width = 1f;
    protected float height = 1f;
    protected QuadRotationMode rotationMode = QuadRotationMode.BILLBOARD;

    public QuadTextureParticle(ClientLevel level, double x, double y, double z, TextureAtlasSprite sprite) {
        super(level, x, y, z);
        this.sprite = sprite;
    }

    public QuadTextureParticle(ClientLevel level, double x, double y, double z, double dx, double dy, double dz, TextureAtlasSprite sprite) {
        super(level, x, y, z, dx, dy, dz);
        this.sprite = sprite;
    }

    public void setSprite(TextureAtlasSprite sprite) {
        this.sprite = sprite;
    }

    public void pickSprite(SpriteSet spriteSet) {
        this.setSprite(spriteSet.get(this.random));
    }
    public void setSpriteFromAge(SpriteSet spriteSet) {
        this.setSprite(spriteSet.get(this.age, this.lifetime));
    }
    // ★ 等比设置
    public void setSize(float size) {
        this.width = size;
        this.height = size;
    }
    // ★ 独立设置
    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    @Override
    protected void applyScale(float partialTick) {
        float progress = getScaleSize(getProgress(partialTick));
        scaleX = width * progress;
        scaleY = height * progress;
    }
    protected float getScaleSize(float progress) {
        return 1f;
    }


    @Override
    protected void applyRotation(float partialTick, Camera camera) {
        switch (rotationMode){
            case BILLBOARD -> {
                rotation.set(camera.rotation());
                rotation.rotateZ(Mth.lerp(partialTick, this.oRoll, this.roll));
            }
            case FIXED_Y -> {
                float halfAngle = Mth.DEG_TO_RAD*camera.getYRot() * 0.5f;
                rotation.set(0, Mth.sin(halfAngle), 0, Mth.cos(halfAngle));
            }
        }
    }


    @Override
    protected final void buildVertices(VertexConsumer consumer, float cx, float cy, float cz, Quaternionf rotation, float partialTick) {
        int light = getLightColor(partialTick);
        switch (rotationMode){
            case BILLBOARD,FIXED_Y -> QuadRenderer.renderZ0(consumer,cx,cy,cz,scaleX,scaleY,rotation,rCol,gCol,bCol,alpha,light,sprite.getU0(),sprite.getV0(),sprite.getU1(),sprite.getV1());
            default -> QuadRenderer.render(consumer,cx,cy,cz,scaleX,scaleY,rotation,rCol,gCol,bCol,alpha,light,sprite.getU0(),sprite.getV0(),sprite.getU1(),sprite.getV1());
        }
    }
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderTypesFxLib.PARTICLE_TRANSLUCENT;
    }
    @Override
    protected int getLightColor(float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }

    public void writeInstanceData(ByteBuffer buffer, InstanceFormat format) {
        format.writePosition(buffer, (float) worldX, (float) worldY, (float) worldZ);
        format.writeRotation(buffer, rotation);
        format.writeScale(buffer, scaleX, scaleY, scaleZ);
        format.writeColor(buffer, rCol, gCol, bCol, alpha);
        float u0 = sprite != null ? sprite.getU0() : 0;
        float v0 = sprite != null ? sprite.getV0() : 0;
        float u1 = sprite != null ? sprite.getU1() : 1;
        float v1 = sprite != null ? sprite.getV1() : 1;
        format.writeUV(buffer, u0, v0, u1, v1);
    }
}
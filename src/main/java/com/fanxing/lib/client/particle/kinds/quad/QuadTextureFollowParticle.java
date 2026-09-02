package com.fanxing.lib.client.particle.kinds.quad;


import com.fanxing.lib.client.particle.AbstractParticle;
import com.fanxing.lib.client.particle.rendertypes.ParticleRenderTypesFxLib;
import com.fanxing.lib.client.particle.tree.FollowParticle;
import com.fanxing.lib.client.render.geometry.QuadRenderer;
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

/**
 * 四边形纹理粒子（跟随父粒子）。
 * 继承 {@link FollowParticle}，位置来自父粒子或曲线路径。
 *
 * @author dyed_fanxing
 * @since 2026/7/3 00:00
 */
public abstract class QuadTextureFollowParticle extends FollowParticle {

    protected TextureAtlasSprite sprite;
    protected float width = 1f;
    protected float height = 1f;
    protected QuadRotationMode rotationMode = QuadRotationMode.BILLBOARD;

    public QuadTextureFollowParticle(ClientLevel level,double lsx,double lsy,double lsz,double lex, double ley, double lez, AbstractParticle parent,TextureAtlasSprite sprite) {
        super(level, lsx,lsy,lsz,lex,ley,lez,parent);
        this.sprite = sprite;
    }
    public QuadTextureFollowParticle(ClientLevel level,double lx,double ly,double lz,AbstractParticle parent, TextureAtlasSprite sprite) {
        super(level,lx,ly,lz,lx,ly,lz,parent);
        this.sprite = sprite;
    }
    public QuadTextureFollowParticle(ClientLevel level, double lsx, double lsy, double lsz, double lex, double ley, double lez, TextureAtlasSprite sprite) {
        super(level, lsx, lsy, lsz, lex, ley, lez);
        this.sprite = sprite;
    }
    public QuadTextureFollowParticle(ClientLevel level,double lx,double ly,double lz,TextureAtlasSprite sprite) {
        super(level,lx,ly,lz,lx,ly,lz);
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

}
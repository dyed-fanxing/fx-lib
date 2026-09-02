package com.fanxing.lib.client.particle.kinds.cube;

import com.fanxing.lib.client.particle.AbstractParticle;
import com.fanxing.lib.client.particle.rendertypes.ParticleRenderTypesFxLib;
import com.fanxing.lib.client.render.geometry.CubeRenderer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

/**
 * @author dyed_fanxing
 * @since 2026/6/13 18:24
 */
public abstract class CubeParticle extends AbstractParticle {

    protected float width,height,length;
    public CubeParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        setSize(1f,1f,1f);
    }
    public CubeParticle(ClientLevel level, double x, double y, double z,float width,float height,float length) {
        super(level, x, y, z);
        setSize(width,height,length);
    }

    @Override
    protected int getLightColor(float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderTypesFxLib.PARTICLE_TRANSLUCENT;
    }

    public void setSize(float width, float height, float length) {
        this.width = width;
        this.height = height;
        this.length = length;
        updateBoundingBox();
    }

    @Override
    public void setPos(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        updateBoundingBox();
    }

    public void updateBoundingBox() {
        float radius = (float) Math.sqrt(width * width + height * height + length * length) * 0.5f;
        this.setBoundingBox(new AABB(x - radius, y - radius, z - radius,
                x + radius, y + radius, z + radius));
    }



    public abstract static class ForwardCubeParticle  extends CubeParticle {
        public ForwardCubeParticle(ClientLevel level, double x, double y, double z) {
            super(level, x, y, z);
        }
        public ForwardCubeParticle(ClientLevel level, double x, double y, double z, float width, float height, float length) {
            super(level, x, y, z, width, height, length);
        }

        @Override
        public void updateBoundingBox() {
            float radius = (float) Math.sqrt(width * width + height * height + 4*length * length) * 0.5f;
            this.setBoundingBox(new AABB(x - radius, y - radius, z - radius,
                    x + radius, y + radius, z + radius));
        }
        public void renderGeometry(@NotNull VertexConsumer consumer, float cx, float cy, float cz,float width,float height,float length,Quaternionf rotation, float partialTick,float u0,float v0,float u1,float v1){
            CubeRenderer.renderForward(consumer,cx,cy,cz,rotation,width,height,length,rCol,gCol,bCol,alpha,getLightColor(partialTick),u0,v0,u1,v1);
        }
    }
}
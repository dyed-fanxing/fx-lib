package com.fanxing.lib.client.particle.editor.base;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import org.jetbrains.annotations.NotNull;

/**
 * @author dyed_fanxing
 * @since 2026/5/24 20:55
 */
public abstract class Abstract3DPropertyParticle extends AbstractPropertyParticle {
    protected ParticleRenderType renderType;
    public float lerpScaleX, lerpScaleY, lerpScaleZ;

    public Abstract3DPropertyParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz) {
        super(level, x, y, z, vx, vy, vz);
    }


    public void setParticleRenderType(ParticleRenderType renderType) {
        this.renderType = renderType;
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return renderType;
    }
}

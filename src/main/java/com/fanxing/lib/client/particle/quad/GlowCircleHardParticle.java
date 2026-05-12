package com.fanxing.lib.client.particle.quad;

import com.fanxing.lib.FxLib;
import com.fanxing.lib.client.particle.property.RotationStrategy;
import com.fanxing.lib.client.particle.rendertypes.ParticleRenderTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * @author dyed_fanxing
 * @date 2026/5/10 17:33
 */
public class GlowCircleHardParticle extends FreeQuadParticle{
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FxLib.MOD_ID,"textures/particle/glow_circle_hard.png");
    public GlowCircleHardParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz) {
        super(level, x, y, z);
        setRotationProperty(RotationStrategy.LOOKAT_XYZ);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderTypes.ADDITIVE_QUADS_CLAMP.apply(TEXTURE);
    }
}

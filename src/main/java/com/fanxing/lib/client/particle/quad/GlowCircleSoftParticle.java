package com.fanxing.lib.client.particle.quad;

import com.fanxing.lib.FxLib;
import com.fanxing.lib.client.particle.property.RotationStrategy;
import com.fanxing.lib.client.particle.rendertypes.BeamParticleRenderTypes;
import com.fanxing.lib.client.particle.rendertypes.ParticleRenderTypes;
import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dyed_fanxing
 * @date 2026/5/10 17:33
 */
public class GlowCircleSoftParticle extends FreeQuadParticle{
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FxLib.MOD_ID,"textures/particle/glow_circle_soft.png");
    private static final Logger log = LoggerFactory.getLogger(GlowCircleSoftParticle.class);

    public GlowCircleSoftParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz,
                                  FloatUnaryOperator length, FloatUnaryOperator height,
                                  FloatUnaryOperator uMin, FloatUnaryOperator vMin, FloatUnaryOperator uMax, FloatUnaryOperator vMax,
                                  FloatUnaryOperator alphaFactory) {
        super(level, x, y, z, vx, vy, vz, length, height, uMin, vMin, uMax, vMax, alphaFactory);
        setRotationProperty(RotationStrategy.LOOKAT_XYZ);
    }

    public GlowCircleSoftParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        setRotationProperty(RotationStrategy.LOOKAT_XYZ);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return BeamParticleRenderTypes.ADDITIVE_QUADS_CLAMP.apply(TEXTURE);
    }
}

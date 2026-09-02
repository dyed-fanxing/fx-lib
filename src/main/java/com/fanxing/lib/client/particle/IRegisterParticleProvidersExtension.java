package com.fanxing.lib.client.particle;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

/**
 * @author dyed_fanxing
 * @since 2026/7/3 18:38
 */
public interface IRegisterParticleProvidersExtension {
    <T extends ParticleOptions> void registerSpriteOnly(ParticleType<T> type);
}
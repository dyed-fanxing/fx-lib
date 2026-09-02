package com.fanxing.lib.client.particle.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

/**
 * @author dyed_fanxing
 * @since 2026/6/13 23:15
 */
public class ParticleSpriteSetUtils {
    /**
     * 获取指定粒子类型的 SpriteSet
     * @param particleKey 粒子的注册 ID（例如 "yourmod:glow"）
     * @return 对应的 SpriteSet，如果不存在则返回 null
     */
    public static SpriteSet getSpriteSet(ResourceLocation particleKey) {
        return Minecraft.getInstance().particleEngine.spriteSets.get(particleKey);
    }
    /**
     * 获取指定粒子类型的 SpriteSet，注册的粒子类型
     */
    public static SpriteSet getSpriteSet(ParticleType<?> particleId) {
        ResourceLocation key = BuiltInRegistries.PARTICLE_TYPE.getKey(particleId);
        return Minecraft.getInstance().particleEngine.spriteSets.get(key);
    }

    public static TextureAtlasSprite getTextureAtlasSprite(ParticleType<?> particleId) {
        return getSpriteSet(particleId).get(0,1);
    }
}

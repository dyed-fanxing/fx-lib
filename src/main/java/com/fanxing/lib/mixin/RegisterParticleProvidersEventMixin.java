package com.fanxing.lib.mixin;

import com.fanxing.lib.client.particle.IRegisterParticleProvidersExtension;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import org.spongepowered.asm.mixin.*;

/**
 * 提供只注册Sprite精灵集的粒子注册接口
 * @author dyed_fanxing
 * @since 2026/7/3 18:03
 */
@Mixin(RegisterParticleProvidersEvent.class)
@Implements(@Interface(iface = IRegisterParticleProvidersExtension.class, prefix = "fx_lib$"))
public abstract class RegisterParticleProvidersEventMixin{
    @Final
    @Shadow
    private ParticleEngine particleEngine;


    @Unique
    public <T extends ParticleOptions> void fx_lib$registerSpriteOnly(ParticleType<T> type) {
        particleEngine.spriteSets.put(BuiltInRegistries.PARTICLE_TYPE.getKey(type), new ParticleEngine.MutableSpriteSet());
    }
}
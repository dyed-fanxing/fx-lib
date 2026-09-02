package com.fanxing.lib.client.steup;

import com.fanxing.lib.client.particle.IRegisterParticleProvidersExtension;
import com.fanxing.lib.client.particle.plain.quad.CustomWhiteAshNoGravityParticle;
import com.fanxing.lib.client.particle.plain.quad.CustomWhiteAshParticle;
import com.fanxing.lib.registry.ParticleTypesFxLib;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

/**
 * @author dyed_fanxing
 * @since 2026/6/12 00:27
 */
@EventBusSubscriber(value = Dist.CLIENT)
public class ParticleRegisterFxLib {

    /**
     * 监听客户端注册粒子提供者事件
     */
    @SubscribeEvent
    public static void registerParticleProviderHandler(final RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ParticleTypesFxLib.CUSTOM_WHITE_ASH.get(), CustomWhiteAshParticle.Provider::new);
        event.registerSpriteSet(ParticleTypesFxLib.CUSTOM_NO_GRAVITY_WHITE_ASH.get(), CustomWhiteAshNoGravityParticle.Provider::new);



        IRegisterParticleProvidersExtension e = ((IRegisterParticleProvidersExtension) event);
        e.registerSpriteOnly(ParticleTypesFxLib.DOT.get());
        e.registerSpriteOnly(ParticleTypesFxLib.ORB_GLOW_HARD.get());
        e.registerSpriteOnly(ParticleTypesFxLib.ORB_GLOW.get());
        e.registerSpriteOnly(ParticleTypesFxLib.ORB.get());
        e.registerSpriteOnly(ParticleTypesFxLib.ORB_C2.get());
        e.registerSpriteOnly(ParticleTypesFxLib.RING_CHANGE.get());
        e.registerSpriteOnly(ParticleTypesFxLib.SHOCK_WAVE_GLOW.get());
    }
}

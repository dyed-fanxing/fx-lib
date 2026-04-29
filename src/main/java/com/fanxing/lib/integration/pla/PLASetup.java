package com.fanxing.lib.integration.pla;

import com.fanxing.lib.client.PlayerAnimations;
import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.api.PlayerAnimationFactory;
import com.zigythebird.playeranimcore.enums.PlayState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * @author dyed_fanxing
 * @date 2026/4/29 15:39
 */
public class PLASetup {
    /**
     * PLA API的注册玩家动画控制器，类似gecklib的动画控制器
     */
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // 注册动画层。1000 是优先级，你可以根据需要调整（文档建议重要动画用 1500+）
            PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(PlayerAnimations.ATTACK, 1500,
                    player -> new PlayerAnimationController(player, (controller, state, animSetter) -> PlayState.STOP)
            );
        });
    }

    public static void register(IEventBus bus) {
        bus.addListener(PLASetup::onClientSetup);
    }
}

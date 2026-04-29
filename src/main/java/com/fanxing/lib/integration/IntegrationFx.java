package com.fanxing.lib.integration;

import com.fanxing.lib.integration.curio.CurioSetup;
import com.fanxing.lib.integration.pla.PLASetup;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;

/**
 * @author dyed_fanxing
 * @date 2026/4/28 18:55
 * 联动其他模组时的注册
 */
public class IntegrationFx {
    public static final boolean IS_LOAD_CURIOS = ModList.get().isLoaded("curios");
    public static final boolean IS_LOAD_PLA = ModList.get().isLoaded("player_animation_library");

    public static void register(IEventBus bus) {
        if (IS_LOAD_CURIOS) CurioSetup.register(bus);
        if (IS_LOAD_PLA) PLASetup.register(bus);
    }
}

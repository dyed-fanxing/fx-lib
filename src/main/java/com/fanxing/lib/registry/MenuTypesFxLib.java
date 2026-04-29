package com.fanxing.lib.registry;

import com.fanxing.lib.FxLib;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MenuTypesFxLib {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, FxLib.MOD_ID);
    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}

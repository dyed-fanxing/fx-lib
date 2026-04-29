package com.fanxing.lib.registry;

import com.fanxing.lib.FxLib;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SoundEventsFxLib {
    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, FxLib.MOD_ID);
    public static void register(IEventBus bus) {
        SOUNDS.register(bus);
    }

    public static DeferredHolder<SoundEvent, SoundEvent> register(String name){
        return SOUNDS.register(name,() ->
                SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(FxLib.MOD_ID,name)));
    }
}

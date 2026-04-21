package com.fanxing.corelib.registry;

import com.fanxing.corelib.FxCoreLib;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class DataComponents {

    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, FxCoreLib.MOD_ID);


    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> USING_ENTITY_ID = DATA_COMPONENTS.registerComponentType("using_entity_id",
            builder -> builder.networkSynchronized(ByteBufCodecs.VAR_INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<Integer>>> COLOR_SCHEME = DATA_COMPONENTS.registerComponentType("rgba_array",
            builder -> builder.persistent(Codec.INT.listOf()).networkSynchronized(ByteBufCodecs.INT.apply(ByteBufCodecs.list())));

    public static void register(IEventBus bus) {
        DATA_COMPONENTS.register(bus);
    }
}

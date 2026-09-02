package com.fanxing.lib.registry;

import com.fanxing.lib.FxLib;
import com.fanxing.lib.item.compoent.ColorPalette;
import com.fanxing.lib.item.compoent.ParticleEditorScreenConfig;
import com.fanxing.lib.item.compoent.particle.ParticleLayer;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

public class DataComponentsFxLib {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, FxLib.MOD_ID);


    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> USING_ENTITY_ID = DATA_COMPONENTS.registerComponentType(
            "using_entity_id", builder -> builder.networkSynchronized(ByteBufCodecs.VAR_INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<Integer>>> COLOR_SCHEME = DATA_COMPONENTS.registerComponentType(
            "color_scheme", builder -> builder.persistent(Codec.INT.listOf()).networkSynchronized(ByteBufCodecs.INT.apply(ByteBufCodecs.list())));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ColorPalette>>> COLOR_PALETTES = DATA_COMPONENTS.registerComponentType(
            "color_palettes", builder -> builder.persistent(Codec.list(ColorPalette.CODEC))
                    .networkSynchronized(ByteBufCodecs.collection(ArrayList::new, ColorPalette.STREAM_CODEC))
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ParticleEditorScreenConfig>> PARTICLE_EDITOR_SCREEN_CONFIG = DATA_COMPONENTS.registerComponentType(
            "particle_editor_screen_config", builder -> builder.persistent(ParticleEditorScreenConfig.CODEC)
                    .networkSynchronized(ParticleEditorScreenConfig.STREAM_CODEC)
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ParticleLayer>> PARTICLE_LAYER = DATA_COMPONENTS.registerComponentType(
            "particle_layer", builder -> builder.persistent(ParticleLayer.CODEC)
                    .networkSynchronized(ParticleLayer.STREAM_CODEC)
    );
    public static void register(IEventBus bus) {
        DATA_COMPONENTS.register(bus);
    }
}

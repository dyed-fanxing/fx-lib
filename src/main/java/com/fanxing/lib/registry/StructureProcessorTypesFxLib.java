package com.fanxing.lib.registry;

import com.fanxing.lib.FxLib;
import com.fanxing.lib.worldgen.processor.OriginWakeProcessor;
import com.fanxing.lib.worldgen.processor.WakeProcessor;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class StructureProcessorTypesFxLib {
    public static final DeferredRegister<StructureProcessorType<?>> PROCESSORS = DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, FxLib.MOD_ID);


    public static final Supplier<StructureProcessorType<WakeProcessor>> WAKE_SPAWNER = PROCESSORS.register("wake", () -> () ->  WakeProcessor.CODEC);
    public static final Supplier<StructureProcessorType<OriginWakeProcessor>> ORIGIN_WAKE_SPAWNER = PROCESSORS.register("origin_wake", () -> () ->  OriginWakeProcessor.CODEC);


    public static void register(IEventBus bus) {
        PROCESSORS.register(bus);
    }
}

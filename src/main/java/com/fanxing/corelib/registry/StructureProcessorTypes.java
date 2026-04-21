package com.fanxing.corelib.registry;

import com.fanxing.corelib.FxCoreLib;
import com.fanxing.corelib.worldgen.processor.OriginWakeProcessor;
import com.fanxing.corelib.worldgen.processor.WakeProcessor;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class StructureProcessorTypes {
    public static final DeferredRegister<StructureProcessorType<?>> PROCESSORS = DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, FxCoreLib.MOD_ID);


    public static final Supplier<StructureProcessorType<WakeProcessor>> WAKE_SPAWNER = PROCESSORS.register("wake", () -> () ->  WakeProcessor.CODEC);
    public static final Supplier<StructureProcessorType<OriginWakeProcessor>> ORIGIN_WAKE_SPAWNER = PROCESSORS.register("origin_wake", () -> () ->  OriginWakeProcessor.CODEC);


    public static void register(IEventBus bus) {
        PROCESSORS.register(bus);
    }
}

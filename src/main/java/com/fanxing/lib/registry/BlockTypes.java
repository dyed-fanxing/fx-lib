package com.fanxing.lib.registry;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.fanxing.lib.FxLib.MOD_ID;

public class BlockTypes {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);// 方块注册器
    public static void register(IEventBus bus) {
        // 将方块延迟注册器注册到模组事件总线
        BLOCKS.register(bus);
    }



}

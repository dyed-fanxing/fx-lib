package com.fanxing.lib.registry;

import com.fanxing.lib.item.ParticleEditorItem;
import com.fanxing.lib.item.compoent.ColorPalette;
import com.fanxing.lib.item.compoent.ParticleEditorScreenConfig;
import com.fanxing.lib.item.compoent.particle.ParticleLayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

import static com.fanxing.lib.FxLib.MOD_ID;

public class ItemTypesFxLib {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID); // 物品注册器
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);// 创造标签页注册器

    public static final DeferredHolder<Item, ParticleEditorItem> PARTICLE_EDITOR = ITEMS.register("particle_editor",
            ()-> new ParticleEditorItem( new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)
                    .component(DataComponentsFxLib.PARTICLE_EDITOR_SCREEN_CONFIG,new ParticleEditorScreenConfig(0.7f,0.8f,0.5f))
                    .component(DataComponentsFxLib.PARTICLE_LAYER,new ParticleLayer())
            )
    );

    // 创建创造标签页，并添加物品，放置在战斗标签页之后
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("fx_lib_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.fx_lib")) // 标签页标题
                    .icon(() -> new ItemStack(PARTICLE_EDITOR.get()))
                    .withTabsBefore(CreativeModeTabs.COMBAT) // 定位在战斗标签页前
                    .displayItems((parameters, output) -> {
                        output.accept(PARTICLE_EDITOR.get());
                    }).build());

    public static void register(IEventBus bus) {
        // 将物品延迟注册器注册到模组事件总线
        ITEMS.register(bus);
        // 将创造创造标签页延迟注册器注册到模组事件总线
        CREATIVE_MODE_TABS.register(bus);
    }
}

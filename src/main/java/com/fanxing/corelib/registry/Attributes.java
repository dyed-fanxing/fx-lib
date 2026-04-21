package com.fanxing.corelib.registry;

import com.fanxing.corelib.FxCoreLib;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.registries.DeferredRegister;


public class Attributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, FxCoreLib.MOD_ID);
    // 伤害间隔（单位：tick）
    public static final Holder<Attribute> DAMAGE_INTERVAL = ATTRIBUTES.register("damage_interval",
            () -> new RangedAttribute("attribute.fx_corelib.damage_interval", 20, 0, 20).setSyncable(true));
}

package com.fanxing.lib.registry;

import com.fanxing.lib.FxLib;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredRegister;


public class AttributesFxLib {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, FxLib.MOD_ID);
    // 注册
    public static final Holder<Attribute> TARGET_INVULNERABILITY_TICKS =
            ATTRIBUTES.register("generic.target_invulnerability_ticks",
                    () -> new RangedAttribute("attribute.name." + FxLib.MOD_ID + ".generic.target_invulnerability_ticks", 1.0, 0.0, 10.0)
                            .setSentiment(Attribute.Sentiment.NEGATIVE).setSyncable(true));

    public static final Holder<Attribute> SELF_INVULNERABILITY_TICKS =
            ATTRIBUTES.register("generic.self_invulnerability_ticks",
                    () -> new RangedAttribute("attribute.name." + FxLib.MOD_ID + ".generic.self_invulnerability_ticks", 1.0, 0.0, 10.0)
                            .setSyncable(true));

    public static void register(IEventBus bus) {
        ATTRIBUTES.register(bus);
    }

    public static void onEntityAttributeModification(EntityAttributeModificationEvent event) {
        for (EntityType<? extends LivingEntity> type : event.getTypes()) {
            event.add(type, AttributesFxLib.TARGET_INVULNERABILITY_TICKS);
            event.add(type, AttributesFxLib.SELF_INVULNERABILITY_TICKS);
        }
    }
}

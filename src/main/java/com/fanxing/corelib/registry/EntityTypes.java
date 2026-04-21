package com.fanxing.corelib.registry;

import com.fanxing.corelib.FxCoreLib;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EntityTypes {
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, FxCoreLib.MOD_ID);
    public static void register(IEventBus bus) {
        ENTITY_TYPES.register(bus);
    }

    public static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(String name, EntityType.Builder<T> builder){
        return ENTITY_TYPES.register(name,() -> builder.build(ResourceLocation.fromNamespaceAndPath(FxCoreLib.MOD_ID,name).toString()));
    }


//    public static final DeferredHolder<EntityType<?>,EntityType<GasterBlaster>> GASTER_BLASTER =
//            ENTITY_TYPES.register("gaster_blaster",
//                    () -> EntityType.Builder.<GasterBlaster>of(GasterBlaster::new, MobCategory.MISC)
//                            .sized(1.5f, 1.5f)  // 碰撞箱
//                            .eyeHeight(0.4f)
//                            .clientTrackingRange(4)  // 客户端同步范围，以区块为单位
//                            .build(ResourceLocation.fromNamespaceAndPath(FxUndertale.MOD_ID,"gaster_blaster").toString())
//            );
//
//    public static final DeferredHolder<EntityType<?>, EntityType<Sans>> SANS = register("sans",
//            EntityType.Builder.of(Sans::new, MobCategory.MONSTER)
//                    .sized(0.8f, 2.0f)  // 碰撞箱
//                    .eyeHeight(1.6665f)
//                    .attach(EntityAttachment.WARDEN_CHEST,0.2f,1.6665f,0.4f)
//                    .clientTrackingRange(4)  // 客户端同步范围，以区块为单位)
//    );

}

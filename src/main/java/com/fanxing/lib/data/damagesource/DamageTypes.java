package com.fanxing.lib.data.damagesource;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

import static com.fanxing.lib.FxLib.MOD_ID;

public interface DamageTypes {

    ResourceKey<DamageType> BEAM = create( "beam");
    ResourceKey<DamageType> FRAME = create("frame");
    ResourceKey<DamageType> KARMA_BLOCKABLE = create("frame_blockable");

    private static ResourceKey<DamageType> create(String name){
        return  ResourceKey.create(Registries.DAMAGE_TYPE,ResourceLocation.fromNamespaceAndPath(MOD_ID, name));
    }
}

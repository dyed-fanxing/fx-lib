package com.fanxing.lib.client.render;

import com.fanxing.lib.FxLib;
import net.minecraft.resources.ResourceLocation;

public interface ResourceLocations {
    ResourceLocation WHITE_TEXTURE = ResourceLocation.withDefaultNamespace("textures/misc/white.png");
    ResourceLocation BLACK_TEXTURE = ResourceLocation.fromNamespaceAndPath(FxLib.MOD_ID,"textures/misc/black.png");
}

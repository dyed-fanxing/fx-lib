package com.fanxing.lib.client.render;

import com.fanxing.lib.FxLib;
import net.minecraft.resources.ResourceLocation;

public interface ResourceLocations {
    ResourceLocation WHITE_TEXTURE = ResourceLocation.withDefaultNamespace("textures/misc/white.png");
    ResourceLocation TEST_TEXTURE = ResourceLocation.fromNamespaceAndPath(FxLib.MOD_ID,"textures/misc/test.png");
    ResourceLocation TWISTING_GLOW_TEXTURE = ResourceLocation.fromNamespaceAndPath(FxLib.MOD_ID,"textures/misc/twisting_glow.png");
    ResourceLocation RIBBON_GLOW_TEXTURE = ResourceLocation.fromNamespaceAndPath(FxLib.MOD_ID,"textures/misc/ribbon_glow_horizontal.png");

}

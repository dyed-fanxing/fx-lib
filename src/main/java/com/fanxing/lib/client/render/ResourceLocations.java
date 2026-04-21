package com.fanxing.lib.client.render;

import com.fanxing.lib.FxLib;
import net.minecraft.resources.ResourceLocation;

public interface ResourceLocations {
    ResourceLocation WHITE_TEXTURE = ResourceLocation.withDefaultNamespace("textures/misc/white.png");
    ResourceLocation BEAM_FLOW_TEXTURE = ResourceLocation.fromNamespaceAndPath(FxLib.MOD_ID,"textures/misc/beam_flow.png");
}

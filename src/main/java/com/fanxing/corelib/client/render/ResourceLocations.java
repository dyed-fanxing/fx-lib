package com.fanxing.corelib.client.render;

import com.fanxing.corelib.FxCoreLib;
import net.minecraft.resources.ResourceLocation;

public interface ResourceLocations {
    ResourceLocation WHITE_TEXTURE = ResourceLocation.withDefaultNamespace("textures/misc/white.png");
    ResourceLocation BEAM_FLOW_TEXTURE = ResourceLocation.fromNamespaceAndPath(FxCoreLib.MOD_ID,"textures/misc/beam_flow.png");
}

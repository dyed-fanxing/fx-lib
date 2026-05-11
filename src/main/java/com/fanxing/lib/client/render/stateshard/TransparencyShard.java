package com.fanxing.lib.client.render.stateshard;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.RenderStateShard;

public interface TransparencyShard {
    RenderStateShard.TransparencyStateShard LINEAR_ADDITIVE_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("linear_additive_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });

    RenderStateShard.TransparencyStateShard PREMULTIPLIED_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("premultiplied_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }, () ->
    {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });





}

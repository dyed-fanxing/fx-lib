package com.fanxing.lib.client.particle.rendertypes;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

/**
 * @author dyed_fanxing
 * @date 2026/5/7 18:33
 */
public enum Blend {
    // 加法混合 (SRC_ALPHA, ONE)
    TRANSPARENT_ADDITIVE(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE),
    // 预乘混合 (ONE, ONE_MINUS_SRC_ALPHA)
    PREMULTIPLIED(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA),
    // 标准透明 (SRC_ALPHA, ONE_MINUS_SRC_ALPHA)
    TRANSLUCENT(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

    public final GlStateManager.SourceFactor rgbSourceFactor;
    public final GlStateManager.DestFactor rgbDestFactor;

    public final GlStateManager.SourceFactor alphaSourceFactor;
    public final GlStateManager.DestFactor alphaDestFactor;

    Blend(GlStateManager.SourceFactor rgbSourceFactor, GlStateManager.DestFactor rgbDestFactor, GlStateManager.SourceFactor alphaSourceFactor, GlStateManager.DestFactor alphaDestFactor) {
        this.rgbSourceFactor = rgbSourceFactor;
        this.rgbDestFactor = rgbDestFactor;
        this.alphaSourceFactor = alphaSourceFactor;
        this.alphaDestFactor = alphaDestFactor;
    }

    public void apply() {
        RenderSystem.blendFuncSeparate(
                rgbSourceFactor, rgbDestFactor,
                alphaSourceFactor, alphaDestFactor
        );
    }
}

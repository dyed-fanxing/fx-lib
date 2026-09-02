package com.fanxing.lib.client.particle.rendertypes;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.StringRepresentable;
import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum BlendMode implements StringRepresentable {
    NO_TRANSPARENT(
            GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE,
            GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE
    ),
    TRANSLUCENT(
            GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
            GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
    ),
    TRANSPARENT_ADDITIVE(
            GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE,
            GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE
    )
    ;
    public final GlStateManager.SourceFactor rgbSourceFactor;
    public final GlStateManager.DestFactor rgbDestFactor;
    public final GlStateManager.SourceFactor alphaSourceFactor;
    public final GlStateManager.DestFactor alphaDestFactor;

    BlendMode(GlStateManager.SourceFactor rgbSourceFactor, GlStateManager.DestFactor rgbDestFactor,
              GlStateManager.SourceFactor alphaSourceFactor, GlStateManager.DestFactor alphaDestFactor) {
        this.rgbSourceFactor = rgbSourceFactor;
        this.rgbDestFactor = rgbDestFactor;
        this.alphaSourceFactor = alphaSourceFactor;
        this.alphaDestFactor = alphaDestFactor;
    }

    @Override
    public @NotNull String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static final Codec<BlendMode> CODEC = StringRepresentable.fromEnum(BlendMode::values);

    public void apply() {
        if(this == NO_TRANSPARENT) RenderSystem.disableBlend();
        else {
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(rgbSourceFactor, rgbDestFactor, alphaSourceFactor, alphaDestFactor);
        }
    }
}
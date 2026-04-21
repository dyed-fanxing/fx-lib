package com.fanxing.lib.client.render.type;

import com.fanxing.lib.client.render.ResourceLocations;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiFunction;

import static net.minecraft.client.renderer.RenderStateShard.*;
import static net.minecraft.client.renderer.RenderStateShard.ADDITIVE_TRANSPARENCY;
import static net.minecraft.client.renderer.RenderStateShard.NO_CULL;
import static net.minecraft.client.renderer.RenderStateShard.OVERLAY;
import static net.minecraft.client.renderer.RenderStateShard.RENDERTYPE_BEACON_BEAM_SHADER;

/**
 * 所有的能量（发光）渲染类型，即加法混合或者更适合特效发光的
 */
public interface EnergyGlowRenderType {

    /**
     * ENERGY_TRIANGLES
     * 能量效果 - 三角形模式
     */
    BiFunction<ResourceLocation, Boolean, RenderType> ENERGY = Util.memoize((texture, sortOnUpload) -> RenderType.create(
            "energy_triangles", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 1536, false, sortOnUpload,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_BEACON_BEAM_SHADER)
                    .setTextureState(new TextureStateShard(texture, false, false))
                    .setTransparencyState(ADDITIVE_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .createCompositeState(false)
    ));


    /**
     * ENERGY_TRIANGLES
     * 能量效果 - 三角形模式
     */
    BiFunction<ResourceLocation, Boolean, RenderType> ENERGY_TRIANGLES = Util.memoize((texture, sortOnUpload) -> RenderType.create(
            "energy_triangles", DefaultVertexFormat.BLOCK, VertexFormat.Mode.TRIANGLES, 1536, false, sortOnUpload,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_BEACON_BEAM_SHADER)
                    .setTextureState(new TextureStateShard(texture, false, false))
                    .setTransparencyState(ADDITIVE_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .createCompositeState(false)
    ));

    /**
     * ENERGY_TRIANGLE_STRIP
     * 能量效果 - 条带模式
     */
    BiFunction<ResourceLocation, Boolean, RenderType> ENERGY_TRIANGLE_STRIP = Util.memoize((texture, sortOnUpload) -> RenderType.create(
            "energy_triangle_strip", DefaultVertexFormat.BLOCK, VertexFormat.Mode.TRIANGLE_STRIP, 1536, false, sortOnUpload,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_BEACON_BEAM_SHADER)
                    .setTextureState(new TextureStateShard(texture, false, false))
                    .setTransparencyState(ADDITIVE_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(false)
    ));

    /**
     * ENERGY_TRIANGLE_FAN
     * 能量效果 - 扇形模式
     */
    BiFunction<ResourceLocation, Boolean, RenderType> ENERGY_TRIANGLE_FAN = Util.memoize((texture, sortOnUpload) -> RenderType.create(
            "energy_triangle_fan", DefaultVertexFormat.BLOCK, VertexFormat.Mode.TRIANGLE_FAN, 1536, false, sortOnUpload,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_BEACON_BEAM_SHADER)
                    .setTextureState(new TextureStateShard(texture, false, false))
                    .setTransparencyState(ADDITIVE_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .createCompositeState(false)
    ));

    RenderType ENERGY_TRIANGLE_STRIP_WHITE = ENERGY_TRIANGLE_STRIP.apply(ResourceLocations.WHITE_TEXTURE, true);
    RenderType ENERGY_TRIANGLE_FAN_WHITE = ENERGY_TRIANGLE_FAN.apply(ResourceLocations.WHITE_TEXTURE, true);

}

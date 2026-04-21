package com.fanxing.lib.client.render.type;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

import static net.minecraft.client.renderer.RenderStateShard.*;

public interface LightingRenderType {
//    public static final RenderType LIGHTNING = create(
//            "lightning",
//            DefaultVertexFormat.POSITION_COLOR,
//            VertexFormat.Mode.QUADS,
//            1536,
//            false,
//            true,
//            RenderType.CompositeState.builder()
//                    .setShaderState(RENDERTYPE_LIGHTNING_SHADER)
//                    .setWriteMaskState(COLOR_DEPTH_WRITE)
//                    .setTransparencyState(LIGHTNING_TRANSPARENCY)
//                    .setOutputState(WEATHER_TARGET)
//                    .createCompositeState(false)
//    );
    // ========== ENERGY 能量渲染效果 ==========
    /**
     * LIGHTNING_TRIANGLES
     * 能量效果 - 三角形模式
     */
    Function<ResourceLocation, RenderType> LIGHTNING_TRIANGLES = Util.memoize((texture) -> RenderType.create(
            "lightning_triangles", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLES, 1536, false, true,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_LIGHTNING_SHADER)
                    .setTextureState(new TextureStateShard(texture, false, false))
                    .setTransparencyState(LIGHTNING_TRANSPARENCY)
                    .setOutputState(WEATHER_TARGET)
                    .setCullState(NO_CULL)
                    .createCompositeState(false)
    ));

    /**
     * LIGHTNING_TRIANGLE_STRIP
     * 能量效果 - 条带模式
     */
    Function<ResourceLocation, RenderType> LIGHTNING_TRIANGLE_STRIP = Util.memoize((texture) -> RenderType.create(
            "lightning_triangle_strip", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLE_STRIP, 1536, false, true,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_LIGHTNING_SHADER)
                    .setTextureState(new TextureStateShard(texture, false, false))
                    .setTransparencyState(LIGHTNING_TRANSPARENCY)
                    .setOutputState(WEATHER_TARGET)
                    .setCullState(NO_CULL)
                    .createCompositeState(false)
    ));

    /**
     * LIGHTNING_TRIANGLE_FAN
     */
    Function<ResourceLocation, RenderType> LIGHTNING_TRIANGLE_FAN = Util.memoize((texture) -> RenderType.create(
            "lightning_triangle_fan", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLE_FAN, 1536, false, true,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_LIGHTNING_SHADER)
                    .setTextureState(new TextureStateShard(texture, false, false))
                    .setTransparencyState(LIGHTNING_TRANSPARENCY)
                    .setOutputState(WEATHER_TARGET)
                    .setCullState(NO_CULL)
                    .createCompositeState(false)
    ));



    /**
     * LIGHTNING_TRIANGLES
     * 能量效果 - 三角形模式
     */
    Function<ResourceLocation, RenderType> LIGHTNING_ENERGY = Util.memoize((texture) -> RenderType.create(
            "lightning_additive", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 1536, false, true,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_LIGHTNING_SHADER)
                    .setTextureState(new TextureStateShard(texture, false, false))
                    .setTransparencyState(ADDITIVE_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .createCompositeState(false)
    ));
}

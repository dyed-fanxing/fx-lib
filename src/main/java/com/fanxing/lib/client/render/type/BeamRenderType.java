package com.fanxing.lib.client.render.type;

import com.fanxing.lib.client.render.ResourceLocations;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiFunction;
import java.util.function.Function;

import static net.minecraft.client.renderer.RenderStateShard.*;
import static net.minecraft.client.renderer.RenderStateShard.ADDITIVE_TRANSPARENCY;
import static net.minecraft.client.renderer.RenderStateShard.COLOR_DEPTH_WRITE;
import static net.minecraft.client.renderer.RenderStateShard.COLOR_WRITE;
import static net.minecraft.client.renderer.RenderStateShard.NO_CULL;
import static net.minecraft.client.renderer.RenderStateShard.NO_TRANSPARENCY;
import static net.minecraft.client.renderer.RenderStateShard.RENDERTYPE_BEACON_BEAM_SHADER;
import static net.minecraft.client.renderer.RenderStateShard.TRANSLUCENT_TRANSPARENCY;

public interface BeamRenderType {

    /**
     * 与原版beam的区别为 NO_CULL，因为这个GB炮是跟着实体渲染的，而原版的信标光束是跟着方块渲染的，顺序不一样
     * 如果使用原版的beam，会导致光束不会覆盖穿过的实体，而是会在光束中看到实体，且光束的两端会被GB炮覆盖导致透明
     */
    BiFunction<ResourceLocation, Boolean, RenderType> BEAM_NO_CULL = Util.memoize((texture, translucent) -> RenderType.create(
            "beam_no_cull", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 1536, false, translucent,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_BEACON_BEAM_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                    .setTransparencyState(translucent ? TRANSLUCENT_TRANSPARENCY : NO_TRANSPARENCY)
                    .setWriteMaskState(translucent ? COLOR_WRITE : COLOR_DEPTH_WRITE) // 透明只写颜色，不透明写颜色+深度
                    .setCullState(NO_CULL)
                    .createCompositeState(false)
    ));
    BiFunction<ResourceLocation, Boolean, RenderType> BEAM_NO_CULL_TRIANGLE_STRIP = Util.memoize((texture, translucent) -> RenderType.create(
            "beam_no_cull_triangle_strip", DefaultVertexFormat.BLOCK, VertexFormat.Mode.TRIANGLE_STRIP, 1536, false, translucent,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_BEACON_BEAM_SHADER)
                    .setTextureState(new TextureStateShard(texture, false, false))
                    .setTransparencyState(translucent ? TRANSLUCENT_TRANSPARENCY : NO_TRANSPARENCY)
                    .setWriteMaskState(translucent ? COLOR_WRITE : COLOR_DEPTH_WRITE) // 透明只写颜色，不透明写颜色+深度
                    .setCullState(NO_CULL)
                    .createCompositeState(false)
    ));

    /**
     * ENERGY_BEAM_FLOW - 高亮能量流动效果（带偏移）
     * 使用信标着色器 + 加法混合 + 无光照，颜色鲜艳且不受光影影响。
     */
    Function<ResourceLocation, RenderType> ENERGY_BEAM = Util.memoize((texture) -> RenderType.create(
            "energy_beam",DefaultVertexFormat.BLOCK,VertexFormat.Mode.QUADS,1536,false,false,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_BEACON_BEAM_SHADER)
                    .setTextureState(new TextureStateShard(texture, false, false))
                    .setTransparencyState(ADDITIVE_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .setWriteMaskState(COLOR_WRITE)          // 不写深度
                    .createCompositeState(false)
    ));
    /**
     * ENERGY_BEAM_FLOW_TRIANGLE_STRIP - 高亮能量流动效果（带偏移，条带模式）
     * 使用信标着色器 + 加法混合 + 无光照，颜色鲜艳且不受光影影响。
     */
    Function<ResourceLocation, RenderType> ENERGY_BEAM_TRIANGLE = Util.memoize((texture) -> RenderType.create(
            "energy_beam_triangle_strip",DefaultVertexFormat.BLOCK,VertexFormat.Mode.TRIANGLES,1536,false,false,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_BEACON_BEAM_SHADER)
                    .setTextureState(new TextureStateShard(texture, false, false))
                    .setTransparencyState(ADDITIVE_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .setWriteMaskState(COLOR_WRITE)          // 不写深度
                    .createCompositeState(false)
    ));
    /**
     * ENERGY_BEAM_FLOW_TRIANGLE_STRIP - 高亮能量流动效果（带偏移，条带模式）
     * 使用信标着色器 + 加法混合 + 无光照，颜色鲜艳且不受光影影响。
     */
    Function<ResourceLocation, RenderType> ENERGY_BEAM_TRIANGLE_STRIP = Util.memoize((texture) -> RenderType.create(
            "energy_beam_triangle_strip",DefaultVertexFormat.BLOCK,VertexFormat.Mode.TRIANGLE_STRIP,1536,false,false,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_BEACON_BEAM_SHADER)
                    .setTextureState(new TextureStateShard(texture, false, false))
                    .setTransparencyState(ADDITIVE_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .setWriteMaskState(COLOR_WRITE)          // 不写深度
                    .createCompositeState(false)
    ));
    /**
     * ENERGY_TRIANGLE_FAN
     * 能量效果 - 扇形模式
     */
    Function<ResourceLocation, RenderType> ENERGY_BEAM_TRIANGLE_FAN = Util.memoize((texture) -> RenderType.create(
            "energy_beam_triangle_fan", DefaultVertexFormat.BLOCK, VertexFormat.Mode.TRIANGLE_FAN, 1536, false, false,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_BEACON_BEAM_SHADER)
                    .setTextureState(new TextureStateShard(texture, false, false))
                    .setTransparencyState(ADDITIVE_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .createCompositeState(false)
    ));

    RenderType ENERGY_BEAM_TRIANGLE_STRIP_WHITE = ENERGY_BEAM_TRIANGLE_STRIP.apply(ResourceLocations.WHITE_TEXTURE);
    RenderType ENERGY_BEAM_TRIANGLE_FAN_WHITE = ENERGY_BEAM_TRIANGLE_FAN.apply(ResourceLocations.WHITE_TEXTURE);
}

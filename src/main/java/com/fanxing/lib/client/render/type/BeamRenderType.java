package com.fanxing.lib.client.render.type;

import com.fanxing.lib.FxLib;
import com.fanxing.lib.client.render.ResourceLocations;
import com.fanxing.lib.client.render.stateshard.TransparencyShard;
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
    ResourceLocation FLOW_BEAM_TEXTURE = ResourceLocation.fromNamespaceAndPath(FxLib.MOD_ID, "textures/misc/flow_beam_glow.png");
    ResourceLocation FLOW_ROLL_BEAM_TEXTURE = ResourceLocation.fromNamespaceAndPath(FxLib.MOD_ID, "textures/misc/diagonal_mirror_gradient.png");
    ResourceLocation RIBBON_GLOW_TEXTURE = ResourceLocation.fromNamespaceAndPath(FxLib.MOD_ID, "textures/misc/ribbon_glow_horizontal.png");

    /**
     * 与原版beam的区别为 NO_CULL，因为这个GB炮是跟着实体渲染的，而原版的信标光束是跟着方块渲染的，顺序不一样
     * 如果使用原版的beam，会导致光束不会覆盖穿过的实体，而是会在光束中看到实体，且光束的两端会被GB炮覆盖导致透明
     */
    BiFunction<ResourceLocation, Boolean, RenderType> BEAM_NO_CULL = Util.memoize((texture, translucent) -> RenderType.create(
            "beam_no_cull", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 1536, false, true,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_BEACON_BEAM_SHADER)
                    .setTextureState(new TextureStateShard(texture, false, false))
                    .setTransparencyState(translucent ? TRANSLUCENT_TRANSPARENCY : NO_TRANSPARENCY)
                    .setWriteMaskState(translucent ? COLOR_WRITE : COLOR_DEPTH_WRITE) // 透明只写颜色，不透明写颜色+深度
                    .setCullState(NO_CULL)
                    .createCompositeState(false)
    ));
    RenderType BEAM_NO_TRANSPARENCY_WHITE = BeamRenderType.BEAM_NO_CULL.apply(ResourceLocations.WHITE_TEXTURE, false);
    RenderType BEAM_TRANSPARENCY_WHITE = BeamRenderType.BEAM_NO_CULL.apply(ResourceLocations.WHITE_TEXTURE, true);


    BiFunction<ResourceLocation, Boolean, RenderType> BEAM_NO_CULL_TRIANGLE = Util.memoize((texture, translucent) -> RenderType.create(
            "beam_no_cull_triangle", DefaultVertexFormat.BLOCK, VertexFormat.Mode.TRIANGLES, 1536, false, true,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_BEACON_BEAM_SHADER)
                    .setTextureState(new TextureStateShard(texture, false, false))
                    .setTransparencyState(translucent ? TRANSLUCENT_TRANSPARENCY : NO_TRANSPARENCY)
                    .setWriteMaskState(translucent ? COLOR_WRITE : COLOR_DEPTH_WRITE) // 透明只写颜色，不透明写颜色+深度
                    .setCullState(NO_CULL)
                    .createCompositeState(false)
    ));
    RenderType BEAM_TRANSPARENCY_TRIANGLE_WHITE = BeamRenderType.BEAM_NO_CULL_TRIANGLE.apply(ResourceLocations.WHITE_TEXTURE, true);

    RenderType BEAM_NO_TRANSPARENCY_TRIANGLE_WHITE = BeamRenderType.BEAM_NO_CULL_TRIANGLE.apply(ResourceLocations.WHITE_TEXTURE, false);
    BiFunction<ResourceLocation, Boolean, RenderType> BEAM_NO_CULL_TRIANGLE_STRIP = Util.memoize((texture, translucent) -> RenderType.create(
            "beam_no_cull_triangle_strip", DefaultVertexFormat.BLOCK, VertexFormat.Mode.TRIANGLE_STRIP, 1536, false, true,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_BEACON_BEAM_SHADER)
                    .setTextureState(new TextureStateShard(texture, false, false))
                    .setTransparencyState(translucent ? TRANSLUCENT_TRANSPARENCY : NO_TRANSPARENCY)
                    .setWriteMaskState(translucent ? COLOR_WRITE : COLOR_DEPTH_WRITE) // 透明只写颜色，不透明写颜色+深度
                    .setCullState(NO_CULL)
                    .createCompositeState(false)
    ));
    RenderType BEAM_NO_TRANSPARENCY_TRIANGLE_STRIP_WHITE = BeamRenderType.BEAM_NO_CULL_TRIANGLE_STRIP.apply(ResourceLocations.WHITE_TEXTURE, false);
    RenderType BEAM_TRANSPARENCY_TRIANGLE_STRIP_WHITE = BeamRenderType.BEAM_NO_CULL_TRIANGLE_STRIP.apply(ResourceLocations.WHITE_TEXTURE, true);



    /**
     * ENERGY_BEAM - 高亮能量效果（带偏移）
     * 使用信标着色器 + 加法混合 + 无光照，颜色鲜艳且不受光照影响。
     */
    Function<ResourceLocation, RenderType> ENERGY_BEAM = Util.memoize((texture) -> RenderType.create(
            "energy_beam", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 1536, false, true,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_BEACON_BEAM_SHADER)
                    .setTextureState(new TextureStateShard(texture, false, false))
                    .setTransparencyState(TransparencyShard.PREMULTIPLIED_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .setWriteMaskState(COLOR_WRITE)          // 不写深度
                    .createCompositeState(false)
    ));
    RenderType ENERGY_BEAM_WHITE = ENERGY_BEAM.apply(ResourceLocations.WHITE_TEXTURE);
    RenderType ENERGY_FLOW_BEAM_WHITE = ENERGY_BEAM.apply(FLOW_BEAM_TEXTURE);
    /**
     * ENERGY_BEAM_TRIANGLE_STRIP - 高亮能量效果（带偏移，条带模式）
     * 使用信标着色器 + 加法混合 + 无光照，颜色鲜艳且不受光照影响。
     */
    Function<ResourceLocation, RenderType> ENERGY_BEAM_TRIANGLE = Util.memoize((texture) -> RenderType.create(
            "energy_beam_triangle", DefaultVertexFormat.BLOCK, VertexFormat.Mode.TRIANGLES, 1536, false, true,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_BEACON_BEAM_SHADER)
                    .setTextureState(new TextureStateShard(texture, false, false))
                    .setTransparencyState(TransparencyShard.PREMULTIPLIED_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .setWriteMaskState(COLOR_WRITE)          // 不写深度
                    .createCompositeState(false)
    ));
    RenderType ENERGY_BEAM_TRIANGLE_WHITE = ENERGY_BEAM_TRIANGLE.apply(ResourceLocations.WHITE_TEXTURE);
    RenderType ENERGY_FLOW_BEAM_TRIANGLE_WHITE = ENERGY_BEAM_TRIANGLE.apply(FLOW_BEAM_TEXTURE);

    /**
     * ENERGY_BEAM_TRIANGLE_STRIP - 高亮能量效果（带偏移，条带模式）
     * 使用信标着色器 + 加法混合 + 无光照，颜色鲜艳且不受光照影响。
     */
    Function<ResourceLocation, RenderType> ENERGY_BEAM_TRIANGLE_STRIP = Util.memoize((texture) -> RenderType.create(
            "energy_beam_triangle_strip", DefaultVertexFormat.BLOCK, VertexFormat.Mode.TRIANGLE_STRIP, 1536, false, true,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_BEACON_BEAM_SHADER)
                    .setTextureState(new TextureStateShard(texture, false, false))
                    .setTransparencyState(TransparencyShard.PREMULTIPLIED_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .setWriteMaskState(COLOR_WRITE)          // 不写深度
                    .createCompositeState(false)
    ));

    RenderType ENERGY_BEAM_TRIANGLE_STRIP_WHITE = ENERGY_BEAM_TRIANGLE_STRIP.apply(ResourceLocations.WHITE_TEXTURE);
    RenderType ENERGY_FLOW_BEAM_TRIANGLE_STRIP_WHITE = ENERGY_BEAM_TRIANGLE_STRIP.apply(FLOW_BEAM_TEXTURE);
    /**
     * ENERGY_TRIANGLE_FAN
     * 能量效果 - 扇形模式
     */
    Function<ResourceLocation, RenderType> ENERGY_BEAM_TRIANGLE_FAN = Util.memoize((texture) -> RenderType.create(
            "energy_beam_triangle_fan", DefaultVertexFormat.BLOCK, VertexFormat.Mode.TRIANGLE_FAN, 1536, false, true,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_BEACON_BEAM_SHADER)
                    .setTextureState(new TextureStateShard(texture, false, false))
                    .setTransparencyState(ADDITIVE_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .createCompositeState(false)
    ));
    RenderType ENERGY_BEAM_TRIANGLE_FAN_WHITE = ENERGY_BEAM_TRIANGLE_FAN.apply(ResourceLocations.WHITE_TEXTURE);


}

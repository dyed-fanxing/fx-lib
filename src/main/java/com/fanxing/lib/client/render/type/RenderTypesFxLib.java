package com.fanxing.lib.client.render.type;

import com.fanxing.lib.client.render.ResourceLocations;
import com.fanxing.lib.client.render.stateshard.ShaderStateShards;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiFunction;
import java.util.function.Function;

import static net.minecraft.client.renderer.RenderStateShard.*;

/**
 * @author Sakpeipei
 * @since 2026/1/6 13:50
 */
public class RenderTypesFxLib {
    public static final RenderType DEPTH_QUAD = RenderType.create("depth_quad", DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS, 1536, false, true,
            RenderType.CompositeState.builder()
                    .setShaderState(POSITION_SHADER)
                    .setCullState(NO_CULL)
                    .setWriteMaskState(DEPTH_WRITE)
                    .createCompositeState(false));

    public static final RenderType ENTITY_TRANSLUCENT_EMISSIVE_WHITE = RenderType.ENTITY_TRANSLUCENT_EMISSIVE.apply(ResourceLocations.WHITE_TEXTURE,true);

    // ##### BEAM 光束 ######
    public static final BiFunction<ResourceLocation, Boolean, RenderType> BEAM = Util.memoize((texture, translucent) -> RenderType.create(
            "beam", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 1536, false, false,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_BEACON_BEAM_SHADER)
                    .setTextureState(new TextureStateShard(texture, false, false))
                    .setTransparencyState(translucent ? TRANSLUCENT_TRANSPARENCY : NO_TRANSPARENCY)
                    .setWriteMaskState(translucent ? COLOR_WRITE : COLOR_DEPTH_WRITE) // 透明只写颜色，不透明写颜色+深度
                    .createCompositeState(false)
    ));

    /**
     * KEY 用于半透明写入颜色深度，以防止半透明遮挡渲染错误
     *  由于MC引擎架构问题，无法完全解决半透明排序问题，只能尽量使用写入深度来解决，即使是半透明也要写入深度
     *  1.原版已存在的问题：
     *    1.1 在实体阶段渲染特效，不写深度会被方块实体、水面、云等在实体渲染阶段之后的半透明效果混合或覆盖，而写入深度则会覆盖后面这些效果
     *    1.2 半透明混合：半透明实体会透视实体渲染之后的阶段，导致混合错误，例如：原版旋风人、史莱姆等半透明实体就可以透视信标光束（方块实体阶段）、水面、云等
     *  2.画质模式的区别：
     *    2.1 原版的半透明粒子在非极佳画质模式下，会被水面截断，
     *    2.2 极佳画质下和水面会正确混合，不会被截断
     *  3.半透明混合特效与非透明特效
     *    3.1 半透明混合特效：设计在粒子阶段或之后，以保证不会被之前的阶段，写过深度的片段遮挡
     *    3.2 非透明特效：可以直接设计在实体渲染阶段
     *    3.3 简单背景特效直接用原版粒子系统
     *    3.4 含有判定伤害的特效使用实体，不涉及到半透明混合的复杂特效时，直接使用粒子点缀
     *    3.5 需要复杂特效（透明混合特效、3D物体等复杂网格等复合特效）的时候，使用EntityEffectStageRender，在深度内部的点缀特效自己渲染，不在的可以直接使用原版粒子
     *    3.6 如果在粒子阶段之后渲染，那么在该特效网格深度内部的额外点缀特效就不能使用粒子，而是在半透明渲染之前自己手动渲染这种点缀特效，在所有半透明特效混合写完之后再写入深度
     *  4.总结
     *    4.1 就是一句话，向粒子看齐，粒子有半透明和不透明粒子，它们都会写入深度
     *    4.2 不写入深度就会被之后阶段的片段混合或覆盖，写入深度就会透视之后阶段的片段
     *    4.3 由于所有特效都要写入深度，所以所有透明混合的特效内部，都必须自己手动渲染自己决定混合顺序，
     *        最后写入整体的大网格深度，这样就保证这个内部细节是正确混合的，且整体写入了深度，就不会发生混合错误
     */
    public static final  Function<ResourceLocation, RenderType> BEAM_TRANSPARENCY_COLOR_DEPTH = Util.memoize((texture) -> RenderType.create(
            "beam_transparency_depth", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 1536, false, true,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_BEACON_BEAM_SHADER)
                    .setTextureState(new TextureStateShard(texture, false, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setWriteMaskState(COLOR_DEPTH_WRITE)
                    .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                    .createCompositeState(false)
    ));

    public static final RenderType BEAM_NO_TRANSPARENCY_WHITE = BEAM.apply(ResourceLocations.WHITE_TEXTURE, false);
    public static final RenderType BEAM_TRANSPARENCY_WHITE = BEAM.apply(ResourceLocations.WHITE_TEXTURE, true);


    /**
     * ENERGY_BEAM - 高亮能量效果（带偏移）
     * 使用信标着色器 + 雷电加法混合 + 无光照，颜色鲜艳且不受光照影响。
     */
    public static final Function<ResourceLocation, RenderType> ENERGY_BEAM = Util.memoize((texture) -> RenderType.create(
            "energy_beam", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 1536, false, false,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_BEACON_BEAM_SHADER)
                    .setTextureState(new TextureStateShard(texture, false, false))
                    .setTransparencyState(LIGHTNING_TRANSPARENCY)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(false)
    ));
    public static final RenderType ENERGY_BEAM_WHITE = ENERGY_BEAM.apply(ResourceLocations.WHITE_TEXTURE);



    // ######################################## 粒子渲染类型 ########################################
    public static final RenderType PARTICLE_SHEET_TRANSLUCENT = RenderType.create(
            "particle_sheet_translucent", DefaultVertexFormat.PARTICLE, VertexFormat.Mode.QUADS, 1536, false, false,
            RenderType.CompositeState.builder()
                    .setShaderState(ShaderStateShards.PARTICLE_SHADER)
                    .setTextureState(new TextureStateShard(TextureAtlas.LOCATION_PARTICLES, false, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(false)
    );
    public static final RenderType PARTICLE_SHEET_OPAQUE = RenderType.create(
            "particle_sheet_opaque", DefaultVertexFormat.PARTICLE, VertexFormat.Mode.QUADS, 1536, false, false,
            RenderType.CompositeState.builder()
                    .setShaderState(ShaderStateShards.PARTICLE_SHADER)
                    .setTextureState(new TextureStateShard(TextureAtlas.LOCATION_PARTICLES, false, false))
                    .setWriteMaskState(COLOR_DEPTH_WRITE)
                    .createCompositeState(false)
    );

}

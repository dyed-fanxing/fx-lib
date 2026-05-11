package com.fanxing.lib.client.render.type;

import com.fanxing.lib.client.Shaders;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

/**
 * @author dyed_fanxing
 * @date 2026/5/5 00:31
 */
public interface DebugRenderType {
    static RenderType depthDebug(ResourceLocation texture) {
        return RenderType.create(
                "depth_debug",
                DefaultVertexFormat.POSITION_TEX,
                VertexFormat.Mode.QUADS,
                256,
                false,
                false,
                RenderType.CompositeState.builder()
                        .setShaderState(new RenderStateShard.ShaderStateShard(Shaders::getDepthDebugShader))
                        .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                        .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
                        .setCullState(RenderStateShard.NO_CULL)
                        .createCompositeState(false)
        );
    }
    public static RenderType depthDebugTriangles(ResourceLocation texture) {
        return RenderType.create(
                "depth_debug_triangles",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.TRIANGLES,
                1536,
                false,
                false,
                RenderType.CompositeState.builder()
                        .setShaderState(new RenderStateShard.ShaderStateShard(Shaders::getDepthDebugShader))
                        .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                        .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
                        .setCullState(RenderStateShard.NO_CULL)
                        .createCompositeState(false)
        );
    }

    public static RenderType depthDebugTriangleStrip(ResourceLocation texture) {
        return RenderType.create(
                "depth_debug_triangle_strip",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.TRIANGLE_STRIP,
                1536,
                false,
                false,
                RenderType.CompositeState.builder()
                        .setShaderState(new RenderStateShard.ShaderStateShard(Shaders::getDepthDebugShader))
                        .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                        .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
                        .setCullState(RenderStateShard.NO_CULL)
                        .createCompositeState(false)
        );
    }
}

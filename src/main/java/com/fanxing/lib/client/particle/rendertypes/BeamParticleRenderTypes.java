package com.fanxing.lib.client.particle.rendertypes;

import com.fanxing.lib.client.vertex.VertexFormatFxLib;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.util.function.Function;

public interface BeamParticleRenderTypes {

    // ========== QUADS ==========
    Function<ResourceLocation, ParticleRenderType> ADDITIVE_QUADS_CLAMP = createClamp(VertexFormat.Mode.QUADS, Blend.TRANSPARENT_ADDITIVE);
    Function<ResourceLocation, ParticleRenderType> ADDITIVE_QUADS_REPEAT = createRepeat(VertexFormat.Mode.QUADS, Blend.TRANSPARENT_ADDITIVE);
    Function<ResourceLocation, ParticleRenderType> PREMULTIPLIED_QUADS_CLAMP = createClamp(VertexFormat.Mode.QUADS, Blend.PREMULTIPLIED);
    Function<ResourceLocation, ParticleRenderType> PREMULTIPLIED_QUADS_REPEAT = createRepeat(VertexFormat.Mode.QUADS, Blend.PREMULTIPLIED);
    Function<ResourceLocation, ParticleRenderType> TRANSLUCENT_QUADS_REPEAT = createRepeat(VertexFormat.Mode.QUADS, Blend.TRANSLUCENT);

    // ========== TRIANGLES ==========
    Function<ResourceLocation, ParticleRenderType> ADDITIVE_TRIANGLES_CLAMP = createClamp(VertexFormat.Mode.TRIANGLES, Blend.TRANSPARENT_ADDITIVE);
    Function<ResourceLocation, ParticleRenderType> ADDITIVE_TRIANGLES_REPEAT = createRepeat(VertexFormat.Mode.TRIANGLES, Blend.TRANSPARENT_ADDITIVE);
    Function<ResourceLocation, ParticleRenderType> PREMULTIPLIED_TRIANGLES_CLAMP = createClamp(VertexFormat.Mode.TRIANGLES, Blend.PREMULTIPLIED);
    Function<ResourceLocation, ParticleRenderType> PREMULTIPLIED_TRIANGLES_REPEAT = createRepeat(VertexFormat.Mode.TRIANGLES, Blend.PREMULTIPLIED);

    // ---------- 工厂方法 ----------
    static Function<ResourceLocation, ParticleRenderType> createClamp(VertexFormat.Mode mode, Blend blend) {
        return Util.memoize(texture -> new ParticleRenderType() {
            @Override
            public BufferBuilder begin(@NotNull Tesselator tesselator, @NotNull TextureManager textureManager) {
                RenderSystem.enableBlend();
                RenderSystem.disableCull();
                blend.apply();
                RenderSystem.depthMask(false);
                RenderSystem.setShader(GameRenderer::getRendertypeBeaconBeamShader);
                RenderSystem.setShaderTexture(0, texture);
                // 默认 CLAMP
                return tesselator.begin(mode, DefaultVertexFormat.BLOCK);
            }
            @Override
            public String toString() {
                return "PARTICLE_" + mode.name() + "_" + blend.name() + "_CLAMP";
            }
        });
    }

        static Function<ResourceLocation, ParticleRenderType> createRepeat(VertexFormat.Mode mode, Blend blend) {
            return Util.memoize(texture -> new ParticleRenderType() {
                @Override
                public BufferBuilder begin(@NotNull Tesselator tesselator, @NotNull TextureManager textureManager) {
                    // 获取纹理的 OpenGL ID
                    AbstractTexture abstractTexture = textureManager.getTexture(texture);
                    RenderSystem.bindTexture(abstractTexture.getId());
                    RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
                    RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                    RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
                    RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
                    // 绑定纹理并设置参数

                    RenderSystem.setShaderTexture(0, texture);
                    RenderSystem.enableBlend();
                    RenderSystem.disableCull();
                    blend.apply();
                    RenderSystem.depthMask(false);
                    RenderSystem.setShader(GameRenderer::getRendertypeBeaconBeamShader);
                    return tesselator.begin(mode, VertexFormatFxLib.POSITION_COLOR_TEX);
                }
                @Override
                public String toString() {
                    return "PARTICLE_" + mode.name() + "_" + blend.name() + "_REPEAT";
                }
            });
        }
}
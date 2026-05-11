package com.fanxing.lib.client.particle.rendertypes;

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
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.util.function.Function;

public interface ParticleRenderTypes {

    // ========== 图集版（固定纹理，用于原版兼容）==========
    ParticleRenderType PARTICLE_SHEET_ADDITIVE = new ParticleRenderType() {
        @Override
        public BufferBuilder begin(Tesselator tesselator, @NotNull TextureManager textureManager) {
            RenderSystem.enableBlend();
            Blend.TRANSPARENT_ADDITIVE.apply();
            RenderSystem.depthMask(false);
            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }
        @Override
        public String toString() { return "PARTICLE_SHEET_ADDITIVE"; }
    };

    ParticleRenderType PARTICLE_SHEET_PREMULTIPLIED = new ParticleRenderType() {
        @Override
        public BufferBuilder begin(@NotNull Tesselator tesselator, @NotNull TextureManager textureManager) {
            RenderSystem.enableBlend();
            Blend.PREMULTIPLIED.apply();
            RenderSystem.depthMask(false);
            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }
        @Override
        public String toString() { return "PARTICLE_SHEET_PREMULTIPLIED"; }
    };
    Function<ResourceLocation, ParticleRenderType> TRANSLUCENT_QUADS_REPEAT = createRepeat(VertexFormat.Mode.QUADS, Blend.TRANSLUCENT);
    // ========== QUADS ==========
    Function<ResourceLocation, ParticleRenderType> ADDITIVE_QUADS_CLAMP = createClamp(VertexFormat.Mode.QUADS, Blend.TRANSPARENT_ADDITIVE);
    Function<ResourceLocation, ParticleRenderType> ADDITIVE_QUADS_REPEAT = createRepeat(VertexFormat.Mode.QUADS, Blend.TRANSPARENT_ADDITIVE);
    Function<ResourceLocation, ParticleRenderType> PREMULTIPLIED_QUADS_CLAMP = createClamp(VertexFormat.Mode.QUADS, Blend.PREMULTIPLIED);
    Function<ResourceLocation, ParticleRenderType> PREMULTIPLIED_QUADS_REPEAT = createRepeat(VertexFormat.Mode.QUADS, Blend.PREMULTIPLIED);

    // ========== TRIANGLES ==========
    Function<ResourceLocation, ParticleRenderType> ADDITIVE_TRIANGLES_CLAMP = createClamp(VertexFormat.Mode.TRIANGLES, Blend.TRANSPARENT_ADDITIVE);
    Function<ResourceLocation, ParticleRenderType> ADDITIVE_TRIANGLES_REPEAT = createRepeat(VertexFormat.Mode.TRIANGLES, Blend.TRANSPARENT_ADDITIVE);
    Function<ResourceLocation, ParticleRenderType> PREMULTIPLIED_TRIANGLES_CLAMP = createClamp(VertexFormat.Mode.TRIANGLES, Blend.PREMULTIPLIED);
    Function<ResourceLocation, ParticleRenderType> PREMULTIPLIED_TRIANGLES_REPEAT = createRepeat(VertexFormat.Mode.TRIANGLES, Blend.PREMULTIPLIED);

    // ---------- 通用工厂（根据 Mode 和 Blend 生成，不平铺）----------
    private static Function<ResourceLocation, ParticleRenderType> createClamp(VertexFormat.Mode mode, Blend blend) {
        return Util.memoize(texture -> new ParticleRenderType() {
            @Override
            public BufferBuilder begin(@NotNull Tesselator tesselator, @NotNull TextureManager textureManager) {
                RenderSystem.disableCull();
                RenderSystem.enableBlend();
                blend.apply();
                RenderSystem.depthMask(false);
                RenderSystem.setShader(GameRenderer::getParticleShader);
                RenderSystem.setShaderTexture(0, texture);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                // 默认 CLAMP，不设置 REPEAT
                return tesselator.begin(mode, DefaultVertexFormat.PARTICLE);
            }
            @Override
            public String toString() {
                return "PARTICLE_" + mode.name() + "_" + blend.name() + "_CLAMP";
            }
        });
    }

    // ---------- 通用工厂（根据 Mode 和 Blend 生成，平铺）----------
    private static Function<ResourceLocation, ParticleRenderType> createRepeat(VertexFormat.Mode mode, Blend blend) {
        return Util.memoize(texture -> new ParticleRenderType() {
            @Override
            public BufferBuilder begin(@NotNull Tesselator tesselator, @NotNull TextureManager textureManager) {
                // 获取纹理的 OpenGL ID ，开启线性插值
                AbstractTexture abstractTexture = textureManager.getTexture(texture);
                // 绑定纹理并设置参数
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, abstractTexture.getId());
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
                GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

                RenderSystem.disableCull();
                RenderSystem.enableBlend();
                blend.apply();
                RenderSystem.depthMask(false);
                RenderSystem.setShader(GameRenderer::getParticleShader);
                RenderSystem.setShaderTexture(0, texture);
                return tesselator.begin(mode, DefaultVertexFormat.PARTICLE);
            }
            @Override
            public String toString() {
                return "PARTICLE_" + mode.name() + "_" + blend.name() + "_REPEAT";
            }
        });
    }
}
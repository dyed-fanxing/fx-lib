package com.fanxing.lib.client.particle;

import com.fanxing.lib.client.render.ResourceLocations;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

public class TestParticle extends TextureSheetParticle {
    public TestParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z, 0, 0, 0);
        this.lifetime = 100;
        this.setColor(1.0f, 0.0f, 0.0f); // 红色
        this.setAlpha(1.0f);
    }

    @Override
    public void render(@NotNull VertexConsumer consumer, @NotNull Camera camera, float partialTick) {
        Vec3 camPos = camera.getPosition();

        float dx = (float)(x - camPos.x);
        float dy = (float)(y - camPos.y);
        float dz = (float)(z - camPos.z);
        float halfSize = 0.4f;
        int light = 0x00F000F0; // 全亮

        // 手动添加四个顶点（面向相机，永远正面）
        // 注意顺序：右下、右上、左上、左下（逆时针）
        consumer.addVertex(dx + halfSize, dy - halfSize, dz).setUv(1, 0).setColor(rCol, gCol, bCol, alpha).setLight(light);
        consumer.addVertex(dx + halfSize, dy + halfSize, dz).setUv(1, 1).setColor(rCol, gCol, bCol, alpha).setLight(light);
        consumer.addVertex(dx - halfSize, dy + halfSize, dz).setUv(0, 1).setColor(rCol, gCol, bCol, alpha).setLight(light);
        consumer.addVertex(dx - halfSize, dy - halfSize, dz).setUv(0, 0).setColor(rCol, gCol, bCol, alpha).setLight(light);
    }


    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return SIMPLE_RED;
    }

    public static final ParticleRenderType SIMPLE_RED = new ParticleRenderType() {
        @Override
        public @NotNull BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
            RenderSystem.disableCull();
            RenderSystem.depthMask(false);
            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.setShaderTexture(0, ResourceLocations.WHITE_TEXTURE);
            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

//            RenderSystem.enableBlend();
//            RenderSystem.defaultBlendFunc();
//            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.setShaderTexture(0, ResourceLocations.WHITE_TEXTURE);
            // 不绑定任何纹理，使用顶点颜色
           return tesselator .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }
        @Override
        public String toString() {
            return "simple_red_particle";
        }
    };
}
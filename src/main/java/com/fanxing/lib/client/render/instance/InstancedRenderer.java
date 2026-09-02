package com.fanxing.lib.client.render.instance;

import com.fanxing.lib.client.particle.AbstractParticle;
import com.fanxing.lib.client.render.instance.comp.GpuSorter;
import com.fanxing.lib.client.render.instance.format.InstanceFormat;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.TextureAtlas;
import org.joml.Matrix4f;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.List;

public final class InstancedRenderer {
    private static int renderSsbo;
    private static ByteBuffer mappedBuffer;
    private static int capacityFloats;
    private static InstancedShaderProgram shader;
    private static int vao;

    // Uniform 位置
    private static int uniModelViewMat;
    private static int uniProjMat;
    private static int uniFogShape;
    private static int uniColorModulator;
    private static int uniFogStart;
    private static int uniFogEnd;
    private static int uniFogColor;
    private static int uniSampler0;

    private static boolean initialized;

    private InstancedRenderer() {}

    public static void init(InstancedShaderProgram particleShader, int initialCapacity) {
        shader = particleShader;
        int floatsPerInstance = shader.format.getFloatsPerInstance();
        capacityFloats = initialCapacity * floatsPerInstance;

        // 渲染 SSBO + 持久映射
        renderSsbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, renderSsbo);
        long size = (long) capacityFloats * Float.BYTES;
        GL45.glBufferStorage(GL43.GL_SHADER_STORAGE_BUFFER, size,
                GL45.GL_MAP_WRITE_BIT | GL45.GL_MAP_PERSISTENT_BIT | GL45.GL_MAP_COHERENT_BIT);
        mappedBuffer = GL45.glMapBufferRange(GL43.GL_SHADER_STORAGE_BUFFER, 0, size,
                GL45.GL_MAP_WRITE_BIT | GL45.GL_MAP_PERSISTENT_BIT | GL45.GL_MAP_COHERENT_BIT);
        GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0);

        // 获取 uniform 位置
        uniModelViewMat   = shader.getUniformLocation("ModelViewMat");
        uniProjMat        = shader.getUniformLocation("ProjMat");
        uniFogShape       = shader.getUniformLocation("FogShape");
        uniColorModulator = shader.getUniformLocation("ColorModulator");
        uniFogStart       = shader.getUniformLocation("FogStart");
        uniFogEnd         = shader.getUniformLocation("FogEnd");
        uniFogColor       = shader.getUniformLocation("FogColor");
        uniSampler0       = shader.getUniformLocation("Sampler0");

        // 粒子空 VAO
        vao = GL30.glGenVertexArrays();

        GpuSorter.init();
        initialized = true;
    }

    public static boolean isInitialized() { return initialized; }

    public static void render(Camera camera, List<AbstractParticle> particles,
                              Frustum frustum, boolean hasLayer) {
        if (!initialized || particles.isEmpty()) return;

        // CPU 视锥剔除
        int count = 0;
        for (AbstractParticle p : particles) {
            if (frustum == null || frustum.isVisible(p.getRenderBoundingBox(0))) {
                count++;
            }
        }
        if (count == 0) return;

        // GPU 排序
        int[] sortedIndices;
        if (hasLayer) {
            sortedIndices = GpuSorter.sortWithLayer(particles, camera);
        } else {
            sortedIndices = GpuSorter.sortDistance(particles, camera);
        }

        // 填充渲染 SSBO
        InstanceFormat format = shader.format;
        int floatsPerInstance = format.getFloatsPerInstance();
        ensureCapacity(count * floatsPerInstance);
        mappedBuffer.clear();

        for (int i = 0; i < count; i++) {
            AbstractParticle p = particles.get(sortedIndices[i]);
//            p.writeInstanceData(mappedBuffer, format);
        }

        // 设置渲染状态
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();

        // 绑定 SSBO 并激活着色器
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 0, renderSsbo);
        shader.use();

        // 矩阵
        Matrix4f modelView = new Matrix4f(RenderSystem.getModelViewMatrix());
        Matrix4f proj      = new Matrix4f(RenderSystem.getProjectionMatrix());
        shader.setUniformMatrix4f(uniModelViewMat, modelView);
        shader.setUniformMatrix4f(uniProjMat, proj);

        // 雾效与颜色
        shader.setUniform4f(uniColorModulator, 1, 1, 1, 1);
        shader.setUniform1f(uniFogStart, RenderSystem.getShaderFogStart());
        shader.setUniform1f(uniFogEnd, RenderSystem.getShaderFogEnd());
        float[] fogCol = RenderSystem.getShaderFogColor();
        shader.setUniform4f(uniFogColor, fogCol[0], fogCol[1], fogCol[2], fogCol[3]);
        shader.setUniform1i(uniFogShape, RenderSystem.getShaderFogShape().getIndex());

        // 纹理
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
        shader.setUniform1i(uniSampler0, 0);

        // 绘制
        GL30.glBindVertexArray(vao);
        GL40.glDrawArraysInstanced(GL11.GL_TRIANGLE_STRIP, 0, 4, count);
        GL30.glBindVertexArray(0);

        shader.unuse();
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 0, 0);

        // 恢复状态
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
    }

    private static void ensureCapacity(int requiredFloats) {
        if (requiredFloats <= capacityFloats) return;
        GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, renderSsbo);
        GL15.glUnmapBuffer(GL43.GL_SHADER_STORAGE_BUFFER);
        capacityFloats = Math.max(requiredFloats, capacityFloats * 2);
        long newSize = (long) capacityFloats * Float.BYTES;
        GL45.glBufferStorage(GL43.GL_SHADER_STORAGE_BUFFER, newSize,
                GL45.GL_MAP_WRITE_BIT | GL45.GL_MAP_PERSISTENT_BIT | GL45.GL_MAP_COHERENT_BIT);
        mappedBuffer = GL45.glMapBufferRange(GL43.GL_SHADER_STORAGE_BUFFER, 0, newSize,
                GL45.GL_MAP_WRITE_BIT | GL45.GL_MAP_PERSISTENT_BIT | GL45.GL_MAP_COHERENT_BIT);
        GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0);
    }

    public static void destroy() {
        if (!initialized) return;
        GL15.glUnmapBuffer(GL43.GL_SHADER_STORAGE_BUFFER);
        GL15.glDeleteBuffers(renderSsbo);
        GL30.glDeleteVertexArrays(vao);
        if (shader != null) shader.close();
        GpuSorter.destroy();
        initialized = false;
    }
}
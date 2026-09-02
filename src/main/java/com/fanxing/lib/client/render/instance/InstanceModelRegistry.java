package com.fanxing.lib.client.render.instance;


import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL43;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;


/**
 * 实例化模型注册器，模仿原版 DeferredRegister 的设计：
 * - registerModel() 只记录数据，可在模组构造阶段调用
 * - uploadAll() 创建 SSBO 并上传，必须在 FMLClientSetupEvent 中调用
 * - 外部只需调用这两个方法，bindBuffers 仅供 InstancedRenderer 内部使用
 * @author dyed_fanxing
 * @since 2026/7/7 13:20
 */
public final class InstanceModelRegistry {
    private InstanceModelRegistry() {}

    public record ModelSlice(int vertexStart, int vertexCount, int indexStart, int indexCount) {}

    // 注册时保存的原始数据
    private static final List<float[]> storedVertices = new ArrayList<>();
    private static final List<int[]> storedIndices   = new ArrayList<>();
    private static final List<ModelSlice> slices     = new ArrayList<>();

    // 上传后创建
    private static int vertexSSBO;
    private static int indexSSBO;

    // ========== 第一阶段：仅记录数据 ==========

    /**
     * 注册一个模型，仅记录顶点/索引数据，不执行任何 GPU 操作。
     * 可在模组构造器或任意注册阶段安全调用。
     */
    public static ModelSlice registerModel(float[] vertices, int[] indices) {
        int vertexStart = 0, indexStart = 0;
        if (!slices.isEmpty()) {
            ModelSlice last = slices.getLast();
            vertexStart = last.vertexStart + last.vertexCount * 3;
            indexStart  = last.indexStart  + last.indexCount;
        }
        ModelSlice slice = new ModelSlice(vertexStart, vertices.length / 3, indexStart, indices.length);
        slices.add(slice);
        storedVertices.add(vertices);
        storedIndices.add(indices);
        return slice;
    }

    // ========== 第二阶段：创建 SSBO 并上传（需 OpenGL 上下文） ==========

    /**
     * 完成注册并上传所有模型到 GPU。
     * 必须在 OpenGL 上下文存在时调用（如 FMLClientSetupEvent 中）。
     */
    public static void uploadAll() {
        vertexSSBO = GL15.glGenBuffers();
        indexSSBO  = GL15.glGenBuffers();

        int totalVertices = 0, totalIndices = 0;
        for (ModelSlice s : slices) {
            totalVertices += s.vertexCount;
            totalIndices  += s.indexCount;
        }

        // 合并顶点
        FloatBuffer vb = MemoryUtil.memAllocFloat(totalVertices * 3);
        for (float[] verts : storedVertices) vb.put(verts);
        vb.flip();
        GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, vertexSSBO);
        GL15.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, vb, GL15.GL_STATIC_DRAW);
        MemoryUtil.memFree(vb);

        // 合并索引
        IntBuffer ib = MemoryUtil.memAllocInt(totalIndices);
        for (int[] inds : storedIndices) ib.put(inds);
        ib.flip();
        GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, indexSSBO);
        GL15.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, ib, GL15.GL_STATIC_DRAW);
        MemoryUtil.memFree(ib);

        storedVertices.clear();
        storedIndices.clear();
    }

    // ========== 渲染时绑定（仅 InstancedRenderer 内部使用） ==========

    static void bindBuffers(int vertexBinding, int indexBinding) {
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, vertexBinding, vertexSSBO);
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, indexBinding, indexSSBO);
    }
}
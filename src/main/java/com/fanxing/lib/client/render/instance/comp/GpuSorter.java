package com.fanxing.lib.client.render.instance.comp;

import com.fanxing.lib.client.particle.AbstractParticle;
import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;

import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

/**
 * GPU 粒子排序器，负责纯距离排序和带图层的二次排序。
 * 所有排序均在 Compute Shader 中完成，CPU 零负担。
 */
public final class GpuSorter {
    // 着色器程序
    private static int distSortProgram;
    private static int layerSortProgram;
    private static int extractProgram;
    private static int writebackProgram;

    // SSBO 句柄
    private static int sortKeysSsbo;      // 排序键缓冲区
    private static int particleMetaSsbo;  // 粒子元数据（layerID, orderInLayer）
    private static int layerKeysSsbo;     // 提取的 layer 粒子
    private static int layerCountSsbo;    // 原子计数器

    // 缓存的 uniform 位置
    private static int uniN;
    private static int uniStage;
    private static int uniPass;

    private static boolean initialized;

    private GpuSorter() {}

    public static void init() {
        // 加载编译 Compute Shader
        distSortProgram   = compileComputeShader("sort_distance.comp");
        layerSortProgram  = compileComputeShader("sort_layer.comp");
        extractProgram    = compileComputeShader("extract_layer.comp");
        writebackProgram  = compileComputeShader("writeback_layer.comp");

        // 创建 SSBO
        sortKeysSsbo     = GL15.glGenBuffers();
        particleMetaSsbo = GL15.glGenBuffers();
        layerKeysSsbo    = GL15.glGenBuffers();
        layerCountSsbo   = GL15.glGenBuffers();

        // 初始化原子计数器为 0
        GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, layerCountSsbo);
        GL15.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, 4, GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0);

        // 缓存 uniform 位置
        uniN     = GL20.glGetUniformLocation(distSortProgram, "N");
        uniStage = GL20.glGetUniformLocation(distSortProgram, "stage");
        uniPass  = GL20.glGetUniformLocation(distSortProgram, "pass");

        initialized = true;
    }

    public static boolean isInitialized() { return initialized; }

    /**
     * 纯距离排序，返回排序后的原始粒子索引数组（远→近）。
     */
    public static int[] sortDistance(List<AbstractParticle> particles, Camera camera) {
        final int N = particles.size();
        if (N == 0) return new int[0];

        final Vec3 camPos = camera.getPosition();
        final int pow2 = nextPowerOfTwo(N);

        // 填充排序键：distance, instanceIndex
        FloatBuffer keys = MemoryUtil.memAllocFloat(pow2 * 2);
        for (int i = 0; i < N; i++) {
            AbstractParticle p = particles.get(i);
            double dx = p.worldX - camPos.x;
            double dy = p.worldY - camPos.y;
            double dz = p.worldZ - camPos.z;
            keys.put((float)(dx*dx + dy*dy + dz*dz));
            keys.put(i);
        }
        // 补齐虚拟元素：极小距离，任意索引
        for (int i = N; i < pow2; i++) {
            keys.put(-Float.MAX_VALUE);
            keys.put(0);
        }
        keys.flip();

        uploadData(sortKeysSsbo, keys);
        MemoryUtil.memFree(keys);

        dispatchBitonic(distSortProgram, pow2);

        return readIndices(N);
    }

    /**
     * 带图层特效的二次排序：全局距离排序后，仅对有 layerID 的粒子按 (layerID, orderInLayer) 重排。
     */
    public static int[] sortWithLayer(List<AbstractParticle> particles, Camera camera) {
        final int N = particles.size();
        if (N == 0) return new int[0];

        // 1. 全局距离排序，得到初步索引
        int[] indices = sortDistance(particles, camera);

        // 2. 上传粒子元数据（layerID, orderInLayer）
        FloatBuffer meta = MemoryUtil.memAllocFloat(N * 2);
        for (AbstractParticle p : particles) {
            meta.put(p.getLayerID());
            meta.put(p.getOrderInLayer());
        }
        meta.flip();
        uploadData(particleMetaSsbo, meta);
        MemoryUtil.memFree(meta);

        // 3. 上传距离排序后的索引（覆盖 sortKeysSsbo）
        FloatBuffer sortedKeys = MemoryUtil.memAllocFloat(N * 2);
        for (int i = 0; i < N; i++) {
            sortedKeys.put(0f);          // distance 占位
            sortedKeys.put(indices[i]);  // 原始粒子索引
        }
        sortedKeys.flip();
        uploadData(sortKeysSsbo, sortedKeys);
        MemoryUtil.memFree(sortedKeys);

        // 4. 重置原子计数器
        resetAtomicCounter(layerCountSsbo);

        // 5. 分配 layer 粒子缓冲区（最大 N 个，每个 4 floats）
        GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, layerKeysSsbo);
        GL15.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, (long) N * 16, GL15.GL_STATIC_DRAW);

        // 6. 提取 layer 粒子
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 0, sortKeysSsbo);
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 1, particleMetaSsbo);
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 2, layerKeysSsbo);
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 3, layerCountSsbo);
        GL20.glUseProgram(extractProgram);
        GL20.glUniform1i(GL20.glGetUniformLocation(extractProgram, "totalParticles"), N);
        GL43.glDispatchCompute((N + 255) / 256, 1, 1);
        GL42.glMemoryBarrier(GL43.GL_SHADER_STORAGE_BARRIER_BIT);

        // 7. 读取 layer 粒子数量
        int layerCount = readAtomicCounter(layerCountSsbo);

        // 8. 有 layer 粒子则排序并写回
        if (layerCount > 0) {
            dispatchBitonic(layerSortProgram, layerCount);

            GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 0, layerKeysSsbo);
            GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 1, sortKeysSsbo);
            GL20.glUseProgram(writebackProgram);
            GL20.glUniform1i(GL20.glGetUniformLocation(writebackProgram, "layerCount"), layerCount);
            GL43.glDispatchCompute((layerCount + 255) / 256, 1, 1);
            GL42.glMemoryBarrier(GL43.GL_SHADER_STORAGE_BARRIER_BIT);
        }

        // 9. 解绑所有 SSBO
        unbindAllSsbo();

        return readIndices(N);
    }

    // ==================== 内部工具 ====================

    private static void uploadData(int ssbo, FloatBuffer data) {
        GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, ssbo);
        GL15.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, data, GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0);
    }

    private static int[] readIndices(int N) {
        IntBuffer ib = MemoryUtil.memAllocInt(N);
        GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, sortKeysSsbo);
        // 每个元素第二个 float 是索引，偏移 4 字节
        GL15.glGetBufferSubData(GL43.GL_SHADER_STORAGE_BUFFER, 4, ib);
        int[] result = new int[N];
        for (int i = 0; i < N; i++) {
            result[i] = ib.get(i);
        }
        MemoryUtil.memFree(ib);
        GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0);
        return result;
    }

    private static void dispatchBitonic(int program, int pow2) {
        GL20.glUseProgram(program);
        GL20.glUniform1i(uniN, pow2);
        for (int s = 0; (1 << s) < pow2; s++) {
            GL20.glUniform1i(uniStage, s);
            for (int p = 0; p <= s; p++) {
                GL20.glUniform1i(uniPass, p);
                GL43.glDispatchCompute(pow2 / 256, 1, 1);
                GL42.glMemoryBarrier(GL43.GL_SHADER_STORAGE_BARRIER_BIT);
            }
        }
        GL20.glUseProgram(0);
    }

    private static void resetAtomicCounter(int ssbo) {
        GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, ssbo);
        GL15.glBufferSubData(GL43.GL_SHADER_STORAGE_BUFFER, 0, new int[]{0});
        GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0);
    }

    private static int readAtomicCounter(int ssbo) {
        GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, ssbo);
        int[] val = new int[1];
        GL15.glGetBufferSubData(GL43.GL_SHADER_STORAGE_BUFFER, 0, val);
        GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0);
        return val[0];
    }

    private static void unbindAllSsbo() {
        for (int i = 0; i < 4; i++) {
            GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, i, 0);
        }
    }

    private static int nextPowerOfTwo(int n) {
        int pow = Integer.highestOneBit(n);
        return pow < n ? pow << 1 : pow;
    }

    // ==================== 资源加载与编译 ====================

    private static int compileComputeShader(String name) {
        String source = readShaderSource(name);
        int shader = GL20.glCreateShader(GL43.GL_COMPUTE_SHADER);
        GL20.glShaderSource(shader, source);
        GL20.glCompileShader(shader);
        if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            String log = GL20.glGetShaderInfoLog(shader);
            GL20.glDeleteShader(shader);
            throw new RuntimeException("Failed to compile compute shader [" + name + "]:\n" + log);
        }
        int program = GL20.glCreateProgram();
        GL20.glAttachShader(program, shader);
        GL20.glLinkProgram(program);
        GL20.glDeleteShader(shader);
        if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            String log = GL20.glGetProgramInfoLog(program);
            throw new RuntimeException("Failed to link compute shader [" + name + "]:\n" + log);
        }
        return program;
    }

    private static String readShaderSource(String name) {
        try (InputStream in = GpuSorter.class.getClassLoader()
                .getResourceAsStream("assets/fx_lib/shaders/comp/" + name)) {
            if (in == null) throw new RuntimeException("Shader file not found: " + name);
            return new String(in.readAllBytes());
        } catch (Exception e) {
            throw new RuntimeException("Failed to read shader: " + name, e);
        }
    }

    // ==================== 清理 ====================

    public static void destroy() {
        if (!initialized) return;
        GL20.glDeleteProgram(distSortProgram);
        GL20.glDeleteProgram(layerSortProgram);
        GL20.glDeleteProgram(extractProgram);
        GL20.glDeleteProgram(writebackProgram);
        GL15.glDeleteBuffers(sortKeysSsbo);
        GL15.glDeleteBuffers(particleMetaSsbo);
        GL15.glDeleteBuffers(layerKeysSsbo);
        GL15.glDeleteBuffers(layerCountSsbo);
        initialized = false;
    }
}
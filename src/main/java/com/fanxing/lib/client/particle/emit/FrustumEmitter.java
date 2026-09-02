package com.fanxing.lib.client.particle.emit;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

/**
 * 圆台发射器 —— 纯局部坐标生成（轴线 Z，原点偏移）。
 * <p>所有方法均严格按粒子数量生成，支持偏移、壳层厚度和抖动。
 * 与 {@link CylinderEmitter}、{@link OrbEmitter} 风格一致。</p>
 *
 * @author dyed_fanxing
 * @since 2026/6/25
 */
public class FrustumEmitter {

    // ==================== 端面方向发射（局部坐标 + 局部方向） ====================

    /**
     * 从圆台小端面发射粒子，方向在锥体内随机扩散（局部空间）。
     * 轴线为 Z 轴，小端位于 Z=0 平面，大端位于 Z=length 平面。
     *
     * @param cx,cy,cz  小端面圆心偏移
     * @param length    锥体长度
     * @param smallR    小端半径
     * @param largeR    大端半径
     * @param count     粒子数量
     * @param jitter    方向抖动幅度，0 = 严格锥体内均匀，1 = 接近随机
     * @param random    随机源
     * @param emitter   位置和方向回调（局部坐标 + 局部方向）
     */
    public static void emit(float cx, float cy, float cz, float length, float smallR, float largeR,
                            int count, float jitter, RandomSource random, PositionEmitter emitter) {
        float halfAngleTan = (largeR - smallR) / length;
        for (int i = 0; i < count; i++) {
            float angle = random.nextFloat() * Mth.TWO_PI;
            float r = smallR * (float) Math.sqrt(random.nextFloat());
            float lx = cx + (float) Math.cos(angle) * r;
            float ly = cy + (float) Math.sin(angle) * r;
            float lz = cz;

            float dirX = 0f, dirY = 0f, dirZ = 1f;
            if (halfAngleTan > 0f && jitter > 0f) {
                float phi = random.nextFloat() * Mth.TWO_PI;
                float maxAngle = (float) Math.atan(halfAngleTan) * jitter;
                float theta = maxAngle * (float) Math.sqrt(random.nextFloat());
                float sinT = (float) Math.sin(theta);
                float cosT = (float) Math.cos(theta);
                dirX = sinT * (float) Math.cos(phi);
                dirY = sinT * (float) Math.sin(phi);
                dirZ = cosT;
            }
            emitter.accept(lx, ly, lz, dirX, dirY, dirZ);
        }
    }

    /** 无抖动便捷重载 */
    public static void emit(float cx, float cy, float cz, float length, float smallR, float largeR,
                            int count, RandomSource random, PositionEmitter emitter) {
        emit(cx, cy, cz, length, smallR, largeR, count, 0f, random, emitter);
    }

    // ==================== 均匀随机体内填充 ====================

    /**
     * 均匀填充圆台体内（体积均匀），支持恒定壳层厚度。
     * 轴线 Z，小端 Z=0，大端 Z=length。
     *
     * @param cx,cy,cz  起点偏移（小端圆心）
     * @param length    锥体长度
     * @param thickness 壳层厚度（>=0），0 表示实心
     * @param r1        起点外半径（小端）
     * @param r2        终点外半径（大端）
     * @param count     粒子数量
     * @param random    随机源
     * @param placer    位置回调 (localX, localY, localZ)
     */
    public static void fillVolume(float cx, float cy, float cz, float length,
                                  float thickness, float r1, float r2, int count,
                                  RandomSource random, PositionPlacer placer) {
        for (int i = 0; i < count; i++) {
            float t = random.nextFloat();
            float curOuter = r1 + (r2 - r1) * t;
            float curInner = Math.max(0, curOuter - thickness);
            float r;
            if (curInner <= 0) {
                r = curOuter * (float) Math.sqrt(random.nextFloat());
            } else {
                float innerSq = curInner * curInner;
                float outerSq = curOuter * curOuter;
                r = (float) Math.sqrt(innerSq + random.nextFloat() * (outerSq - innerSq));
            }
            float angle = random.nextFloat() * Mth.TWO_PI;
            float axial = t * length;
            float lx = cx + (float) Math.cos(angle) * r;
            float ly = cy + (float) Math.sin(angle) * r;
            placer.accept(lx, ly, cz + axial);
        }
    }

    /** 实心圆台便捷重载 */
    public static void fillVolume(float cx, float cy, float cz, float length,
                                  float r1, float r2, int count,
                                  RandomSource random, PositionPlacer placer) {
        fillVolume(cx, cy, cz, length, 0f, r1, r2, count, random, placer);
    }

    // ==================== 高斯随机体内填充 ====================

    /**
     * 高斯分布填充圆台体内，粒子集中在轴心附近和中间高度。
     *
     * @param thickness   壳层厚度（>=0）
     * @param r1          起点外半径
     * @param r2          终点外半径
     * @param sigmaFactor 聚集度，越小越集中
     */
    public static void fillVolumeGaussian(float cx, float cy, float cz, float length,
                                          float thickness, float r1, float r2, int count,
                                          float sigmaFactor, RandomSource random, PositionPlacer placer) {
        float maxR = Math.max(r1, r2);
        float sigmaR = maxR * sigmaFactor;
        float sigmaAxial = length * sigmaFactor;
        for (int i = 0; i < count; i++) {
            float gaussAxial = (float) random.nextGaussian() * sigmaAxial;
            float axial = length / 2.0f + gaussAxial;
            if (axial < 0) axial = 0;
            if (axial > length) axial = length;

            float t = axial / length;
            float curOuter = r1 + (r2 - r1) * t;
            float curInner = Math.max(0, curOuter - thickness);

            float gaussR = (float) random.nextGaussian() * sigmaR;
            float r = Math.abs(gaussR);
            if (r < curInner) r = curInner;
            if (r > curOuter) r = curOuter;

            float angle = random.nextFloat() * Mth.TWO_PI;
            float lx = cx + (float) Math.cos(angle) * r;
            float ly = cy + (float) Math.sin(angle) * r;
            placer.accept(lx, ly, cz + axial);
        }
    }

    /** 实心高斯圆台便捷重载 */
    public static void fillVolumeGaussian(float cx, float cy, float cz, float length,
                                          float r1, float r2, int count,
                                          float sigmaFactor, RandomSource random, PositionPlacer placer) {
        fillVolumeGaussian(cx, cy, cz, length, 0f, r1, r2, count, sigmaFactor, random, placer);
    }

    /** 默认聚集度 sigmaFactor = 0.33 */
    public static void fillVolumeGaussian(float cx, float cy, float cz, float length,
                                          float r1, float r2, int count,
                                          RandomSource random, PositionPlacer placer) {
        fillVolumeGaussian(cx, cy, cz, length, 0f, r1, r2, count, 0.33f, random, placer);
    }

    // ==================== 顺序体内填充（严格按 count，轴向均匀） ====================

    /**
     * 顺序填充圆台体内，严格按 {@code count} 生成粒子，支持抖动。
     * 采用面积权重精确分配，轴向从头到尾均匀覆盖。
     *
     * @param cx,cy,cz  起点偏移（小端圆心）
     * @param length    锥体长度
     * @param thickness 壳层厚度（>=0），0 表示实心
     * @param r1        起点外半径（小端）
     * @param r2        终点外半径（大端）
     * @param count     粒子数量（严格）
     * @param jitter    抖动幅度，0 = 严格网格
     * @param random    随机源
     * @param placer    位置回调 (localX, localY, localZ)
     */
    public static void fillVolumeOrdered(float cx, float cy, float cz, float length,
                                         float thickness, float r1, float r2, int count,
                                         float jitter, RandomSource random, PositionPlacer placer) {
        if (count <= 0 || length <= 0 || r2 <= r1) return;

        // 计算平均侧面积，用于确定径向层数和轴向密度
        float avgR = (r1 + r2) / 2f;
        float slantHeight = (float) Math.sqrt(length * length + (r2 - r1) * (r2 - r1));
        float avgArea = Mth.PI * (r2 + r1) * slantHeight; // 圆台侧面积近似
        float cellSide = (float) Math.sqrt(avgArea / count);
        int radialLayers = Math.max(1, (int) ((r2 - thickness) / cellSide));

        // 计算各层权重（半径）并精确分配粒子
        float[] layerRadii = new float[radialLayers];
        double totalWeight = 0.0;
        for (int i = 0; i < radialLayers; i++) {
            layerRadii[i] = r1 + (r2 - r1) * (i + 0.5f) / radialLayers;
            totalWeight += layerRadii[i];
        }

        int[] layerCounts = new int[radialLayers];
        int assigned = 0;
        for (int i = 0; i < radialLayers; i++) {
            layerCounts[i] = (int) (count * layerRadii[i] / totalWeight);
            assigned += layerCounts[i];
        }
        int remaining = count - assigned;
        for (int i = 0; i < remaining; i++) {
            layerCounts[i % radialLayers]++;
        }

        // 为每一层生成粒子（类似圆柱表面填充，但半径随轴向变化）
        for (int layer = 0; layer < radialLayers; layer++) {
            int lc = layerCounts[layer];
            if (lc <= 0) continue;

            float rStart = layerRadii[layer];
            float rEnd = rStart + (r2 - r1) / radialLayers;
            float layerArea = Mth.PI * (rStart + rEnd) * slantHeight;
            float layerCellSide = (float) Math.sqrt(layerArea / lc);
            int axialCount = Math.max(1, (int) (length / layerCellSide));

            float axialStep = length / axialCount;
            for (int a = 0; a < axialCount; a++) {
                float axial = axialStep * (a + 0.5f);
                float t = axial / length;
                float curR = r1 + (r2 - r1) * t;
                float curInner = Math.max(0, curR - thickness);
                float layerR = curInner + (curR - curInner) * (layer + 0.5f) / radialLayers;
                int ringCount = Math.max(1, lc / axialCount);
                for (int i = 0; i < ringCount; i++) {
                    float angle = i * Mth.TWO_PI / ringCount;
                    if (jitter > 0) {
                        angle += (random.nextFloat() * 2f - 1f) * (Mth.TWO_PI / ringCount * jitter);
                        float jitterR = layerR + (random.nextFloat() * 2f - 1f) * (cellSide * jitter);
                        jitterR = Mth.clamp(jitterR, 0, r2);
                        float jitterAxial = axial + (random.nextFloat() * 2f - 1f) * (axialStep * jitter);
                        jitterAxial = Mth.clamp(jitterAxial, 0f, length);
                        float lx = cx + (float) Math.cos(angle) * jitterR;
                        float ly = cy + (float) Math.sin(angle) * jitterR;
                        placer.accept(lx, ly, cz + jitterAxial);
                    } else {
                        float lx = cx + (float) Math.cos(angle) * layerR;
                        float ly = cy + (float) Math.sin(angle) * layerR;
                        placer.accept(lx, ly, cz + axial);
                    }
                }
            }
        }
    }

    /** 实心顺序圆台便捷重载 */
    public static void fillVolumeOrdered(float cx, float cy, float cz, float length,
                                         float r1, float r2, int count,
                                         float jitter, RandomSource random, PositionPlacer placer) {
        fillVolumeOrdered(cx, cy, cz, length, 0f, r1, r2, count, jitter, random, placer);
    }

    // ==================== 均匀表面填充 ====================

    /**
     * 在圆台侧表面均匀随机生成粒子（面积均匀）。
     *
     * @param cx,cy,cz 起点偏移
     * @param length   锥体长度
     * @param r1      起点处半径
     * @param r2      终点处半径
     * @param count   粒子数量
     * @param random  随机源
     * @param placer  位置回调 (localX, localY, localZ)
     */
    public static void fillSurface(float cx, float cy, float cz, float length,
                                   float r1, float r2, int count,
                                   RandomSource random, PositionPlacer placer) {
        float dr = r2 - r1;
        float avgR = (r1 + r2) / 2f;
        for (int i = 0; i < count; i++) {
            float u = random.nextFloat();
            float t;
            if (Math.abs(dr) < 1e-6f) {
                t = u;
            } else {
                float disc = r1 * r1 + 2f * dr * avgR * u;
                t = (-r1 + (float) Math.sqrt(disc)) / dr;
            }
            float angle = random.nextFloat() * Mth.TWO_PI;
            float curR = r1 + dr * t;
            float lx = cx + curR * (float) Math.cos(angle);
            float lz = cz + curR * (float) Math.sin(angle);
            float ly = cy + t * length;
            placer.accept(lx, ly, lz);
        }
    }

    // ==================== 顺序表面填充（严格按 count） ====================

    /**
     * 在圆台侧表面顺序生成网格粒子，严格按 {@code count} 生成，支持抖动。
     *
     * @param cx,cy,cz 起点偏移
     * @param length   锥体长度
     * @param r1      起点处半径
     * @param r2      终点处半径
     * @param count   粒子数量（严格）
     * @param jitter  抖动幅度，0 = 严格网格
     * @param random  随机源
     * @param placer  位置回调 (localX, localY, localZ)
     */
    public static void fillSurfaceOrdered(float cx, float cy, float cz, float length,
                                          float r1, float r2, int count, float jitter,
                                          RandomSource random, PositionPlacer placer) {
        if (count <= 0 || length <= 0) return;

        float slantHeight = (float) Math.sqrt(length * length + (r2 - r1) * (r2 - r1));
        float avgR = (r1 + r2) / 2f;
        float area = Mth.PI * (r1 + r2) * slantHeight;
        float cellSide = (float) Math.sqrt(area / count);
        int axialCount = Math.max(1, (int) (length / cellSide));
        float axialStep = length / axialCount;
        int basePerSegment = count / axialCount;
        int remainder = count % axialCount;

        for (int a = 0; a < axialCount; a++) {
            float axial = axialStep * (a + 0.5f);
            float t = axial / length;
            float curR = r1 + (r2 - r1) * t;
            int ringCount = basePerSegment + (a < remainder ? 1 : 0);
            for (int i = 0; i < ringCount; i++) {
                float angle = i * Mth.TWO_PI / ringCount;
                if (jitter > 0) {
                    angle += (random.nextFloat() * 2f - 1f) * (Mth.TWO_PI / ringCount * jitter);
                    float jitterR = curR + (random.nextFloat() * 2f - 1f) * (cellSide * jitter);
                    jitterR = Mth.clamp(jitterR, Math.min(r1,r2), Math.max(r1,r2));
                    float jitterAxial = axial + (random.nextFloat() * 2f - 1f) * (axialStep * jitter);
                    jitterAxial = Mth.clamp(jitterAxial, 0f, length);
                    float lx = cx + (float) Math.cos(angle) * jitterR;
                    float lz = cz + (float) Math.sin(angle) * jitterR;
                    placer.accept(lx, cy + jitterAxial, lz);
                } else {
                    float lx = cx + (float) Math.cos(angle) * curR;
                    float lz = cz + (float) Math.sin(angle) * curR;
                    placer.accept(lx, cy + axial, lz);
                }
            }
        }
    }

    /** 无抖动便捷重载 */
    public static void fillSurfaceOrdered(float cx, float cy, float cz, float length,
                                          float r1, float r2, int count,
                                          RandomSource random, PositionPlacer placer) {
        fillSurfaceOrdered(cx, cy, cz, length, r1, r2, count, 0f, random, placer);
    }
}
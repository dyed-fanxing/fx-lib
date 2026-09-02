package com.fanxing.lib.client.particle.emit;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

/**
 * 圆环/圆盘发射器 —— 纯局部坐标生成（Z=0 平面，原点偏移）。
 * <p>
 * 提供：
 * - 有序圆环：emitRadial / fillRing（参数 segments 表示角度分段数）
 * - 随机圆盘填充：fillDisc / fillDiscGaussian（参数 count 表示粒子数）
 * - 随机径向发射：emitRadialRandom / emitRadialGaussian（参数 count 表示粒子数）
 * </p>
 *
 * @author dyed_fanxing
 * @since 2026/6/25
 */
public class CircleRingEmitter {

    // ==================== 有序圆环（网格驱动，用 segments） ====================

    /**
     * 在圆环上发射粒子，方向径向向外（Z=0），角度均匀等距。
     *
     * @param cx,cy,cz  圆心偏移，平面 Z=cz
     * @param radius    圆环半径
     * @param segments  角度分段数（≥1），粒子数量 = segments
     * @param jitter    角度抖动幅度（0~1），0=完全等距，1=最大偏移半个步长
     * @param random    随机源
     * @param emitter   位置+方向回调
     */
    public static void emitRadial(float cx, float cy, float cz, float radius,
                                  int segments, float jitter,
                                  RandomSource random, PositionEmitter emitter) {
        if (segments <= 0 || radius <= 0) return;
        float angleStep = Mth.TWO_PI / segments;
        for (int i = 0; i < segments; i++) {
            float angle = i * angleStep;
            if (jitter > 0) {
                angle += (random.nextFloat() * 2f - 1f) * angleStep * jitter;
            }
            float cosA = Mth.cos(angle);
            float sinA = Mth.sin(angle);
            emitter.accept(cx + cosA * radius, cy + sinA * radius, cz, cosA, sinA, 0f);
        }
    }

    public static void emitRadial(float cx, float cy, float cz, float radius,
                                  int segments, RandomSource random, PositionEmitter emitter) {
        emitRadial(cx, cy, cz, radius, segments, 0f, random, emitter);
    }

    /**
     * 在圆环上填充粒子（无方向），角度均匀等距。
     *
     * @param segments  角度分段数（≥1），粒子数量 = segments
     */
    public static void fillRing(float cx, float cy, float cz, float radius,
                                int segments, float jitter,
                                RandomSource random, PositionPlacer placer) {
        if (segments <= 0 || radius <= 0) return;
        float angleStep = Mth.TWO_PI / segments;
        for (int i = 0; i < segments; i++) {
            float angle = i * angleStep;
            if (jitter > 0) {
                angle += (random.nextFloat() * 2f - 1f) * angleStep * jitter;
            }
            float cosA = Mth.cos(angle);
            float sinA = Mth.sin(angle);
            placer.accept(cx + cosA * radius, cy + sinA * radius, cz);
        }
    }

    public static void fillRing(float cx, float cy, float cz, float radius,
                                int segments, RandomSource random, PositionPlacer placer) {
        fillRing(cx, cy, cz, radius, segments, 0f, random, placer);
    }

    // ==================== 随机圆盘填充（数量驱动，用 count） ====================

    /**
     * 均匀随机填充圆盘/环带（无方向）。
     * 半径使用 sqrt 采样保证面积均匀，角度均匀随机。
     *
     * @param count  粒子数量（严格）
     */
    public static void fillDisc(float cx, float cy, float cz,
                                float innerR, float outerR,
                                int count, RandomSource random,
                                PositionPlacer placer) {
        if (count <= 0 || outerR <= innerR) return;
        float innerSq = innerR * innerR;
        float outerSq = outerR * outerR;
        for (int i = 0; i < count; i++) {
            float r = (float) Math.sqrt(innerSq + random.nextFloat() * (outerSq - innerSq));
            float theta = random.nextFloat() * Mth.TWO_PI;
            float x = cx + r * Mth.cos(theta);
            float y = cy + r * Mth.sin(theta);
            placer.accept(x, y, cz);
        }
    }

    /**
     * 高斯随机填充圆盘/环带（无方向）。
     * 半径服从高斯分布（截断至 [innerR, outerR]），角度均匀随机。
     *
     * @param sigmaFactor 标准差系数（相对于 outerR），推荐 0.33~0.5
     */
    public static void fillDiscGaussian(float cx, float cy, float cz,
                                        float innerR, float outerR,
                                        int count, float sigmaFactor,
                                        RandomSource random,
                                        PositionPlacer placer) {
        if (count <= 0 || outerR <= innerR) return;
        float sigma = outerR * sigmaFactor;
        for (int i = 0; i < count; i++) {
            float r = Math.abs((float) random.nextGaussian() * sigma);
            r = Mth.clamp(r, innerR, outerR);
            float theta = random.nextFloat() * Mth.TWO_PI;
            float x = cx + r * Mth.cos(theta);
            float y = cy + r * Mth.sin(theta);
            placer.accept(x, y, cz);
        }
    }

    public static void fillDiscGaussian(float cx, float cy, float cz,
                                        float innerR, float outerR,
                                        int count, RandomSource random,
                                        PositionPlacer placer) {
        fillDiscGaussian(cx, cy, cz, innerR, outerR, count, 0.33f, random, placer);
    }

    // ==================== 随机径向发射（数量驱动，用 count） ====================

    /**
     * 均匀随机径向发射（方向径向向外），粒子分布在圆盘/环带上。
     */
    public static void emitRadialRandom(float cx, float cy, float cz,
                                        float innerR, float outerR,
                                        int count, RandomSource random,
                                        PositionEmitter emitter) {
        if (count <= 0 || outerR <= innerR) return;
        float innerSq = innerR * innerR;
        float outerSq = outerR * outerR;
        for (int i = 0; i < count; i++) {
            float r = (float) Math.sqrt(innerSq + random.nextFloat() * (outerSq - innerSq));
            float theta = random.nextFloat() * Mth.TWO_PI;
            float cosA = Mth.cos(theta);
            float sinA = Mth.sin(theta);
            float x = cx + r * cosA;
            float y = cy + r * sinA;
            emitter.accept(x, y, cz, cosA, sinA, 0f);
        }
    }

    /**
     * 高斯随机径向发射（方向径向向外），粒子分布在圆盘/环带上。
     */
    public static void emitRadialGaussian(float cx, float cy, float cz,
                                          float innerR, float outerR,
                                          int count, float sigmaFactor,
                                          RandomSource random,
                                          PositionEmitter emitter) {
        if (count <= 0 || outerR <= innerR) return;
        float sigma = outerR * sigmaFactor;
        for (int i = 0; i < count; i++) {
            float r = Math.abs((float) random.nextGaussian() * sigma);
            r = Mth.clamp(r, innerR, outerR);
            float theta = random.nextFloat() * Mth.TWO_PI;
            float cosA = Mth.cos(theta);
            float sinA = Mth.sin(theta);
            float x = cx + r * cosA;
            float y = cy + r * sinA;
            emitter.accept(x, y, cz, cosA, sinA, 0f);
        }
    }

    public static void emitRadialGaussian(float cx, float cy, float cz,
                                          float innerR, float outerR,
                                          int count, RandomSource random,
                                          PositionEmitter emitter) {
        emitRadialGaussian(cx, cy, cz, innerR, outerR, count, 0.33f, random, emitter);
    }

    // ==================== 端面对齐别名（与 CylinderEmitter 兼容） ====================

    public static void emitDisc(float cx, float cy, float cz, float radius,
                                int segments, float jitter,
                                RandomSource random, PositionEmitter emitter) {
        emitRadial(cx, cy, cz, radius, segments, jitter, random, emitter);
    }

    public static void emitDisc(float cx, float cy, float cz, float radius,
                                int segments, RandomSource random, PositionEmitter emitter) {
        emitRadial(cx, cy, cz, radius, segments, 0f, random, emitter);
    }

    public static void fillDisc(float cx, float cy, float cz, float radius,
                                int segments, float jitter,
                                RandomSource random, PositionPlacer placer) {
        fillRing(cx, cy, cz, radius, segments, jitter, random, placer);
    }

    public static void fillDisc(float cx, float cy, float cz, float radius,
                                int segments, RandomSource random, PositionPlacer placer) {
        fillRing(cx, cy, cz, radius, segments, 0f, random, placer);
    }
}
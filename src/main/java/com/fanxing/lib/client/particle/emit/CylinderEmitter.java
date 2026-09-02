package com.fanxing.lib.client.particle.emit;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

/**
 * 圆柱发射器 —— 纯局部坐标生成（原点偏移 + 单位空间）。
 * <p>
 * 提供以下功能：
 * - 端面发射：emitDisc / emitDiscGaussian（随机均匀）
 * - 体积填充：fillVolume / fillVolumeGaussian（纯随机，用于爆炸/烟雾）
 * - 表面填充：fillSurface（纯随机）、fillSurfaceOrdered（有序+抖动）
 * </p>
 *
 * @author dyed_fanxing
 * @since 2026/6/24
 */
public class CylinderEmitter {

    // ==================== 端面发射（均匀，无方向，数量准确） ====================

    public static void emitDisc(float cx, float cy, float cz, float innerR, float outerR, int count,
                                RandomSource random, PositionPlacer placer) {
        for (int i = 0; i < count; i++) {
            float angle = random.nextFloat() * Mth.TWO_PI;
            float r;
            if (innerR <= 0) {
                r = outerR * (float) Math.sqrt(random.nextFloat());
            } else {
                float innerSq = innerR * innerR;
                float outerSq = outerR * outerR;
                r = (float) Math.sqrt(innerSq + random.nextFloat() * (outerSq - innerSq));
            }
            float localX = cx + (float) Math.cos(angle) * r;
            float localY = cy + (float) Math.sin(angle) * r;
            placer.accept(localX, localY, cz);
        }
    }

    public static void emitDisc(Vec3 center, float innerR, float outerR, int count,
                                RandomSource random, PositionPlacer placer) {
        emitDisc((float) center.x, (float) center.y, (float) center.z, innerR, outerR, count, random, placer);
    }

    // ==================== 端面发射（高斯，无方向，数量准确） ====================

    public static void emitDiscGaussian(float cx, float cy, float cz, float innerR, float outerR, int count,
                                        float sigmaFactor, RandomSource random, PositionPlacer placer) {
        float sigma = outerR * sigmaFactor;
        for (int i = 0; i < count; i++) {
            float gaussR = (float) random.nextGaussian() * sigma;
            float r = Math.abs(gaussR);
            if (r < innerR) r = innerR;
            if (r > outerR) r = outerR;
            float angle = random.nextFloat() * Mth.TWO_PI;
            float localX = cx + (float) Math.cos(angle) * r;
            float localY = cy + (float) Math.sin(angle) * r;
            placer.accept(localX, localY, cz);
        }
    }

    public static void emitDiscGaussian(float cx, float cy, float cz, float innerR, float outerR, int count,
                                        RandomSource random, PositionPlacer placer) {
        emitDiscGaussian(cx, cy, cz, innerR, outerR, count, 0.33f, random, placer);
    }

    public static void emitDiscGaussian(Vec3 center, float innerR, float outerR, int count,
                                        float sigmaFactor, RandomSource random, PositionPlacer placer) {
        emitDiscGaussian((float) center.x, (float) center.y, (float) center.z, innerR, outerR, count, sigmaFactor, random, placer);
    }

    // ==================== 体积填充（纯随机，用于爆炸/烟雾） ====================

    public static void fillVolume(float cx, float cy, float cz, float length,
                                  float innerR, float outerR, int count,
                                  RandomSource random, PositionPlacer placer) {
        for (int i = 0; i < count; i++) {
            float r;
            if (innerR <= 0) {
                r = outerR * (float) Math.sqrt(random.nextFloat());
            } else {
                float innerSq = innerR * innerR;
                float outerSq = outerR * outerR;
                r = (float) Math.sqrt(innerSq + random.nextFloat() * (outerSq - innerSq));
            }
            float angle = random.nextFloat() * Mth.TWO_PI;
            float localX = cx + (float) Math.cos(angle) * r;
            float localY = cy + (float) Math.sin(angle) * r;
            float localZ = cz + random.nextFloat() * length;
            placer.accept(localX, localY, localZ);
        }
    }

    public static void fillVolume(Vec3 center, float length, float innerR, float outerR, int count,
                                  RandomSource random, PositionPlacer placer) {
        fillVolume((float) center.x, (float) center.y, (float) center.z, length, innerR, outerR, count, random, placer);
    }

    public static void fillVolumeGaussian(float cx, float cy, float cz, float length,
                                          float innerR, float outerR, int count,
                                          float sigmaFactor, RandomSource random, PositionPlacer placer) {
        float sigma = outerR * sigmaFactor;
        float sigmaAxial = length * sigmaFactor;
        for (int i = 0; i < count; i++) {
            float gaussR = (float) random.nextGaussian() * sigma;
            float r = Math.abs(gaussR);
            if (r < innerR) r = innerR;
            if (r > outerR) r = outerR;

            float gaussAxial = (float) random.nextGaussian() * sigmaAxial;
            float axial = length / 2.0f + gaussAxial;
            if (axial < 0) axial = 0;
            if (axial > length) axial = length;

            float angle = random.nextFloat() * Mth.TWO_PI;
            float localX = cx + (float) Math.cos(angle) * r;
            float localY = cy + (float) Math.sin(angle) * r;
            placer.accept(localX, localY, cz + axial);
        }
    }

    public static void fillVolumeGaussian(float cx, float cy, float cz, float length,
                                          float innerR, float outerR, int count,
                                          RandomSource random, PositionPlacer placer) {
        fillVolumeGaussian(cx, cy, cz, length, innerR, outerR, count, 0.33f, random, placer);
    }

    public static void fillVolumeGaussian(Vec3 center, float length, float innerR, float outerR, int count,
                                          float sigmaFactor, RandomSource random, PositionPlacer placer) {
        fillVolumeGaussian((float) center.x, (float) center.y, (float) center.z, length, innerR, outerR, count, sigmaFactor, random, placer);
    }

    // ==================== 表面填充 ====================

    /**
     * 纯随机表面填充 —— 角度和轴向均随机，适用于自然弥散。
     */
    public static void fillSurface(float cx, float cy, float cz, float length, float radius, int count,
                                   RandomSource random, PositionPlacer placer) {
        for (int i = 0; i < count; i++) {
            float angle = random.nextFloat() * Mth.TWO_PI;
            float localX = cx + (float) Math.cos(angle) * radius;
            float localY = cy + (float) Math.sin(angle) * radius;
            float localZ = cz + random.nextFloat() * length;
            placer.accept(localX, localY, localZ);
        }
    }

    public static void fillSurface(Vec3 center, float length, float radius, int count,
                                   RandomSource random, PositionPlacer placer) {
        fillSurface((float) center.x, (float) center.y, (float) center.z, length, radius, count, random, placer);
    }

    /**
     * 有序表面填充 —— 轴向等距分段，圆周位置独立随机，支持轴向抖动。
     * <p>
     * 粒子在圆柱表面呈直筒状随机分布，从侧面看光束是直的。
     * 圆周位置（绕中轴线的角度）使用均匀随机，不随轴向变化。
     * jitter 只控制轴向偏移，不扰动圆周位置。
     * </p>
     */
    public static void fillSurfaceOrdered(float cx, float cy, float cz, float length, float radius, int count,
                                          float jitter, RandomSource random,
                                          PositionPlacer placer) {
        if (count <= 0 || length <= 0 || radius <= 0) return;
        float spacing = length / count;
        float halfSpacing = spacing * 0.5f;
        for (int i = 0; i < count; i++) {
            // ----- 轴向：等距分段 -----
            float finalZ = (i + 0.5f) * spacing;
            if (jitter > 0) {
                finalZ += (random.nextFloat() * 2f - 1f) * halfSpacing * jitter;
                finalZ = Mth.clamp(finalZ, 0f, length);
            }
            // ----- 圆周位置：均匀随机（绕中轴线的角度） -----
            float theta = random.nextFloat() * Mth.TWO_PI;
            float x = (float) Math.cos(theta) * radius;
            float y = (float) Math.sin(theta) * radius;
            placer.accept(cx + x, cy + y, cz + finalZ);
        }
    }

    public static void fillSurfaceOrdered(float length, float radius, int count,
                                          float jitter, RandomSource random,
                                          PositionPlacer placer) {
        fillSurfaceOrdered(0, 0, 0, length, radius, count, jitter, random, placer);
    }

    public static void fillSurfaceOrdered(float cx, float cy, float cz, float length, float radius, int count,
                                          RandomSource random, PositionPlacer placer) {
        fillSurfaceOrdered(cx, cy, cz, length, radius, count, 0f, random, placer);
    }

    public static void fillSurfaceOrdered(float length, float radius, int count,
                                          RandomSource random, PositionPlacer placer) {
        fillSurfaceOrdered(0, 0, 0, length, radius, count, 0f, random, placer);
    }
}
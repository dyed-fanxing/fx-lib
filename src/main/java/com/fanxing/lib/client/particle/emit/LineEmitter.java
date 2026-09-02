package com.fanxing.lib.client.particle.emit;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

/**
 * 线段发射器 —— 在局部 Z 轴上生成粒子位置，支持均匀随机和顺序两种分布。
 * <p>所有方法均基于粒子数量生成，支持局部原点偏移，外部通过刚性变换转换到世界空间。</p>
 *
 * @author dyed_fanxing
 * @since 2026/6/25 20:07
 */
public class LineEmitter {

    // ==================== 均匀随机填充 ====================

    /**
     * 在局部 Z 轴上均匀随机放置粒子，轴向范围 [0, length]。
     *
     * @param cx,cy,cz  局部偏移（通常为 0）
     * @param length    线段长度（沿 Z 轴）
     * @param count     粒子数量
     * @param random    随机源
     * @param placer    位置回调 (localX, localY, localZ)
     */
    public static void fillRandom(float cx, float cy, float cz, float length, int count,
                                  RandomSource random, PositionPlacer placer) {
        for (int i = 0; i < count; i++) {
            float z = cz + random.nextFloat() * length;
            placer.accept(cx, cy, z);
        }
    }

    /** 无偏移便捷重载 */
    public static void fillRandom(float length, int count,
                                  RandomSource random, PositionPlacer placer) {
        fillRandom(0f, 0f, 0f, length, count, random, placer);
    }

    // ==================== 顺序填充（基于粒子数量） ====================

    /**
     * 沿局部 Z 轴等距放置粒子，严格按 {@code count} 生成，支持轴向随机抖动。
     *
     * @param cx,cy,cz  局部偏移（通常为 0）
     * @param length    线段长度（沿 Z 轴）
     * @param count     粒子数量
     * @param jitter    轴向随机抖动振幅（格），0 表示无随机
     * @param random    随机源
     * @param placer    位置回调 (localX, localY, localZ)
     */
    public static void fillOrdered(float cx, float cy, float cz, float length, int count, float jitter,
                                   RandomSource random, PositionPlacer placer) {
        if (count <= 0) return;
        float step = length / count;
        for (int i = 0; i < count; i++) {
            float z = cz + step * (i + 0.5f); // 粒子放在每个小段的中点
            if (jitter > 0) z += (random.nextFloat() * 2f - 1f) * jitter;
            z = Mth.clamp(z - cz, 0f, length) + cz; // 钳位后再加回偏移
            placer.accept(cx, cy, z);
        }
    }

    /** 无偏移便捷重载 */
    public static void fillOrdered(float length, int count, float jitter,
                                   RandomSource random, PositionPlacer placer) {
        fillOrdered(0f, 0f, 0f, length, count, jitter, random, placer);
    }

    /** 无偏移、无抖动便捷重载 */
    public static void fillOrdered(float length, int count,
                                   RandomSource random, PositionPlacer placer) {
        fillOrdered(0f, 0f, 0f, length, count, 0f, random, placer);
    }
}
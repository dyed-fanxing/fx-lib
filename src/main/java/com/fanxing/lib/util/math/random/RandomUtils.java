package com.fanxing.lib.util.math.random;


import net.minecraft.util.RandomSource;

/**
 * @author dyed_fanxing
 * @date 2026/5/9 16:52
 * 粒子属性随机化工具类。
 * 提供常用随机值生成方法，方便在创建粒子时生成随机参数（缩放起止、角速度、寿命等）。
 */
public final class RandomUtils {
    private static final RandomSource RANDOM = RandomSource.create();

    /**
     * 返回 [min, max) 范围内的随机浮点数
     */
    public static float range(float min, float max) {
        return min + RANDOM.nextFloat() * (max - min);
    }

    /**
     * 返回以 avg 为中心，振幅为 amp 的随机浮点数，范围 [avg - amp/2, avg + amp/2]
     */
    public static float avgAmp(float avg, float amp) {
        return avg + (RANDOM.nextFloat() - 0.5f) * amp;
    }

    /**
     * 生成一个随机数，其绝对值在 [absAvg - amp/2, absAvg + amp/2] 范围内，正负随机。
     * 例如：absAvg=10, amp=10 → 绝对值范围 [5,15] → 可能输出 -12.3, 8.7, -6.1, 14.2 等。
     * 若需要约束输出为整数，可在外面取整。
     */
    public static float avgAbsAmp(float absAvg, float amp) {
        float half = amp / 2f;
        float minAbs = Math.max(0, absAvg - half);
        float maxAbs = absAvg + half;
        float absVal = minAbs + RANDOM.nextFloat() * (maxAbs - minAbs);
        return RANDOM.nextBoolean() ? absVal : -absVal;
    }





    /**
     * 返回 [min, max] 范围内的随机整数（包含两端）
     */
    public static int range(int min, int max) {
        return min + RANDOM.nextInt(max - min + 1);
    }

    /**
     * 返回以 avg 为中心，振幅为 amp 的随机整数，范围 [avg - amp/2, avg + amp/2]
     */
    public static int avgAmp(int avg, int amp) {
        return avg + RANDOM.nextInt(amp + 1) - amp / 2;
    }



    /**
     * 生成一个随机数，其绝对值在 [absAvg - amp/2, absAvg + amp/2] 范围内，正负随机。
     * 例如：absAvg=10, amp=10 → 绝对值范围 [5,15] → 可能输出 -12.3, 8.7, -6.1, 14.2 等。
     * 若需要约束输出为整数，可在外面取整。
     */
    public static int avgAbsAmp(int absAvg, int amp) {
        int half = amp / 2;
        int minAbs = Math.max(0, absAvg - half);
        int maxAbs = absAvg + half;
        int absVal = minAbs + RANDOM.nextInt(maxAbs - minAbs);
        return RANDOM.nextBoolean() ? absVal : -absVal;
    }


}
package com.fanxing.lib.util.math.ease;

/**
 * 缓动工具类。
 * <p>
 * 每种曲线包含五种变体：
 * <ul>
 *   <li>In：缓入（先慢后快）</li>
 *   <li>Out：缓出（先快后慢）</li>
 *   <li>InOut：缓入缓出（两头慢中间快）</li>
 *   <li>InHoldOut：缓入→保持→缓出（上升段缓入，保持，下降段缓出）</li>
 *   <li>OutHoldIn：缓出→保持→缓入（上升段缓出，保持，下降段缓入）</li>
 * </ul>
 * </p>
 */
public final class EaseUtils {

    // ==================== 工具方法 ====================

    /**
     * 三段式：上升 → 保持 → 下降
     *
     * @param t         当前进度 [0,1]
     * @param riseRatio 上升段占比 (0~1)
     * @param holdRatio 保持段占比 (0~1)，剩余为下降段
     * @param riseEase  上升段缓动函数
     * @param fallEase  下降段缓动函数
     * @return 曲线值 [0,1]
     */
    private static float riseHoldFall(float t,
                                      float riseRatio,
                                      float holdRatio,
                                      java.util.function.Function<Float, Float> riseEase,
                                      java.util.function.Function<Float, Float> fallEase) {
        float fallRatio = 1.0f - riseRatio - holdRatio;
        if (fallRatio < 0) throw new IllegalArgumentException("riseRatio + holdRatio must be <= 1");

        if (t < riseRatio) {
            float p = t / riseRatio;
            return riseEase.apply(p);
        } else if (t < riseRatio + holdRatio) {
            return 1.0f;
        } else {
            float p = (t - riseRatio - holdRatio) / fallRatio;
            return 1.0f - fallEase.apply(p);
        }
    }

    // ==================== Quad（二次方） ====================

    public static float inQuad(float t) { return t * t; }
    public static float outQuad(float t) { return 1 - (1 - t) * (1 - t); }
    public static float inOutQuad(float t) {
        if (t < 0.5f) return 2 * t * t;
        return 1 - (float) Math.pow(-2 * t + 2, 2) / 2;
    }
    public static float inHoldOutQuad(float t, float riseRatio, float holdRatio) {
        return riseHoldFall(t, riseRatio, holdRatio, EaseUtils::inQuad, EaseUtils::outQuad);
    }
    public static float outHoldInQuad(float t, float riseRatio, float holdRatio) {
        return riseHoldFall(t, riseRatio, holdRatio, EaseUtils::outQuad, EaseUtils::inQuad);
    }

    // ==================== Cubic（三次方） ====================

    public static float inCubic(float t) { return t * t * t; }
    public static float outCubic(float t) { return 1 - (float) Math.pow(1 - t, 3); }
    public static float inOutCubic(float t) {
        if (t < 0.5f) return 4 * t * t * t;
        return 1 - (float) Math.pow(-2 * t + 2, 3) / 2;
    }
    public static float inHoldOutCubic(float t, float riseRatio, float holdRatio) {
        return riseHoldFall(t, riseRatio, holdRatio, EaseUtils::inCubic, EaseUtils::outCubic);
    }
    public static float outHoldInCubic(float t, float riseRatio, float holdRatio) {
        return riseHoldFall(t, riseRatio, holdRatio, EaseUtils::outCubic, EaseUtils::inCubic);
    }

    // ==================== Quart（四次方） ====================

    public static float inQuart(float t) { return t * t * t * t; }
    public static float outQuart(float t) { return 1 - (float) Math.pow(1 - t, 4); }
    public static float inOutQuart(float t) {
        if (t < 0.5f) return 8 * t * t * t * t;
        return 1 - (float) Math.pow(-2 * t + 2, 4) / 2;
    }
    public static float inHoldOutQuart(float t, float riseRatio, float holdRatio) {
        return riseHoldFall(t, riseRatio, holdRatio, EaseUtils::inQuart, EaseUtils::outQuart);
    }
    public static float outHoldInQuart(float t, float riseRatio, float holdRatio) {
        return riseHoldFall(t, riseRatio, holdRatio, EaseUtils::outQuart, EaseUtils::inQuart);
    }

    // ==================== Quint（五次方） ====================

    public static float inQuint(float t) { return t * t * t * t * t; }
    public static float outQuint(float t) { return 1 - (float) Math.pow(1 - t, 5); }
    public static float inOutQuint(float t) {
        if (t < 0.5f) return 16 * t * t * t * t * t;
        return 1 - (float) Math.pow(-2 * t + 2, 5) / 2;
    }
    public static float inHoldOutQuint(float t, float riseRatio, float holdRatio) {
        return riseHoldFall(t, riseRatio, holdRatio, EaseUtils::inQuint, EaseUtils::outQuint);
    }
    public static float outHoldInQuint(float t, float riseRatio, float holdRatio) {
        return riseHoldFall(t, riseRatio, holdRatio, EaseUtils::outQuint, EaseUtils::inQuint);
    }

    // ==================== Sine（正弦） ====================

    public static float inSine(float t) { return 1 - (float) Math.cos(t * Math.PI / 2); }
    public static float outSine(float t) { return (float) Math.sin(t * Math.PI / 2); }
    public static float inOutSine(float t) { return (float) (1 - Math.cos(Math.PI * t)) / 2; }
    public static float inHoldOutSine(float t, float riseRatio, float holdRatio) {
        return riseHoldFall(t, riseRatio, holdRatio, EaseUtils::inSine, EaseUtils::outSine);
    }
    public static float outHoldInSine(float t, float riseRatio, float holdRatio) {
        return riseHoldFall(t, riseRatio, holdRatio, EaseUtils::outSine, EaseUtils::inSine);
    }

    // ==================== Expo（指数） ====================

    public static float inExpo(float t) { return (t == 0) ? 0 : (float) Math.pow(2, 10 * (t - 1)); }
    public static float outExpo(float t) { return (t == 1) ? 1 : 1 - (float) Math.pow(2, -10 * t); }
    public static float inOutExpo(float t) {
        if (t == 0) return 0;
        if (t == 1) return 1;
        if (t < 0.5f) return (float) Math.pow(2, 20 * t - 10) / 2;
        return 1 - (float) Math.pow(2, -20 * t + 10) / 2;
    }
    public static float inHoldOutExpo(float t, float riseRatio, float holdRatio) {
        return riseHoldFall(t, riseRatio, holdRatio, EaseUtils::inExpo, EaseUtils::outExpo);
    }
    public static float outHoldInExpo(float t, float riseRatio, float holdRatio) {
        return riseHoldFall(t, riseRatio, holdRatio, EaseUtils::outExpo, EaseUtils::inExpo);
    }

    // ==================== Circ（圆形） ====================

    public static float inCirc(float t) { return 1 - (float) Math.sqrt(1 - t * t); }
    public static float outCirc(float t) { return (float) Math.sqrt(1 - (t - 1) * (t - 1)); }
    public static float inOutCirc(float t) {
        if (t < 0.5f) return (1 - (float) Math.sqrt(1 - 4 * t * t)) / 2;
        return (1 + (float) Math.sqrt(1 - 4 * (t - 1) * (t - 1))) / 2;
    }
    public static float inHoldOutCirc(float t, float riseRatio, float holdRatio) {
        return riseHoldFall(t, riseRatio, holdRatio, EaseUtils::inCirc, EaseUtils::outCirc);
    }
    public static float outHoldInCirc(float t, float riseRatio, float holdRatio) {
        return riseHoldFall(t, riseRatio, holdRatio, EaseUtils::outCirc, EaseUtils::inCirc);
    }

    // ==================== Elastic（弹性） ====================

    public static float inElastic(float t) {
        if (t == 0 || t == 1) return t;
        float a = 1f, p = 0.3f;
        float s = p / (float)(2 * Math.PI) * (float) Math.asin(1 / a);
        return -(a * (float) Math.pow(2, 10 * (t - 1)) * (float) Math.sin((t - 1 - s) * (2 * Math.PI) / p));
    }
    public static float outElastic(float t) {
        if (t == 0 || t == 1) return t;
        float a = 1f, p = 0.3f;
        float s = p / (float)(2 * Math.PI) * (float) Math.asin(1 / a);
        return a * (float) Math.pow(2, -10 * t) * (float) Math.sin((t - s) * (2 * Math.PI) / p) + 1;
    }
    public static float inOutElastic(float t) {
        if (t == 0 || t == 1) return t;
        float a = 1f, p = 0.3f;
        float s = p / (float)(2 * Math.PI) * (float) Math.asin(1 / a);
        if (t < 0.5f) {
            float u = 2 * t;
            return -(a * (float) Math.pow(2, 10 * (u - 1)) * (float) Math.sin((u - 1 - s) * (2 * Math.PI) / p)) / 2;
        } else {
            float u = 2 - 2 * t;
            return a * (float) Math.pow(2, -10 * u) * (float) Math.sin((u - s) * (2 * Math.PI) / p) / 2 + 0.5f;
        }
    }
    public static float inHoldOutElastic(float t, float riseRatio, float holdRatio) {
        return riseHoldFall(t, riseRatio, holdRatio, EaseUtils::inElastic, EaseUtils::outElastic);
    }
    public static float outHoldInElastic(float t, float riseRatio, float holdRatio) {
        return riseHoldFall(t, riseRatio, holdRatio, EaseUtils::outElastic, EaseUtils::inElastic);
    }

    // ==================== Back（回弹） ====================

    public static float inBack(float t) { float s = 1.70158f; return t * t * ((s + 1) * t - s); }
    public static float outBack(float t) { float s = 1.70158f; float u = 1 - t; return 1 - u * u * ((s + 1) * u - s); }
    public static float inOutBack(float t) {
        float s = 1.70158f * 1.525f;
        if (t < 0.5f) {
            float u = 2 * t;
            return u * u * ((s + 1) * u - s) / 2;
        } else {
            float u = 2 - 2 * t;
            return 1 - u * u * ((s + 1) * u - s) / 2;
        }
    }
    public static float inHoldOutBack(float t, float riseRatio, float holdRatio) {
        return riseHoldFall(t, riseRatio, holdRatio, EaseUtils::inBack, EaseUtils::outBack);
    }
    public static float outHoldInBack(float t, float riseRatio, float holdRatio) {
        return riseHoldFall(t, riseRatio, holdRatio, EaseUtils::outBack, EaseUtils::inBack);
    }

    // ==================== Bounce（反弹） ====================

    public static float inBounce(float t) { return 1 - outBounce(1 - t); }
    public static float outBounce(float t) {
        if (t < 1 / 2.75f) return 7.5625f * t * t;
        if (t < 2 / 2.75f) {
            float x = t - 1.5f / 2.75f;
            return 7.5625f * x * x + 0.75f;
        }
        if (t < 2.5 / 2.75f) {
            float x = t - 2.25f / 2.75f;
            return 7.5625f * x * x + 0.9375f;
        }
        float x = t - 2.625f / 2.75f;
        return 7.5625f * x * x + 0.984375f;
    }
    public static float inOutBounce(float t) {
        if (t < 0.5f) return inBounce(t * 2) / 2;
        return outBounce(t * 2 - 1) / 2 + 0.5f;
    }
    public static float inHoldOutBounce(float t, float riseRatio, float holdRatio) {
        return riseHoldFall(t, riseRatio, holdRatio, EaseUtils::inBounce, EaseUtils::outBounce);
    }
    public static float outHoldInBounce(float t, float riseRatio, float holdRatio) {
        return riseHoldFall(t, riseRatio, holdRatio, EaseUtils::outBounce, EaseUtils::inBounce);
    }

    // ==================== 额外工具 ====================

    /**
     * 线性插值（无缓动）
     */
    public static float linear(float t) { return t; }

    /**
     * 线性-保持-线性（无缓动的三段式）
     */
    public static float linearHoldLinear(float t, float riseRatio, float holdRatio) {
        return riseHoldFall(t, riseRatio, holdRatio, EaseUtils::linear, EaseUtils::linear);
    }














    // ==================== 波形函数 ====================

    /**
     * 三角波：在 0→1 内完成 waveCount 个波
     * @param t          当前进度 0~1
     * @param waveCount  在 0→1 内完成的波形次数
     */
    public static float triangle(float t, float waveCount) {
        float phase = (t * waveCount) % 1.0f;
        return phase < 0.5f ? phase * 2 : 2 - phase * 2;
    }

    /**
     * 方波：在 0→1 内完成 waveCount 个波
     */
    public static float square(float t, float waveCount, float duty) {
        float phase = (t * waveCount) % 1.0f;
        return phase < duty ? 1 : 0;
    }

    public static float square(float t, float waveCount) {
        return square(t, waveCount, 0.5f);
    }

    /**
     * 锯齿波：在 0→1 内完成 waveCount 个波
     */
    public static float sawtooth(float t, float waveCount) {
        return (t * waveCount) % 1.0f;
    }

    /**
     * 波形 + 振幅控制（所有输入都是 progress 0~1）
     * @param t          当前进度 0~1
     * @param waveCount  在 0~1 内完成多少个周期
     * @param amplitude  振幅函数，输入 t，输出振幅 0~1
     * @param wave       波形函数，输入 phase 0~1，输出波形值
     * @return 波形值 × 振幅
     */
    public static float wave(float t, float waveCount,
                             java.util.function.Function<Float, Float> amplitude,
                             java.util.function.Function<Float, Float> wave) {
        float phase = (t * waveCount) % 1.0f;
        return wave.apply(phase) * amplitude.apply(t);
    }
    // ==================== 波形函数（接收 phase 0~1，输出波形值） ====================

    /**
     * 三角波：phase 0→1 时，值 0→1→0
     */
    public static float triangle(float phase) {
        return phase < 0.5f ? phase * 2 : 2 - phase * 2;
    }

    /**
     * 方波：phase 0→1 时，在 0 和 1 之间切换
     */
    public static float square(float phase) {
        return phase < 0.5f ? 1 : 0;
    }

    /**
     * 锯齿波：phase 0→1 时，值 0→1 线性上升
     */
    public static float sawtooth(float phase) {
        return phase;
    }
}
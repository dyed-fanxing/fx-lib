package com.fanxing.lib.util.math.ease;

/**
 * 缓动曲线工具类，提供各种常用的缓动函数（Easing Functions）。
 * <p>
 * 命名规则：形状（In/Out/InOut）在前，次数/类型在后。
 * 例如：<code>inQuad</code> 表示二次方缓入，<code>outCubic</code> 表示三次方缓出，
 * <code>inOutSine</code> 表示正弦缓入缓出。
 * </p>
 * <p>
 * 所有函数的输入 t 范围为 [0,1]，输出范围为 [0,1]。
 * </p>
 */
public final class EaseCurvesUtils {

    // ==================== 幂函数缓动 ====================

    /**
     * 二次方缓入（Quadratic In）。
     * @param t 进度 [0,1]
     * @return 缓动值
     */
    public static float inQuad(float t) { return t * t; }

    /**
     * 二次方缓出（Quadratic Out）。
     * @param t 进度 [0,1]
     * @return 缓动值
     */
    public static float outQuad(float t) { return 1 - (1 - t) * (1 - t); }

    /**
     * 二次方缓入缓出（Quadratic InOut）。
     * @param t 进度 [0,1]
     * @return 缓动值
     */
    public static float inOutQuad(float t) {
        if (t < 0.5f) return 2 * t * t;
        return 1 - (float) Math.pow(-2 * t + 2, 2) / 2;
    }

    /**
     * 三次方缓入（Cubic In）。
     * @param t 进度 [0,1]
     * @return 缓动值
     */
    public static float inCubic(float t) { return t * t * t; }

    /**
     * 三次方缓出（Cubic Out）。
     * @param t 进度 [0,1]
     * @return 缓动值
     */
    public static float outCubic(float t) { return 1 - (float) Math.pow(1 - t, 3); }

    /**
     * 三次方缓入缓出（Cubic InOut）。
     * @param t 进度 [0,1]
     * @return 缓动值
     */
    public static float inOutCubic(float t) {
        if (t < 0.5f) return 4 * t * t * t;
        return 1 - (float) Math.pow(-2 * t + 2, 3) / 2;
    }

    /**
     * 四次方缓入（Quartic In）。
     * @param t 进度 [0,1]
     * @return 缓动值
     */
    public static float inQuart(float t) { return t * t * t * t; }

    /**
     * 四次方缓出（Quartic Out）。
     * @param t 进度 [0,1]
     * @return 缓动值
     */
    public static float outQuart(float t) { return 1 - (float) Math.pow(1 - t, 4); }

    /**
     * 四次方缓入缓出（Quartic InOut）。
     * @param t 进度 [0,1]
     * @return 缓动值
     */
    public static float inOutQuart(float t) {
        if (t < 0.5f) return 8 * t * t * t * t;
        return 1 - (float) Math.pow(-2 * t + 2, 4) / 2;
    }

    /**
     * 五次方缓入（Quintic In）。
     * @param t 进度 [0,1]
     * @return 缓动值
     */
    public static float inQuint(float t) { return t * t * t * t * t; }

    /**
     * 五次方缓出（Quintic Out）。
     * @param t 进度 [0,1]
     * @return 缓动值
     */
    public static float outQuint(float t) { return 1 - (float) Math.pow(1 - t, 5); }

    /**
     * 五次方缓入缓出（Quintic InOut）。
     * @param t 进度 [0,1]
     * @return 缓动值
     */
    public static float inOutQuint(float t) {
        if (t < 0.5f) return 16 * t * t * t * t * t;
        return 1 - (float) Math.pow(-2 * t + 2, 5) / 2;
    }

    // ==================== 正弦缓动 ====================

    /** 正弦缓入。 */
    public static float inSine(float t) { return 1 - (float) Math.cos(t * Math.PI / 2); }
    /** 正弦缓出。 */
    public static float outSine(float t) { return (float) Math.sin(t * Math.PI / 2); }
    /** 正弦缓入缓出。 */
    public static float inOutSine(float t) { return (float) (1 - Math.cos(Math.PI * t)) / 2; }

    // ==================== 指数缓动 ====================

    /** 指数缓入。 */
    public static float inExpo(float t) { return (t == 0) ? 0 : (float) Math.pow(2, 10 * (t - 1)); }
    /** 指数缓出。 */
    public static float outExpo(float t) { return (t == 1) ? 1 : 1 - (float) Math.pow(2, -10 * t); }
    /** 指数缓入缓出。 */
    public static float inOutExpo(float t) {
        if (t == 0) return 0;
        if (t == 1) return 1;
        if (t < 0.5f) return (float) Math.pow(2, 20 * t - 10) / 2;
        return 1 - (float) Math.pow(2, -20 * t + 10) / 2;
    }

    // ==================== 圆形缓动 ====================

    /** 圆形缓入。 */
    public static float inCirc(float t) { return 1 - (float) Math.sqrt(1 - t * t); }
    /** 圆形缓出。 */
    public static float outCirc(float t) { return (float) Math.sqrt(1 - (t - 1) * (t - 1)); }
    /** 圆形缓入缓出。 */
    public static float inOutCirc(float t) {
        if (t < 0.5f) return (1 - (float) Math.sqrt(1 - 4 * t * t)) / 2;
        return (1 + (float) Math.sqrt(1 - 4 * (t - 1) * (t - 1))) / 2;
    }

    // ==================== 弹性缓动 ====================

    /** 弹性缓入（默认振幅1，周期0.3）。 */
    public static float inElastic(float t) {
        if (t == 0 || t == 1) return t;
        float a = 1f, p = 0.3f;
        float s = p / (float)(2 * Math.PI) * (float) Math.asin(1 / a);
        return -(a * (float) Math.pow(2, 10 * (t - 1)) * (float) Math.sin((t - 1 - s) * (2 * Math.PI) / p));
    }

    /** 弹性缓出（默认振幅1，周期0.3）。 */
    public static float outElastic(float t) {
        if (t == 0 || t == 1) return t;
        float a = 1f, p = 0.3f;
        float s = p / (float)(2 * Math.PI) * (float) Math.asin(1 / a);
        return a * (float) Math.pow(2, -10 * t) * (float) Math.sin((t - s) * (2 * Math.PI) / p) + 1;
    }

    /** 弹性缓入缓出（默认振幅1，周期0.3）。 */
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

    // ==================== 回弹缓动 ====================

    /** 回弹缓入（默认过冲量1.70158）。 */
    public static float inBack(float t) {
        float s = 1.70158f;
        return t * t * ((s + 1) * t - s);
    }

    /** 回弹缓出（默认过冲量1.70158）。 */
    public static float outBack(float t) {
        float s = 1.70158f;
        float u = 1 - t;
        return 1 - u * u * ((s + 1) * u - s);
    }

    /** 回弹缓入缓出（默认过冲量1.70158）。 */
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

    // ==================== 反弹缓动 ====================

    /** 反弹缓入。 */
    public static float inBounce(float t) { return 1 - outBounce(1 - t); }

    /** 反弹缓出。 */
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

    /** 反弹缓入缓出。 */
    public static float inOutBounce(float t) {
        if (t < 0.5f) return inBounce(t * 2) / 2;
        return outBounce(t * 2 - 1) / 2 + 0.5f;
    }

    // ==================== 贝塞尔曲线 ====================

    /**
     * 二次贝塞尔曲线求值。
     * @param t  参数 [0,1]
     * @param p0 起点
     * @param p1 控制点
     * @param p2 终点
     * @return 曲线值
     */
    public static float bezier(float t, float p0, float p1, float p2) {
        float o = 1 - t;
        return o * o * p0 + 2 * o * t * p1 + t * t * p2;
    }

    /**
     * 三次贝塞尔曲线求值。
     * @param t  参数 [0,1]
     * @param p0 起点
     * @param p1 控制点1
     * @param p2 控制点2
     * @param p3 终点
     * @return 曲线值
     */
    public static float bezierCubic(float t, float p0, float p1, float p2, float p3) {
        float o = 1 - t;
        return o * o * o * p0 + 3 * o * o * t * p1 + 3 * o * t * t * p2 + t * t * t * p3;
    }

    // ==================== 线性上升-保持-下降 ====================

    /**
     * 线性上升-保持-下降分段函数。
     * @param t             进度 [0,1]
     * @param holdTimeScale 保持时间比例 (0~1)
     * @return 曲线值
     */
    public static float linearRiseHoldFall(float t, float holdTimeScale) {
        float rise = (1 - holdTimeScale) / 2;
        if (t < rise) return t / rise;
        if (t < rise + holdTimeScale) return 1;
        return 1 - (t - rise - holdTimeScale) / rise;
    }

    // ========== 上升-保持-下降贝塞尔曲线 ==========
    /**
     * 上升-保持-下降分段贝塞尔曲线（可分别为上升和下降段指定缓动参数）。
     *
     * @param t             时间参数 (0~1)
     * @param holdTimeScale 停留时间比例 (0~1)
     * @param riseEase      上升段缓动参数 (0: 缓入，1: 缓出)
     * @param fallEase      下降段缓动参数 (0: 缓出，1: 缓入)
     * @return 曲线值 (0~1)
     */
    public static float riseHoldFallBezier(float t, float holdTimeScale, float riseEase, float fallEase) {
        float riseTime = (1.0f - holdTimeScale) / 2.0f;
        if (t < riseTime) {
            return bezier(t / riseTime, 0, riseEase, 1);
        } else if (t < riseTime + holdTimeScale) {
            return 1.0f;
        } else {
            float t2 = (t - riseTime - holdTimeScale) / riseTime;
            return bezier(t2, 1, fallEase, 0);
        }
    }

    /**
     * 上升-保持-下降分段贝塞尔曲线（上升和下降使用相同的缓动参数）。
     *
     * @param t             时间参数 (0~1)
     * @param holdTimeScale 停留时间比例 (0~1)
     * @param ease          缓动参数 (0: 缓入，1: 缓出)
     * @return 曲线值 (0~1)
     */
    public static float riseHoldFallBezier(float t, float holdTimeScale, float ease) {
        return riseHoldFallBezier(t, holdTimeScale, ease, ease);
    }
}
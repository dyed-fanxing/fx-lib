package com.fanxing.lib.util.math;

/**
 * 缓动曲线工具类，提供各种常用的缓动函数（Easing Functions）。
 *
 * 包含线性、幂函数、贝塞尔曲线、正弦、指数、圆形、弹性、回弹、反弹等类型，
 * 以及上升-保持-下降的分段曲线。所有函数输入 t 范围 [0,1]。
 */
public final class EaseCurvesUtils {

    // ==================== 上升-保持-下降分段贝塞尔曲线 ====================
    /**
     * 上升-保持-下降分段贝塞尔曲线（使用相同的缓入缓出参数）。
     *
     * @param t             时间参数 (0~1)
     * @param holdTimeScale 停留时间比例 (0~1)
     * @param ease          缓动强度 (0 为纯缓入，1 为纯缓出)
     * @return 曲线值 (0~1)
     */
    public static float riseHoldFallBezier(float t, float holdTimeScale, float ease) {
        return riseHoldFallBezier(t, holdTimeScale, ease, ease);
    }

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

    // ==================== 贝塞尔曲线 ====================

    /**
     * 二次贝塞尔曲线求值。
     *
     * @param t  参数 [0,1]
     * @param p0 起点
     * @param p1 控制点
     * @param p2 终点
     * @return 曲线值
     */
    public static float bezier(float t, float p0, float p1, float p2) {
        float oneMinusT = 1 - t;
        return oneMinusT * oneMinusT * p0 + 2 * oneMinusT * t * p1 + t * t * p2;
    }

    /**
     * 三次贝塞尔曲线求值。
     *
     * @param t  参数 [0,1]
     * @param p0 起点
     * @param p1 控制点1
     * @param p2 控制点2
     * @param p3 终点
     * @return 曲线值
     */
    public static float bezierCubic(float t, float p0, float p1, float p2, float p3) {
        float oneMinusT = 1 - t;
        return oneMinusT * oneMinusT * oneMinusT * p0
                + 3 * oneMinusT * oneMinusT * t * p1
                + 3 * oneMinusT * t * t * p2
                + t * t * t * p3;
    }

    /**
     * 四次贝塞尔曲线求值。
     *
     * @param t  参数 [0,1]
     * @param p0 起点
     * @param p1 控制点1
     * @param p2 控制点2
     * @param p3 控制点3
     * @param p4 终点
     * @return 曲线值
     */
    public static float bezierQuartic(float t, float p0, float p1, float p2, float p3, float p4) {
        float oneMinusT = 1 - t;
        return oneMinusT * oneMinusT * oneMinusT * oneMinusT * p0
                + 4 * oneMinusT * oneMinusT * oneMinusT * t * p1
                + 6 * oneMinusT * oneMinusT * t * t * p2
                + 4 * oneMinusT * t * t * t * p3
                + t * t * t * t * p4;
    }


    // ==================== 幂函数缓动 ====================

    /**
     * 幂函数缓入（先慢后快）。
     *
     * @param t     参数 [0,1]
     * @param power 指数，越大前期越平坦
     * @return 曲线值
     */
    public static float easeIn(float t, float power) {
        return (float) Math.pow(t, power);
    }

    /**
     * 幂函数缓出（先快后慢）。
     *
     * @param t     参数 [0,1]
     * @param power 指数，大于0
     * @return 曲线值
     */
    public static float easeOut(float t, float power) {
        return 1 - (float) Math.pow(1 - t, power);
    }

    /**
     * 幂函数缓入缓出（对称）。
     *
     * @param t     参数 [0,1]
     * @param power 指数，大于0
     * @return 曲线值
     */
    public static float easeInOut(float t, float power) {
        if (t < 0.5f) {
            return (float) Math.pow(2 * t, power) / 2;
        } else {
            return 1 - (float) Math.pow(2 - 2 * t, power) / 2;
        }
    }

    // ==================== 正弦缓动 ====================

    /**
     * 正弦缓入。
     *
     * @param t 参数 [0,1]
     * @return 曲线值
     */
    public static float sineIn(float t) {
        return 1 - (float) Math.cos(t * Math.PI / 2);
    }

    /**
     * 正弦缓出。
     *
     * @param t 参数 [0,1]
     * @return 曲线值
     */
    public static float sineOut(float t) {
        return (float) Math.sin(t * Math.PI / 2);
    }

    /**
     * 正弦缓入缓出。
     *
     * @param t 参数 [0,1]
     * @return 曲线值
     */
    public static float sineInOut(float t) {
        return (float) (1 - Math.cos(Math.PI * t)) / 2;
    }

    // ==================== 指数缓动 ====================

    /**
     * 指数缓入。
     *
     * @param t 参数 [0,1]
     * @return 曲线值
     */
    public static float expoIn(float t) {
        return (t == 0) ? 0 : (float) Math.pow(2, 10 * (t - 1));
    }

    /**
     * 指数缓出。
     *
     * @param t 参数 [0,1]
     * @return 曲线值
     */
    public static float expoOut(float t) {
        return (t == 1) ? 1 : 1 - (float) Math.pow(2, -10 * t);
    }

    /**
     * 指数缓入缓出。
     *
     * @param t 参数 [0,1]
     * @return 曲线值
     */
    public static float expoInOut(float t) {
        if (t == 0) return 0;
        if (t == 1) return 1;
        if (t < 0.5f) return (float) Math.pow(2, 20 * t - 10) / 2;
        return 1 - (float) Math.pow(2, -20 * t + 10) / 2;
    }


    // ==================== 弹性缓动 ====================

    /**
     * 弹性缓入。
     *
     * @param t         参数 [0,1]
     * @param amplitude 振幅
     * @param period    周期
     * @return 曲线值
     */
    public static float elasticIn(float t, float amplitude, float period) {
        if (t == 0 || t == 1) return t;
        float s = period / (float)(2 * Math.PI) * (float) Math.asin(1 / amplitude);
        return -(amplitude * (float) Math.pow(2, 10 * (t - 1)) * (float) Math.sin((t - 1 - s) * (2 * Math.PI) / period));
    }

    /**
     * 弹性缓出。
     *
     * @param t         参数 [0,1]
     * @param amplitude 振幅
     * @param period    周期
     * @return 曲线值
     */
    public static float elasticOut(float t, float amplitude, float period) {
        if (t == 0 || t == 1) return t;
        float s = period / (float)(2 * Math.PI) * (float) Math.asin(1 / amplitude);
        return amplitude * (float) Math.pow(2, -10 * t) * (float) Math.sin((t - s) * (2 * Math.PI) / period) + 1;
    }

    /**
     * 弹性缓入缓出。
     *
     * @param t         参数 [0,1]
     * @param amplitude 振幅
     * @param period    周期
     * @return 曲线值
     */
    public static float elasticInOut(float t, float amplitude, float period) {
        if (t == 0 || t == 1) return t;
        if (t < 0.5f) {
            float u = 2 * t;
            float s = period / (float)(2 * Math.PI) * (float) Math.asin(1 / amplitude);
            return -(amplitude * (float) Math.pow(2, 10 * (u - 1)) *
                    (float) Math.sin((u - 1 - s) * (2 * Math.PI) / period)) / 2;
        } else {
            float u = 2 - 2 * t;
            float s = period / (float)(2 * Math.PI) * (float) Math.asin(1 / amplitude);
            return amplitude * (float) Math.pow(2, -10 * u) * (float) Math.sin((u - s) * (2 * Math.PI) / period) / 2 + 0.5f;
        }
    }

    // ==================== 回弹缓动（Back） ====================

    /**
     * 回弹缓入（先反向超出再回到目标）。
     *
     * @param t         参数 [0,1]
     * @param overshoot 超出量，通常为1.70158
     * @return 曲线值
     */
    public static float backIn(float t, float overshoot) {
        return t * t * ((overshoot + 1) * t - overshoot);
    }

    /**
     * 回弹缓出。
     *
     * @param t         参数 [0,1]
     * @param overshoot 超出量
     * @return 曲线值
     */
    public static float backOut(float t, float overshoot) {
        float u = 1 - t;
        return 1 - u * u * ((overshoot + 1) * u - overshoot);
    }

    /**
     * 回弹缓入缓出。
     *
     * @param t         参数 [0,1]
     * @param overshoot 超出量
     * @return 曲线值
     */
    public static float backInOut(float t, float overshoot) {
        float factor = overshoot * 1.525f; // 标准修正因子
        if (t < 0.5f) {
            float u = 2 * t;
            return u * u * ((factor + 1) * u - factor) / 2;
        } else {
            float u = 2 - 2 * t;
            return 1 - u * u * ((factor + 1) * u - factor) / 2;
        }
    }

    // ==================== 反弹缓动（Bounce） ====================

    /**
     * 反弹缓出（落地弹跳效果）。
     *
     * @param t 参数 [0,1]
     * @return 曲线值
     */
    public static float bounceOut(float t) {
        if (t < 1 / 2.75f) {
            return 7.5625f * t * t;
        } else if (t < 2 / 2.75f) {
            float x = t - 1.5f / 2.75f;
            return 7.5625f * x * x + 0.75f;
        } else if (t < 2.5 / 2.75f) {
            float x = t - 2.25f / 2.75f;
            return 7.5625f * x * x + 0.9375f;
        } else {
            float x = t - 2.625f / 2.75f;
            return 7.5625f * x * x + 0.984375f;
        }
    }

    /**
     * 反弹缓入。
     *
     * @param t 参数 [0,1]
     * @return 曲线值
     */
    public static float bounceIn(float t) {
        return 1 - bounceOut(1 - t);
    }

    /**
     * 反弹缓入缓出。
     *
     * @param t 参数 [0,1]
     * @return 曲线值
     */
    public static float bounceInOut(float t) {
        if (t < 0.5f) return bounceIn(t * 2) / 2;
        else return bounceOut(t * 2 - 1) / 2 + 0.5f;
    }


    // ==================== 圆 ====================
    /**
     * 圆形缓入曲线。
     * 从 0 开始非常缓慢地加速，在 t=1 时到达 1。形状为四分之一圆弧。
     * 公式：{@code 1 - sqrt(1 - t^2)}
     *
     * @param t 进度，范围 [0,1]
     * @return 缓动后的值，范围 [0,1]
     */
    public static float circIn(float t) {
        return 1 - (float) Math.sqrt(1 - t * t);
    }

    /**
     * 圆形缓出曲线。
     * 从 0 开始快速加速后减速，在 t=1 时到达 1。形状为四分之一圆弧的镜像。
     * 公式：{@code sqrt(1 - (t-1)^2)}
     *
     * @param t 进度，范围 [0,1]
     * @return 缓动后的值，范围 [0,1]
     */
    public static float circOut(float t) {
        return (float) Math.sqrt(1 - (t - 1) * (t - 1));
    }

    /**
     * 圆形缓入缓出曲线。
     * 先缓入（慢加速）后缓出（快减速），对称于 t=0.5。
     * 对于 t < 0.5：{@code (1 - sqrt(1 - 4*t^2)) / 2}
     * 对于 t >= 0.5：{@code (1 + sqrt(1 - 4*(t-1)^2)) / 2}
     *
     * @param t 进度，范围 [0,1]
     * @return 缓动后的值，范围 [0,1]
     */
    public static float circInOut(float t) {
        if (t < 0.5f) {
            return (1 - (float) Math.sqrt(1 - 4 * t * t)) / 2;
        } else {
            return (1 + (float) Math.sqrt(1 - 4 * (t - 1) * (t - 1))) / 2;
        }
    }
}
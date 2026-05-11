package com.fanxing.lib.phys.ease;

import com.fanxing.lib.util.math.EaseCurvesUtils;
import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;

/**
 * 缓动曲线工厂，提供所有常用缓动函数，返回 FloatUnaryOperator。
 * 完全基于 EaseCurvesUtils 中的静态方法实现。
 */
public final class Easing {

    // ------------------------------------------------------------
    // 1. 线性
    // ------------------------------------------------------------
    public static FloatUnaryOperator linear() {
        return t -> t;
    }

    // ------------------------------------------------------------
    // 2. 幂函数（指数可配置）
    // ------------------------------------------------------------
    public static FloatUnaryOperator power(float exponent) {
        return t -> EaseCurvesUtils.easeIn(t, exponent);
    }

    // 常用幂曲线预设（指数 2,3,4,5）
    public static FloatUnaryOperator quadIn()   { return t -> EaseCurvesUtils.easeIn(t, 2); }
    public static FloatUnaryOperator quadOut()  { return t -> EaseCurvesUtils.easeOut(t, 2); }
    public static FloatUnaryOperator quadInOut() { return t -> EaseCurvesUtils.easeInOut(t, 2); }

    public static FloatUnaryOperator cubicIn()   { return t -> EaseCurvesUtils.easeIn(t, 3); }
    public static FloatUnaryOperator cubicOut()  { return t -> EaseCurvesUtils.easeOut(t, 3); }
    public static FloatUnaryOperator cubicInOut() { return t -> EaseCurvesUtils.easeInOut(t, 3); }

    public static FloatUnaryOperator quartIn()   { return t -> EaseCurvesUtils.easeIn(t, 4); }
    public static FloatUnaryOperator quartOut()  { return t -> EaseCurvesUtils.easeOut(t, 4); }
    public static FloatUnaryOperator quartInOut() { return t -> EaseCurvesUtils.easeInOut(t, 4); }

    public static FloatUnaryOperator quintIn()   { return t -> EaseCurvesUtils.easeIn(t, 5); }
    public static FloatUnaryOperator quintOut()  { return t -> EaseCurvesUtils.easeOut(t, 5); }
    public static FloatUnaryOperator quintInOut() { return t -> EaseCurvesUtils.easeInOut(t, 5); }

    // ------------------------------------------------------------
    // 3. 正弦（无参数）
    // ------------------------------------------------------------
    public static FloatUnaryOperator sineIn()   { return EaseCurvesUtils::sineIn; }
    public static FloatUnaryOperator sineOut()  { return EaseCurvesUtils::sineOut; }
    public static FloatUnaryOperator sineInOut() { return EaseCurvesUtils::sineInOut; }
    // ------------------------------------------------------------
    // 3. 圆形（无参数）
    // ------------------------------------------------------------
    public static FloatUnaryOperator expoIn()   { return EaseCurvesUtils::expoIn; }
    public static FloatUnaryOperator expoOut()  { return EaseCurvesUtils::expoOut; }
    public static FloatUnaryOperator expoInOut() { return EaseCurvesUtils::expoInOut; }
    // ------------------------------------------------------------
    // 3. 圆形（无参数）
    // ------------------------------------------------------------
    public static FloatUnaryOperator circIn()   { return EaseCurvesUtils::circIn; }
    public static FloatUnaryOperator circOut()  { return EaseCurvesUtils::circOut; }
    public static FloatUnaryOperator circInOut() { return EaseCurvesUtils::circInOut; }

    // ------------------------------------------------------------
    // 4. 弹性曲线（参数：振幅 amplitude，周期 period）
    // ------------------------------------------------------------
    public static FloatUnaryOperator elasticIn(float amplitude, float period) {
        return t -> EaseCurvesUtils.elasticIn(t, amplitude, period);
    }
    public static FloatUnaryOperator elasticOut(float amplitude, float period) {
        return t -> EaseCurvesUtils.elasticOut(t, amplitude, period);
    }
    public static FloatUnaryOperator elasticInOut(float amplitude, float period) {
        return t -> EaseCurvesUtils.elasticInOut(t, amplitude, period);
    }

    // 常用预设（振幅1，周期0.3）
    public static FloatUnaryOperator elasticIn()   { return elasticIn(1f, 0.3f); }
    public static FloatUnaryOperator elasticOut()  { return elasticOut(1f, 0.3f); }
    public static FloatUnaryOperator elasticInOut(){ return elasticInOut(1f, 0.3f); }

    // ------------------------------------------------------------
    // 5. 回弹曲线（参数：过冲量 overshoot）
    // ------------------------------------------------------------
    public static FloatUnaryOperator backIn(float overshoot) {
        return t -> EaseCurvesUtils.backIn(t, overshoot);
    }
    public static FloatUnaryOperator backOut(float overshoot) {
        return t -> EaseCurvesUtils.backOut(t, overshoot);
    }
    public static FloatUnaryOperator backInOut(float overshoot) {
        return t -> EaseCurvesUtils.backInOut(t, overshoot);
    }

    // 常用预设（过冲量1.70158）
    public static FloatUnaryOperator backIn()   { return backIn(1.70158f); }
    public static FloatUnaryOperator backOut()  { return backOut(1.70158f); }
    public static FloatUnaryOperator backInOut(){ return backInOut(1.70158f); }

    // ------------------------------------------------------------
    // 6. 反弹曲线（无参数）
    // ------------------------------------------------------------
    public static FloatUnaryOperator bounceOut() { return EaseCurvesUtils::bounceOut; }
    public static FloatUnaryOperator bounceIn()  { return EaseCurvesUtils::bounceIn; }
    public static FloatUnaryOperator bounceInOut() { return EaseCurvesUtils::bounceInOut; }

    // ------------------------------------------------------------
    // 7. 三次贝塞尔曲线（可配置四个控制点）
    // ------------------------------------------------------------
    public static FloatUnaryOperator cubicBezier(float p0, float p1, float p2, float p3) {
        return t -> EaseCurvesUtils.bezierCubic(t, p0, p1, p2, p3);
    }

    // 常用预设（ease-in-out 的经典贝塞尔）
    public static FloatUnaryOperator easeInOutBezier() {
        return cubicBezier(0.42f, 0.0f, 0.58f, 1.0f);
    }

    // ------------------------------------------------------------
    // 8. 上升-保持-下降分段曲线
    // ------------------------------------------------------------
    public static FloatUnaryOperator riseHoldFall(float holdTimeScale, float riseEase, float fallEase) {
        return t -> EaseCurvesUtils.riseHoldFallBezier(t, holdTimeScale, riseEase, fallEase);
    }
}
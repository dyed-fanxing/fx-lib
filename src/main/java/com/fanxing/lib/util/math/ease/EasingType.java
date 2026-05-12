package com.fanxing.lib.util.math.ease;

import com.fanxing.lib.FxLib;
import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;

import java.util.Map;

/**
 * 缓动曲线类型枚举工厂，提供常用缓动曲线的单例对象。
 * <p>
 * 命名规则遵循 {@link EaseCurvesUtils} 中的“形状在前，次数在后”规则。
 * 例如 {@code IN_QUAD}、{@code OUT_CUBIC}、{@code IN_OUT_SINE}。
 * </p>
 */
public final class EasingType {

    // ========== 幂次缓动 ==========
    public static final FloatUnaryOperator LINEAR = t -> t;
    public static final FloatUnaryOperator IN_QUAD = EaseCurvesUtils::inQuad;
    public static final FloatUnaryOperator OUT_QUAD = EaseCurvesUtils::outQuad;
    public static final FloatUnaryOperator IN_OUT_QUAD = EaseCurvesUtils::inOutQuad;

    public static final FloatUnaryOperator IN_CUBIC = EaseCurvesUtils::inCubic;
    public static final FloatUnaryOperator OUT_CUBIC = EaseCurvesUtils::outCubic;
    public static final FloatUnaryOperator IN_OUT_CUBIC = EaseCurvesUtils::inOutCubic;

    public static final FloatUnaryOperator IN_QUART = EaseCurvesUtils::inQuart;
    public static final FloatUnaryOperator OUT_QUART = EaseCurvesUtils::outQuart;
    public static final FloatUnaryOperator IN_OUT_QUART = EaseCurvesUtils::inOutQuart;

    public static final FloatUnaryOperator IN_QUINT = EaseCurvesUtils::inQuint;
    public static final FloatUnaryOperator OUT_QUINT = EaseCurvesUtils::outQuint;
    public static final FloatUnaryOperator IN_OUT_QUINT = EaseCurvesUtils::inOutQuint;

    // ========== 正弦/指数/圆形缓动 ==========
    public static final FloatUnaryOperator IN_SINE = EaseCurvesUtils::inSine;
    public static final FloatUnaryOperator OUT_SINE = EaseCurvesUtils::outSine;
    public static final FloatUnaryOperator IN_OUT_SINE = EaseCurvesUtils::inOutSine;

    public static final FloatUnaryOperator IN_EXPO = EaseCurvesUtils::inExpo;
    public static final FloatUnaryOperator OUT_EXPO = EaseCurvesUtils::outExpo;
    public static final FloatUnaryOperator IN_OUT_EXPO = EaseCurvesUtils::inOutExpo;

    public static final FloatUnaryOperator IN_CIRC = EaseCurvesUtils::inCirc;
    public static final FloatUnaryOperator OUT_CIRC = EaseCurvesUtils::outCirc;
    public static final FloatUnaryOperator IN_OUT_CIRC = EaseCurvesUtils::inOutCirc;

    // ========== 弹性/回弹/反弹缓动 ==========
    public static final FloatUnaryOperator IN_ELASTIC = EaseCurvesUtils::inElastic;
    public static final FloatUnaryOperator OUT_ELASTIC = EaseCurvesUtils::outElastic;
    public static final FloatUnaryOperator IN_OUT_ELASTIC = EaseCurvesUtils::inOutElastic;

    public static final FloatUnaryOperator IN_BACK = EaseCurvesUtils::inBack;
    public static final FloatUnaryOperator OUT_BACK = EaseCurvesUtils::outBack;
    public static final FloatUnaryOperator IN_OUT_BACK = EaseCurvesUtils::inOutBack;

    public static final FloatUnaryOperator IN_BOUNCE = EaseCurvesUtils::inBounce;
    public static final FloatUnaryOperator OUT_BOUNCE = EaseCurvesUtils::outBounce;
    public static final FloatUnaryOperator IN_OUT_BOUNCE = EaseCurvesUtils::inOutBounce;

    // ========== 贝塞尔预设 ==========
    public static final FloatUnaryOperator EASE_IN_OUT_BEZIER = bezierCubic(0.42f,0.0f,0.58f,1.0f);

    // ========== 线性上升-保持-下降预设 ==========
    /**
     * 无保持，三角（0.0）线性上升-下降
     */
    public static final FloatUnaryOperator IN_OUT_LINEAR = inHoldOutLinear(0f);
    /**
     * 短保持（0.3）线性上升-保持-下降
     */
    public static final FloatUnaryOperator IN_SHORT_OUT_LINEAR = inHoldOutLinear(0.3f);
    /**
     * 无保持（0.5）线性上升-保持-下降
     */
    public static final FloatUnaryOperator IN_MID_OUT_LINEAR = inHoldOutLinear(0.5f);
    /**
     * 长保持（0.7）线性上升-保持-下降
     */
    public static final FloatUnaryOperator IN_LONG_OUT_LINEAR = inHoldOutLinear(0.7f);

    // ========== 上升-保持-下降贝塞尔曲线预设 ==========
    /**
     * 短保持
     */
    public static final FloatUnaryOperator IN_SHORT_OUT_BEZIER = inHoldOutBezier(0.3f);
    /**
     * 中保持
     */
    public static final FloatUnaryOperator IN_MID_OUT_BEZIER = inHoldOutBezier(0.5f);
    /**
     * 长保持
     */
    public static final FloatUnaryOperator IN_LONG_OUT_BEZIER = inHoldOutBezier(0.7f);

    // ========== GUI 选择映射 ==========
    public static final Map<String, FloatUnaryOperator> EASING_MAP = Map.ofEntries(
            Map.entry("util.math." + FxLib.MOD_ID + ".ease.linear", LINEAR),
            Map.entry("util.math." + FxLib.MOD_ID + ".ease.in_out_linear", IN_OUT_LINEAR),
            Map.entry("util.math." + FxLib.MOD_ID + ".ease.in_quad", IN_QUAD),
            Map.entry("util.math." + FxLib.MOD_ID + ".ease.out_quad", OUT_QUAD),
            Map.entry("util.math." + FxLib.MOD_ID + ".ease.in_out_quad", IN_OUT_QUAD),
            Map.entry("util.math." + FxLib.MOD_ID + ".ease.in_cubic", IN_CUBIC),
            Map.entry("util.math." + FxLib.MOD_ID + ".ease.out_cubic", OUT_CUBIC),
            Map.entry("util.math." + FxLib.MOD_ID + ".ease.in_out_cubic", IN_OUT_CUBIC),
            Map.entry("util.math." + FxLib.MOD_ID + ".ease.in_quart", IN_QUART),
            Map.entry("util.math." + FxLib.MOD_ID + ".ease.out_quart", OUT_QUART),
            Map.entry("util.math." + FxLib.MOD_ID + ".ease.in_out_quart", IN_OUT_QUART),
            Map.entry("util.math." + FxLib.MOD_ID + ".ease.in_quint", IN_QUINT),
            Map.entry("util.math." + FxLib.MOD_ID + ".ease.out_quint", OUT_QUINT),
            Map.entry("util.math." + FxLib.MOD_ID + ".ease.in_out_quint", IN_OUT_QUINT),
            Map.entry("util.math." + FxLib.MOD_ID + ".ease.in_sine", IN_SINE),
            Map.entry("util.math." + FxLib.MOD_ID + ".ease.out_sine", OUT_SINE),
            Map.entry("util.math." + FxLib.MOD_ID + ".ease.in_out_sine", IN_OUT_SINE),
            Map.entry("util.math." + FxLib.MOD_ID + ".ease.in_expo", IN_EXPO),
            Map.entry("util.math." + FxLib.MOD_ID + ".ease.out_expo", OUT_EXPO),
            Map.entry("util.math." + FxLib.MOD_ID + ".ease.in_out_expo", IN_OUT_EXPO),
            Map.entry("util.math." + FxLib.MOD_ID + ".ease.in_circ", IN_CIRC),
            Map.entry("util.math." + FxLib.MOD_ID + ".ease.out_circ", OUT_CIRC),
            Map.entry("util.math." + FxLib.MOD_ID + ".ease.in_out_circ", IN_OUT_CIRC),
            Map.entry("util.math." + FxLib.MOD_ID + ".ease.in_elastic", IN_ELASTIC),
            Map.entry("util.math." + FxLib.MOD_ID + ".ease.out_elastic", OUT_ELASTIC),
            Map.entry("util.math." + FxLib.MOD_ID + ".ease.in_out_elastic", IN_OUT_ELASTIC),
            Map.entry("util.math." + FxLib.MOD_ID + ".ease.in_back", IN_BACK),
            Map.entry("util.math." + FxLib.MOD_ID + ".ease.out_back", OUT_BACK),
            Map.entry("util.math." + FxLib.MOD_ID + ".ease.in_out_back", IN_OUT_BACK),
            Map.entry("util.math." + FxLib.MOD_ID + ".ease.in_bounce", IN_BOUNCE),
            Map.entry("util.math." + FxLib.MOD_ID + ".ease.out_bounce", OUT_BOUNCE),
            Map.entry("util.math." + FxLib.MOD_ID + ".ease.in_out_bounce", IN_OUT_BOUNCE),
            Map.entry("util.math." + FxLib.MOD_ID + ".ease.in_out_bezier", EASE_IN_OUT_BEZIER),
            Map.entry("util.math." + FxLib.MOD_ID + ".ease.in_short_out_linear", IN_SHORT_OUT_LINEAR),
            Map.entry("util.math." + FxLib.MOD_ID + ".ease.in_mid_out_linear", IN_MID_OUT_LINEAR),
            Map.entry("util.math." + FxLib.MOD_ID + ".ease.in_long_out_linear", IN_LONG_OUT_LINEAR),
            Map.entry("util.math." + FxLib.MOD_ID + ".ease.in_short_out_bezier", IN_SHORT_OUT_BEZIER),
            Map.entry("util.math." + FxLib.MOD_ID + ".ease.in_mid_out_bezier", IN_MID_OUT_BEZIER),
            Map.entry("util.math." + FxLib.MOD_ID + ".ease.in_long_out_bezier", IN_LONG_OUT_BEZIER)
    );

    // 有参工厂方法（保留，供需要自定义参数的场景使用）
    public static FloatUnaryOperator power(float exponent) {
        return t -> EaseCurvesUtils.bezier(t, 0, exponent, 1);
    }

    public static FloatUnaryOperator elasticIn(float a, float p) {
        return t -> {
            if (t == 0 || t == 1) return t;
            float s = p / (float) (2 * Math.PI) * (float) Math.asin(1 / a);
            return -(a * (float) Math.pow(2, 10 * (t - 1)) * (float) Math.sin((t - 1 - s) * (2 * Math.PI) / p));
        };
    }

    public static FloatUnaryOperator elasticOut(float a, float p) {
        return t -> {
            if (t == 0 || t == 1) return t;
            float s = p / (float) (2 * Math.PI) * (float) Math.asin(1 / a);
            return a * (float) Math.pow(2, -10 * t) * (float) Math.sin((t - s) * (2 * Math.PI) / p) + 1;
        };
    }

    public static FloatUnaryOperator elasticInOut(float a, float p) {
        return t -> {
            if (t == 0 || t == 1) return t;
            float s = p / (float) (2 * Math.PI) * (float) Math.asin(1 / a);
            if (t < 0.5f) {
                float u = 2 * t;
                return -(a * (float) Math.pow(2, 10 * (u - 1)) * (float) Math.sin((u - 1 - s) * (2 * Math.PI) / p)) / 2;
            } else {
                float u = 2 - 2 * t;
                return a * (float) Math.pow(2, -10 * u) * (float) Math.sin((u - s) * (2 * Math.PI) / p) / 2 + 0.5f;
            }
        };
    }

    public static FloatUnaryOperator backIn(float overshoot) {
        return t -> {
            float s = overshoot;
            return t * t * ((s + 1) * t - s);
        };
    }

    public static FloatUnaryOperator backOut(float overshoot) {
        return t -> {
            float s = overshoot;
            float u = 1 - t;
            return 1 - u * u * ((s + 1) * u - s);
        };
    }

    public static FloatUnaryOperator backInOut(float overshoot) {
        final float s = overshoot * 1.525f;
        return t -> {
            if (t < 0.5f) {
                float u = 2 * t;
                return u * u * ((s + 1) * u - s) / 2;
            } else {
                float u = 2 - 2 * t;
                return 1 - u * u * ((s + 1) * u - s) / 2;
            }
        };
    }

    public static FloatUnaryOperator bezierCubic(float p0, float p1, float p2, float p3) {
        return t -> EaseCurvesUtils.bezierCubic(t, p0, p1, p2, p3);
    }

    public static FloatUnaryOperator inHoldOutLinear(float holdTimeScale) {
        return t -> EaseCurvesUtils.linearRiseHoldFall(t, holdTimeScale);
    }

    public static FloatUnaryOperator inHoldOutBezier(float holdTimeScale, float riseEase, float fallEase) {
        return t -> EaseCurvesUtils.riseHoldFallBezier(t, holdTimeScale, riseEase, fallEase);
    }

    public static FloatUnaryOperator inHoldOutBezier(float holdTimeScale) {
        return t -> EaseCurvesUtils.riseHoldFallBezier(t, holdTimeScale, 0f, 1f);
    }

    public static FloatUnaryOperator outHoldInBezier(float holdTimeScale) {
        return t -> EaseCurvesUtils.riseHoldFallBezier(t, holdTimeScale, 1f, 0f);
    }
}
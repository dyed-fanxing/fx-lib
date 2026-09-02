package com.fanxing.lib.util.math.ease;

import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 缓动曲线类型枚举工厂，提供常用缓动曲线的单例对象。
 * <p>
 * 命名规则遵循 {@link EaseUtils} 中的“形状在前，次数在后”规则。
 * 例如 {@code IN_QUAD}、{@code OUT_CUBIC}、{@code IN_OUT_SINE}。
 * </p>
 */
public final class EaseType {

    // ========== 幂次缓动 ==========
    public static final FloatUnaryOperator LINEAR = t -> t;
    public static final FloatUnaryOperator IN_QUAD = EaseUtils::inQuad;
    public static final FloatUnaryOperator OUT_QUAD = EaseUtils::outQuad;
    public static final FloatUnaryOperator IN_OUT_QUAD = EaseUtils::inOutQuad;

    public static final FloatUnaryOperator IN_CUBIC = EaseUtils::inCubic;
    public static final FloatUnaryOperator OUT_CUBIC = EaseUtils::outCubic;
    public static final FloatUnaryOperator IN_OUT_CUBIC = EaseUtils::inOutCubic;

    public static final FloatUnaryOperator IN_QUART = EaseUtils::inQuart;
    public static final FloatUnaryOperator OUT_QUART = EaseUtils::outQuart;
    public static final FloatUnaryOperator IN_OUT_QUART = EaseUtils::inOutQuart;

    public static final FloatUnaryOperator IN_QUINT = EaseUtils::inQuint;
    public static final FloatUnaryOperator OUT_QUINT = EaseUtils::outQuint;
    public static final FloatUnaryOperator IN_OUT_QUINT = EaseUtils::inOutQuint;

    // ========== 正弦/指数/圆形缓动 ==========
    public static final FloatUnaryOperator IN_SINE = EaseUtils::inSine;
    public static final FloatUnaryOperator OUT_SINE = EaseUtils::outSine;
    public static final FloatUnaryOperator IN_OUT_SINE = EaseUtils::inOutSine;

    public static final FloatUnaryOperator IN_EXPO = EaseUtils::inExpo;
    public static final FloatUnaryOperator OUT_EXPO = EaseUtils::outExpo;
    public static final FloatUnaryOperator IN_OUT_EXPO = EaseUtils::inOutExpo;

    public static final FloatUnaryOperator IN_CIRC = EaseUtils::inCirc;
    public static final FloatUnaryOperator OUT_CIRC = EaseUtils::outCirc;
    public static final FloatUnaryOperator IN_OUT_CIRC = EaseUtils::inOutCirc;

    // ========== 弹性/回弹/反弹缓动 ==========
    public static final FloatUnaryOperator IN_ELASTIC = EaseUtils::inElastic;
    public static final FloatUnaryOperator OUT_ELASTIC = EaseUtils::outElastic;
    public static final FloatUnaryOperator IN_OUT_ELASTIC = EaseUtils::inOutElastic;

    public static final FloatUnaryOperator IN_BACK = EaseUtils::inBack;
    public static final FloatUnaryOperator OUT_BACK = EaseUtils::outBack;
    public static final FloatUnaryOperator IN_OUT_BACK = EaseUtils::inOutBack;

    public static final FloatUnaryOperator IN_BOUNCE = EaseUtils::inBounce;
    public static final FloatUnaryOperator OUT_BOUNCE = EaseUtils::outBounce;
    public static final FloatUnaryOperator IN_OUT_BOUNCE = EaseUtils::inOutBounce;


    // ========== GUI 选择映射 ==========
    public static final Map<String, FloatUnaryOperator> EASES = new LinkedHashMap<>();

    static {
        EASES.put("linear", LINEAR);
        EASES.put("in_quad", IN_QUAD);
        EASES.put("out_quad", OUT_QUAD);
        EASES.put("in_out_quad", IN_OUT_QUAD);
        EASES.put("in_cubic", IN_CUBIC);
        EASES.put("out_cubic", OUT_CUBIC);
        EASES.put("in_out_cubic", IN_OUT_CUBIC);
        EASES.put("in_quart", IN_QUART);
        EASES.put("out_quart", OUT_QUART);
        EASES.put("in_out_quart", IN_OUT_QUART);
        EASES.put("in_quint", IN_QUINT);
        EASES.put("out_quint", OUT_QUINT);
        EASES.put("in_out_quint", IN_OUT_QUINT);
        EASES.put("in_sine", IN_SINE);
        EASES.put("out_sine", OUT_SINE);
        EASES.put("in_out_sine", IN_OUT_SINE);
        EASES.put("in_expo", IN_EXPO);
        EASES.put("out_expo", OUT_EXPO);
        EASES.put("in_out_expo", IN_OUT_EXPO);
        EASES.put("in_circ", IN_CIRC);
        EASES.put("out_circ", OUT_CIRC);
        EASES.put("in_out_circ", IN_OUT_CIRC);
        EASES.put("in_elastic", IN_ELASTIC);
        EASES.put("out_elastic", OUT_ELASTIC);
        EASES.put("in_out_elastic", IN_OUT_ELASTIC);
        EASES.put("in_back", IN_BACK);
        EASES.put("out_back", OUT_BACK);
        EASES.put("in_out_back", IN_OUT_BACK);
        EASES.put("in_bounce", IN_BOUNCE);
        EASES.put("out_bounce", OUT_BOUNCE);
        EASES.put("in_out_bounce", IN_OUT_BOUNCE);
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

}
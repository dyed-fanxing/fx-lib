package com.fanxing.lib.client.particle.util;

import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;

/**
 * @author dyed_fanxing
 * @date 2026/5/11 16:13
 */
public interface F2FFactoryUtils {
    // 恒定
    static FloatUnaryOperator constant(float value) {
        return t -> value;
    }

    static FloatUnaryOperator easing(float start, float end, FloatUnaryOperator curve) {
        return t -> start + (end - start) * curve.apply(t);
    }

    // 缓动曲线（宽和长使用相同曲线）
    static FloatUnaryOperator[] easing(float startX, float endX, FloatUnaryOperator curveX,
                                       float startY, float endY, FloatUnaryOperator curveY) {
        return new FloatUnaryOperator[]{
                t -> startX + (endX - startX) * curveX.apply(t),
                t -> startY + (endY - startY) * curveY.apply(t)
        };
    }

    static FloatUnaryOperator[] easing(float startX, float endX, FloatUnaryOperator curveX,
                                       float startY, float endY, FloatUnaryOperator curveY,
                                       float startZ, float endZ, FloatUnaryOperator curveZ) {
        return new FloatUnaryOperator[]{
                t -> startX + (endX - startX) * curveX.apply(t),
                t -> startY + (endY - startY) * curveY.apply(t),
                t -> startZ + (endZ - startZ) * curveZ.apply(t)
        };
    }

    // 滚动：uMin = progress * speed, uMax = uMin + repetitions
    static FloatUnaryOperator scrollMin(float speed) {
        return t -> t * speed;
    }
    static FloatUnaryOperator scrollMax(float speed, float repeat) {
        return t -> t * speed + repeat;
    }
    static FloatUnaryOperator[] scrollUV(float uSpeed, float uRep, float vSpeed, float vRep) {
        FloatUnaryOperator uMin = t -> t * uSpeed;
        FloatUnaryOperator uMax = t -> t * uSpeed + uRep;
        FloatUnaryOperator vMin = t -> t * vSpeed;
        FloatUnaryOperator vMax = t -> t * vSpeed + vRep;
        return new FloatUnaryOperator[]{uMin, uMax, vMin, vMax};
    }

}

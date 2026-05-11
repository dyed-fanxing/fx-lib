package com.fanxing.lib.client.particle.property;

import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;

public interface QuadSizeFactory {
    // 恒定尺寸
    static FloatUnaryOperator[] constant(float width, float length) {
        return new FloatUnaryOperator[]{ t -> width, t -> length };
    }

    // 缓动曲线（宽和长使用相同曲线）
    static FloatUnaryOperator[] easing(float startWidth, float endWidth,
                                       float startLength, float endLength,
                                       FloatUnaryOperator curve) {
        return new FloatUnaryOperator[]{
                t -> startWidth + (endWidth - startWidth) * curve.apply(t),
                t -> startLength + (endLength - startLength) * curve.apply(t)
        };
    }

    // 宽和长使用不同曲线
    static FloatUnaryOperator[] easing(float startWidth, float endWidth, FloatUnaryOperator curveWidth,
                                       float startLength, float endLength, FloatUnaryOperator curveLength) {
        return new FloatUnaryOperator[]{
                t -> startWidth + (endWidth - startWidth) * curveWidth.apply(t),
                t -> startLength + (endLength - startLength) * curveLength.apply(t)
        };
    }
}
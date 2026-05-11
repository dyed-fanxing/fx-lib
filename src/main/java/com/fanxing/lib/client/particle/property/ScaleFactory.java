package com.fanxing.lib.client.particle.property;

import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;

/**
 * @author dyed_fanxing
 * @date 2026/5/9 15:25
 */
public interface ScaleFactory {
    // 恒定缩放
    static FloatUnaryOperator constant(float value) {
        return t -> value;
    }

    static FloatUnaryOperator[] constant(float x, float y, float z) {
        // 如果需要各轴独立，分别赋值
        return new FloatUnaryOperator[]{t -> x, t -> y, t -> z};
        // 仅作示例，实际使用需分开字段
    }

    static FloatUnaryOperator easing(float start, float end, FloatUnaryOperator curve) {
        return t -> start + (end - start) * curve.apply(t);
    }


    static FloatUnaryOperator[] easing(float startX, float endX, FloatUnaryOperator curveX,
                                       float startY, float endY, FloatUnaryOperator curveY,
                                       float startZ, float endZ, FloatUnaryOperator curveZ
    ) {
        return new FloatUnaryOperator[]{
                t -> startX + (endX - startX) * curveX.apply(t),
                t -> startY + (endY - startY) * curveY.apply(t),
                t -> startZ + (endZ - startZ) * curveZ.apply(t)
        };
    }
}

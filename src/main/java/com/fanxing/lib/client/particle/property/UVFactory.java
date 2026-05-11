package com.fanxing.lib.client.particle.property;

import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;

/**
 * @author dyed_fanxing
 * @date 2026/5/9 15:10
 */
public interface UVFactory {
    // 恒定
    static FloatUnaryOperator constant(float value) {
        return t -> value;
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

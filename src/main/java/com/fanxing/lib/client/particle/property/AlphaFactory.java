package com.fanxing.lib.client.particle.property;

import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;

/**
 * @author dyed_fanxing
 * @date 2026/5/9 15:34
 */
public interface AlphaFactory {
    static FloatUnaryOperator constant(float alpha) {
        return t -> alpha;
    }
    static FloatUnaryOperator easing(float start, float end, FloatUnaryOperator curve) {
        return t -> start + (end - start) * curve.apply(t);
    }
}

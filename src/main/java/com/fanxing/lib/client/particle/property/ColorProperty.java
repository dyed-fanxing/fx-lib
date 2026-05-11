package com.fanxing.lib.client.particle.property;


import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import net.minecraft.util.FastColor;

/**
 * @author dyed_fanxing
 * @date 2026/5/8 21:20
 * 颜色属性接口，提供基于生命周期进度的颜色值（ARGB）。
 */
public interface ColorProperty {
    int getColor(float progress);
    /**
     * 恒定颜色策略。
     */
    class Constant implements ColorProperty {
        private final int color;
        public Constant(int color) { this.color = color; }
        @Override public int getColor(float progress) { return color; }
    }

    /**
     * 缓动曲线颜色策略，支持自定义插值曲线。
     */
    class Easing implements ColorProperty {
        private final int startColor;
        private final int endColor;
        private final FloatUnaryOperator curve;

        public Easing(int startColor, int endColor, FloatUnaryOperator curve) {
            this.startColor = startColor;
            this.endColor = endColor;
            this.curve = curve;
        }

        @Override
        public int getColor(float progress) {
            return FastColor.ARGB32.lerp(curve.apply(progress),startColor, endColor);
        }
    }
}
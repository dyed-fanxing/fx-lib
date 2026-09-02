package com.fanxing.lib.client.particle.editor.strategy;



import com.fanxing.lib.client.particle.editor.base.DynamicFreeQuadParticle;
import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;

/**
 * 缩放策略：控制粒子的宽度和高度变化。
 * @author dyed_fanxing
 * @since 2026/5/24 19:53
 */
@FunctionalInterface
public interface SizeStrategy {
    default void tick(DynamicFreeQuadParticle particle) {}
    void applyLerpScale(DynamicFreeQuadParticle particle, float partialTick);

    // 加速度缩放（线性变化）
    class Acceleration implements SizeStrategy {
        private float width, height;
        private float widthVel, heightVel;
        private final float widthAcc, heightAcc;

        public Acceleration(float startWidth, float startHeight,
                            float widthVel, float heightVel,
                            float widthAcc, float heightAcc) {
            this.width = startWidth;
            this.height = startHeight;
            this.widthVel = widthVel;
            this.heightVel = heightVel;
            this.widthAcc = widthAcc;
            this.heightAcc = heightAcc;
        }

        @Override
        public void tick(DynamicFreeQuadParticle particle) {
            widthVel += widthAcc;
            heightVel += heightAcc;
            width += widthVel;
            height += heightVel;
        }

        @Override
        public void applyLerpScale(DynamicFreeQuadParticle particle, float partialTick) {
            particle.lerpWidth = width;
            particle.lerpHeight = height;
        }
    }

    // 缓动缩放
    class Ease implements SizeStrategy {
        private final float startWidth, startHeight;
        private final float endWidth, endHeight;
        private final FloatUnaryOperator easeWidth, easeHeight;

        public Ease(float startWidth, float startHeight,
                    float endWidth, float endHeight,
                    FloatUnaryOperator easeWidth,
                    FloatUnaryOperator easeHeight) {
            this.startWidth = startWidth;
            this.startHeight = startHeight;
            this.endWidth = endWidth;
            this.endHeight = endHeight;
            this.easeWidth = easeWidth;
            this.easeHeight = easeHeight;
        }

        @Override
        public void applyLerpScale(DynamicFreeQuadParticle particle, float partialTick) {
            float t = particle.getProgress(partialTick);
            particle.lerpWidth = startWidth + (endWidth - startWidth) * easeWidth.apply(t);
            particle.lerpHeight = startHeight + (endHeight - startHeight) * easeHeight.apply(t);
        }
    }
}
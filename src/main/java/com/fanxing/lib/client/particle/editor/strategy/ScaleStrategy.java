package com.fanxing.lib.client.particle.editor.strategy;

import com.fanxing.lib.client.particle.editor.base.Abstract3DPropertyParticle;
import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;

/**
 * 3D缩放策略：控制粒子的 X, Y, Z 轴缩放变化。
 * 适用于继承 Abstract3DParticle 的粒子（如圆柱、球体）。
 * @author dyed_fanxing
 * @since 2026/5/24 21:15
 */
@FunctionalInterface
public interface ScaleStrategy {
    default void tick(Abstract3DPropertyParticle particle) {}
    void applyLerpScale(Abstract3DPropertyParticle particle, float partialTick);

    // 加速度缩放（线性变化）
    class Acceleration implements ScaleStrategy {
        private float x, y, z;
        private float vx, vy, vz;
        private final float ax, ay, az;

        public Acceleration(float startX, float startY, float startZ,
                            float vx, float vy, float vz,
                            float ax, float ay, float az) {
            this.x = startX;
            this.y = startY;
            this.z = startZ;
            this.vx = vx;
            this.vy = vy;
            this.vz = vz;
            this.ax = ax;
            this.ay = ay;
            this.az = az;
        }

        @Override
        public void tick(Abstract3DPropertyParticle particle) {
            vx += ax;
            vy += ay;
            vz += az;
            x += vx;
            y += vy;
            z += vz;
        }

        @Override
        public void applyLerpScale(Abstract3DPropertyParticle particle, float partialTick) {
            particle.lerpScaleX = x;
            particle.lerpScaleY = y;
            particle.lerpScaleZ = z;
        }
    }

    // 缓动缩放
    class Ease implements ScaleStrategy {
        private final float startX, startY, startZ;
        private final float endX, endY, endZ;
        private final FloatUnaryOperator easeX, easeY, easeZ;

        public Ease(float startX, float startY, float startZ,
                    float endX, float endY, float endZ,
                    FloatUnaryOperator easeX,
                    FloatUnaryOperator easeY,
                    FloatUnaryOperator easeZ) {
            this.startX = startX;
            this.startY = startY;
            this.startZ = startZ;
            this.endX = endX;
            this.endY = endY;
            this.endZ = endZ;
            this.easeX = easeX;
            this.easeY = easeY;
            this.easeZ = easeZ;
        }

        @Override
        public void applyLerpScale(Abstract3DPropertyParticle particle, float partialTick) {
            float t = particle.getProgress(partialTick);
            if (t >= 1.0f) {
                particle.lerpScaleX = endX;
                particle.lerpScaleY = endY;
                particle.lerpScaleZ = endZ;
                return;
            }
            particle.lerpScaleX = startX + (endX - startX) * easeX.apply(t);
            particle.lerpScaleY = startY + (endY - startY) * easeY.apply(t);
            particle.lerpScaleZ = startZ + (endZ - startZ) * easeZ.apply(t);
        }
    }
}
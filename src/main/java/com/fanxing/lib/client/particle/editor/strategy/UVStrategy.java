package com.fanxing.lib.client.particle.editor.strategy;

import com.fanxing.lib.client.particle.editor.base.AbstractPropertyParticle;

/**
 * UV 策略：控制纹理坐标的变化（滚动、序列帧）。
 * @author dyed_fanxing
 * @since 2026/5/24 21:00
 */
@FunctionalInterface
public interface UVStrategy {
    default void tick(AbstractPropertyParticle particle) {}
    void applyLerpUV(AbstractPropertyParticle particle, float partialTick);

    // 滚动 UV
    class Scroll implements UVStrategy {
        private final float uSpeed, vSpeed;
        private final float uRepeat, vRepeat;
        private float uOffset, vOffset;

        public Scroll(float uSpeed, float vSpeed, float uRepeat, float vRepeat) {
            this.uSpeed = uSpeed;
            this.vSpeed = vSpeed;
            this.uRepeat = uRepeat;
            this.vRepeat = vRepeat;
        }

        @Override
        public void tick(AbstractPropertyParticle particle) {
            uOffset += uSpeed;
            vOffset += vSpeed;
        }

        @Override
        public void applyLerpUV(AbstractPropertyParticle particle, float partialTick) {
            particle.lerpU0 = uOffset;
            particle.lerpV0 = vOffset;
            particle.lerpU1 = uOffset + uRepeat;
            particle.lerpV1 = vOffset + vRepeat;
        }
    }

    // 序列帧 UV
    class Animated implements UVStrategy {
        private final int frameCountV, frameTicks;
        private final float uSize, vSize;
        private final float startU0, startV0;
        private int currentFrame;
        private int tickCounter;

        public Animated(int frameCountV, int frameTicks, float uSize, float vSize, float startU0, float startV0) {
            this.frameCountV = frameCountV;
            this.frameTicks = frameTicks;
            this.uSize = uSize;
            this.vSize = vSize;
            this.startU0 = startU0;
            this.startV0 = startV0;
        }

        @Override
        public void tick(AbstractPropertyParticle particle) {
            tickCounter++;
            if (tickCounter >= frameTicks) {
                tickCounter = 0;
                currentFrame = (currentFrame + 1) % frameCountV;
            }
        }

        @Override
        public void applyLerpUV(AbstractPropertyParticle particle, float partialTick) {
            particle.lerpU0 = startU0;
            particle.lerpV0 = startV0 + currentFrame * vSize;
            particle.lerpU1 = startU0 + uSize;
            particle.lerpV1 = particle.lerpV0 + vSize;
        }
    }
}
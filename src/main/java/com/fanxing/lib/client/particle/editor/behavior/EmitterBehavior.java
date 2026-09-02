package com.fanxing.lib.client.particle.editor.behavior;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;

import java.util.function.Supplier;

/**
 * 发射器行为的抽象基类，提供公共字段和子粒子管理功能。
 */
public abstract class EmitterBehavior{
    protected Supplier<Particle> childFactory;
    public EmitterBehavior(){
    }

    public EmitterBehavior(Supplier<Particle> childFactory) {
        this.childFactory = childFactory;
    }

    public void setChild(Supplier<Particle> childFactory) {
        this.childFactory = childFactory;
    }

    /**
     * 发射一个子粒子（由子类在适当时机调用）
     */
    protected void spawnChild() {
        if (childFactory == null) return;
        Particle particle = childFactory.get();
        Minecraft.getInstance().particleEngine.add(particle);
    }


    public abstract void tick(int age);

    /**
     * 连续发射器行为：每 tick 按固定速率发射子粒子。
     */
    public static class ContinuousEmitter extends EmitterBehavior {
        /** 每 tick 发射数量（小数表示概率），例如 0.5 平均每 2 tick 发射 1 个 */
        public float rate = 0.5f;
        private float accumulator = 0f;

        public ContinuousEmitter() {
            super();
        }
        public ContinuousEmitter(float rate,Supplier<Particle> childFactory ) {
            super(childFactory);
            this.rate = rate;
        }
        public ContinuousEmitter(float rate) {
            this.rate = rate;
        }
        @Override
        public void tick(int age) {
            accumulator += rate;
            int toEmit = (int) accumulator;
            accumulator -= toEmit;
            for (int i = 0; i < toEmit; i++) {
                spawnChild();
            }
        }
    }



    /**
     * 爆发发射器行为：支持一次性爆发或周期性爆发。
     */
    public static class BurstEmitter extends EmitterBehavior {
        /** 每次爆发的粒子数量 */
        public int count = 10;
        /** 爆发间隔（tick），0 表示仅爆发一次 */
        public int interval = 0;
        /** 初始延迟（tick） */
        public int startDelay = 0;
        private int nextEmitTick = -1;
        private boolean done = false;

        public BurstEmitter() {
            super();
        }
        public BurstEmitter(Supplier<Particle> childFactory, int count, int interval, int startDelay) {
            super(childFactory);
            this.count = count;
            this.interval = interval;
            this.startDelay = startDelay;
        }




        @Override
        public void tick(int age) {
            if (done) return;
            if (nextEmitTick < 0) {
                nextEmitTick = age + startDelay;
                return;
            }
            if (age >= nextEmitTick) {
                for (int i = 0; i < count; i++) {
                    spawnChild();
                }
                if (interval <= 0) done = true;
                else nextEmitTick += interval;

            }
        }
    }

}
package com.fanxing.lib.item.compoent.particle;


import com.fanxing.lib.util.math.random.RandomType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;


import java.util.Objects;

/**
 * 发射器行为配置
 *
 * @author dyed_fanxing
 * @since 2026/5/25 13:44
 */
public final class EmitterBehavior {
    public RandomType interval;            // 间隔 多少Tick发射一次
    public RandomType amount;              // 数量 发射一次发射多少
    public RandomType startDelay;          // 起始时间 什么时候开始发射
    public int totalCount;                  // 总数量
    public List<ParticleLayer> particles;   // 粒子层 要发射的粒子

    public EmitterBehavior() {
    }

    public EmitterBehavior(RandomType interval, RandomType amount, RandomType startDelay,
                           int totalCount, List<ParticleLayer> particles) {
        this.interval = interval;
        this.amount = amount;
        this.startDelay = startDelay;
        this.totalCount = totalCount;
        this.particles = particles;
    }

    public static final Codec<EmitterBehavior> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    RandomType.CODEC.fieldOf("interval").forGetter(e -> e.interval),
                    RandomType.CODEC.fieldOf("amount").forGetter(e -> e.amount),
                    RandomType.CODEC.fieldOf("startDelay").forGetter(e -> e.startDelay),
                    Codec.INT.optionalFieldOf("totalCount", -1).forGetter(e -> e.totalCount),
                    ParticleLayer.CODEC.listOf().fieldOf("particles").forGetter(e -> e.particles)
            ).apply(instance, EmitterBehavior::new)
    );

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmitterBehavior that)) return false;
        return totalCount == that.totalCount &&
                Objects.equals(interval, that.interval) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(startDelay, that.startDelay) &&
                Objects.equals(particles, that.particles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(interval, amount, startDelay, totalCount, particles);
    }

    @Override
    public String toString() {
        return "EmitterBehavior{" +
                "interval=" + interval +
                ", amount=" + amount +
                ", startDelay=" + startDelay +
                ", totalCount=" + totalCount +
                ", particles=" + particles +
                '}';
    }
}
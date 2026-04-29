package com.fanxing.lib.loot.numberprovider;


import com.fanxing.lib.registry.NumberProvidersFxLib;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @author dyed_fanxing
 * @date 2026/4/27 00:09
 */
public record GaussianNumberProvider(double mean, double stdDev, Optional<Double> min,
                                     Optional<Double> max) implements NumberProvider {

    public static final MapCodec<GaussianNumberProvider> CODEC = RecordCodecBuilder.mapCodec(
            inst -> inst.group(
                    Codec.DOUBLE.fieldOf("mean").forGetter(GaussianNumberProvider::mean),
                    Codec.DOUBLE.fieldOf("std_dev").forGetter(GaussianNumberProvider::stdDev),
                    Codec.DOUBLE.optionalFieldOf("min").forGetter(GaussianNumberProvider::min),
                    Codec.DOUBLE.optionalFieldOf("max").forGetter(GaussianNumberProvider::max)
            ).apply(inst, GaussianNumberProvider::new)
    );

    @Override
    public float getFloat(LootContext context) {
        RandomSource random = context.getRandom();
        random.nextGaussian();
        double value = mean + stdDev * random.nextGaussian();
        if (min.isPresent() && value < min.get()) value = min.get();
        if (max.isPresent() && value > max.get()) value = max.get();
        return (float) value;
    }

    @Override
    public @NotNull LootNumberProviderType getType() {
        return NumberProvidersFxLib.GAUSSIAN_PROVIDER.get();
    }
}
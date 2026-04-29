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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record ExponentialNumberProvider(double min, double max, double exponent) implements NumberProvider {
    public static final MapCodec<ExponentialNumberProvider> CODEC = RecordCodecBuilder.mapCodec(
            inst -> inst.group(
                    Codec.DOUBLE.fieldOf("min").forGetter(ExponentialNumberProvider::min),
                    Codec.DOUBLE.fieldOf("max").forGetter(ExponentialNumberProvider::max),
                    Codec.DOUBLE.fieldOf("exponent").forGetter(ExponentialNumberProvider::exponent)
            ).apply(inst, ExponentialNumberProvider::new)
    );
    private static final Logger log = LoggerFactory.getLogger(ExponentialNumberProvider.class);

    @Override
    public float getFloat(LootContext context) {
        RandomSource random = context.getRandom();
        double r = random.nextDouble(); // 均匀 0~1
        double value = min + (max - min) * Math.pow(r, exponent);
        return (float) value;
    }

    @Override
    public @NotNull LootNumberProviderType getType() {
        return NumberProvidersFxLib.EXPONENTIAL_PROVIDER.get();
    }
}
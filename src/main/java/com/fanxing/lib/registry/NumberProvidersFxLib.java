package com.fanxing.lib.registry;

import com.fanxing.lib.FxLib;
import com.fanxing.lib.loot.numberprovider.ExponentialNumberProvider;
import com.fanxing.lib.loot.numberprovider.GaussianNumberProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * @author dyed_fanxing
 * @date 2026/4/26 23:09
 */
public class NumberProvidersFxLib {
    public static final DeferredRegister<LootNumberProviderType> NUMBER_PROVIDER_TYPES = DeferredRegister.create(Registries.LOOT_NUMBER_PROVIDER_TYPE, FxLib.MOD_ID);

    public static final Supplier<LootNumberProviderType> EXPONENTIAL_PROVIDER = NUMBER_PROVIDER_TYPES.register(
            "exponential", () -> new LootNumberProviderType(ExponentialNumberProvider.CODEC));
    public static final Supplier<LootNumberProviderType> GAUSSIAN_PROVIDER = NUMBER_PROVIDER_TYPES.register(
            "gaussian", () -> new LootNumberProviderType(GaussianNumberProvider.CODEC));

    public static void register(IEventBus bus) {
        NUMBER_PROVIDER_TYPES.register(bus);
    }
}

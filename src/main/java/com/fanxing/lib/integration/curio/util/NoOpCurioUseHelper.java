package com.fanxing.lib.integration.curio.util;


import net.minecraft.client.KeyMapping;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author dyed_fanxing
 * @date 2026/4/29 16:28
 * 默认没有引入Curio依赖的空操作
 */
public class NoOpCurioUseHelper implements ICurioUseHelper {
    @Override
    public void handleInputKey(LivingEntity entity, KeyMapping key, java.util.function.Predicate<SlotInfo> filter, String... slotTypes) {
    }

    @Override
    public Map<String, List<Integer>> collectAndActivateSlots(LivingEntity entity, java.util.function.Predicate<SlotInfo> filter, String... slotTypes) {
        return Collections.emptyMap();
    }

    @Override
    public void deactivateSlots(LivingEntity entity, Map<String, List<Integer>> slots) {
    }

    @Override
    public boolean isUsingCurio(LivingEntity entity, String slotType, Item item) {
        return false;
    }

    @Override
    public UsingInfo getUsingCurio(LivingEntity entity, String slotType, Item item) {
        return null;
    }

    @Override
    public Map<String, Map<Integer, Integer>> getUsingCurios(LivingEntity entity) {
        return Collections.emptyMap();
    }
}
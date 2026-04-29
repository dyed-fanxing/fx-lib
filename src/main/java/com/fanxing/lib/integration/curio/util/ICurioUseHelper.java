package com.fanxing.lib.integration.curio.util;

import net.minecraft.client.KeyMapping;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author dyed_fanxing
 * @date 2026/4/29 16:23
 */
public interface ICurioUseHelper {
    void handleInputKey(LivingEntity entity, KeyMapping key, Predicate<SlotInfo> filter, String... slotTypes);
    Map<String, List<Integer>> collectAndActivateSlots(LivingEntity entity, Predicate<SlotInfo> filter, String... slotTypes);
    void deactivateSlots(LivingEntity entity, Map<String, List<Integer>> slots);
    boolean isUsingCurio(LivingEntity entity, String slotType, Item item);
    UsingInfo getUsingCurio(LivingEntity entity, String slotType, Item item);
    Map<String, Map<Integer, Integer>> getUsingCurios(LivingEntity entity);
}
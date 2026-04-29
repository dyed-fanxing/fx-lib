package com.fanxing.lib.integration.curio.util;

import com.fanxing.lib.integration.curio.capability.UsableCurioCapability;
import com.fanxing.lib.integration.curio.network.packet.StopUseCurioPacket;
import com.fanxing.lib.integration.curio.network.packet.UseCurioPacket;
import com.fanxing.lib.integration.curio.register.AttachmentTypesFxLibItg;
import com.fanxing.lib.integration.curio.register.CapabilitiesFxLibItg;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class CurioUseHelper implements ICurioUseHelper {
    // 存储每个按键当前激活的槽位列表
    private final Map<KeyMapping, Map<String, List<Integer>>> ACTIVE_SLOTS = new HashMap<>();

    public void handleInputKey(LivingEntity entity, KeyMapping key, Predicate<SlotInfo> filter, String... slotTypes) {
        if (entity == null) return;
        // 按下时：收集并激活
        if (key.consumeClick()) {
            Map<String, List<Integer>> toActivate = collectAndActivateSlots(entity, filter, slotTypes);
            if (!toActivate.isEmpty()) {
                PacketDistributor.sendToServer(new UseCurioPacket(toActivate));
                ACTIVE_SLOTS.put(key, toActivate);
            }
        }

        // 释放时：使用存储的列表停止（只执行一次，不会每 tick 重复）
        if (!key.isDown() && ACTIVE_SLOTS.containsKey(key)) {
            Map<String, List<Integer>> toDeactivate = ACTIVE_SLOTS.remove(key);
            if (toDeactivate != null && !toDeactivate.isEmpty()) {
                deactivateSlots(entity, toDeactivate);
                PacketDistributor.sendToServer(new StopUseCurioPacket(toDeactivate));
            }
        }
    }

    // 收集符合条件的槽位（不激活）
    public Map<String, List<Integer>> collectAndActivateSlots(LivingEntity entity, Predicate<SlotInfo> filter, String... slotTypes) {
        Map<String, List<Integer>> result = new HashMap<>();
        CuriosApi.getCuriosInventory(entity).ifPresent(inv -> {
            for (String slotType : slotTypes) {
                inv.getStacksHandler(slotType).ifPresent(handler -> {
                    int slots = handler.getSlots();
                    for (int i = 0; i < slots; i++) {
                        ItemStack stack = handler.getStacks().getStackInSlot(i);
                        if (!stack.isEmpty()) {
                            UsableCurioCapability usableCurio = stack.getCapability(CapabilitiesFxLibItg.USABLE_CURIO);
                            if (usableCurio != null) {
                                SlotInfo info = new SlotInfo(slotType, i, stack);
                                if (filter.test(info)) {
                                    result.computeIfAbsent(slotType, k -> new ArrayList<>()).add(i);
                                    usableCurio.useCurio(entity, slotType, i);
                                }
                            }
                        }
                    }
                });
            }
        });
        return result;
    }

    // 停止指定槽位列表中的饰品
    public void deactivateSlots(LivingEntity entity, Map<String, List<Integer>> slots) {
        for (Map.Entry<String, List<Integer>> entry : slots.entrySet()) {
            String slotType = entry.getKey();
            for (int idx : entry.getValue()) {
                CuriosApi.getCuriosInventory(entity).flatMap(inv -> inv.getStacksHandler(slotType)).ifPresent(handler -> {
                    ItemStack stack = handler.getStacks().getStackInSlot(idx);
                    if (!stack.isEmpty()) {
                        UsableCurioCapability usable = stack.getCapability(CapabilitiesFxLibItg.USABLE_CURIO);
                        if (usable != null) {
                            usable.stopUsingCurio(entity, slotType, idx);
                        }
                    }
                });
            }
        }
    }

    // ========== 其他工具方法（保持不变）==========
    public boolean isUsingCurio(LivingEntity entity, String slotType, Item item) {
        var curios = CuriosApi.getCuriosInventory(entity).orElse(null);
        if (curios == null) return false;
        var iCurioStacksHandler = curios.getStacksHandler(slotType).orElse(null);
        if (iCurioStacksHandler == null) return false;
        IDynamicStackHandler slotTypeInventory = iCurioStacksHandler.getStacks();
        Map<Integer, Integer> usingSlotMap = getUsingCurios(entity).get(slotType);
        for (Map.Entry<Integer, Integer> usingSlot : usingSlotMap.entrySet()) {
            ItemStack stack = slotTypeInventory.getStackInSlot(usingSlot.getKey());
            if (stack.getItem() == item) return true;
        }
        return false;
    }


    public UsingInfo getUsingCurio(LivingEntity entity, String slotType, Item item) {
        var curios = CuriosApi.getCuriosInventory(entity).orElse(null);
        if (curios == null) return null;
        var iCurioStacksHandler = curios.getStacksHandler(slotType).orElse(null);
        if (iCurioStacksHandler == null) return null;
        IDynamicStackHandler slotTypeInventory = iCurioStacksHandler.getStacks();
        Map<Integer, Integer> usingSlotMap = getUsingCurios(entity).get(slotType);
        if (usingSlotMap == null) return null;
        for (Map.Entry<Integer, Integer> usingSlot : usingSlotMap.entrySet()) {
            ItemStack stack = slotTypeInventory.getStackInSlot(usingSlot.getKey());
            if (stack.getItem() == item) {
                return new UsingInfo(stack, usingSlot.getValue(), item.getUseDuration(stack, entity) - usingSlot.getValue());
            }
        }
        return null;
    }

    public Map<String, Map<Integer, Integer>> getUsingCurios(LivingEntity entity) {
        return entity.getData(AttachmentTypesFxLibItg.USING_CURIOS);
    }
}
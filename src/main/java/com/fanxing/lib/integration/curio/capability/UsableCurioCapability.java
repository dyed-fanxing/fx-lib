package com.fanxing.lib.integration.curio.capability;

import com.fanxing.lib.integration.curio.CurioSetup;
import com.fanxing.lib.integration.curio.util.CurioUseHelper;
import net.minecraft.world.entity.LivingEntity;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.Map;

/**
 * 饰品可激活能力。
 * 完全模拟原版物品的 use / startUsingItem / stopUsingItem 逻辑。
 */
public interface UsableCurioCapability extends ICurio {

    /**
     * 一次性使用或开始持续使用（类似原版 Item.use）。
     * 默认实现可调用 startUsingCurio()。
     *
     * @param entity    使用该饰品的实体（玩家或生物）
     * @param slotType  槽位类型标识符
     * @param slotIndex 槽位索引（同一槽位类型下的第几个格子）
     */
    void useCurio(LivingEntity entity, String slotType, int slotIndex);

    /**
     * 开始持续使用（类似原版 LivingEntity.startUsingItem 触发的逻辑）。
     * 持续物品应在此初始化状态，并在 curioTick 中执行效果。
     *
     * @param entity    使用该饰品的实体
     * @param slotType  槽位类型标识符
     * @param slotIndex 槽位索引
     */
    void startUsingCurio(LivingEntity entity, String slotType, int slotIndex);

    /**
     * 停止持续使用（类似原版 LivingEntity.stopUsingItem）。
     *
     * @param entity    使用该饰品的实体
     * @param slotType  槽位类型标识符
     * @param slotIndex 槽位索引
     */
    void stopUsingCurio(LivingEntity entity, String slotType, int slotIndex);

    /**
     * 当前是否正在持续使用中（相当于原版的 isUsingItem）。
     * 用于客户端判断是否应该停止。
     *
     * @param entity    使用该饰品的实体
     * @param slotType  槽位类型标识符
     * @param slotIndex 槽位索引
     * @return 是否正在使用中
     */
    boolean isUsingCurio(LivingEntity entity, String slotType, int slotIndex);

    /**
     * 获取已使用的刻数（用于渲染进度）。
     *
     * @param entity    使用该饰品的实体
     * @return 已使用的刻数（由onUseTick传入），如果未使用则返回0
     */
    default int getUsingTicks(LivingEntity entity,String slotType, int slotIndex) {
        return getUsingTicks(entity,getRemainingTicks(entity, slotType, slotIndex));
    }
    /**
     * 获取已使用的刻数（用于渲染进度）。
     *
     * @param entity    使用该饰品的实体
     * @return 已使用的刻数（由onUseTick传入），如果未使用则返回0
     */
    default int getUsingTicks(LivingEntity entity,int remaining) {
        if(remaining == -1) return 0;
        return getStack().getUseDuration(entity) - remaining;
    }

    default int getRemainingTicks(LivingEntity entity, String slotType, int slotIndex) {
        Map<Integer, Integer> slotMap = CurioSetup.HELPER.getUsingCurios(entity).get(slotType);
        if (slotMap == null) return -1;
        return slotMap.getOrDefault(slotIndex, -1);
    }

    default void setRemainingTicks(LivingEntity entity, String slotType, int slotIndex, int ticks) {
        Map<String, Map<Integer, Integer>> root = CurioSetup.HELPER.getUsingCurios(entity);
        Map<Integer, Integer> slotMap = root.computeIfAbsent(slotType, k -> new java.util.HashMap<>());
        if (ticks == -1) {
            slotMap.remove(slotIndex);
            if (slotMap.isEmpty()) root.remove(slotType);
        } else {
            slotMap.put(slotIndex, ticks);
        }
    }
}
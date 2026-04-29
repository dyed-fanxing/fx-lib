package com.fanxing.lib.integration.curio.capability;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;

/**
 * 可激活饰品的基类实现，将使用状态存储在玩家附件中。
 * 子类应覆盖钩子方法以实现具体逻辑。
 */
public class UsableCurio implements UsableCurioCapability {
    protected final ItemStack stack;

    public UsableCurio(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public void useCurio(LivingEntity entity, String slotType, int slotIndex) {
    }

    @Override
    public void startUsingCurio(LivingEntity entity, String slotType, int slotIndex) {
        if (getRemainingTicks(entity, slotType, slotIndex) == -1) {
            int duration = stack.getUseDuration(entity);
            setRemainingTicks(entity, slotType, slotIndex, duration);
        }
    }

    @Override
    public void stopUsingCurio(LivingEntity entity, String slotType, int slotIndex) {
        if (getRemainingTicks(entity, slotType, slotIndex) != -1) {
            setRemainingTicks(entity, slotType, slotIndex, -1);
            onStopUsing(entity);
        }
    }

    @Override
    public boolean isUsingCurio(LivingEntity entity, String slotType, int slotIndex) {
        return getRemainingTicks(entity, slotType, slotIndex) != -1;
    }



    // ========== ICurio 接口方法（必须实现，因为 Curios 强制使用 SlotContext）==========
    @Override
    public void curioTick(SlotContext ctx) {
        LivingEntity wearer = ctx.entity();
        int remaining = getRemainingTicks(wearer, ctx.identifier(), ctx.index());
        if (remaining == -1) return;
        remaining--;
        onUseTick(wearer.level(), wearer, stack, remaining);
        if (remaining <= 0) {
            stopUsingCurio(wearer, ctx.identifier(), ctx.index());
        } else {
            setRemainingTicks(wearer, ctx.identifier(), ctx.index(), remaining);
        }
    }


    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack) {
        stopUsingCurio(slotContext.entity(), slotContext.identifier(), slotContext.index());
    }

    /**
     * 停止使用时的回调（双端调用）
     */
    protected void onStopUsing(LivingEntity entity) {
    }

    /**
     * 每 tick 效果（双端调用），默认调用原物品的 onUseTick。
     * 子类可覆盖以自定义逻辑。
     *
     * @param level          世界
     * @param entity         使用者
     * @param stack          物品栈
     * @param remainingTicks 剩余刻数
     */
    protected void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingTicks) {
        stack.onUseTick(level,entity,remainingTicks);
    }



    @Override
    public ItemStack getStack() {
        return stack;
    }
}
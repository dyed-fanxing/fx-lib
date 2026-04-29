package com.fanxing.lib.integration.curio.util;

import net.minecraft.world.item.ItemStack;

/**
 * @author dyed_fanxing
 * @date 2026/4/29 16:25
 */
public record UsingInfo(ItemStack stack, int remainingTicks, int usingTicks) {
}

package com.fanxing.lib.integration.curio.register;

import com.fanxing.lib.FxLib;
import com.fanxing.lib.integration.curio.capability.UsableCurioCapability;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.ItemCapability;

/**
 * @author dyed_fanxing
 * @date 2026/4/28 14:55
 */
public class CapabilitiesFxLibItg {
    // 自定义能力：可主动使用的饰品
    public static final ItemCapability<UsableCurioCapability, Void> USABLE_CURIO = ItemCapability.createVoid(
            ResourceLocation.fromNamespaceAndPath(FxLib.MOD_ID, "usable_curio"), UsableCurioCapability.class
    );
}

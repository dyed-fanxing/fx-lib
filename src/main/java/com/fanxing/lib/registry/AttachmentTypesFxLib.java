package com.fanxing.lib.registry;

import com.fanxing.lib.FxLib;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * @author FanXing
 * @since 2025-09-13 22:07
 */
public class AttachmentTypesFxLib {
    public static DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, FxLib.MOD_ID);

    public static void register(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }

}

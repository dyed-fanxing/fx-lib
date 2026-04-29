package com.fanxing.lib.integration.curio.network.packet;

import com.fanxing.lib.FxLib;
import com.fanxing.lib.integration.curio.capability.UsableCurioCapability;
import com.fanxing.lib.integration.curio.register.CapabilitiesFxLibItg;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dyed_fanxing
 * @date 2026/4/27 23:24
 */
public record StopUseCurioPacket(Map<String, List<Integer>> slotsToActivate) implements CustomPacketPayload {
    public static final Type<StopUseCurioPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(FxLib.MOD_ID, "stop_use_curio_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, StopUseCurioPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(RegistryFriendlyByteBuf buf, StopUseCurioPacket packet) {
            buf.writeVarInt(packet.slotsToActivate.size());
            for (Map.Entry<String, List<Integer>> entry : packet.slotsToActivate.entrySet()) {
                buf.writeUtf(entry.getKey());
                List<Integer> list = entry.getValue();
                buf.writeVarInt(list.size());
                for (int val : list) {
                    buf.writeVarInt(val);
                }
            }
        }

        @Override
        public @NotNull StopUseCurioPacket decode(RegistryFriendlyByteBuf buf) {
            Map<String, List<Integer>> result = new HashMap<>();
            int mapSize = buf.readVarInt();
            for (int i = 0; i < mapSize; i++) {
                String key = buf.readUtf();
                int listSize = buf.readVarInt();
                List<Integer> list = new ArrayList<>(listSize);
                for (int j = 0; j < listSize; j++) {
                    list.add(buf.readVarInt());
                }
                result.put(key, list);
            }
            return new StopUseCurioPacket(result);
        }
    };
    private static final Logger log = LoggerFactory.getLogger(StopUseCurioPacket.class);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(StopUseCurioPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            for (Map.Entry<String, List<Integer>> entry : pkt.slotsToActivate().entrySet()) {
                String slotType = entry.getKey();
                List<Integer> indices = entry.getValue();
                CuriosApi.getCuriosInventory(player).flatMap(inv -> inv.getStacksHandler(slotType)).ifPresent(handler -> {
                    IDynamicStackHandler slotTypeInventory = handler.getStacks();
                    for (int idx : indices) {
                        if (idx >= 0 && idx < slotTypeInventory.getSlots()) {
                            ItemStack stack = slotTypeInventory.getStackInSlot(idx);
                            if (!stack.isEmpty()) {
                                UsableCurioCapability usableCurio = stack.getCapability(CapabilitiesFxLibItg.USABLE_CURIO);
                                if (usableCurio != null) {
                                    usableCurio.stopUsingCurio(player, slotType, idx);
                                }
                            }
                        }
                    }
                });
            }
        });
    }
}

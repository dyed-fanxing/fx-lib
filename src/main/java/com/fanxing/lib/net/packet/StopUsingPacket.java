package com.fanxing.lib.net.packet;

import com.fanxing.lib.FxLib;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class StopUsingPacket implements CustomPacketPayload {
    public static final Type<StopUsingPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(FxLib.MOD_ID, "stop_using_packet"));

    public static final StopUsingPacket INSTANCE = new StopUsingPacket();
    public static final StreamCodec<RegistryFriendlyByteBuf, StopUsingPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);
    private static final Logger log = LoggerFactory.getLogger(StopUsingPacket.class);

    private StopUsingPacket() {} // 私有构造，防止外部实例化
    public static void handle(StopUsingPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> context.player().stopUsingItem());
    }
    @Override
    public @NotNull Type<StopUsingPacket> type() {
        return TYPE;
    }
}
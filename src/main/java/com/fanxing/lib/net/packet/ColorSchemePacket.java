package com.fanxing.lib.net.packet;

import com.fanxing.lib.FxLib;
import com.fanxing.lib.registry.DataComponentsFxLib;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

// 保留你原来的名字 ColorSchemePacket
public record ColorSchemePacket(List<Integer> colors) implements CustomPacketPayload {
    public static final Type<ColorSchemePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(FxLib.MOD_ID, "color_scheme_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ColorSchemePacket> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.INT.apply(ByteBufCodecs.list()),ColorSchemePacket::colors,ColorSchemePacket::new);
    // 服务端处理逻辑写在这里
    public static void handle(ColorSchemePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            ItemStack stack = player.getMainHandItem();
            stack.set(DataComponentsFxLib.COLOR_SCHEME.get(), packet.colors());
        });
    }
    @Override
    public @NotNull Type<ColorSchemePacket> type() {
        return TYPE;
    }
}
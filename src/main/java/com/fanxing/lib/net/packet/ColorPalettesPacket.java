package com.fanxing.lib.net.packet;

import com.fanxing.lib.FxLib;
import com.fanxing.lib.item.compoent.ColorPalette;
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

import java.util.ArrayList;
import java.util.List;

public record ColorPalettesPacket(List<ColorPalette> palettes) implements CustomPacketPayload {
    public static final Type<ColorPalettesPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(FxLib.MOD_ID, "color_palettes_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ColorPalettesPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.collection(ArrayList::new, ColorPalette.STREAM_CODEC), // 复用你定义好的
                    ColorPalettesPacket::palettes,
                    ColorPalettesPacket::new
            );

    public static void handle(ColorPalettesPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            ItemStack stack = player.getMainHandItem();
            stack.set(DataComponentsFxLib.COLOR_PALETTES.get(), packet.palettes());
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
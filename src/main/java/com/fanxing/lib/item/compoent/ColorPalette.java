package com.fanxing.lib.item.compoent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record ColorPalette(Component label, List<Integer> colors) {
    // 定义 Codec
    public static final Codec<ColorPalette> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ComponentSerialization.CODEC.fieldOf("label").forGetter(ColorPalette::label),
            Codec.INT.listOf().fieldOf("colors").forGetter(ColorPalette::colors)
    ).apply(instance, ColorPalette::new));


    public static final StreamCodec<RegistryFriendlyByteBuf, ColorPalette> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull ColorPalette  decode(@NotNull RegistryFriendlyByteBuf buf) {
            Component label = ComponentSerialization.STREAM_CODEC.decode(buf);
            int size = buf.readInt();
            List<Integer> colors = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                colors.add(buf.readInt());
            }
            return new ColorPalette(label, colors);
        }

        @Override
        public void encode(@NotNull RegistryFriendlyByteBuf buf, ColorPalette palette) {
            ComponentSerialization.STREAM_CODEC.encode(buf, palette.label());
            List<Integer> colors = palette.colors();
            buf.writeInt(colors.size());
            for (int c : colors) {
                buf.writeInt(c);
            }
        }
    };

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        ColorPalette that = (ColorPalette) o;
        return Objects.equals(label, that.label) && Objects.equals(colors, that.colors);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(label);
        result = 31 * result + Objects.hashCode(colors);
        return result;
    }

    @Override
    public @NotNull String toString() {
        return "ColorPalette{" +
                "label='" + label + '\'' +
                ", colors=" + colors +
                '}';
    }
}

package com.fanxing.lib.item.compoent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record ColorPalette(String label, List<Integer> colors) {
    // 定义 Codec
    public static final Codec<ColorPalette> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("label").forGetter(ColorPalette::label),
            Codec.INT.listOf().fieldOf("colors").forGetter(ColorPalette::colors)
    ).apply(instance, ColorPalette::new));


    public static final StreamCodec<ByteBuf, ColorPalette> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull ColorPalette decode(@NotNull ByteBuf buf) {
            String label = ByteBufCodecs.STRING_UTF8.decode(buf);
            int size = buf.readInt();
            List<Integer> colors = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                colors.add(buf.readInt());
            }
            return new ColorPalette(label, colors);
        }

        @Override
        public void encode(ByteBuf buf, ColorPalette palette) {
            ByteBufCodecs.STRING_UTF8.encode(buf, palette.label());
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

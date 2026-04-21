package com.fanxing.lib.codec;//package com.fanxing.fx_lib.common.codec;
//
//import com.fanxing.fx_lib.utils.ColorUtils;
//import com.mojang.serialization.Codec;
//import io.netty.buffer.ByteBuf;
//import net.minecraft.network.codec.ByteBufCodecs;
//import net.minecraft.network.codec.StreamCodec;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.Arrays;
//import java.util.List;
//
///**
// * rgba 数组，支持任意行，列数必须是4列，分别存储 r g b a
// * [r,g,b,a]
// * ...
// */
//public record ColorScheme(int ...colors) {
//    public static final Codec<int[][]> CODEC = Codec.list(Codec.INT.listOf()).xmap(outerList -> {
//                if (outerList.isEmpty()) return new int[0][0];
//                int rows = outerList.size();
//                int[][] result = new int[rows][4];
//                for (int i = 0; i < rows; i++) {
//                    List<Integer> inner = outerList.get(i);
//                    if (inner.size() != 4) {
//                        throw new IllegalArgumentException("Each row must have exactly 4 elements");
//                    }
//                    result[i] = new int[]{inner.get(0), inner.get(1), inner.get(2), inner.get(3)};
//                }
//        return result;
//            }, array -> Arrays.stream(array).map(row -> Arrays.stream(row).boxed().toList()).toList());
//    public static final StreamCodec<ByteBuf, int[][]> STREAM_CODEC = new StreamCodec<>() {
//        @Override
//        public void encode(@NotNull ByteBuf buf, int[][] value) {
//            int rows = value.length;
//            ByteBufCodecs.VAR_INT.encode(buf, rows);
//            int[] colors = ColorUtils.rgbaArrayToInt(value);
//            for (int color : colors) {
//                ByteBufCodecs.VAR_INT.encode(buf, color);
//            }
//        }
//        @Override
//        public int[][] decode(@NotNull ByteBuf buf) {
//            int rows = ByteBufCodecs.VAR_INT.decode(buf);
//            int[] colors = new int[rows];
//            for (int i = 0; i < rows; i++) {
//                colors[i] = ByteBufCodecs.VAR_INT.decode(buf);
//            }
//            return ColorUtils.intToRgbaArray(colors);
//        }
//    };
//
//
//
//    public static final Codec<int[]> CODEC_1 = Codec.INT.listOf().xmap(list -> {
//                if (list.size() != 4) throw new IllegalArgumentException("Exactly 4 elements required");
//                return new int[]{list.get(0), list.get(1), list.get(2), list.get(3)};
//            }, arr -> Arrays.stream(arr).boxed().toList());
//    public static final StreamCodec<ByteBuf, int[]> STREAM_CODEC_1 = new StreamCodec<>() {
//        @Override
//        public void encode(@NotNull ByteBuf buf, int[] value) {
//            ByteBufCodecs.VAR_INT.encode(buf, ColorUtils.rgbaArrayToInt(value));
//        }
//        @Override
//        public int[] decode(@NotNull ByteBuf buf) {
//            return ColorUtils.intToRgbaArray(ByteBufCodecs.VAR_INT.decode(buf));
//        }
//    };
//
//
//
//
//
//
//    public static final Codec<int[][]> CODEC_2 = createCodec(2);
//    public static final StreamCodec<ByteBuf, int[][]> STREAM_CODEC_2 = createStreamCodec(2);
//
//    public static final Codec<int[][]> CODEC_3 = createCodec(3);
//    public static final StreamCodec<ByteBuf, int[][]> STREAM_CODEC_3 = createStreamCodec(3);
//
//    public static final Codec<int[][]> CODEC_4 = createCodec(4);
//    public static final StreamCodec<ByteBuf, int[][]> STREAM_CODEC_4 = createStreamCodec(4);
//
//    public static Codec<int[][]> createCodec(int length) {
//        return Codec.list(Codec.INT.listOf()).xmap(
//                outerList -> {
//                    if (outerList.isEmpty()) return new int[0][0];
//                    int[][] result = new int[length][4];
//                    for (int i = 0; i < length; i++) {
//                        List<Integer> inner = outerList.get(i);
//                        if (inner.size() != 4) throw new IllegalArgumentException("Each row must have exactly 4 elements");
//                        result[i] = new int[]{inner.get(0), inner.get(1), inner.get(2), inner.get(3)};
//                    }
//                    return result;
//                },
//                array -> Arrays.stream(array).map(row -> Arrays.stream(row).boxed().toList()).toList()
//        );
//    }
//    public static StreamCodec<ByteBuf, int[][]> createStreamCodec(int length) {
//        return new StreamCodec<>() {
//            @Override
//            public void encode(@NotNull ByteBuf buf, int[][] value) {
//                int[] colors = ColorUtils.rgbaArrayToInt(value);
//                for (int color : colors) {
//                    ByteBufCodecs.VAR_INT.encode(buf, color);
//                }
//            }
//
//            @Override
//            public int[][] decode(@NotNull ByteBuf buf) {
//                int[] colors = new int[length];
//                for (int i = 0; i < length; i++) {
//                    colors[i] = ByteBufCodecs.VAR_INT.decode(buf);
//                }
//                return ColorUtils.intToRgbaArray(colors);
//            }
//        };
//    }
//}
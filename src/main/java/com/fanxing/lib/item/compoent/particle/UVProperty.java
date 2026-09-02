package com.fanxing.lib.item.compoent.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Objects;

/**
 * 粒子纹理坐标数据。支持固定 UV、滚动 UV、序列帧动画。
 */
public sealed interface UVProperty permits UVProperty.Fixed, UVProperty.Scroll, UVProperty.Animated {
    String FIXED = "fixed";
    String SCROLL = "scroll";
    String ANIMATED = "animated";

    String mode();

    Codec<UVProperty> CODEC = Codec.STRING.dispatch(UVProperty::mode, mode -> switch (mode) {
        case FIXED -> Fixed.CODEC;
        case SCROLL -> Scroll.CODEC;
        case ANIMATED -> Animated.CODEC;
        default -> throw new IllegalArgumentException("Unknown UV mode: " + mode);
    });

    // 固定 UV
    final class Fixed implements UVProperty {
        public float u0, v0, u1, v1;

        public Fixed() {
        }

        public Fixed(float u0, float v0, float u1, float v1) {
            this.u0 = u0;
            this.v0 = v0;
            this.u1 = u1;
            this.v1 = v1;
        }

        public static final MapCodec<Fixed> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.FLOAT.fieldOf("u0").forGetter(a -> a.u0),
                        Codec.FLOAT.fieldOf("v0").forGetter(a -> a.v0),
                        Codec.FLOAT.fieldOf("u1").forGetter(f -> f.u1),
                        Codec.FLOAT.fieldOf("v1").forGetter(f -> f.v1)
                ).apply(instance, Fixed::new)
        );

        @Override
        public String mode() { return FIXED; }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Fixed fixed)) return false;
            return Objects.equals(u0, fixed.u0) && Objects.equals(v0, fixed.v0) &&
                    Objects.equals(u1, fixed.u1) && Objects.equals(v1, fixed.v1);
        }

        @Override
        public int hashCode() {
            return Objects.hash(u0, v0, u1, v1);
        }

        @Override
        public String toString() {
            return "Fixed{u0=" + u0 + ", v0=" + v0 + ", u1=" + u1 + ", v1=" + v1 + "}";
        }
    }

    // 滚动 UV
    final class Scroll implements UVProperty {
        public float u0, v0, uSize, vSize,uSpeed,vSpeed;

        public Scroll() {
        }

        public Scroll(float u0, float v0, float uSize, float vSize, float uSpeed, float vSpeed) {
            this.u0 = u0;
            this.v0 = v0;
            this.uSize = uSize;
            this.vSize = vSize;
            this.uSpeed = uSpeed;
            this.vSpeed = vSpeed;
        }

        public static final MapCodec<Scroll> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.FLOAT.fieldOf("u0").forGetter(a -> a.u0),
                        Codec.FLOAT.fieldOf("v0").forGetter(a -> a.v0),
                        Codec.FLOAT.fieldOf("uSize").forGetter(a -> a.uSize),
                        Codec.FLOAT.fieldOf("vSize").forGetter(a -> a.vSize),
                        Codec.FLOAT.fieldOf("uSpeed").forGetter(s -> s.uSpeed),
                        Codec.FLOAT.fieldOf("vSize").forGetter(s -> s.vSize)
                ).apply(instance, Scroll::new)
        );

        @Override
        public String mode() { return SCROLL; }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Scroll scroll)) return false;
            return Float.compare(u0, scroll.u0) == 0 && Float.compare(v0, scroll.v0) == 0 && Float.compare(uSize, scroll.uSize) == 0 && Float.compare(vSize, scroll.vSize) == 0 && Float.compare(uSpeed, scroll.uSpeed) == 0 && Float.compare(vSpeed, scroll.vSpeed) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(u0,v0,uSize,vSize,uSpeed,vSpeed);
        }

        @Override
        public String toString() {
            return "Scroll{" +
                    "u0=" + u0 +
                    ", v0=" + v0 +
                    ", uSize=" + uSize +
                    ", vSize=" + vSize +
                    ", uSpeed=" + uSpeed +
                    ", vSpeed=" + vSpeed +
                    '}';
        }
    }

    // 序列帧 UV
    final class Animated implements UVProperty {
        public float u0, v0, uSize, vSize;
        public int frameCountV, frameTicks;

        public Animated() {
            this.frameCountV = 1;
            this.frameTicks = 1;
        }

        public Animated(float u0, float v0, float uSize, float vSize,
                        int frameCountV, int frameTicks) {
            this.u0 = u0;
            this.v0 = v0;
            this.uSize = uSize;
            this.vSize = vSize;
            this.frameCountV = frameCountV;
            this.frameTicks = frameTicks;
        }

        public static final MapCodec<Animated> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.FLOAT.fieldOf("u0").forGetter(a -> a.u0),
                        Codec.FLOAT.fieldOf("v0").forGetter(a -> a.v0),
                        Codec.FLOAT.fieldOf("uSize").forGetter(a -> a.uSize),
                        Codec.FLOAT.fieldOf("vSize").forGetter(a -> a.vSize),
                        Codec.INT.fieldOf("frameCountV").forGetter(a -> a.frameCountV),
                        Codec.INT.fieldOf("frameTicks").forGetter(a -> a.frameTicks)
                ).apply(instance, Animated::new)
        );

        @Override
        public String mode() { return ANIMATED; }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Animated animated)) return false;
            return frameCountV == animated.frameCountV && frameTicks == animated.frameTicks &&
                    Objects.equals(u0, animated.u0) && Objects.equals(v0, animated.v0) &&
                    Objects.equals(uSize, animated.uSize) && Objects.equals(vSize, animated.vSize);
        }

        @Override
        public int hashCode() {
            return Objects.hash(u0, v0, uSize, vSize, frameCountV, frameTicks);
        }

        @Override
        public String toString() {
            return "Animated{u0=" + u0 + ", v0=" + v0 +
                    ", uSize=" + uSize + ", vSize=" + vSize +
                    ", frameCountV=" + frameCountV + ", frameTicks=" + frameTicks + "}";
        }
    }
}
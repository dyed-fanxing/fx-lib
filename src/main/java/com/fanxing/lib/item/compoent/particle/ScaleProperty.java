package com.fanxing.lib.item.compoent.particle;

import com.fanxing.lib.util.math.ease.EaseType;
import com.fanxing.lib.util.math.random.RandomType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Objects;

/**
 * 3D 缩放数据（X, Y, Z）。对于 2D 精灵，UI 会将 X 和 Y 映射为宽度/高度。
 * @since 2025/5/26 18:10
 * @author dyed_fanxing
 */
public sealed interface ScaleProperty permits ScaleProperty.Fixed, ScaleProperty.Acceleration, ScaleProperty.Ease {
    String FIXED = "fixed";
    String ACCELERATION = "acceleration";
    String EASE = "ease";

    String mode();

    Codec<ScaleProperty> CODEC = Codec.STRING.dispatch(ScaleProperty::mode, mode -> switch (mode) {
        case FIXED -> Fixed.CODEC;
        case ACCELERATION -> Acceleration.CODEC;
        case EASE -> Ease.CODEC;
        default -> throw new IllegalArgumentException("Unknown scale mode: " + mode);
    });

    final class Fixed implements ScaleProperty {
        public RandomType scaleX, scaleY, scaleZ;

        public Fixed() {
            this.scaleX = new RandomType.AvgAmpFloat();
            this.scaleY = new RandomType.AvgAmpFloat();
            this.scaleZ = new RandomType.AvgAmpFloat();
        }

        public Fixed(RandomType scaleX, RandomType scaleY, RandomType scaleZ) {
            this.scaleX = scaleX;
            this.scaleY = scaleY;
            this.scaleZ = scaleZ;
        }

        public static final MapCodec<Fixed> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        RandomType.CODEC.fieldOf("scaleX").forGetter(f -> f.scaleX),
                        RandomType.CODEC.fieldOf("scaleY").forGetter(f -> f.scaleY),
                        RandomType.CODEC.fieldOf("scaleZ").forGetter(f -> f.scaleZ)
                ).apply(instance, Fixed::new)
        );

        @Override
        public String mode() { return FIXED; }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Fixed fixed)) return false;
            return Objects.equals(scaleX, fixed.scaleX) && Objects.equals(scaleY, fixed.scaleY) && Objects.equals(scaleZ, fixed.scaleZ);
        }

        @Override
        public int hashCode() {
            return Objects.hash(scaleX, scaleY, scaleZ);
        }

        @Override
        public String toString() {
            return "Fixed{scaleX=" + scaleX + ", scaleY=" + scaleY + ", scaleZ=" + scaleZ + "}";
        }
    }

    final class Acceleration implements ScaleProperty {
        public RandomType startX, startY, startZ;
        public RandomType velX, velY, velZ;
        public RandomType accX, accY, accZ;

        public Acceleration() {
            this.startX = new RandomType.AvgAmpFloat();
            this.startY = new RandomType.AvgAmpFloat();
            this.startZ = new RandomType.AvgAmpFloat();
            this.velX = new RandomType.AvgAmpFloat();
            this.velY = new RandomType.AvgAmpFloat();
            this.velZ = new RandomType.AvgAmpFloat();
            this.accX = new RandomType.AvgAmpFloat();
            this.accY = new RandomType.AvgAmpFloat();
            this.accZ = new RandomType.AvgAmpFloat();
        }

        public Acceleration(RandomType startX, RandomType startY, RandomType startZ,
                            RandomType velX, RandomType velY, RandomType velZ,
                            RandomType accX, RandomType accY, RandomType accZ) {
            this.startX = startX;
            this.startY = startY;
            this.startZ = startZ;
            this.velX = velX;
            this.velY = velY;
            this.velZ = velZ;
            this.accX = accX;
            this.accY = accY;
            this.accZ = accZ;
        }

        public static final MapCodec<Acceleration> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        RandomType.CODEC.fieldOf("startX").forGetter(a -> a.startX),
                        RandomType.CODEC.fieldOf("startY").forGetter(a -> a.startY),
                        RandomType.CODEC.fieldOf("startZ").forGetter(a -> a.startZ),
                        RandomType.CODEC.fieldOf("velX").forGetter(a -> a.velX),
                        RandomType.CODEC.fieldOf("velY").forGetter(a -> a.velY),
                        RandomType.CODEC.fieldOf("velZ").forGetter(a -> a.velZ),
                        RandomType.CODEC.fieldOf("accX").forGetter(a -> a.accX),
                        RandomType.CODEC.fieldOf("accY").forGetter(a -> a.accY),
                        RandomType.CODEC.fieldOf("accZ").forGetter(a -> a.accZ)
                ).apply(instance, Acceleration::new)
        );

        @Override
        public String mode() { return ACCELERATION; }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Acceleration that)) return false;
            return Objects.equals(startX, that.startX) && Objects.equals(startY, that.startY) &&
                    Objects.equals(startZ, that.startZ) && Objects.equals(velX, that.velX) &&
                    Objects.equals(velY, that.velY) && Objects.equals(velZ, that.velZ) &&
                    Objects.equals(accX, that.accX) && Objects.equals(accY, that.accY) && Objects.equals(accZ, that.accZ);
        }

        @Override
        public int hashCode() {
            return Objects.hash(startX, startY, startZ, velX, velY, velZ, accX, accY, accZ);
        }

        @Override
        public String toString() {
            return "Acceleration{startX=" + startX + ", startY=" + startY + ", startZ=" + startZ +
                    ", velX=" + velX + ", velY=" + velY + ", velZ=" + velZ +
                    ", accX=" + accX + ", accY=" + accY + ", accZ=" + accZ + "}";
        }
    }

    final class Ease implements ScaleProperty {
        public RandomType startX, startY, startZ;
        public RandomType endX, endY, endZ;
        public String easeCurve;

        public Ease() {
            this.startX = new RandomType.AvgAmpFloat();
            this.startY = new RandomType.AvgAmpFloat();
            this.startZ = new RandomType.AvgAmpFloat();
            this.endX = new RandomType.AvgAmpFloat();
            this.endY = new RandomType.AvgAmpFloat();
            this.endZ = new RandomType.AvgAmpFloat();
            this.easeCurve = EaseType.EASES.keySet().iterator().next();
        }

        public Ease(RandomType startX, RandomType startY, RandomType startZ,
                    RandomType endX, RandomType endY, RandomType endZ,
                    String easeCurve) {
            this.startX = startX;
            this.startY = startY;
            this.startZ = startZ;
            this.endX = endX;
            this.endY = endY;
            this.endZ = endZ;
            this.easeCurve = easeCurve;
        }

        public static final MapCodec<Ease> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        RandomType.CODEC.fieldOf("startX").forGetter(e -> e.startX),
                        RandomType.CODEC.fieldOf("startY").forGetter(e -> e.startY),
                        RandomType.CODEC.fieldOf("startZ").forGetter(e -> e.startZ),
                        RandomType.CODEC.fieldOf("endX").forGetter(e -> e.endX),
                        RandomType.CODEC.fieldOf("endY").forGetter(e -> e.endY),
                        RandomType.CODEC.fieldOf("endZ").forGetter(e -> e.endZ),
                        Codec.STRING.fieldOf("easeCurve").forGetter(e -> e.easeCurve)
                ).apply(instance, Ease::new)
        );

        @Override
        public String mode() { return EASE; }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Ease ease)) return false;
            return Objects.equals(startX, ease.startX) && Objects.equals(startY, ease.startY) &&
                    Objects.equals(startZ, ease.startZ) && Objects.equals(endX, ease.endX) &&
                    Objects.equals(endY, ease.endY) && Objects.equals(endZ, ease.endZ) &&
                    Objects.equals(easeCurve, ease.easeCurve);
        }

        @Override
        public int hashCode() {
            return Objects.hash(startX, startY, startZ, endX, endY, endZ, easeCurve);
        }

        @Override
        public String toString() {
            return "Ease{startX=" + startX + ", startY=" + startY + ", startZ=" + startZ +
                    ", endX=" + endX + ", endY=" + endY + ", endZ=" + endZ +
                    ", easeCurve='" + easeCurve + "'}";
        }
    }
}
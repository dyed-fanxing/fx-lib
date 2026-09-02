package com.fanxing.lib.item.compoent.particle;

import com.fanxing.lib.util.math.ease.EaseType;
import com.fanxing.lib.util.math.random.RandomType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Objects;

/**
 * 粒子位置运动数据，支持多种运动模式：固定位置、加速度运动、缓动运动、相机偏移。
 * 使用密封接口 + 多态 Codec 实现序列化。
 * @author dyed_fanxing
 * @since 2026/5/25 11:55
 */
public sealed interface PositionProperty permits PositionProperty.Fixed, PositionProperty.Acceleration, PositionProperty.Ease, PositionProperty.CameraOffset {
    String FIXED = "fixed";
    String ACCELERATION = "acceleration";
    String EASE = "ease";
    String CAMERA_OFFSET = "camera_offset";
    String mode();

    // 多态 Codec
    Codec<PositionProperty> CODEC =  Codec.STRING.dispatch(PositionProperty::mode, mode -> switch (mode) {
        case "fixed" -> Fixed.CODEC;
        case "acceleration" -> Acceleration.CODEC;
        case "ease" -> Ease.CODEC;
        case "camera_offset" -> CameraOffset.CODEC;
        default -> throw new IllegalArgumentException("Unknown position mode: " + mode);
    });

    // 固定位置
    final class Fixed implements PositionProperty {
        public RandomType x, y, z;

        public Fixed() {
            this.x = new RandomType.AvgAmpFloat();
            this.y = new RandomType.AvgAmpFloat();
            this.z = new RandomType.AvgAmpFloat();
        }
        public Fixed(RandomType x, RandomType y, RandomType z) { this.x = x; this.y = y; this.z = z; }

        public static final MapCodec<Fixed> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        RandomType.CODEC.fieldOf("x").forGetter(f -> f.x),
                        RandomType.CODEC.fieldOf("y").forGetter(f -> f.y),
                        RandomType.CODEC.fieldOf("z").forGetter(f -> f.z)
                ).apply(instance, Fixed::new)
        );
        @Override public String mode() { return FIXED; }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Fixed fixed)) return false;

            return Objects.equals(x, fixed.x) && Objects.equals(y, fixed.y) && Objects.equals(z, fixed.z);
        }

        @Override
        public int hashCode() {
            int result = Objects.hashCode(x);
            result = 31 * result + Objects.hashCode(y);
            result = 31 * result + Objects.hashCode(z);
            return result;
        }

        @Override
        public String toString() {
            return "Fixed{" +
                    "x=" + x +
                    ", y=" + y +
                    ", z=" + z +
                    '}';
        }
    }

    // 加速度运动
    final class Acceleration implements PositionProperty {
        public RandomType x, y, z;
        public RandomType velX, velY, velZ;
        public RandomType accX, accY, accZ;

        public Acceleration() {
            this.x = new RandomType.AvgAmpFloat();
            this.y = new RandomType.AvgAmpFloat();
            this.z = new RandomType.AvgAmpFloat();
            this.velX = new RandomType.AvgAmpFloat();
            this.velY = new RandomType.AvgAmpFloat();
            this.velZ = new RandomType.AvgAmpFloat();
            this.accX = new RandomType.AvgAmpFloat();
            this.accY = new RandomType.AvgAmpFloat();
            this.accZ = new RandomType.AvgAmpFloat();
        }
        public Acceleration(RandomType x, RandomType y, RandomType z,
                            RandomType velX, RandomType velY, RandomType velZ,
                            RandomType accX, RandomType accY, RandomType accZ) {
            this.x = x; this.y = y; this.z = z;
            this.velX = velX; this.velY = velY; this.velZ = velZ;
            this.accX = accX; this.accY = accY; this.accZ = accZ;
        }

        public static final MapCodec<Acceleration> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        RandomType.CODEC.fieldOf("x").forGetter(a -> a.x),
                        RandomType.CODEC.fieldOf("y").forGetter(a -> a.y),
                        RandomType.CODEC.fieldOf("z").forGetter(a -> a.z),
                        RandomType.CODEC.fieldOf("velX").forGetter(a -> a.velX),
                        RandomType.CODEC.fieldOf("velY").forGetter(a -> a.velY),
                        RandomType.CODEC.fieldOf("velZ").forGetter(a -> a.velZ),
                        RandomType.CODEC.fieldOf("accX").forGetter(a -> a.accX),
                        RandomType.CODEC.fieldOf("accY").forGetter(a -> a.accY),
                        RandomType.CODEC.fieldOf("accZ").forGetter(a -> a.accZ)
                ).apply(instance, Acceleration::new)
        );
        @Override public String mode() { return ACCELERATION; }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Acceleration that)) return false;

            return Objects.equals(x, that.x) && Objects.equals(y, that.y) && Objects.equals(z, that.z) && Objects.equals(velX, that.velX) && Objects.equals(velY, that.velY) && Objects.equals(velZ, that.velZ) && Objects.equals(accX, that.accX) && Objects.equals(accY, that.accY) && Objects.equals(accZ, that.accZ);
        }

        @Override
        public int hashCode() {
            int result = Objects.hashCode(x);
            result = 31 * result + Objects.hashCode(y);
            result = 31 * result + Objects.hashCode(z);
            result = 31 * result + Objects.hashCode(velX);
            result = 31 * result + Objects.hashCode(velY);
            result = 31 * result + Objects.hashCode(velZ);
            result = 31 * result + Objects.hashCode(accX);
            result = 31 * result + Objects.hashCode(accY);
            result = 31 * result + Objects.hashCode(accZ);
            return result;
        }

        @Override
        public String toString() {
            return "Acceleration{" +
                    "x=" + x +
                    ", y=" + y +
                    ", z=" + z +
                    ", velX=" + velX +
                    ", velY=" + velY +
                    ", velZ=" + velZ +
                    ", accX=" + accX +
                    ", accY=" + accY +
                    ", accZ=" + accZ +
                    '}';
        }
    }

    // 缓动运动
    final class Ease implements PositionProperty {
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
            this.startX = startX; this.startY = startY; this.startZ = startZ;
            this.endX = endX; this.endY = endY; this.endZ = endZ;
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
        @Override public String mode() { return EASE; }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Ease ease)) return false;

            return Objects.equals(startX, ease.startX) && Objects.equals(startY, ease.startY) && Objects.equals(startZ, ease.startZ) && Objects.equals(endX, ease.endX) && Objects.equals(endY, ease.endY) && Objects.equals(endZ, ease.endZ) && Objects.equals(easeCurve, ease.easeCurve);
        }

        @Override
        public int hashCode() {
            int result = Objects.hashCode(startX);
            result = 31 * result + Objects.hashCode(startY);
            result = 31 * result + Objects.hashCode(startZ);
            result = 31 * result + Objects.hashCode(endX);
            result = 31 * result + Objects.hashCode(endY);
            result = 31 * result + Objects.hashCode(endZ);
            result = 31 * result + Objects.hashCode(easeCurve);
            return result;
        }

        @Override
        public String toString() {
            return "Ease{" +
                    "startX=" + startX +
                    ", startY=" + startY +
                    ", startZ=" + startZ +
                    ", endX=" + endX +
                    ", endY=" + endY +
                    ", endZ=" + endZ +
                    ", easeCurve='" + easeCurve + '\'' +
                    '}';
        }
    }

    // 相机偏移
    final class CameraOffset implements PositionProperty {
        public RandomType offsetX, offsetY, offsetZ;

        public CameraOffset() {
            offsetX = new RandomType.AvgAmpFloat();
            offsetY = new RandomType.AvgAmpFloat();
            offsetZ = new RandomType.AvgAmpFloat();
        }
        public CameraOffset(RandomType offsetX, RandomType offsetY, RandomType offsetZ) {
            this.offsetX = offsetX; this.offsetY = offsetY; this.offsetZ = offsetZ;
        }

        public static final MapCodec<CameraOffset> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        RandomType.CODEC.fieldOf("offsetX").forGetter(c -> c.offsetX),
                        RandomType.CODEC.fieldOf("offsetY").forGetter(c -> c.offsetY),
                        RandomType.CODEC.fieldOf("offsetZ").forGetter(c -> c.offsetZ)
                ).apply(instance, CameraOffset::new)
        );
        @Override public String mode() { return CAMERA_OFFSET; }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof CameraOffset that)) return false;

            return Objects.equals(offsetX, that.offsetX) && Objects.equals(offsetY, that.offsetY) && Objects.equals(offsetZ, that.offsetZ);
        }

        @Override
        public int hashCode() {
            int result = Objects.hashCode(offsetX);
            result = 31 * result + Objects.hashCode(offsetY);
            result = 31 * result + Objects.hashCode(offsetZ);
            return result;
        }

        @Override
        public String toString() {
            return "CameraOffset{" +
                    "offsetX=" + offsetX +
                    ", offsetY=" + offsetY +
                    ", offsetZ=" + offsetZ +
                    '}';
        }
    }

}
package com.fanxing.lib.item.compoent.particle;

import com.fanxing.lib.util.math.ease.EaseType;
import com.fanxing.lib.util.math.random.RandomType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Objects;

/**
 * 粒子旋转数据，支持固定角度、角速度旋转、缓动旋转、面向相机、仅绕 Y 轴旋转、面向运动方向。
 * @since 2025/5/26 18:10
 * @author dyed_fanxing
 */

public sealed interface RotationProperty permits RotationProperty.Fixed, RotationProperty.Acceleration, RotationProperty.Ease,
        RotationProperty.FacingCamera, RotationProperty.FixedY, RotationProperty.FacingMovement {

    String FIXED = "fixed";
    String ACCELERATION = "acceleration";
    String EASE = "ease";
    String FACING_CAMERA = "facing_camera";
    String FIXED_Y = "fixed_y";
    String FACING_MOVEMENT = "facing_movement";

    String mode();

    Codec<RotationProperty> CODEC = Codec.STRING.dispatch(RotationProperty::mode, mode -> switch (mode) {
        case FIXED -> Fixed.CODEC;
        case ACCELERATION -> Acceleration.CODEC;
        case EASE -> Ease.CODEC;
        case FACING_CAMERA -> FacingCamera.CODEC;
        case FIXED_Y -> FixedY.CODEC;
        case FACING_MOVEMENT -> FacingMovement.CODEC;
        default -> throw new IllegalArgumentException("Unknown rotation mode: " + mode);
    });

    // 固定角度（欧拉角，度数）
    final class Fixed implements RotationProperty {
        public RandomType angleX, angleY, angleZ;

        public Fixed() {
            this.angleX = new RandomType.AvgAmpFloat();
            this.angleY = new RandomType.AvgAmpFloat();
            this.angleZ = new RandomType.AvgAmpFloat();
        }

        public Fixed(RandomType angleX, RandomType angleY, RandomType angleZ) {
            this.angleX = angleX;
            this.angleY = angleY;
            this.angleZ = angleZ;
        }

        public static final MapCodec<Fixed> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        RandomType.CODEC.fieldOf("angleX").forGetter(f -> f.angleX),
                        RandomType.CODEC.fieldOf("angleY").forGetter(f -> f.angleY),
                        RandomType.CODEC.fieldOf("angleZ").forGetter(f -> f.angleZ)
                ).apply(instance, Fixed::new)
        );

        @Override
        public String mode() { return FIXED; }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Fixed fixed)) return false;
            return Objects.equals(angleX, fixed.angleX) && Objects.equals(angleY, fixed.angleY) && Objects.equals(angleZ, fixed.angleZ);
        }

        @Override
        public int hashCode() {
            return Objects.hash(angleX, angleY, angleZ);
        }

        @Override
        public String toString() {
            return "Fixed{angleX=" + angleX + ", angleY=" + angleY + ", angleZ=" + angleZ + "}";
        }
    }

    // 加速度运动（角度+角速度+角加速度）
    final class Acceleration implements RotationProperty {
        public RandomType angleX, angleY, angleZ;
        public RandomType velX, velY, velZ;
        public RandomType accX, accY, accZ;

        public Acceleration() {
            this.angleX = new RandomType.AvgAmpFloat();
            this.angleY = new RandomType.AvgAmpFloat();
            this.angleZ = new RandomType.AvgAmpFloat();
            this.velX = new RandomType.AvgAmpFloat();
            this.velY = new RandomType.AvgAmpFloat();
            this.velZ = new RandomType.AvgAmpFloat();
            this.accX = new RandomType.AvgAmpFloat();
            this.accY = new RandomType.AvgAmpFloat();
            this.accZ = new RandomType.AvgAmpFloat();
        }

        public Acceleration(RandomType angleX, RandomType angleY, RandomType angleZ,
                            RandomType velX, RandomType velY, RandomType velZ,
                            RandomType accX, RandomType accY, RandomType accZ) {
            this.angleX = angleX;
            this.angleY = angleY;
            this.angleZ = angleZ;
            this.velX = velX;
            this.velY = velY;
            this.velZ = velZ;
            this.accX = accX;
            this.accY = accY;
            this.accZ = accZ;
        }

        public static final MapCodec<Acceleration> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        RandomType.CODEC.fieldOf("angleX").forGetter(a -> a.angleX),
                        RandomType.CODEC.fieldOf("angleY").forGetter(a -> a.angleY),
                        RandomType.CODEC.fieldOf("angleZ").forGetter(a -> a.angleZ),
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
            return Objects.equals(angleX, that.angleX) && Objects.equals(angleY, that.angleY) && Objects.equals(angleZ, that.angleZ) &&
                    Objects.equals(velX, that.velX) && Objects.equals(velY, that.velY) && Objects.equals(velZ, that.velZ) &&
                    Objects.equals(accX, that.accX) && Objects.equals(accY, that.accY) && Objects.equals(accZ, that.accZ);
        }

        @Override
        public int hashCode() {
            return Objects.hash(angleX, angleY, angleZ, velX, velY, velZ, accX, accY, accZ);
        }

        @Override
        public String toString() {
            return "Acceleration{angleX=" + angleX + ", angleY=" + angleY + ", angleZ=" + angleZ +
                    ", velX=" + velX + ", velY=" + velY + ", velZ=" + velZ +
                    ", accX=" + accX + ", accY=" + accY + ", accZ=" + accZ + "}";
        }
    }

    // 缓动运动（起始角度+终止角度+缓动曲线）
    final class Ease implements RotationProperty {
        public RandomType startAngleX, startAngleY, startAngleZ;
        public RandomType endAngleX, endAngleY, endAngleZ;
        public String easeCurve;

        public Ease() {
            this.startAngleX = new RandomType.AvgAmpFloat();
            this.startAngleY = new RandomType.AvgAmpFloat();
            this.startAngleZ = new RandomType.AvgAmpFloat();
            this.endAngleX = new RandomType.AvgAmpFloat();
            this.endAngleY = new RandomType.AvgAmpFloat();
            this.endAngleZ = new RandomType.AvgAmpFloat();
            this.easeCurve = EaseType.EASES.keySet().iterator().next();
        }

        public Ease(RandomType startAngleX, RandomType startAngleY, RandomType startAngleZ,
                    RandomType endAngleX, RandomType endAngleY, RandomType endAngleZ,
                    String easeCurve) {
            this.startAngleX = startAngleX;
            this.startAngleY = startAngleY;
            this.startAngleZ = startAngleZ;
            this.endAngleX = endAngleX;
            this.endAngleY = endAngleY;
            this.endAngleZ = endAngleZ;
            this.easeCurve = easeCurve;
        }

        public static final MapCodec<Ease> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        RandomType.CODEC.fieldOf("startAngleX").forGetter(e -> e.startAngleX),
                        RandomType.CODEC.fieldOf("startAngleY").forGetter(e -> e.startAngleY),
                        RandomType.CODEC.fieldOf("startAngleZ").forGetter(e -> e.startAngleZ),
                        RandomType.CODEC.fieldOf("endAngleX").forGetter(e -> e.endAngleX),
                        RandomType.CODEC.fieldOf("endAngleY").forGetter(e -> e.endAngleY),
                        RandomType.CODEC.fieldOf("endAngleZ").forGetter(e -> e.endAngleZ),
                        Codec.STRING.fieldOf("easeCurve").forGetter(e -> e.easeCurve)
                ).apply(instance, Ease::new)
        );

        @Override
        public String mode() { return EASE; }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Ease ease)) return false;
            return Objects.equals(startAngleX, ease.startAngleX) && Objects.equals(startAngleY, ease.startAngleY) &&
                    Objects.equals(startAngleZ, ease.startAngleZ) && Objects.equals(endAngleX, ease.endAngleX) &&
                    Objects.equals(endAngleY, ease.endAngleY) && Objects.equals(endAngleZ, ease.endAngleZ) &&
                    Objects.equals(easeCurve, ease.easeCurve);
        }

        @Override
        public int hashCode() {
            return Objects.hash(startAngleX, startAngleY, startAngleZ, endAngleX, endAngleY, endAngleZ, easeCurve);
        }

        @Override
        public String toString() {
            return "Ease{startAngleX=" + startAngleX + ", startAngleY=" + startAngleY + ", startAngleZ=" + startAngleZ +
                    ", endAngleX=" + endAngleX + ", endAngleY=" + endAngleY + ", endAngleZ=" + endAngleZ +
                    ", easeCurve='" + easeCurve + "'}";
        }
    }

    // 始终面向相机（全轴）
    final class FacingCamera implements RotationProperty {
        public static final FacingCamera INSTANCE = new FacingCamera();

        private FacingCamera() {}

        public static final MapCodec<FacingCamera> CODEC = MapCodec.unit(INSTANCE);

        @Override
        public String mode() { return FACING_CAMERA; }
    }

    // 仅绕 Y 轴旋转
    final class FixedY implements RotationProperty {
        public RandomType angleY;

        public FixedY() {
            this.angleY = new RandomType.AvgAmpFloat();
        }

        public FixedY(RandomType angleY) {
            this.angleY = angleY;
        }

        public static final MapCodec<FixedY> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        RandomType.CODEC.fieldOf("angleY").forGetter(f -> f.angleY)
                ).apply(instance, FixedY::new)
        );

        @Override
        public String mode() { return FIXED_Y; }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof FixedY fixedY)) return false;
            return Objects.equals(angleY, fixedY.angleY);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(angleY);
        }

        @Override
        public String toString() {
            return "FixedY{angleY=" + angleY + "}";
        }
    }

    // 面向运动方向
    final class FacingMovement implements RotationProperty {
        public static final FacingMovement INSTANCE = new FacingMovement();

        private FacingMovement() {}

        public static final MapCodec<FacingMovement> CODEC = MapCodec.unit(INSTANCE);

        @Override
        public String mode() { return FACING_MOVEMENT; }
    }
}
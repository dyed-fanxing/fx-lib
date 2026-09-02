package com.fanxing.lib.util.math.random;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Objects;

/**
 * @author dyed_fanxing
 * @date 2026/5/13 14:28
 */
public interface RandomType {
    String AVG_AMP_FLOAT = "avg_amp_float";
    String RANGE_FLOAT = "range_float";
    String ABS_AVG_AMP_FLOAT = "abs_avg_amp_float";

    String AVG_AMP_INT = "avg_amp_int";
    String RANGE_INT = "range_int";
    String ABS_AVG_AMP_INT = "abs_avg_amp_int";

    float value();

    String type();

    Codec<RandomType> CODEC = Codec.STRING.dispatch(RandomType::type, type -> switch (type) {
        case AVG_AMP_FLOAT -> AvgAmpFloat.CODEC;
        case RANGE_FLOAT -> RangeFloat.CODEC;
        case ABS_AVG_AMP_FLOAT -> AbsAvgAmpFloat.CODEC;
        case AVG_AMP_INT -> AvgAmpInt.CODEC;
        case RANGE_INT -> RangeInt.CODEC;
        case ABS_AVG_AMP_INT -> AbsAvgAmpInt.CODEC;
        default -> throw new IllegalStateException("Unexpected value: " + type);
    });

    class AvgAmpFloat implements RandomType {
        public float avg;
        public float amp;

        public AvgAmpFloat() {
        }

        public AvgAmpFloat(float avg, float amp) {
            this.avg = avg;
            this.amp = amp;
        }

        @Override
        public float value() {
            return RandomUtils.avgAmp(avg, amp);
        }

        @Override
        public String type() {
            return AVG_AMP_FLOAT;
        }

        public float getAvg() {
            return avg;
        }

        public float getAmp() {
            return amp;
        }

        public static final MapCodec<AvgAmpFloat> CODEC = RecordCodecBuilder.mapCodec(inst ->
                inst.group(
                        Codec.FLOAT.fieldOf("avg").forGetter(AvgAmpFloat::getAvg),
                        Codec.FLOAT.fieldOf("amp").forGetter(AvgAmpFloat::getAvg)
                ).apply(inst, AvgAmpFloat::new)
        );

        @Override
        public final boolean equals(Object o) {
            if (!(o instanceof AvgAmpFloat that)) return false;
            return Float.compare(avg, that.avg) == 0 && Float.compare(amp, that.amp) == 0;
        }

        @Override
        public int hashCode() {
            int result = Float.hashCode(avg);
            result = 31 * result + Float.hashCode(amp);
            return result;
        }

        @Override
        public String toString() {
            return "AvgAmpFloat{avg=" + avg + ", amp=" + amp + "}";
        }
    }

    class RangeFloat implements RandomType {
        public float min;
        public float max;

        public RangeFloat() {
        }

        public RangeFloat(float min, float max) {
            this.min = min;
            this.max = max;
        }

        public float getMin() {
            return min;
        }

        public float getMax() {
            return max;
        }

        @Override
        public float value() {
            return RandomUtils.range(min, max);
        }

        @Override
        public String type() {
            return RANGE_FLOAT;
        }

        public static final MapCodec<RangeFloat> CODEC = RecordCodecBuilder.mapCodec(inst ->
                inst.group(
                        Codec.FLOAT.fieldOf("min").forGetter(RangeFloat::getMin),
                        Codec.FLOAT.fieldOf("max").forGetter(RangeFloat::getMax)
                ).apply(inst, RangeFloat::new)
        );

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RangeFloat that)) return false;
            return Float.compare(that.min, min) == 0 && Float.compare(that.max, max) == 0;
        }

        @Override
        public int hashCode() {
            int result = Float.hashCode(min);
            result = 31 * result + Float.hashCode(max);
            return result;
        }

        @Override
        public String toString() {
            return "RangeFloat{min=" + min + ", max=" + max + "}";
        }
    }

    class AbsAvgAmpFloat implements RandomType {
        public float absAvg;
        public float amp;

        public AbsAvgAmpFloat() {
        }

        public AbsAvgAmpFloat(float absAvg, float amp) {
            this.absAvg = absAvg;
            this.amp = amp;
        }

        public float getAbsAvg() {
            return absAvg;
        }

        public float getAmp() {
            return amp;
        }

        @Override
        public float value() {
            return RandomUtils.avgAbsAmp(absAvg, amp);
        }

        @Override
        public String type() {
            return ABS_AVG_AMP_FLOAT;
        }

        public static final MapCodec<AbsAvgAmpFloat> CODEC = RecordCodecBuilder.mapCodec(inst ->
                inst.group(
                        Codec.FLOAT.fieldOf("absAvg").forGetter(AbsAvgAmpFloat::getAbsAvg),
                        Codec.FLOAT.fieldOf("amp").forGetter(AbsAvgAmpFloat::getAmp)
                ).apply(inst, AbsAvgAmpFloat::new)
        );

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AbsAvgAmpFloat that)) return false;
            return Float.compare(that.absAvg, absAvg) == 0 && Float.compare(that.amp, amp) == 0;
        }

        @Override
        public int hashCode() {
            int result = Float.hashCode(absAvg);
            result = 31 * result + Float.hashCode(amp);
            return result;
        }

        @Override
        public String toString() {
            return "AbsAvgAmpFloat{absAvg=" + absAvg + ", amp=" + amp + "}";
        }
    }

    class AvgAmpInt implements RandomType {
        public int avg;
        public int amp;
        public static final MapCodec<AvgAmpInt> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Codec.INT.fieldOf("avg").forGetter(AvgAmpInt::getAvg),
                Codec.INT.fieldOf("amp").forGetter(AvgAmpInt::getAmp)
        ).apply(inst, AvgAmpInt::new));

        public AvgAmpInt() {}
        public AvgAmpInt(int avg, int amp) {
            this.avg = avg;
            this.amp = amp;
        }

        @Override
        public float value() {
            return RandomUtils.avgAmp(avg, amp);
        }

        @Override
        public String type() {
            return AVG_AMP_INT;
        }

        public int getAvg() {
            return avg;
        }

        public int getAmp() {
            return amp;
        }

        @Override
        public final boolean equals(Object o) {
            if (!(o instanceof AvgAmpInt avgAmpInt)) return false;
            return avg == avgAmpInt.avg && amp == avgAmpInt.amp;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(amp);
        }

        @Override
        public String toString() {
            return "AvgAmpInt{" +
                    "avg=" + avg +
                    ", amp=" + amp +
                    '}';
        }
    }

    // RangeInt 作为普通 class
    class RangeInt implements RandomType {
        public int min;
        public int max;

        public RangeInt() {}

        public RangeInt(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public static final MapCodec<RangeInt> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Codec.INT.fieldOf("min").forGetter(r -> r.min),
                Codec.INT.fieldOf("max").forGetter(r -> r.max)
        ).apply(inst, RangeInt::new));

        @Override
        public float value() {
            return RandomUtils.range(min, max);
        }

        @Override
        public String type() {
            return RANGE_INT;
        }
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof RangeInt that)) return false;
            return min == that.min && max == that.max;
        }

        @Override
        public int hashCode() {
            return Objects.hash(min, max);
        }

        @Override
        public String toString() {
            return "RangeInt{min=" + min + ", max=" + max + "}";
        }
    }

    class AbsAvgAmpInt implements RandomType {
        public int absAvg;
        public int amp;

        public AbsAvgAmpInt() {}

        public AbsAvgAmpInt(int absAvg, int amp) {
            this.absAvg = absAvg;
            this.amp = amp;
        }

        public static final MapCodec<AbsAvgAmpInt> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Codec.INT.fieldOf("absAvg").forGetter(a -> a.absAvg),
                Codec.INT.fieldOf("amp").forGetter(a -> a.amp)
        ).apply(inst, AbsAvgAmpInt::new));

        @Override
        public float value() {
            return RandomUtils.avgAbsAmp(absAvg, amp);
        }

        @Override
        public String type() {
            return ABS_AVG_AMP_INT;
        }
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof AbsAvgAmpInt that)) return false;
            return absAvg == that.absAvg && amp == that.amp;
        }

        @Override
        public int hashCode() {
            return Objects.hash(absAvg, amp);
        }

        @Override
        public String toString() {
            return "AbsAvgAmpInt{absAvg=" + absAvg + ", amp=" + amp + "}";
        }
    }
}
package com.fanxing.lib.util.math.ease;

/**
 * @author dyed_fanxing
 * @date 2026/5/13 16:24
 */
public class EaseValue {
    private float start;
    private float end;
    private String type;

    // 全参构造器（供 Codec 使用）
    public EaseValue(float start, float end, String type) {
        this.start = start;
        this.end = end;
        this.type = type;
    }

    // Getter（供 Codec 读取）
    public float getStart() { return start; }
    public float getEnd() { return end; }

    // Setter（供 UI 直接修改）
    public void setStart(float start) { this.start = start; }
    public void setEnd(float end) { this.end = end; }
    public void setType(String type) { this.type = type; }
//
//    public static final MapCodec<EasingMotion> MAP_CODEC = RecordCodecBuilder.mapCodec(inst ->
//            inst.group(
//                    Codec.FLOAT.fieldOf("start").forGetter(EasingMotion::start),
//                    Codec.FLOAT.fieldOf("end").forGetter(EasingMotion::end),
//                    Codec.STRING.fieldOf("curve").forGetter(EasingMotion::curveId)
//            ).apply(inst, EasingMotion::new)
//    );

    public String type() { return "easing"; }

//    public float getValue(float progress) {
//        FloatUnaryOperator curve = EasingType.EASING_MAP.get(curveId);
//        if (curve == null) curve = EasingType.LINEAR;
//        return start + (end - start) * curve.apply(progress);
//    }
}
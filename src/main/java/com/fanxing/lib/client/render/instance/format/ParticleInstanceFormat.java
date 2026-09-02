package com.fanxing.lib.client.render.instance.format;

import java.nio.ByteBuffer;

/**
 * 粒子实例化数据格式，定义了 20 个 float 的布局。
 * 单例模式，通过 INSTANCE 使用。
 */
public class ParticleInstanceFormat extends InstanceFormat {

    public static final ParticleInstanceFormat INSTANCE = new ParticleInstanceFormat();

    public static final int FLOATS_PER_PARTICLE = 20;

    // 偏移常量（可选，用于调试或手动写入）
    public static final int OFFSET_POS_X = 0;
    public static final int OFFSET_POS_Y = 1;
    public static final int OFFSET_POS_Z = 2;
    public static final int OFFSET_POS_PAD = 3;

    public static final int OFFSET_ROT_X = 4;
    public static final int OFFSET_ROT_Y = 5;
    public static final int OFFSET_ROT_Z = 6;
    public static final int OFFSET_ROT_W = 7;

    public static final int OFFSET_SCALE_X = 8;
    public static final int OFFSET_SCALE_Y = 9;
    public static final int OFFSET_SCALE_Z = 10;
    public static final int OFFSET_SCALE_PAD = 11;

    public static final int OFFSET_COLOR_R = 12;
    public static final int OFFSET_COLOR_G = 13;
    public static final int OFFSET_COLOR_B = 14;
    public static final int OFFSET_COLOR_A = 15;

    public static final int OFFSET_UV_U0 = 16;
    public static final int OFFSET_UV_V0 = 17;
    public static final int OFFSET_UV_U1 = 18;
    public static final int OFFSET_UV_V1 = 19;

    private ParticleInstanceFormat() {}

    @Override
    public int getFloatsPerInstance() {
        return FLOATS_PER_PARTICLE;
    }

    @Override
    public void writePosition(ByteBuffer buffer, float x, float y, float z) {
        buffer.putFloat(OFFSET_POS_X * 4, x);
        buffer.putFloat(OFFSET_POS_Y * 4, y);
        buffer.putFloat(OFFSET_POS_Z * 4, z);
        buffer.putFloat(OFFSET_POS_PAD * 4, 0f);
    }

    @Override
    public void writeRotation(ByteBuffer buffer, org.joml.Quaternionf q) {
        buffer.putFloat(OFFSET_ROT_X * 4, q.x);
        buffer.putFloat(OFFSET_ROT_Y * 4, q.y);
        buffer.putFloat(OFFSET_ROT_Z * 4, q.z);
        buffer.putFloat(OFFSET_ROT_W * 4, q.w);
    }

    @Override
    public void writeScale(ByteBuffer buffer, float sx, float sy, float sz) {
        buffer.putFloat(OFFSET_SCALE_X * 4, sx);
        buffer.putFloat(OFFSET_SCALE_Y * 4, sy);
        buffer.putFloat(OFFSET_SCALE_Z * 4, sz);
        buffer.putFloat(OFFSET_SCALE_PAD * 4, 0f);
    }

    @Override
    public void writeColor(ByteBuffer buffer, float r, float g, float b, float a) {
        buffer.putFloat(OFFSET_COLOR_R * 4, r);
        buffer.putFloat(OFFSET_COLOR_G * 4, g);
        buffer.putFloat(OFFSET_COLOR_B * 4, b);
        buffer.putFloat(OFFSET_COLOR_A * 4, a);
    }

    @Override
    public void writeUV(ByteBuffer buffer, float u0, float v0, float u1, float v1) {
        buffer.putFloat(OFFSET_UV_U0 * 4, u0);
        buffer.putFloat(OFFSET_UV_V0 * 4, v0);
        buffer.putFloat(OFFSET_UV_U1 * 4, u1);
        buffer.putFloat(OFFSET_UV_V1 * 4, v1);
    }
}
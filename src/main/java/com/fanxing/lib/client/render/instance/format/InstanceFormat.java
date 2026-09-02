package com.fanxing.lib.client.render.instance.format;

import java.nio.ByteBuffer;

/**
 * 实例化渲染的实例数据格式，相当于原版 {@code VertexFormat}。
 * 每个具体子类对应一种 SSBO 输入布局。
 * @author dyed_fanxing
 * @since 2026/7/6 00:00
 */
public abstract class InstanceFormat {
    public abstract int getFloatsPerInstance();

    // 写入方法，由子类实现，使用自己的偏移常量
    public abstract void writePosition(ByteBuffer buffer, float x, float y, float z);
    public abstract void writeRotation(ByteBuffer buffer, org.joml.Quaternionf q);
    public abstract void writeScale(ByteBuffer buffer, float sx, float sy, float sz);
    public abstract void writeColor(ByteBuffer buffer, float r, float g, float b, float a);
    public abstract void writeUV(ByteBuffer buffer, float u0, float v0, float u1, float v1);
}
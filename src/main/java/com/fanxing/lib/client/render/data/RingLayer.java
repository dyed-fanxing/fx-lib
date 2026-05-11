package com.fanxing.lib.client.render.data;

public class RingLayer {
    public float radius;    // 该层半径（距离圆心的距离）
    public float zOffset;   // 该层Z轴偏移（相对于中心点）
    public int color;       // 该层颜色 (ARGB)

    public RingLayer(float radius, float zOffset, int color) {
        this.radius = radius;
        this.zOffset = zOffset;
        this.color = color;
    }
}
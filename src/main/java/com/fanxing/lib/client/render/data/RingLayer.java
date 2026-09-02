package com.fanxing.lib.client.render.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.ArrayList;
import java.util.List;

public class RingLayer {
    public float radius;
    public float zOffset;
    public int color = 0XFFFFFFFF;

    public RingLayer(float radius, float zOffset) {
        this.radius = radius;
        this.zOffset = zOffset;
    }

    public RingLayer(float radius, float zOffset, int color) {
        this.radius = radius;
        this.zOffset = zOffset;
        this.color = color;
    }

    // ========== 便捷工厂方法 ==========

    /**
     * 创建指定层数的环形层，半径从 startRadius 到 endRadius 均匀分布，zOffset=0，颜色白色。
     * @param layers 层数（至少2，否则无效）
     * @param startRadius 起始半径
     * @param endRadius 结束半径
     * @return 环形层列表
     */
    public static List<RingLayer> createLayers(int layers, float startRadius, float endRadius) {
        return createLayers(layers, startRadius, endRadius, 0f);
    }

    /**
     * 创建指定层数的环形层，半径从 0 到 1 均匀分布，zOffset=0，颜色白色。
     */
    public static List<RingLayer> createLayers(int layers,float endRadius) {
        return createLayers(layers, 0f, endRadius, 0f);
    }

    /**
     * 完全自定义的创建方法
     */
    public static List<RingLayer> createLayers(int layers, float startRadius, float endRadius, float zOffset) {
        if (layers < 2) layers = 2;
        List<RingLayer> list = new ArrayList<>(layers);
        for (int i = 0; i < layers; i++) {
            float t = (float) i / (layers - 1);
            float radius = startRadius + t * (endRadius - startRadius);
            list.add(new RingLayer(radius, zOffset));
        }
        return list;
    }

    /**
     * 根据给定的半径数组创建环形层（zOffset=0，颜色白色）
     */
    public static List<RingLayer> createLayers(float... radii) {
        List<RingLayer> list = new ArrayList<>(radii.length);
        for (float r : radii) {
            list.add(new RingLayer(r, 0f));
        }
        return list;
    }

    /**
     * 根据给定的半径数组和统一的 zOffset、颜色创建
     */
    public static List<RingLayer> createLayers(float zOffset, int color, float... radii) {
        List<RingLayer> list = new ArrayList<>(radii.length);
        for (float r : radii) {
            list.add(new RingLayer(r, zOffset, color));
        }
        return list;
    }

    // ========== 原有方法 ==========
    public static final Codec<RingLayer> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("radius").forGetter(l -> l.radius),
                    Codec.FLOAT.fieldOf("zOffset").forGetter(l -> l.zOffset),
                    Codec.INT.fieldOf("color").forGetter(l -> l.color)
            ).apply(instance, RingLayer::new)
    );

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof RingLayer ringLayer)) return false;
        return Float.compare(radius, ringLayer.radius) == 0 && Float.compare(zOffset, ringLayer.zOffset) == 0 && color == ringLayer.color;
    }

    @Override
    public int hashCode() {
        int result = Float.hashCode(radius);
        result = 31 * result + Float.hashCode(zOffset);
        result = 31 * result + color;
        return result;
    }

    @Override
    public String toString() {
        return "RingLayer{" +
                "radius=" + radius +
                ", zOffset=" + zOffset +
                ", color=" + color +
                '}';
    }
}
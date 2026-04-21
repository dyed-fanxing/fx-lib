package com.fanxing.lib.client.render.component;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 闪电渲染工具类
 * 支持任意方向、圆柱约束、径向渐变颜色
 */
public class Lightning {

    // 默认参数值
    public static final float DEFAULT_DENSITY = 4.0f;
    public static final float DEFAULT_RADIAL_FACTOR = 5.0f;
    public static final float DEFAULT_WIDTH_FACTOR = 1.2f;

    // ======================= 公开 API（使用默认参数） =======================

    /**
     * 渲染一条从起点到终点的折线闪电（使用默认密度、径向因子、宽度因子）
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer, Vec3 start, Vec3 end, float radius, RandomSource random, int coreColor, int edgeColor) {
        Vec3 direction = end.subtract(start).normalize();
        float length = (float) start.distanceTo(end);
        if (length <= 0.01f) return;
        render(pose, consumer, start, direction, length,  radius,DEFAULT_DENSITY, DEFAULT_RADIAL_FACTOR, DEFAULT_WIDTH_FACTOR, random, coreColor, edgeColor);
    }

    /**
     * 渲染一条从起点沿指定方向、指定长度的折线闪电（使用默认密度、径向因子、宽度因子）
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer, Vec3 start, Vec3 direction, float length, float radius, RandomSource random, int coreColor, int edgeColor) {
        render(pose, consumer, start, direction, length, radius, DEFAULT_DENSITY, DEFAULT_RADIAL_FACTOR, DEFAULT_WIDTH_FACTOR, random, coreColor, edgeColor);
    }


    // ======================= 可配置参数的完整版方法 =======================

    /**
     * 渲染一条从起点沿指定方向、指定长度的折线闪电（全部参数可调）
     *
     * @param density      折线点密度。控制闪电折线段的数量，公式为 点数量 = max(4, length * density)。值越大，闪电越曲折、锯齿感越强；值越小，闪电越接近直线。
     * @param radialFactor 径向偏移系数。闪电折线点沿垂直于前进方向的平面随机偏移的最大距离，计算公式为 最大偏移 = radius * radialFactor。值越大，闪电“蓬松”的范围越宽，看起来更狂野。
     *                     更扭曲、毛刺多的闪电：增大 density（例如 6.0）和 radialFactor（例如 8.0）。
     * @param widthFactor  闪电条带宽度系数（相对半径的倍数）
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer, Vec3 start, Vec3 direction, float length, float radius, float density, float radialFactor, float widthFactor, RandomSource random, int coreColor, int edgeColor) {
        int pointCount = Math.max(4, (int) (length * density));
        List<Vec3> points = new ArrayList<>(pointCount);
        points.add(start);

        // 构建局部坐标系
        Vec3 up = new Vec3(0, 1, 0);
        Vec3 localX = direction.cross(up).normalize();
        if (localX.length() < 0.001) localX = new Vec3(1, 0, 0);
        Vec3 localZ = direction.cross(localX).normalize();

        for (int i = 0; i < pointCount - 2; i++) {
            double t = random.nextFloat();
            double along = t * length;
            double r = random.nextFloat() * radialFactor * radius;
            double angle = random.nextFloat() * 2 * Math.PI;
            double offX = Math.cos(angle) * r;
            double offZ = Math.sin(angle) * r;
            Vec3 localPos = new Vec3(offX, along, offZ);
            Vec3 worldPos = start.add(localX.scale(localPos.x)).add(direction.scale(localPos.y)).add(localZ.scale(localPos.z));
            points.add(worldPos);
        }
        points.add(start.add(direction.scale(length)));

        // 按沿方向排序确保带状连续
        points.sort(Comparator.comparingDouble(p -> p.subtract(start).dot(direction)));

        float lightningWidth = radius * widthFactor;
        renderStrip(pose, consumer, points, lightningWidth, coreColor, edgeColor);
    }















    /**
     * 从中心点向周围径向散射多条短闪电（全部参数可调）
     */
    public static void renderRadial(PoseStack.Pose pose, VertexConsumer consumer, Vec3 center, int count, float minLen, float maxLen, float radius, float density, float radialFactor, float widthFactor,
                                    RandomSource random, int coreColor, int edgeColor) {
        for (int i = 0; i < count; i++) {
            double theta = random.nextDouble() * 2 * Math.PI;
            double phi = Math.acos(2 * random.nextDouble() - 1);
            double dx = Math.sin(phi) * Math.cos(theta);
            double dy = Math.sin(phi) * Math.sin(theta);
            double dz = Math.cos(phi);
            Vec3 dir = new Vec3(dx, dy, dz).normalize();
            float len = minLen + random.nextFloat() * (maxLen - minLen);
            render(pose, consumer, center, dir, len, radius, density, radialFactor, widthFactor, random, coreColor, edgeColor);
        }
    }
    /**
     * 从中心点向周围径向散射多条短闪电（使用默认密度、径向因子、宽度因子）
     */
    public static void renderRadial(PoseStack.Pose pose, VertexConsumer consumer, Vec3 center, int count, float minLen, float maxLen, float radius, RandomSource random, int coreColor, int edgeColor) {
        renderRadial(pose, consumer, center, count, minLen, maxLen, radius, DEFAULT_DENSITY, DEFAULT_RADIAL_FACTOR, DEFAULT_WIDTH_FACTOR, random, coreColor, edgeColor);
    }




    // ======================= 核心带状渲染（私有） =======================
    private static void renderStrip(PoseStack.Pose pose, VertexConsumer consumer, List<Vec3> points, float width, int coreColor, int edgeColor) {
        if (points.size() < 2) return;
        float halfW = width / 2f;
        int n = points.size();

        Vec3[] lefts = new Vec3[n];
        Vec3[] centers = new Vec3[n];
        Vec3[] rights = new Vec3[n];
        for (int i = 0; i < n; i++) {
            Vec3 p = points.get(i);
            Vec3 dir;
            if (i == 0) dir = points.get(1).subtract(p).normalize();
            else if (i == n - 1) dir = p.subtract(points.get(i - 1)).normalize();
            else dir = points.get(i + 1).subtract(points.get(i - 1)).normalize();
            Vec3 up = new Vec3(0, 1, 0);
            Vec3 right = dir.cross(up).normalize();
            if (right.length() < 0.001) right = new Vec3(1, 0, 0);
            lefts[i] = p.add(right.scale(-halfW));
            centers[i] = p;
            rights[i] = p.add(right.scale(halfW));
        }

        // 正向：左半条带
        for (int i = 0; i < n; i++) {
            addVertex(consumer, pose, lefts[i], coreColor);
            addVertex(consumer, pose, centers[i], coreColor);
        }
        // 反向：右半条带
        for (int i = n - 1; i >= 0; i--) {
            addVertex(consumer, pose, rights[i], edgeColor);
            addVertex(consumer, pose, centers[i], edgeColor);
        }
    }

    private static void addVertex(VertexConsumer consumer, PoseStack.Pose pose, Vec3 point, int argb) {
        consumer.addVertex(pose.pose(), (float) point.x, (float) point.y, (float) point.z).setColor(argb).setUv(0, 0).setLight(LightTexture.FULL_BRIGHT).setNormal(pose, 0, 1, 0);
    }
}
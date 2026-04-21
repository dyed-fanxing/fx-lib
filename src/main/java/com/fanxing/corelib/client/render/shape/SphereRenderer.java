package com.fanxing.corelib.client.render.shape;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

public class SphereRenderer {

    /**
     * 球体：带UV缩放（使用 QUADS 模式，最优实现），指定球心 *********************************************************************************************
     *
     * @param pose     姿态
     * @param consumer 渲染器（QUADS 或 TRIANGLES）
     * @param center   球心坐标
     * @param radius   半径
     * @param segments 分段数
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer, Vector3f center, float radius, int segments,
                              int r, int g, int b, int a, int overlay, int light, float uScale, float vScale) {
        int latSegments = segments / 2;
        float deltaTheta = Mth.TWO_PI / segments;
        float deltaPhi = Mth.PI / latSegments;

        for (int i = 0; i < latSegments; i++) {
            float phi1 = i * deltaPhi - Mth.HALF_PI;
            float phi2 = (i + 1) * deltaPhi - Mth.HALF_PI;
            float sinPhi1 = Mth.sin(phi1), cosPhi1 = Mth.cos(phi1);
            float sinPhi2 = Mth.sin(phi2), cosPhi2 = Mth.cos(phi2);
            float r1 = radius * cosPhi1;
            float r2 = radius * cosPhi2;
            float v1 = (float) i / latSegments * vScale;
            float v2 = (float) (i + 1) / latSegments * vScale;

            for (int j = 0; j < segments; j++) {
                float theta1 = j * deltaTheta;
                float theta2 = (j + 1) * deltaTheta;
                float sinTheta1 = Mth.sin(theta1), cosTheta1 = Mth.cos(theta1);
                float sinTheta2 = Mth.sin(theta2), cosTheta2 = Mth.cos(theta2);
                float u1 = (float) j / segments * uScale;
                float u2 = (float) (j + 1) / segments * uScale;

                float x1 = r1 * cosTheta1, y1 = radius * sinPhi1, z1 = r1 * sinTheta1;
                float x2 = r2 * cosTheta1, y2 = radius * sinPhi2, z2 = r2 * sinTheta1;
                float x3 = r2 * cosTheta2, y3 = radius * sinPhi2, z3 = r2 * sinTheta2;
                float x4 = r1 * cosTheta2, y4 = radius * sinPhi1, z4 = r1 * sinTheta2;

                float nx1 = cosPhi1 * cosTheta1, nz1 = cosPhi1 * sinTheta1;
                float nx2 = cosPhi2 * cosTheta1, nz2 = cosPhi2 * sinTheta1;
                float nx3 = cosPhi2 * cosTheta2, nz3 = cosPhi2 * sinTheta2;
                float nx4 = cosPhi1 * cosTheta2, nz4 = cosPhi1 * sinTheta2;

                QuadRenderer.render(pose, consumer,
                        center.x() + x1, center.y() + y1, center.z() + z1,
                        center.x() + x2, center.y() + y2, center.z() + z2,
                        center.x() + x3, center.y() + y3, center.z() + z3,
                        center.x() + x4, center.y() + y4, center.z() + z4,
                        nx1, sinPhi1, nz1,
                        nx2, sinPhi2, nz2,
                        nx3, sinPhi2, nz3,
                        nx4, sinPhi1, nz4,
                        r, g, b, a, overlay, light,
                        u1, v1, u1, v2, u2, v2, u2, v1);
            }
        }
    }

    /**
     * 球体：带UV缩放（使用默认 UV 缩放 1.0），球心在原点
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer, Vector3f center, float radius, int segments,
                              int r, int g, int b, int a, int overlay, int light) {
        render(pose, consumer, center, radius, segments, r, g, b, a, overlay, light, 1f, 1f);
    }

    /**
     * 渲染部分球体（支持从底部向上或顶部向下渲染指定高度比例）
     *
     * @param pose        姿态矩阵
     * @param consumer    顶点消费者（QUADS 模式）
     * @param start       基准点（半球最低点或最高点位置，取决于 yRatio 符号）
     * @param radius      球体半径
     * @param yRatio      渲染高度比例，范围 [-1f, 1f]。绝对值表示渲染高度占整个球体高度的比例，
     *                    正值从底部向上渲染，负值从顶部向下渲染。
     * @param yOffset     垂直偏移（相对于基准点）
     * @param segments    水平分段数（经度分段）
     * @param latSegments 纬度分段数（整个球体的纬度段数，应等于 segments/2）
     * @param ringRadius  预计算的各纬度圈半径数组（长度 latSegments+1）
     * @param ringY       预计算的各纬度圈 Y 坐标数组（相对于球心，长度 latSegments+1）
     * @param deltaTheta  水平角度步长（弧度，通常为 2π/segments）
     * @param r,g,b,a     颜色
     * @param overlay     覆盖层
     * @param light       光照
     * @param uScale      UV 横向缩放
     * @param vScale      UV 纵向缩放
     * @param uOffset     U 方向滚动偏移
     * @param vOffset     V 方向滚动偏移
     */
    public static void renderHemisphere(PoseStack.Pose pose, VertexConsumer consumer, Vector3f start, float radius, float yRatio, float yOffset,
                                        int segments, int latSegments, float[] ringRadius, float[] ringY, float deltaTheta,
                                        int r, int g, int b, int a, int overlay, int light, float uScale, float vScale, float uOffset, float vOffset) {
        // 限制 yRatio 在 [-1, 1] 范围
        yRatio = Mth.clamp(yRatio, -1f, 1f);
        float absRatio = Math.abs(yRatio);
        if (absRatio < 1e-6f) return; // 无渲染区域

        int startIndex, endIndex;
        float vStart, vEnd;

        if (yRatio >= 0) {
            // 从底部向上渲染
            startIndex = 0;
            endIndex = (int) (absRatio * latSegments);
            vStart = 0f;
            vEnd = absRatio;
        } else {
            // 从顶部向下渲染
            startIndex = latSegments - (int) (absRatio * latSegments);
            endIndex = latSegments;
            vStart = 1f - absRatio;
            vEnd = 1f;
        }

        // 边界保护
        startIndex = Mth.clamp(startIndex, 0, latSegments);
        endIndex = Mth.clamp(endIndex, 0, latSegments);
        if (startIndex >= endIndex) return;

        float invSteps = 1f / (endIndex - startIndex);

        for (int i = startIndex; i < endIndex; i++) {
            float r1 = ringRadius[i];
            float r2 = ringRadius[i + 1];
            float y1 = ringY[i] + yOffset;
            float y2 = ringY[i + 1] + yOffset;
            float y3 = y2;
            float y4 = y1;

            // 计算当前带的 V 坐标（线性插值，叠加 vOffset）
            float t1 = (i - startIndex) * invSteps;
            float t2 = (i + 1 - startIndex) * invSteps;
            float v1 = (vStart + (vEnd - vStart) * t1) * vScale + vOffset;
            float v2 = (vStart + (vEnd - vStart) * t2) * vScale + vOffset;

            for (int j = 0; j < segments; j++) {
                float theta1 = j * deltaTheta;
                float theta2 = (j + 1) * deltaTheta;
                float cos1 = Mth.cos(theta1), sin1 = Mth.sin(theta1);
                float cos2 = Mth.cos(theta2), sin2 = Mth.sin(theta2);
                float u1 = ((float) j / segments) * uScale + uOffset;
                float u2 = ((float) (j + 1) / segments) * uScale + uOffset;

                // 四个顶点坐标（相对于球心）
                float x1 = r1 * cos1, z1 = r1 * sin1;
                float x2 = r2 * cos1, z2 = r2 * sin1;
                float x3 = r2 * cos2, z3 = r2 * sin2;
                float x4 = r1 * cos2, z4 = r1 * sin2;

                // 法线（基于原始球面，减去 yOffset 还原）
                float nx1 = cos1 * (r1 / radius);
                float ny1 = (y1 - yOffset) / radius;
                float nz1 = sin1 * (r1 / radius);
                float nx2 = cos1 * (r2 / radius);
                float ny2 = (y2 - yOffset) / radius;
                float nz2 = sin1 * (r2 / radius);
                float nx3 = cos2 * (r2 / radius);
                float ny3 = (y2 - yOffset) / radius;
                float nz3 = sin2 * (r2 / radius);
                float nx4 = cos2 * (r1 / radius);
                float ny4 = (y1 - yOffset) / radius;
                float nz4 = sin2 * (r1 / radius);

                QuadRenderer.render(pose, consumer,
                        start.x() + x1, start.y() + y1, start.z() + z1,
                        start.x() + x2, start.y() + y2, start.z() + z2,
                        start.x() + x3, start.y() + y3, start.z() + z3,
                        start.x() + x4, start.y() + y4, start.z() + z4,
                        nx1, ny1, nz1,
                        nx2, ny2, nz2,
                        nx3, ny3, nz3,
                        nx4, ny4, nz4,
                        r, g, b, a, overlay, light,
                        u1, v1, u1, v2, u2, v2, u2, v1);
            }
        }
    }

    /**
     * 渲染部分球体（无 uOffset 和 vOffset，两者默认为 0）
     */
    public static void renderHemisphere(PoseStack.Pose pose, VertexConsumer consumer, Vector3f start, float radius, float yRatio, float yOffset,
                                        int segments, int latSegments, float[] ringRadius, float[] ringY, float deltaTheta,
                                        int r, int g, int b, int a, int overlay, int light, float uScale, float vScale) {
        renderHemisphere(pose, consumer, start, radius, yRatio, yOffset, segments, latSegments, ringRadius, ringY, deltaTheta,
                r, g, b, a, overlay, light, uScale, vScale, 0f, 0f);
    }



    // ==================== ARGB 重载 ====================

    /**
     * 球体：带UV缩放，接受 ARGB 颜色（使用 QUADS 模式）
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer, Vector3f center, float radius, int segments,
                              int argb, int overlay, int light, float uScale, float vScale) {
        render(pose, consumer, center, radius, segments,
                FastColor.ARGB32.red(argb), FastColor.ARGB32.green(argb), FastColor.ARGB32.blue(argb), FastColor.ARGB32.alpha(argb), overlay, light, uScale, vScale);
    }

    /**
     * 球体：使用默认 UV 缩放，接受 ARGB 颜色
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer, Vector3f center, float radius, int segments,
                              int argb, int overlay, int light) {
        render(pose, consumer, center, radius, segments, argb, overlay, light, 1f, 1f);
    }

    /**
     * 渲染部分球体（支持高度比例、UV滚动），接受 ARGB 颜色
     */
    public static void renderHemisphere(PoseStack.Pose pose, VertexConsumer consumer, Vector3f start, float radius, float yRatio, float yOffset,
                                        int segments, int latSegments, float[] ringRadius, float[] ringY, float deltaTheta,
                                        int argb, int overlay, int light, float uScale, float vScale, float uOffset, float vOffset) {
        renderHemisphere(pose, consumer, start, radius, yRatio, yOffset, segments, latSegments, ringRadius, ringY, deltaTheta,
                FastColor.ARGB32.red(argb), FastColor.ARGB32.green(argb), FastColor.ARGB32.blue(argb), FastColor.ARGB32.alpha(argb), overlay, light, uScale, vScale, uOffset, vOffset);
    }

    /**
     * 渲染部分球体（无 uOffset/vOffset），接受 ARGB 颜色
     */
    public static void renderHemisphere(PoseStack.Pose pose, VertexConsumer consumer, Vector3f start, float radius, float yRatio, float yOffset,
                                        int segments, int latSegments, float[] ringRadius, float[] ringY, float deltaTheta,
                                        int argb, int overlay, int light, float uScale, float vScale) {
        renderHemisphere(pose, consumer, start, radius, yRatio, yOffset, segments, latSegments, ringRadius, ringY, deltaTheta,
                argb, overlay, light, uScale, vScale, 0f, 0f);
    }
}
package com.fanxing.lib.client.particle.mesh;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

/**
 * 球体粒子渲染工具（QUAD 模式，世界坐标，UV 边界传参）。
 * 无 PoseStack，无法线，专用于粒子系统。
 */
public final class SphereParticleRenderer {

    /**
     * 渲染完整球体
     *
     * @param consumer 顶点消费者
     * @param center   球心世界坐标
     * @param radius   半径
     * @param segments 分段数（经度分段数，纬度分段数为 segments/2）
     * @param r,g,b,a  颜色分量 (0-255)
     * @param light    光照值
     * @param uMin,vMin,uMax,vMax UV 边界
     */
    public static void render(VertexConsumer consumer, Vector3f center, float radius, int segments,
                              int r, int g, int b, int a, int light,
                              float uMin, float vMin, float uMax, float vMax) {
        int latSegments = Math.max(2, segments / 2);
        float deltaTheta = Mth.TWO_PI / segments;
        float deltaPhi = Mth.PI / latSegments;

        float cx = center.x(), cy = center.y(), cz = center.z();

        for (int i = 0; i < latSegments; i++) {
            float phi1 = i * deltaPhi - Mth.HALF_PI;
            float phi2 = (i + 1) * deltaPhi - Mth.HALF_PI;
            float sinPhi1 = Mth.sin(phi1), cosPhi1 = Mth.cos(phi1);
            float sinPhi2 = Mth.sin(phi2), cosPhi2 = Mth.cos(phi2);

            float r1 = radius * cosPhi1;
            float r2 = radius * cosPhi2;
            float y1 = radius * sinPhi1;
            float y2 = radius * sinPhi2;

            float v1 = vMin + (i / (float) latSegments) * (vMax - vMin);
            float v2 = vMin + ((i + 1) / (float) latSegments) * (vMax - vMin);

            for (int j = 0; j < segments; j++) {
                float theta1 = j * deltaTheta;
                float theta2 = (j + 1) * deltaTheta;
                float sinTheta1 = Mth.sin(theta1), cosTheta1 = Mth.cos(theta1);
                float sinTheta2 = Mth.sin(theta2), cosTheta2 = Mth.cos(theta2);

                float x1 = r1 * cosTheta1, z1 = r1 * sinTheta1;
                float x2 = r2 * cosTheta1, z2 = r2 * sinTheta1;
                float x3 = r2 * cosTheta2, z3 = r2 * sinTheta2;
                float x4 = r1 * cosTheta2, z4 = r1 * sinTheta2;

                float u1 = uMin + (j / (float) segments) * (uMax - uMin);
                float u2 = uMin + ((j + 1) / (float) segments) * (uMax - uMin);

                QuadParticleRenderer.render(consumer,
                        cx + x1, cy + y1, cz + z1,
                        cx + x2, cy + y1, cz + z2,
                        cx + x3, cy + y2, cz + z3,
                        cx + x4, cy + y2, cz + z4,
                        r, g, b, a, light,
                        u1, v1, u2, v1, u2, v2, u1, v2);
            }
        }
    }

    /**
     * 使用默认 UV 范围 [0,1] 渲染完整球体。
     *
     * @param consumer 顶点消费者
     * @param center   球心世界坐标
     * @param radius   半径
     * @param segments 分段数
     * @param r,g,b,a  颜色分量 (0-255)
     * @param light    光照值
     */
    public static void render(VertexConsumer consumer, Vector3f center, float radius, int segments,
                              int r, int g, int b, int a, int light) {
        render(consumer, center, radius, segments, r, g, b, a, light, 0f, 0f, 1f, 1f);
    }

    /**
     * 使用 ARGB 颜色渲染完整球体。
     *
     * @param consumer 顶点消费者
     * @param center   球心世界坐标
     * @param radius   半径
     * @param segments 分段数
     * @param argb     ARGB 颜色
     * @param light    光照值
     * @param uMin,vMin,uMax,vMax UV 边界
     */
    public static void render(VertexConsumer consumer, Vector3f center, float radius, int segments,
                              int argb, int light,
                              float uMin, float vMin, float uMax, float vMax) {
        render(consumer, center, radius, segments,
                FastColor.ARGB32.red(argb), FastColor.ARGB32.green(argb),
                FastColor.ARGB32.blue(argb), FastColor.ARGB32.alpha(argb),
                light, uMin, vMin, uMax, vMax);
    }

    /**
     * 使用 ARGB 颜色和默认 UV 范围 [0,1] 渲染完整球体。
     *
     * @param consumer 顶点消费者
     * @param center   球心世界坐标
     * @param radius   半径
     * @param segments 分段数
     * @param argb     ARGB 颜色
     * @param light    光照值
     */
    public static void render(VertexConsumer consumer, Vector3f center, float radius, int segments,
                              int argb, int light) {
        render(consumer, center, radius, segments, argb, light, 0f, 0f, 1f, 1f);
    }

    /**
     * 渲染半球（内部版本，接受预计算的环数据，供胶囊体等复用）
     *
     * @param consumer     顶点消费者
     * @param center       球心世界坐标
     * @param yRatio       渲染高度比例 [-1,1]，正值从底部向上，负值从顶部向下
     * @param yOffset      垂直偏移（相对于球心）
     * @param segments     经度分段数
     * @param latSegments  纬度分段数
     * @param ringRadius   各纬度圈半径数组（长度 latSegments+1）
     * @param ringY        各纬度圈 Y 坐标数组（相对于球心，长度 latSegments+1）
     * @param deltaTheta   水平角度步长（2π/segments）
     * @param r,g,b,a      颜色分量 (0-255)
     * @param light        光照值
     * @param uMin,vMin,uMax,vMax UV 边界
     */
    public static void renderHemisphere(VertexConsumer consumer, Vector3f center,
                                        float yRatio, float yOffset,
                                        int segments, int latSegments,
                                        float[] ringRadius, float[] ringY, float deltaTheta,
                                        int r, int g, int b, int a, int light,
                                        float uMin, float vMin, float uMax, float vMax) {
        yRatio = Mth.clamp(yRatio, -1f, 1f);
        float absRatio = Math.abs(yRatio);
        if (absRatio < 1e-6f) return;

        int startIndex, endIndex;
        float vStart, vEnd;

        if (yRatio >= 0) {
            startIndex = 0;
            endIndex = (int) (absRatio * latSegments);
            vStart = 0f;
            vEnd = absRatio;
        } else {
            startIndex = latSegments - (int) (absRatio * latSegments);
            endIndex = latSegments;
            vStart = 1f - absRatio;
            vEnd = 1f;
        }
        startIndex = Mth.clamp(startIndex, 0, latSegments);
        endIndex = Mth.clamp(endIndex, 0, latSegments);
        if (startIndex >= endIndex) return;

        float cx = center.x(), cy = center.y(), cz = center.z();
        float invSteps = 1f / (endIndex - startIndex);

        for (int i = startIndex; i < endIndex; i++) {
            float r1 = ringRadius[i];
            float r2 = ringRadius[i + 1];
            float y1 = ringY[i] + yOffset;
            float y2 = ringY[i + 1] + yOffset;

            float t1 = (i - startIndex) * invSteps;
            float t2 = (i + 1 - startIndex) * invSteps;
            float v1 = vMin + (vStart + (vEnd - vStart) * t1) * (vMax - vMin);
            float v2 = vMin + (vStart + (vEnd - vStart) * t2) * (vMax - vMin);

            for (int j = 0; j < segments; j++) {
                float theta1 = j * deltaTheta;
                float theta2 = (j + 1) * deltaTheta;
                float cos1 = Mth.cos(theta1), sin1 = Mth.sin(theta1);
                float cos2 = Mth.cos(theta2), sin2 = Mth.sin(theta2);

                float x1 = r1 * cos1, z1 = r1 * sin1;
                float x2 = r2 * cos1, z2 = r2 * sin1;
                float x3 = r2 * cos2, z3 = r2 * sin2;
                float x4 = r1 * cos2, z4 = r1 * sin2;

                float u1 = uMin + (j / (float) segments) * (uMax - uMin);
                float u2 = uMin + ((j + 1) / (float) segments) * (uMax - uMin);

                QuadParticleRenderer.render(consumer,
                        cx + x1, cy + y1, cz + z1,
                        cx + x2, cy + y1, cz + z2,
                        cx + x3, cy + y2, cz + z3,
                        cx + x4, cy + y2, cz + z4,
                        r, g, b, a, light,
                        u1, v1, u2, v1, u2, v2, u1, v2);
            }
        }
    }

    /**
     * 使用默认 UV 范围 [0,1] 渲染半球（内部版本）。
     *
     * @param consumer     顶点消费者
     * @param center       球心世界坐标
     * @param yRatio       渲染高度比例
     * @param yOffset      垂直偏移
     * @param segments     经度分段数
     * @param latSegments  纬度分段数
     * @param ringRadius   各纬度圈半径数组
     * @param ringY        各纬度圈 Y 坐标数组
     * @param deltaTheta   水平角度步长
     * @param r,g,b,a      颜色分量 (0-255)
     * @param light        光照值
     */
    public static void renderHemisphere(VertexConsumer consumer, Vector3f center,
                                        float yRatio, float yOffset,
                                        int segments, int latSegments,
                                        float[] ringRadius, float[] ringY, float deltaTheta,
                                        int r, int g, int b, int a, int light) {
        renderHemisphere(consumer, center, yRatio, yOffset,
                segments, latSegments, ringRadius, ringY, deltaTheta,
                r, g, b, a, light, 0f, 0f, 1f, 1f);
    }

    /**
     * 使用 ARGB 颜色渲染半球（内部版本）。
     *
     * @param consumer     顶点消费者
     * @param center       球心世界坐标
     * @param yRatio       渲染高度比例
     * @param yOffset      垂直偏移
     * @param segments     经度分段数
     * @param latSegments  纬度分段数
     * @param ringRadius   各纬度圈半径数组
     * @param ringY        各纬度圈 Y 坐标数组
     * @param deltaTheta   水平角度步长
     * @param argb         ARGB 颜色
     * @param light        光照值
     * @param uMin,vMin,uMax,vMax UV 边界
     */
    public static void renderHemisphere(VertexConsumer consumer, Vector3f center,
                                        float yRatio, float yOffset,
                                        int segments, int latSegments,
                                        float[] ringRadius, float[] ringY, float deltaTheta,
                                        int argb, int light,
                                        float uMin, float vMin, float uMax, float vMax) {
        renderHemisphere(consumer, center, yRatio, yOffset,
                segments, latSegments, ringRadius, ringY, deltaTheta,
                FastColor.ARGB32.red(argb), FastColor.ARGB32.green(argb),
                FastColor.ARGB32.blue(argb), FastColor.ARGB32.alpha(argb),
                light, uMin, vMin, uMax, vMax);
    }

    /**
     * 使用 ARGB 颜色和默认 UV 范围 [0,1] 渲染半球（内部版本）。
     *
     * @param consumer     顶点消费者
     * @param center       球心世界坐标
     * @param yRatio       渲染高度比例
     * @param yOffset      垂直偏移
     * @param segments     经度分段数
     * @param latSegments  纬度分段数
     * @param ringRadius   各纬度圈半径数组
     * @param ringY        各纬度圈 Y 坐标数组
     * @param deltaTheta   水平角度步长
     * @param argb         ARGB 颜色
     * @param light        光照值
     */
    public static void renderHemisphere(VertexConsumer consumer, Vector3f center,
                                        float yRatio, float yOffset,
                                        int segments, int latSegments,
                                        float[] ringRadius, float[] ringY, float deltaTheta,
                                        int argb, int light) {
        renderHemisphere(consumer, center, yRatio, yOffset,
                segments, latSegments, ringRadius, ringY, deltaTheta,
                argb, light, 0f, 0f, 1f, 1f);
    }

    /**
     * 渲染半球（外部版本，自动预计算环数据，方便粒子直接调用）
     *
     * @param consumer 顶点消费者
     * @param center   球心世界坐标
     * @param radius   半径
     * @param yRatio   渲染高度比例
     * @param yOffset  垂直偏移
     * @param segments 经度分段数
     * @param r,g,b,a  颜色分量 (0-255)
     * @param light    光照值
     * @param uMin,vMin,uMax,vMax UV 边界
     */
    public static void renderHemisphere(VertexConsumer consumer, Vector3f center, float radius,
                                        float yRatio, float yOffset, int segments,
                                        int r, int g, int b, int a, int light,
                                        float uMin, float vMin, float uMax, float vMax) {
        int latSegments = Math.max(2, segments / 2);
        float deltaPhi = Mth.PI / latSegments;
        float[] ringRadius = new float[latSegments + 1];
        float[] ringY = new float[latSegments + 1];
        for (int i = 0; i <= latSegments; i++) {
            float phi = i * deltaPhi - Mth.HALF_PI;
            float cosPhi = Mth.cos(phi);
            float sinPhi = Mth.sin(phi);
            ringRadius[i] = radius * cosPhi;
            ringY[i] = radius * sinPhi;
        }
        float deltaTheta = Mth.TWO_PI / segments;
        renderHemisphere(consumer, center, yRatio, yOffset,
                segments, latSegments, ringRadius, ringY, deltaTheta,
                r, g, b, a, light, uMin, vMin, uMax, vMax);
    }

    /**
     * 使用默认 UV 范围 [0,1] 渲染半球（外部版本）。
     *
     * @param consumer 顶点消费者
     * @param center   球心世界坐标
     * @param radius   半径
     * @param yRatio   渲染高度比例
     * @param yOffset  垂直偏移
     * @param segments 经度分段数
     * @param r,g,b,a  颜色分量 (0-255)
     * @param light    光照值
     */
    public static void renderHemisphere(VertexConsumer consumer, Vector3f center, float radius,
                                        float yRatio, float yOffset, int segments,
                                        int r, int g, int b, int a, int light) {
        renderHemisphere(consumer, center, radius, yRatio, yOffset, segments,
                r, g, b, a, light, 0f, 0f, 1f, 1f);
    }

    /**
     * 使用 ARGB 颜色渲染半球（外部版本）。
     *
     * @param consumer 顶点消费者
     * @param center   球心世界坐标
     * @param radius   半径
     * @param yRatio   渲染高度比例
     * @param yOffset  垂直偏移
     * @param segments 经度分段数
     * @param argb     ARGB 颜色
     * @param light    光照值
     * @param uMin,vMin,uMax,vMax UV 边界
     */
    public static void renderHemisphere(VertexConsumer consumer, Vector3f center, float radius,
                                        float yRatio, float yOffset, int segments,
                                        int argb, int light,
                                        float uMin, float vMin, float uMax, float vMax) {
        renderHemisphere(consumer, center, radius, yRatio, yOffset, segments,
                FastColor.ARGB32.red(argb), FastColor.ARGB32.green(argb),
                FastColor.ARGB32.blue(argb), FastColor.ARGB32.alpha(argb),
                light, uMin, vMin, uMax, vMax);
    }

    /**
     * 使用 ARGB 颜色和默认 UV 范围 [0,1] 渲染半球（外部版本）。
     *
     * @param consumer 顶点消费者
     * @param center   球心世界坐标
     * @param radius   半径
     * @param yRatio   渲染高度比例
     * @param yOffset  垂直偏移
     * @param segments 经度分段数
     * @param argb     ARGB 颜色
     * @param light    光照值
     */
    public static void renderHemisphere(VertexConsumer consumer, Vector3f center, float radius,
                                        float yRatio, float yOffset, int segments,
                                        int argb, int light) {
        renderHemisphere(consumer, center, radius, yRatio, yOffset, segments,
                argb, light, 0f, 0f, 1f, 1f);
    }

    private SphereParticleRenderer() {}
}
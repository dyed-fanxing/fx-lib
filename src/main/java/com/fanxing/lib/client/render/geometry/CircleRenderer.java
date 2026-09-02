package com.fanxing.lib.client.render.geometry;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class CircleRenderer {
    /**
     * 共法线、UV缩放，使用 TRIANGLE_FAN 模式，最优
     */
    public static void renderTriangleFan(PoseStack.Pose pose, VertexConsumer consumer, Vector3f center, float radius, int segments, Vector3f normal,
                                         int r, int g, int b, int a, int overlay, int light,float uScale, float vScale) {
        float delta = Mth.TWO_PI / segments;
        Matrix4f matrix = pose.pose();
        // 中心点
        consumer.addVertex(matrix, center.x(), center.y(), center.z())
                .setNormal(pose, normal.x(), normal.y(), normal.z())
                .setUv(0.5f * uScale, 0.5f * vScale)
                .setColor(r, g, b, a)
                .setOverlay(overlay)
                .setLight(light);
        // 圆周上的点
        for (int i = 0; i <= segments; i++) {
            float angle = i * delta;
            float cos = Mth.cos(angle);
            float sin = Mth.sin(angle);
            Vector3f point = new Vector3f(center).add(radius * cos, 0, radius * sin);
            float u = 0.5f + 0.5f * cos;
            float v = 0.5f + 0.5f * sin;

            consumer.addVertex(matrix, point.x(), point.y(), point.z())
                    .setNormal(pose, normal.x(), normal.y(), normal.z())
                    .setUv(u * uScale, v * vScale)
                    .setColor(r, g, b, a)
                    .setOverlay(overlay)
                    .setLight(light);
        }
    }

    /**
     * 共法线、无UV缩放
     */
    public static void renderTriangleFan(PoseStack.Pose pose, VertexConsumer consumer, Vector3f center, float radius, int segments, Vector3f normal,
                                         int r, int g, int b, int a, int overlay, int light) {
        renderTriangleFan(pose, consumer, center, radius, segments, normal, r, g, b, a, overlay, light, 1f, 1f);
    }



    /**
     * 共法线、UV缩放，用 TRIANGLES 模式绘制圆（每个三角形独立，适合多实例渲染）
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer, Vector3f center, float radius, int segments, Vector3f normal,
                              int r, int g, int b, int a, int overlay, int light,float uScale, float vScale) {
        float delta = Mth.TWO_PI / segments;
        for (int i = 0; i < segments; i++) {
            float angle1 = i * delta;
            float angle2 = (i + 1) * delta;
            float cos1 = Mth.cos(angle1), sin1 = Mth.sin(angle1);
            float cos2 = Mth.cos(angle2), sin2 = Mth.sin(angle2);
            Vector3f p1 = center;
            Vector3f p2 = new Vector3f(center).add(radius * cos1, 0, radius * sin1);
            Vector3f p3 = new Vector3f(center).add(radius * cos2, 0, radius * sin2);
            // 计算 UV 坐标
            float u1 = 0.5f * uScale, v1 = 0.5f * vScale;
            float u2 = (0.5f + 0.5f * cos1) * uScale, v2 = (0.5f + 0.5f * sin1) * vScale;
            float u3 = (0.5f + 0.5f * cos2) * uScale, v3 = (0.5f + 0.5f * sin2) * vScale;
            // 三角形: p1 → p2 → p3
            // 调用 TriangleRenderer 渲染三角形
            TriangleRenderer.render(pose, consumer, p1, p2, p3, normal,
                    r, g, b, a, overlay, light,
                    u1, v1, u2, v2, u3, v3);
        }
    }

    /**
     * 共法线、无UV缩放
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer, Vector3f center, float radius, int segments, Vector3f normal,
                              int r, int g, int b, int a, int overlay, int light) {
        render(pose, consumer, center, radius, segments, normal, r, g, b, a, overlay, light, 1f, 1f);
    }













    // ==================== ARGB 重载 ====================

    /**
     * 共法线、UV缩放，使用 TRIANGLE_FAN 模式，接受 ARGB 颜色
     */
    public static void renderTriangleFan(PoseStack.Pose pose, VertexConsumer consumer, Vector3f center, float radius, int segments, Vector3f normal,
                                         int argb, int overlay, int light, float uScale, float vScale) {
        renderTriangleFan(pose, consumer, center, radius, segments, normal,
                FastColor.ARGB32.red(argb), FastColor.ARGB32.green(argb), FastColor.ARGB32.blue(argb), FastColor.ARGB32.alpha(argb), overlay, light, uScale, vScale);
    }

    /**
     * 共法线、无UV缩放，使用 TRIANGLE_FAN 模式，接受 ARGB 颜色
     */
    public static void renderTriangleFan(PoseStack.Pose pose, VertexConsumer consumer, Vector3f center, float radius, int segments, Vector3f normal,
                                         int argb, int overlay, int light) {
        renderTriangleFan(pose, consumer, center, radius, segments, normal, argb, overlay, light, 1f, 1f);
    }

    /**
     * 共法线、UV缩放，用 TRIANGLES 模式绘制圆，接受 ARGB 颜色
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer, Vector3f center, float radius, int segments, Vector3f normal,
                              int argb, int overlay, int light, float uScale, float vScale) {
        render(pose, consumer, center, radius, segments, normal,
                FastColor.ARGB32.red(argb), FastColor.ARGB32.green(argb), FastColor.ARGB32.blue(argb), FastColor.ARGB32.alpha(argb), overlay, light, uScale, vScale);
    }

    /**
     * 共法线、无UV缩放，用 TRIANGLES 模式绘制圆，接受 ARGB 颜色
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer, Vector3f center, float radius, int segments, Vector3f normal, int argb, int overlay, int light) {
        render(pose, consumer, center, radius, segments, normal, argb, overlay, light, 1f, 1f);
    }
}
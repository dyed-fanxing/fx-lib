package com.fanxing.lib.client.render.shape;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class CylinderRenderer {
    /**
     * 圆柱体：竖向，UV缩放、偏移
     *
     * @param pose         姿态
     * @param sideConsumer 侧面渲染器（TRIANGLE_STRIP）
     * @param capConsumer  顶底面渲染器（TRIANGLE）
     * @param start        起点，底部中心点
     * @param radius       半径
     * @param height       高度
     * @param segments     分段数
     * @param uScale       UV横向缩放
     * @param vScale       UV纵向缩放
     * @param uOffset      U方向偏移
     * @param vOffset      V方向偏移
     */
    public static void render(PoseStack.Pose pose, VertexConsumer sideConsumer, VertexConsumer capConsumer, Vector3f start, float radius, float height, int segments,
                              int r, int g, int b, int a, int overlay, int light, float uScale, float vScale, float uOffset, float vOffset) {
        renderSide(pose, sideConsumer, start, radius, height, segments, r, g, b, a, overlay, light, uScale, vScale, uOffset, vOffset);
        // 底面圆盘，法线向下
        CircleRenderer.render(pose, capConsumer, start, radius, segments, new Vector3f(0, -1, 0),
                r, g, b, a, overlay, light, uScale, vScale);
        // 顶面圆盘，法线向上
        Vector3f topCenter = new Vector3f(start).add(0, height, 0);
        CircleRenderer.render(pose, capConsumer, topCenter, radius, segments, new Vector3f(0, 1, 0),
                r, g, b, a, overlay, light, uScale, vScale);
    }

    /**
     * 圆柱体：UV缩放（无偏移）
     */
    public static void render(PoseStack.Pose pose, VertexConsumer sideConsumer, VertexConsumer capConsumer, Vector3f start, float radius, float height, int segments,
                              int r, int g, int b, int a, int overlay, int light, float uScale, float vScale) {
        render(pose, sideConsumer, capConsumer, start, radius, height, segments, r, g, b, a, overlay, light, uScale, vScale, 0f, 0f);
    }

    /**
     * 圆柱体：默认 UV 缩放和偏移
     */
    public static void render(PoseStack.Pose pose, VertexConsumer sideConsumer, VertexConsumer capConsumer, Vector3f start, float radius, float height, int segments,
                              int r, int g, int b, int a, int overlay, int light) {
        render(pose, sideConsumer, capConsumer, start, radius, height, segments, r, g, b, a, overlay, light, 1f, 1f, 0f, 0f);
    }

    /**
     * 圆柱侧面：竖向，UV缩放、偏移
     */
    public static void renderSide(PoseStack.Pose pose, VertexConsumer consumer, Vector3f start, float radius, float height, int segments,
                                  int r, int g, int b, int a, int overlay, int light, float uScale, float vScale, float uOffset, float vOffset) {
        float step = Mth.TWO_PI / segments;
        Matrix4f matrix = pose.pose();
        float vBottomBase = 0f;
        for (int i = 0; i <= segments; i++) {
            float theta = i * step;
            float cos = Mth.cos(theta);
            float sin = Mth.sin(theta);
            float u = (float) i / segments * uScale + uOffset;
            // 底部顶点
            float vBottom = vBottomBase + vOffset;
            consumer.addVertex(matrix, start.x() + radius * cos, start.y(), start.z() + radius * sin)
                    .setNormal(pose, cos, 0, sin)
                    .setUv(u, vBottom)
                    .setColor(r, g, b, a)
                    .setOverlay(overlay)
                    .setLight(light);
            // 顶部顶点
            float vTop = vScale + vOffset;
            consumer.addVertex(matrix, start.x() + radius * cos, start.y() + height, start.z() + radius * sin)
                    .setNormal(pose, cos, 0, sin)
                    .setUv(u, vTop)
                    .setColor(r, g, b, a)
                    .setOverlay(overlay)
                    .setLight(light);
        }
    }

    /**
     * 圆柱侧面：UV缩放（无偏移）
     */
    public static void renderSide(PoseStack.Pose pose, VertexConsumer consumer, Vector3f start, float radius, float height, int segments,
                                  int r, int g, int b, int a, int overlay, int light, float uScale, float vScale) {
        renderSide(pose, consumer, start, radius, height, segments, r, g, b, a, overlay, light, uScale, vScale, 0f, 0f);
    }

    /**
     * 圆柱侧面：默认 UV 缩放和偏移
     */
    public static void renderSide(PoseStack.Pose pose, VertexConsumer consumer, Vector3f start, float radius, float height, int segments,
                                  int r, int g, int b, int a, int overlay, int light) {
        renderSide(pose, consumer, start, radius, height, segments, r, g, b, a, overlay, light, 1f, 1f, 0f, 0f);
    }

    /**
     * 圆柱体轮廓（线框），指定底部中心点
     */
    public static void renderOutline(PoseStack.Pose pose, VertexConsumer consumer, Vector3f start, float radius, float height, int segments,
                                     int r, int g, int b, int a) {
        float step = Mth.TWO_PI / segments;
        for (int i = 0; i < segments; i++) {
            float theta1 = i * step;
            float theta2 = (i + 1) * step;
            float cos1 = Mth.cos(theta1), sin1 = Mth.sin(theta1);
            float cos2 = Mth.cos(theta2), sin2 = Mth.sin(theta2);
            Vector3f up1 = new Vector3f(radius * cos1, 0, radius * sin1).add(start);
            Vector3f up2 = new Vector3f(radius * cos2, 0, radius * sin2).add(start);
            Vector3f down1 = new Vector3f(radius * cos1, height, radius * sin1).add(start);
            Vector3f down2 = new Vector3f(radius * cos2, height, radius * sin2).add(start);
            LineRenderer.render(pose, consumer, up1, up2, r, g, b, a);
            LineRenderer.render(pose, consumer, up1, down1, r, g, b, a);
            LineRenderer.render(pose, consumer, down1, down2, r, g, b, a);
        }
    }




    // ==================== ARGB 重载 ====================

    public static void render(PoseStack.Pose pose, VertexConsumer sideConsumer, VertexConsumer capConsumer,
                              Vector3f start, float radius, float height, int segments,
                              int argb, int overlay, int light,
                              float uScale, float vScale, float uOffset, float vOffset) {
        render(pose, sideConsumer, capConsumer, start, radius, height, segments,
                FastColor.ARGB32.red(argb), FastColor.ARGB32.green(argb), FastColor.ARGB32.blue(argb), FastColor.ARGB32.alpha(argb), overlay, light, uScale, vScale, uOffset, vOffset);
    }

    public static void render(PoseStack.Pose pose, VertexConsumer sideConsumer, VertexConsumer capConsumer,
                              Vector3f start, float radius, float height, int segments,
                              int argb, int overlay, int light, float uScale, float vScale) {
        render(pose, sideConsumer, capConsumer, start, radius, height, segments, argb, overlay, light, uScale, vScale, 0f, 0f);
    }

    public static void render(PoseStack.Pose pose, VertexConsumer sideConsumer, VertexConsumer capConsumer,
                              Vector3f start, float radius, float height, int segments,
                              int argb, int overlay, int light) {
        render(pose, sideConsumer, capConsumer, start, radius, height, segments, argb, overlay, light, 1f, 1f, 0f, 0f);
    }

    public static void renderSide(PoseStack.Pose pose, VertexConsumer consumer, Vector3f start,
                                  float radius, float height, int segments,
                                  int argb, int overlay, int light,
                                  float uScale, float vScale, float uOffset, float vOffset) {
        renderSide(pose, consumer, start, radius, height, segments,
                FastColor.ARGB32.red(argb), FastColor.ARGB32.green(argb), FastColor.ARGB32.blue(argb), FastColor.ARGB32.alpha(argb), overlay, light, uScale, vScale, uOffset, vOffset);
    }

    public static void renderSide(PoseStack.Pose pose, VertexConsumer consumer, Vector3f start,
                                  float radius, float height, int segments,
                                  int argb, int overlay, int light, float uScale, float vScale) {
        renderSide(pose, consumer, start, radius, height, segments, argb, overlay, light, uScale, vScale, 0f, 0f);
    }

    public static void renderSide(PoseStack.Pose pose, VertexConsumer consumer, Vector3f start,
                                  float radius, float height, int segments,
                                  int argb, int overlay, int light) {
        renderSide(pose, consumer, start, radius, height, segments, argb, overlay, light, 1f, 1f, 0f, 0f);
    }

    public static void renderOutline(PoseStack.Pose pose, VertexConsumer consumer, Vector3f start,
                                     float radius, float height, int segments,
                                     int argb) {
        renderOutline(pose, consumer, start, radius, height, segments,
                FastColor.ARGB32.red(argb), FastColor.ARGB32.green(argb), FastColor.ARGB32.blue(argb), FastColor.ARGB32.alpha(argb));
    }
}
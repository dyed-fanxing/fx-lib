package com.fanxing.lib.client.render.shape;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.FastColor;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class QuadRenderer {
    /**
     * 独立法线、独立UV
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer,
                              float x1, float y1, float z1,
                              float x2, float y2, float z2,
                              float x3, float y3, float z3,
                              float x4, float y4, float z4,
                              float nx1, float ny1, float nz1,
                              float nx2, float ny2, float nz2,
                              float nx3, float ny3, float nz3,
                              float nx4, float ny4, float nz4,
                              int r, int g, int b, int a, int overlay, int light, float u1, float v1, float u2, float v2, float u3, float v3, float u4, float v4) {
        Matrix4f matrix = pose.pose();
        consumer.addVertex(matrix, x1, y1, z1).setNormal(pose, nx1, ny1, nz1).setUv(u1, v1).setColor(r, g, b, a).setOverlay(overlay).setLight(light);
        consumer.addVertex(matrix, x2, y2, z2).setNormal(pose, nx2, ny2, nz2).setUv(u2, v2).setColor(r, g, b, a).setOverlay(overlay).setLight(light);
        consumer.addVertex(matrix, x3, y3, z3).setNormal(pose, nx3, ny3, nz3).setUv(u3, v3).setColor(r, g, b, a).setOverlay(overlay).setLight(light);
        consumer.addVertex(matrix, x4, y4, z4).setNormal(pose, nx4, ny4, nz4).setUv(u4, v4).setColor(r, g, b, a).setOverlay(overlay).setLight(light);
    }

    /**
     * 共法线、独立UV
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer,
                              float x1, float y1, float z1,
                              float x2, float y2, float z2,
                              float x3, float y3, float z3,
                              float x4, float y4, float z4,
                              float nx, float ny, float nz,
                              int r, int g, int b, int a, int overlay, int light, float u1, float v1, float u2, float v2, float u3, float v3, float u4, float v4) {
        render(pose, consumer, x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4, nx, ny, nz, nx, ny, nz, nx, ny, nz, nx, ny, nz, r, g, b, a, overlay, light, u1, v1, u2, v2, u3, v3, u4, v4);
    }


    /**
     * 共法线、UV 范围
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer, Vector3f p1, Vector3f p2, Vector3f p3, Vector3f p4, float normalX, float normalY, float normalZ,
                              int r, int g, int b, int a, int overlay, int light, float uMin, float vMin, float uMax, float vMax) {
        Matrix4f matrix = pose.pose();
        consumer.addVertex(matrix, p1.x(), p1.y(), p1.z()).setNormal(pose, normalX, normalY, normalZ).setUv(uMin, vMin).setColor(r, g, b, a).setOverlay(overlay).setLight(light);
        consumer.addVertex(matrix, p2.x(), p2.y(), p2.z()).setNormal(pose, normalX, normalY, normalZ).setUv(uMin, vMax).setColor(r, g, b, a).setOverlay(overlay).setLight(light);
        consumer.addVertex(matrix, p3.x(), p3.y(), p3.z()).setNormal(pose, normalX, normalY, normalZ).setUv(uMax, vMax).setColor(r, g, b, a).setOverlay(overlay).setLight(light);
        consumer.addVertex(matrix, p4.x(), p4.y(), p4.z()).setNormal(pose, normalX, normalY, normalZ).setUv(uMax, vMin).setColor(r, g, b, a).setOverlay(overlay).setLight(light);
    }

    /**
     * 共法线、UV 缩放
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer, Vector3f p1, Vector3f p2, Vector3f p3, Vector3f p4, float normalX, float normalY, float normalZ,
                              int r, int g, int b, int a, int overlay, int light, float uScale, float vScale) {
        render(pose, consumer, p1, p2, p3, p4, normalX, normalY, normalZ, r, g, b, a, overlay, light, 0f, 0f, uScale, vScale);
    }

    /**
     * 共法线、无UV缩放
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer, Vector3f p1, Vector3f p2, Vector3f p3, Vector3f p4, float normalX, float normalY, float normalZ,
                              int r, int g, int b, int a, int overlay, int light) {
        render(pose, consumer, p1, p2, p3, p4, normalX, normalY, normalZ, r, g, b, a, overlay, light, 0f, 0f, 1f, 1f);
    }


    /**
     * 从起点向前延伸的四边形（在XZ平面）、共法线、UV缩放
     */
    public static void renderForward(PoseStack.Pose pose, VertexConsumer consumer, Vector3f start, float width, float length, float normalX, float normalY, float normalZ,
                                     int r, int g, int b, int a, int overlay, int light, float uScale, float vScale) {
        float halfWidth = width * 0.5f;
        Vector3f p1 = new Vector3f(start).add(-halfWidth, 0, 0);
        Vector3f p2 = new Vector3f(start).add(halfWidth, 0, 0);
        Vector3f p3 = new Vector3f(start).add(halfWidth, 0, length);
        Vector3f p4 = new Vector3f(start).add(-halfWidth, 0, length);
        // 调用底层 render，UV范围固定为 0-1，缩放由参数控制
        QuadRenderer.render(pose, consumer, p1, p2, p3, p4, normalX, normalY, normalZ,
                r, g, b, a, overlay, light, uScale, vScale);
    }

    /**
     * 默认法线、UV缩放
     */
    public static void renderForward(PoseStack.Pose pose, VertexConsumer consumer, Vector3f start, float width, float length,
                                     int r, int g, int b, int a, int overlay, int light, float uScale, float vScale) {
        renderForward(pose, consumer, start, width, length, 0f, 1f, 0f, r, g, b, a, overlay, light, uScale, vScale);
    }

    /**
     * 默认法线方向，无 UV 缩放
     */
    public static void renderForward(PoseStack.Pose pose, VertexConsumer consumer, Vector3f start, float width, float length,
                                     int r, int g, int b, int a, int overlay, int light) {
        renderForward(pose, consumer, start, width, length, r, g, b, a, overlay, light, 1f, 1f);
    }




    // ARGB 重载
    /**
     * 共法线、UV 范围，接受 ARGB 颜色
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer, Vector3f p1, Vector3f p2, Vector3f p3, Vector3f p4, float normalX, float normalY, float normalZ,
                              int argb, int overlay, int light, float uMin, float vMin, float uMax, float vMax) {
        render(pose, consumer, p1, p2, p3, p4, normalX, normalY, normalZ,
                FastColor.ARGB32.red(argb), FastColor.ARGB32.green(argb), FastColor.ARGB32.blue(argb), FastColor.ARGB32.alpha(argb), overlay, light, uMin, vMin, uMax, vMax);
    }

    /**
     * 共法线、UV 缩放，接受 ARGB 颜色
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer, Vector3f p1, Vector3f p2, Vector3f p3, Vector3f p4, float normalX, float normalY, float normalZ,
                              int argb, int overlay, int light, float uScale, float vScale) {
        render(pose, consumer, p1, p2, p3, p4, normalX, normalY, normalZ,
                FastColor.ARGB32.red(argb), FastColor.ARGB32.green(argb), FastColor.ARGB32.blue(argb), FastColor.ARGB32.alpha(argb), overlay, light, uScale, vScale);
    }

    /**
     * 共法线、无UV缩放，接受 ARGB 颜色
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer, Vector3f p1, Vector3f p2, Vector3f p3, Vector3f p4, float normalX, float normalY, float normalZ,
                              int argb, int overlay, int light) {
        render(pose, consumer, p1, p2, p3, p4, normalX, normalY, normalZ,
                FastColor.ARGB32.red(argb), FastColor.ARGB32.green(argb), FastColor.ARGB32.blue(argb), FastColor.ARGB32.alpha(argb), overlay, light);
    }

    /**
     * 从起点向前延伸的四边形（在XZ平面）、共法线、UV缩放，接受 ARGB 颜色
     */
    public static void renderForward(PoseStack.Pose pose, VertexConsumer consumer, Vector3f start, float width, float length, float normalX, float normalY, float normalZ,
                                     int argb, int overlay, int light, float uScale, float vScale) {
        renderForward(pose, consumer, start, width, length, normalX, normalY, normalZ,
                FastColor.ARGB32.red(argb), FastColor.ARGB32.green(argb), FastColor.ARGB32.blue(argb), FastColor.ARGB32.alpha(argb), overlay, light, uScale, vScale);
    }

    /**
     * 默认法线、UV缩放，接受 ARGB 颜色
     */
    public static void renderForward(PoseStack.Pose pose, VertexConsumer consumer, Vector3f start, float width, float length, int argb, int overlay, int light, float uScale, float vScale) {
        renderForward(pose, consumer, start, width, length,
                FastColor.ARGB32.red(argb), FastColor.ARGB32.green(argb), FastColor.ARGB32.blue(argb), FastColor.ARGB32.alpha(argb), overlay, light, uScale, vScale);
    }

    /**
     * 默认法线方向，无 UV 缩放，接受 ARGB 颜色
     */
    public static void renderForward(PoseStack.Pose pose, VertexConsumer consumer, Vector3f start, float width, float length, int argb, int overlay, int light) {
        renderForward(pose, consumer, start, width, length,
                FastColor.ARGB32.red(argb), FastColor.ARGB32.green(argb), FastColor.ARGB32.blue(argb), FastColor.ARGB32.alpha(argb), overlay, light);
    }

}
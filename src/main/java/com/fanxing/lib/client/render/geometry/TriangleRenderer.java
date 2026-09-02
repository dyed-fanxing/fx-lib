package com.fanxing.lib.client.render.geometry;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.FastColor;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class TriangleRenderer {
    /**
     * 独立法线、独立UV
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer, Vector3f p1, Vector3f p2, Vector3f p3, Vector3f normal1,Vector3f normal2, Vector3f normal3,
                              int r, int g, int b, int a, int overlay, int light, float u1, float v1, float u2, float v2, float u3, float v3) {
        Matrix4f matrix = pose.pose();
        consumer.addVertex(matrix, p1.x, p1.y, p1.z)
                .setNormal(pose, normal1.x, normal1.y, normal1.z)
                .setUv(u1, v1)
                .setColor(r, g, b, a)
                .setOverlay(overlay)
                .setLight(light);
        consumer.addVertex(matrix, p2.x, p2.y, p2.z)
                .setNormal(pose, normal2.x, normal2.y, normal2.z)
                .setUv(u2, v2)
                .setColor(r, g, b, a)
                .setOverlay(overlay)
                .setLight(light);
        consumer.addVertex(matrix, p3.x, p3.y, p3.z)
                .setNormal(pose, normal3.x, normal3.y, normal3.z)
                .setUv(u3, v3)
                .setColor(r, g, b, a)
                .setOverlay(overlay)
                .setLight(light);
    }

    /**
     * 独立UV
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer, Vector3f p1, Vector3f p2, Vector3f p3, Vector3f normal,
                              int r, int g, int b, int a, int overlay, int light, float u1, float v1, float u2, float v2, float u3, float v3) {
        render(pose,consumer,p1,p2,p3,normal,normal,normal,r,g,b,a,overlay,light,u1,v1,u2,v2,u3,v3);
    }


    /**
     * 自动计算法线（使用顶点叉积），独立UV
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer, Vector3f p1, Vector3f p2, Vector3f p3,
                              int r, int g, int b, int a, int overlay, int light, float u1, float v1, float u2, float v2, float u3, float v3) {
        // 计算法线：(p2-p1) × (p3-p1)
        Vector3f e1 = new Vector3f(p2).sub(p1);
        Vector3f e2 = new Vector3f(p3).sub(p1);
        Vector3f normal = e1.cross(e2).normalize();
        if (normal.lengthSquared() < 1e-6) {
            normal = new Vector3f(0, 1, 0);
        }
        render(pose, consumer, p1, p2, p3, normal, r, g, b, a, overlay, light, u1, v1, u2, v2, u3, v3);
    }



    // ==================== ARGB 重载 ====================

    /**
     * 独立法线、独立UV，接受 ARGB 颜色
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer, Vector3f p1, Vector3f p2, Vector3f p3,
                              Vector3f normal1, Vector3f normal2, Vector3f normal3,
                              int argb, int overlay, int light,
                              float u1, float v1, float u2, float v2, float u3, float v3) {
        render(pose, consumer, p1, p2, p3, normal1, normal2, normal3,
                argb >> 16 & 255 ,argb >> 8 & 255,argb & 255, argb >> 24, overlay, light, u1, v1, u2, v2, u3, v3);
    }

    /**
     * 共法线、独立UV，接受 ARGB 颜色
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer, Vector3f p1, Vector3f p2, Vector3f p3,
                              Vector3f normal, int argb, int overlay, int light,
                              float u1, float v1, float u2, float v2, float u3, float v3) {
        render(pose, consumer, p1, p2, p3, normal,
                argb >> 16 & 255 ,argb >> 8 & 255,argb & 255, argb >> 24, overlay, light, u1, v1, u2, v2, u3, v3);
    }

    /**
     * 自动计算法线，独立UV，接受 ARGB 颜色
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer, Vector3f p1, Vector3f p2, Vector3f p3,
                              int argb, int overlay, int light,
                              float u1, float v1, float u2, float v2, float u3, float v3) {
        render(pose, consumer, p1, p2, p3,
                argb >> 16 & 255 ,argb >> 8 & 255,argb & 255, argb >> 24, overlay, light, u1, v1, u2, v2, u3, v3);
    }
}

package com.fanxing.lib.client.render.geometry;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;

public class OrbRenderer {

    // ==================== PoseStack 版本（实体渲染） ====================

    /**
     * 球体：使用 QUADS 模式，顶点法线正确。
     *
     * @param pose      姿态矩阵
     * @param consumer  顶点消费者（需支持 QUADS 模式）
     * @param cx,cy,cz  球心世界坐标
     * @param radius    半径
     * @param segments  分段数（同时决定纬度细节）
     * @param r,g,b,a   颜色 (0-255)
     * @param overlay   叠加纹理
     * @param light     光照
     * @param uMin,vMin UV 矩形左下角
     * @param uMax,vMax UV 矩形右上角
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer, float cx, float cy, float cz, float radius, int segments,
                              int r, int g, int b, int a, int overlay, int light, float uMin, float vMin, float uMax, float vMax) {
        int latSegments = segments / 2;
        if (latSegments < 2) latSegments = 2;
        float deltaTheta = Mth.TWO_PI / segments;
        float deltaPhi = Mth.PI / latSegments;
        float uRange = uMax - uMin;
        float vRange = vMax - vMin;

        for (int i = 0; i < latSegments; i++) {
            float phi1 = i * deltaPhi - Mth.HALF_PI;
            float phi2 = (i + 1) * deltaPhi - Mth.HALF_PI;
            float sinPhi1 = Mth.sin(phi1), cosPhi1 = Mth.cos(phi1);
            float sinPhi2 = Mth.sin(phi2), cosPhi2 = Mth.cos(phi2);

            float r1 = Math.max(0f, radius * cosPhi1);
            float r2 = Math.max(0f, radius * cosPhi2);

            // V 坐标：纬度均匀映射
            float v1 = vMin + ((float) i / latSegments) * vRange;
            float v2 = vMin + ((float) (i + 1) / latSegments) * vRange;

            for (int j = 0; j < segments; j++) {
                float theta1 = j * deltaTheta;
                float theta2 = (j + 1) * deltaTheta;
                float sinTheta1 = Mth.sin(theta1), cosTheta1 = Mth.cos(theta1);
                float sinTheta2 = Mth.sin(theta2), cosTheta2 = Mth.cos(theta2);

                float u1 = uMin + ((float) j / segments) * uRange;
                float u2 = uMin + ((float) (j + 1) / segments) * uRange;

                // 顶点坐标（世界）
                float x1 = cx + r1 * cosTheta1, y1 = cy + radius * sinPhi1, z1 = cz + r1 * sinTheta1;
                float x2 = cx + r2 * cosTheta1, y2 = cy + radius * sinPhi2, z2 = cz + r2 * sinTheta1;
                float x3 = cx + r2 * cosTheta2, y3 = cy + radius * sinPhi2, z3 = cz + r2 * sinTheta2;
                float x4 = cx + r1 * cosTheta2, y4 = cy + radius * sinPhi1, z4 = cz + r1 * sinTheta2;

                // 法线（径向，归一化）
                float nx1 = cosPhi1 * cosTheta1, ny1 = sinPhi1, nz1 = cosPhi1 * sinTheta1;
                float nx2 = cosPhi2 * cosTheta1, ny2 = sinPhi2, nz2 = cosPhi2 * sinTheta1;
                float nx3 = cosPhi2 * cosTheta2, ny3 = sinPhi2, nz3 = cosPhi2 * sinTheta2;
                float nx4 = cosPhi1 * cosTheta2, ny4 = sinPhi1, nz4 = cosPhi1 * sinTheta2;

                float len1 = (float) Math.sqrt(nx1 * nx1 + ny1 * ny1 + nz1 * nz1);
                float len2 = (float) Math.sqrt(nx2 * nx2 + ny2 * ny2 + nz2 * nz2);
                float len3 = (float) Math.sqrt(nx3 * nx3 + ny3 * ny3 + nz3 * nz3);
                float len4 = (float) Math.sqrt(nx4 * nx4 + ny4 * ny4 + nz4 * nz4);
                if (len1 > 1e-6f) { nx1 /= len1; ny1 /= len1; nz1 /= len1; }
                if (len2 > 1e-6f) { nx2 /= len2; ny2 /= len2; nz2 /= len2; }
                if (len3 > 1e-6f) { nx3 /= len3; ny3 /= len3; nz3 /= len3; }
                if (len4 > 1e-6f) { nx4 /= len4; ny4 /= len4; nz4 /= len4; }

                QuadRenderer.render(pose, consumer,
                        x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4,
                        nx1, ny1, nz1, nx2, ny2, nz2, nx3, ny3, nz3, nx4, ny4, nz4,
                        r, g, b, a, overlay, light,
                        u1, v1, u1, v2, u2, v2, u2, v1);
            }
        }
    }

    // ARGB 便利重载
    public static void render(PoseStack.Pose pose, VertexConsumer consumer,float cx, float cy, float cz, float radius, int segments,
                              int argb, int overlay, int light, float uMin, float vMin, float uMax, float vMax) {
        render(pose, consumer, cx, cy, cz, radius, segments,
                argb >> 16 & 255 ,argb >> 8 & 255,argb & 255, argb >> 24,
                overlay, light, uMin, vMin, uMax, vMax);
    }


    // ==================== 世界坐标 + 四元数旋转版本（粒子专用，无 PoseStack） ====================

    /**
     * 球体：直接使用世界坐标和四元数旋转，内部通过 QuadRenderer 提交顶点。
     * 适用于 CUSTOM 粒子等无需 PoseStack 的场景。
     *
     * @param consumer  顶点消费者
     * @param cx,cy,cz  球心世界坐标
     * @param radius    半径
     * @param segments  分段数（至少 4）
     * @param rotation  旋转四元数（绕球心旋转，null 则无旋转）
     * @param r,g,b,a   颜色 (0-1)
     * @param light     光照值
     * @param uMin,vMin UV 矩形左下角
     * @param uMax,vMax UV 矩形右上角
     */
    public static void render(VertexConsumer consumer, float cx, float cy, float cz, float radius, int segments, Quaternionf rotation,
                              float r, float g, float b, float a, int light, float uMin, float vMin, float uMax, float vMax) {
        int latSegments = segments / 2;
        if (latSegments < 2) latSegments = 2;
        float deltaTheta = Mth.TWO_PI / segments;
        float deltaPhi = Mth.PI / latSegments;
        float uRange = uMax - uMin;
        float vRange = vMax - vMin;

        for (int i = 0; i < latSegments; i++) {
            float phi1 = i * deltaPhi - Mth.HALF_PI;
            float phi2 = (i + 1) * deltaPhi - Mth.HALF_PI;
            float sinPhi1 = Mth.sin(phi1), cosPhi1 = Mth.cos(phi1);
            float sinPhi2 = Mth.sin(phi2), cosPhi2 = Mth.cos(phi2);

            float r1 = Math.max(0f, radius * cosPhi1);
            float r2 = Math.max(0f, radius * cosPhi2);

            float v1 = vMin + ((float) i / latSegments) * vRange;
            float v2 = vMin + ((float) (i + 1) / latSegments) * vRange;

            for (int j = 0; j < segments; j++) {
                float theta1 = j * deltaTheta;
                float theta2 = (j + 1) * deltaTheta;
                float sinTheta1 = Mth.sin(theta1), cosTheta1 = Mth.cos(theta1);
                float sinTheta2 = Mth.sin(theta2), cosTheta2 = Mth.cos(theta2);

                float u1 = uMin + ((float) j / segments) * uRange;
                float u2 = uMin + ((float) (j + 1) / segments) * uRange;

                // 局部坐标（相对球心）
                float lx1 = r1 * cosTheta1, ly1 = radius * sinPhi1, lz1 = r1 * sinTheta1;
                float lx2 = r2 * cosTheta1, ly2 = radius * sinPhi2, lz2 = r2 * sinTheta1;
                float lx3 = r2 * cosTheta2, ly3 = radius * sinPhi2, lz3 = r2 * sinTheta2;
                float lx4 = r1 * cosTheta2, ly4 = radius * sinPhi1, lz4 = r1 * sinTheta2;

                QuadRenderer.render(consumer,
                        lx1, ly1, lz1, lx2, ly2, lz2, lx3, ly3, lz3, lx4, ly4, lz4,
                        cx, cy, cz, rotation,
                        r, g, b, a, light,
                        u1, v1, u1, v2, u2, v2, u2, v1);
            }
        }
    }

    // ARGB 便利重载（世界坐标版本）
    public static void render(VertexConsumer consumer, float cx, float cy, float cz, float radius, int segments, Quaternionf rotation,
                              int argb, int light, float uMin, float vMin, float uMax, float vMax) {
        render(consumer, cx, cy, cz, radius, segments, rotation,
                (argb >> 16 & 255) / 255f ,(argb >> 8 & 255) / 255f,(argb & 255) / 255f, (argb >> 24) / 255f,
                light, uMin, vMin, uMax, vMax);
    }
}
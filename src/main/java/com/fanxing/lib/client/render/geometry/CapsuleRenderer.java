package com.fanxing.lib.client.render.geometry;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;

public class CapsuleRenderer {

    /**
     * 水平胶囊体（沿 Z 轴从起点向前延伸），总长度固定为 length。
     * 传入的 (cx, cy, cz) 是后端半球最末端，前端半球最末端位于 cz + length。
     *
     * @param pose      姿态矩阵
     * @param consumer  顶点消费者（需支持 QUADS 模式）
     * @param cx,cy,cz  起点坐标（世界空间）
     * @param radius    半径
     * @param length    胶囊体总长度（从起点到终点的距离）
     * @param segments  水平分段数（至少 4，同时影响半球细节）
     * @param r,g,b,a   颜色 (0-255)
     * @param overlay   叠加纹理
     * @param light     光照
     * @param uMin,vMin UV 矩形左下角
     * @param uMax,vMax UV 矩形右上角
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer,
                              float cx, float cy, float cz,
                              float radius, float length, int segments,
                              int r, int g, int b, int a, int overlay, int light,
                              float uMin, float vMin, float uMax, float vMax) {
        float cylLength = length - 2 * radius;
        if (cylLength < 0) cylLength = 0;
        float halfLen = cylLength * 0.5f;

        // 几何中心 Z 坐标：起点 + 一个半径 + 半个圆柱长度
        float centerZ = cz + radius + halfLen;

        float uRange = uMax - uMin;
        float vRange = vMax - vMin;

        // V 轴分段比例
        float vBottomEnd = vMin + vRange * (radius / length);
        float vCylEnd    = vMin + vRange * ((radius + cylLength) / length);

        int latSegments = segments / 2;            // 每个半球纬度分段数
        float dTheta = Mth.TWO_PI / segments;      // 经度步长
        float dPhi   = (float) Math.PI / (2 * latSegments); // 半球纬度步长

        // ---- 后端半球（-Z 端，靠近起点） ----
        for (int i = 0; i < latSegments; i++) {
            float phi1 = -Mth.HALF_PI + i * dPhi;
            float phi2 = phi1 + dPhi;
            float r1 = radius * Mth.cos(phi1);
            float r2 = radius * Mth.cos(phi2);
            // 局部 Z（相对于几何中心）
            float lz1 = radius * Mth.sin(phi1) - halfLen;
            float lz2 = radius * Mth.sin(phi2) - halfLen;

            float v1 = vMin + (phi1 - (-Mth.HALF_PI)) * 2.0f / Mth.PI * (vBottomEnd - vMin);
            float v2 = vMin + (phi2 - (-Mth.HALF_PI)) * 2.0f / Mth.PI * (vBottomEnd - vMin);

            for (int j = 0; j < segments; j++) {
                float theta1 = j * dTheta;
                float theta2 = theta1 + dTheta;
                float cos1 = Mth.cos(theta1), sin1 = Mth.sin(theta1);
                float cos2 = Mth.cos(theta2), sin2 = Mth.sin(theta2);

                float u1 = uMin + ((float) j / segments) * uRange;
                float u2 = uMin + ((float) (j + 1) / segments) * uRange;

                float x1 = r1 * cos1, y1 = r1 * sin1; // theta1, phi1
                float x2 = r1 * cos2, y2 = r1 * sin2; // theta2, phi1
                float x3 = r2 * cos2, y3 = r2 * sin2; // theta2, phi2
                float x4 = r2 * cos1, y4 = r2 * sin1; // theta1, phi2

                QuadRenderer.render(pose, consumer,
                        cx + x1, cy + y1, centerZ + lz1,
                        cx + x2, cy + y2, centerZ + lz1,
                        cx + x3, cy + y3, centerZ + lz2,
                        cx + x4, cy + y4, centerZ + lz2,
                        Mth.cos(phi1) * cos1, Mth.cos(phi1) * sin1, Mth.sin(phi1),
                        Mth.cos(phi1) * cos2, Mth.cos(phi1) * sin2, Mth.sin(phi1),
                        Mth.cos(phi2) * cos2, Mth.cos(phi2) * sin2, Mth.sin(phi2),
                        Mth.cos(phi2) * cos1, Mth.cos(phi2) * sin1, Mth.sin(phi2),
                        r, g, b, a, overlay, light,
                        u1, v1, u2, v1, u2, v2, u1, v2);
            }
        }

        // ---- 圆柱侧面（Z 从 -halfLen 到 halfLen） ----
        for (int j = 0; j < segments; j++) {
            float theta1 = j * dTheta;
            float theta2 = theta1 + dTheta;
            float cos1 = Mth.cos(theta1), sin1 = Mth.sin(theta1);
            float cos2 = Mth.cos(theta2), sin2 = Mth.sin(theta2);

            float u1 = uMin + ((float) j / segments) * uRange;
            float u2 = uMin + ((float) (j + 1) / segments) * uRange;

            float x1 = radius * cos1, y1 = radius * sin1, lz1 = -halfLen;
            float x2 = radius * cos2, y2 = radius * sin2, lz2 = -halfLen;
            float x3 = radius * cos2, y3 = radius * sin2, lz3 =  halfLen;
            float x4 = radius * cos1, y4 = radius * sin1, lz4 =  halfLen;

            float nx1 = cos1, ny1 = sin1, nz1 = 0;
            float nx2 = cos2, ny2 = sin2, nz2 = 0;

            QuadRenderer.render(pose, consumer,
                    cx + x1, cy + y1, centerZ + lz1,
                    cx + x2, cy + y2, centerZ + lz2,
                    cx + x3, cy + y3, centerZ + lz3,
                    cx + x4, cy + y4, centerZ + lz4,
                    nx1, ny1, nz1, nx2, ny2, nz2,
                    nx2, ny2, nz2, nx1, ny1, nz1,
                    r, g, b, a, overlay, light,
                    u1, vBottomEnd, u2, vBottomEnd, u2, vCylEnd, u1, vCylEnd);
        }

        // ---- 前端半球（+Z 端） ----
        for (int i = 0; i < latSegments; i++) {
            float phi1 = 0 + i * dPhi;
            float phi2 = phi1 + dPhi;
            float r1 = radius * Mth.cos(phi1);
            float r2 = radius * Mth.cos(phi2);
            float lz1 = radius * Mth.sin(phi1) + halfLen;
            float lz2 = radius * Mth.sin(phi2) + halfLen;

            float v1 = vCylEnd + phi1 * 2.0f / Mth.PI * (vMax - vCylEnd);
            float v2 = vCylEnd + phi2 * 2.0f / Mth.PI * (vMax - vCylEnd);

            for (int j = 0; j < segments; j++) {
                float theta1 = j * dTheta;
                float theta2 = theta1 + dTheta;
                float cos1 = Mth.cos(theta1), sin1 = Mth.sin(theta1);
                float cos2 = Mth.cos(theta2), sin2 = Mth.sin(theta2);

                float u1 = uMin + ((float) j / segments) * uRange;
                float u2 = uMin + ((float) (j + 1) / segments) * uRange;

                float x1 = r1 * cos1, y1 = r1 * sin1;
                float x2 = r1 * cos2, y2 = r1 * sin2;
                float x3 = r2 * cos2, y3 = r2 * sin2;
                float x4 = r2 * cos1, y4 = r2 * sin1;

                QuadRenderer.render(pose, consumer,
                        cx + x1, cy + y1, centerZ + lz1,
                        cx + x2, cy + y2, centerZ + lz1,
                        cx + x3, cy + y3, centerZ + lz2,
                        cx + x4, cy + y4, centerZ + lz2,
                        Mth.cos(phi1) * cos1, Mth.cos(phi1) * sin1, Mth.sin(phi1),
                        Mth.cos(phi1) * cos2, Mth.cos(phi1) * sin2, Mth.sin(phi1),
                        Mth.cos(phi2) * cos2, Mth.cos(phi2) * sin2, Mth.sin(phi2),
                        Mth.cos(phi2) * cos1, Mth.cos(phi2) * sin1, Mth.sin(phi2),
                        r, g, b, a, overlay, light,
                        u1, v1, u2, v1, u2, v2, u1, v2);
            }
        }
    }

    // ARGB 便利重载（PoseStack 版本）
    public static void render(PoseStack.Pose pose, VertexConsumer consumer,
                              float cx, float cy, float cz,
                              float radius, float length, int segments,
                              int argb, int overlay, int light,
                              float uMin, float vMin, float uMax, float vMax) {
        render(pose, consumer, cx, cy, cz, radius, length, segments,
                FastColor.ARGB32.red(argb), FastColor.ARGB32.green(argb),
                FastColor.ARGB32.blue(argb), FastColor.ARGB32.alpha(argb),
                overlay, light, uMin, vMin, uMax, vMax);
    }

    // 原地渲染（起点在原点）
    public static void render(PoseStack.Pose pose, VertexConsumer consumer,
                              float radius, float length, int segments,
                              int argb, int overlay, int light,
                              float uMin, float vMin, float uMax, float vMax) {
        render(pose, consumer, 0, 0, 0, radius, length, segments, argb, overlay, light, uMin, vMin, uMax, vMax);
    }

    // ==================== 世界坐标 + 四元数旋转版本（无 PoseStack） ====================

    /**
     * 水平胶囊体（沿 Z 轴从起点向前延伸），总长度固定为 length。
     * 传入的 (cx, cy, cz) 是起点，前端位于 cz + length。
     */
    public static void render(VertexConsumer consumer,
                              float cx, float cy, float cz,
                              float radius, float length, int segments,
                              Quaternionf rotation,
                              float r, float g, float b, float a, int light,
                              float uMin, float vMin, float uMax, float vMax) {
        if (segments < 4) segments = 4;
        float cylLength = length - 2 * radius;
        if (cylLength < 0) cylLength = 0;
        float halfLen = cylLength * 0.5f;
        float centerZ = cz + radius + halfLen;

        float uRange = uMax - uMin;
        float vRange = vMax - vMin;

        float vBottomEnd = vMin + vRange * (radius / length);
        float vCylEnd    = vMin + vRange * ((radius + cylLength) / length);

        int latSegments = segments / 2;
        float dTheta = Mth.TWO_PI / segments;
        float dPhi   = (float) Math.PI / (2 * latSegments);

        // ---- 后端半球（-Z 端） ----
        for (int i = 0; i < latSegments; i++) {
            float phi1 = -Mth.HALF_PI + i * dPhi;
            float phi2 = phi1 + dPhi;
            float r1 = radius * Mth.cos(phi1);
            float r2 = radius * Mth.cos(phi2);
            float lz1 = radius * Mth.sin(phi1) - halfLen;
            float lz2 = radius * Mth.sin(phi2) - halfLen;

            float v1 = vMin + (phi1 - (-Mth.HALF_PI)) * 2.0f / Mth.PI * (vBottomEnd - vMin);
            float v2 = vMin + (phi2 - (-Mth.HALF_PI)) * 2.0f / Mth.PI * (vBottomEnd - vMin);

            for (int j = 0; j < segments; j++) {
                float theta1 = j * dTheta;
                float theta2 = theta1 + dTheta;
                float cos1 = Mth.cos(theta1), sin1 = Mth.sin(theta1);
                float cos2 = Mth.cos(theta2), sin2 = Mth.sin(theta2);

                float u1 = uMin + ((float) j / segments) * uRange;
                float u2 = uMin + ((float) (j + 1) / segments) * uRange;

                float lx1 = r1 * cos1, ly1 = r1 * sin1;
                float lx2 = r1 * cos2, ly2 = r1 * sin2;
                float lx3 = r2 * cos2, ly3 = r2 * sin2;
                float lx4 = r2 * cos1, ly4 = r2 * sin1;

                QuadRenderer.render(consumer,
                        lx1, ly1, lz1, lx2, ly2, lz1,
                        lx3, ly3, lz2, lx4, ly4, lz2,
                        cx, cy, centerZ, rotation,
                        r, g, b, a, light,
                        u1, v1, u2, v1, u2, v2, u1, v2);
            }
        }

        // ---- 圆柱侧面 ----
        for (int j = 0; j < segments; j++) {
            float theta1 = j * dTheta;
            float theta2 = theta1 + dTheta;
            float cos1 = Mth.cos(theta1), sin1 = Mth.sin(theta1);
            float cos2 = Mth.cos(theta2), sin2 = Mth.sin(theta2);

            float u1 = uMin + ((float) j / segments) * uRange;
            float u2 = uMin + ((float) (j + 1) / segments) * uRange;

            float lx1 = radius * cos1, ly1 = radius * sin1, lz1 = -halfLen;
            float lx2 = radius * cos2, ly2 = radius * sin2, lz2 = -halfLen;
            float lx3 = radius * cos2, ly3 = radius * sin2, lz3 =  halfLen;
            float lx4 = radius * cos1, ly4 = radius * sin1, lz4 =  halfLen;

            QuadRenderer.render(consumer,
                    lx1, ly1, lz1, lx2, ly2, lz2,
                    lx3, ly3, lz3, lx4, ly4, lz4,
                    cx, cy, centerZ, rotation,
                    r, g, b, a, light,
                    u1, vBottomEnd, u2, vBottomEnd, u2, vCylEnd, u1, vCylEnd);
        }

        // ---- 前端半球（+Z 端） ----
        for (int i = 0; i < latSegments; i++) {
            float phi1 = 0 + i * dPhi;
            float phi2 = phi1 + dPhi;
            float r1 = radius * Mth.cos(phi1);
            float r2 = radius * Mth.cos(phi2);
            float lz1 = radius * Mth.sin(phi1) + halfLen;
            float lz2 = radius * Mth.sin(phi2) + halfLen;

            float v1 = vCylEnd + phi1 * 2.0f / Mth.PI * (vMax - vCylEnd);
            float v2 = vCylEnd + phi2 * 2.0f / Mth.PI * (vMax - vCylEnd);

            for (int j = 0; j < segments; j++) {
                float theta1 = j * dTheta;
                float theta2 = theta1 + dTheta;
                float cos1 = Mth.cos(theta1), sin1 = Mth.sin(theta1);
                float cos2 = Mth.cos(theta2), sin2 = Mth.sin(theta2);

                float u1 = uMin + ((float) j / segments) * uRange;
                float u2 = uMin + ((float) (j + 1) / segments) * uRange;

                float lx1 = r1 * cos1, ly1 = r1 * sin1;
                float lx2 = r1 * cos2, ly2 = r1 * sin2;
                float lx3 = r2 * cos2, ly3 = r2 * sin2;
                float lx4 = r2 * cos1, ly4 = r2 * sin1;

                QuadRenderer.render(consumer,
                        lx1, ly1, lz1, lx2, ly2, lz1,
                        lx3, ly3, lz2, lx4, ly4, lz2,
                        cx, cy, centerZ, rotation,
                        r, g, b, a, light,
                        u1, v1, u2, v1, u2, v2, u1, v2);
            }
        }
    }

    // ARGB 便利重载（世界坐标版本）
    public static void render(VertexConsumer consumer,
                              float cx, float cy, float cz,
                              float radius, float length, int segments,
                              Quaternionf rotation,
                              int argb, int light,
                              float uMin, float vMin, float uMax, float vMax) {
        render(consumer, cx, cy, cz, radius, length, segments, rotation,
                ((argb >> 16) & 0xFF) / 255f, ((argb >> 8) & 0xFF) / 255f,
                (argb & 0xFF) / 255f, ((argb >> 24) & 0xFF) / 255f,
                light, uMin, vMin, uMax, vMax);
    }
}
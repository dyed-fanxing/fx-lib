package com.fanxing.lib.client.render.geometry;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;

public class CylinderRenderer {

    // ==================== PoseStack 版本（实体渲染） ====================

    /**
     * 圆柱侧面（沿 Y 轴），使用 QUADS 模式，径向法线。
     *
     * @param pose      姿态矩阵
     * @param consumer  顶点消费者（需支持 QUADS 模式）
     * @param cx,cy,cz  底部中心点（世界坐标）
     * @param radius    半径
     * @param height    高度
     * @param segments  水平分段数
     * @param r,g,b,a   颜色 (0-255)
     * @param overlay   叠加纹理
     * @param light     光照
     * @param uMin,vMin UV 矩形左下角
     * @param uMax,vMax UV 矩形右上角
     */
    public static void renderSide(PoseStack.Pose pose, VertexConsumer consumer,
                                  float cx, float cy, float cz,
                                  float radius, float height, int segments,
                                  int r, int g, int b, int a, int overlay, int light,
                                  float uMin, float vMin, float uMax, float vMax) {
        float step = Mth.TWO_PI / segments;
        float uRange = uMax - uMin;
        for (int i = 0; i < segments; i++) {
            float theta1 = i * step;
            float theta2 = (i + 1) * step;
            float cos1 = Mth.cos(theta1), sin1 = Mth.sin(theta1);
            float cos2 = Mth.cos(theta2), sin2 = Mth.sin(theta2);

            float x1 = cx + radius * cos1;
            float z1 = cz + radius * sin1;
            float x2 = cx + radius * cos2;
            float z2 = cz + radius * sin2;
            float yBottom = cy;
            float yTop = cy + height;

            // 径向法线
            float nx1 = cos1, nz1 = sin1;
            float nx2 = cos2, nz2 = sin2;
            float ny = 0;

            float u1 = uMin + ((float) i / segments) * uRange;
            float u2 = uMin + ((float) (i + 1) / segments) * uRange;
            float vBottom = vMin;
            float vTop = vMax;

            // 逆时针提交四边形
            QuadRenderer.render(pose, consumer,
                    x1, yBottom, z1, x2, yBottom, z2, x2, yTop, z2, x1, yTop, z1,
                    nx1, ny, nz1, nx2, ny, nz2, nx2, ny, nz2, nx1, ny, nz1,
                    r, g, b, a, overlay, light,
                    u1, vBottom, u2, vBottom, u2, vTop, u1, vTop);
        }
    }

    // ARGB 便利重载
    public static void renderSide(PoseStack.Pose pose, VertexConsumer consumer,
                                  float cx, float cy, float cz,
                                  float radius, float height, int segments,
                                  int argb, int overlay, int light,
                                  float uMin, float vMin, float uMax, float vMax) {
        renderSide(pose, consumer, cx, cy, cz, radius, height, segments,
                argb >> 16 & 255 ,argb >> 8 & 255,argb & 255, argb >> 24,
                overlay, light, uMin, vMin, uMax, vMax);
    }


    // ==================== 世界坐标 + 四元数旋转版本（粒子专用，无 PoseStack） ====================

    /**
     * 圆柱侧面（沿局部 Y 轴），直接使用世界坐标和四元数旋转。
     * 适用于 CUSTOM 粒子等无需 PoseStack 的场景。
     */
    public static void renderSide(VertexConsumer consumer,
                                  float cx, float cy, float cz,
                                  float radius, float height, int segments,
                                  Quaternionf rotation,
                                  float r, float g, float b, float a, int light,
                                  float uMin, float vMin, float uMax, float vMax) {
        float step = Mth.TWO_PI / segments;
        float uRange = uMax - uMin;
        for (int i = 0; i < segments; i++) {
            float theta1 = i * step;
            float theta2 = i + step;
            float u1 = uMin + ((float) i / segments) * uRange;
            float u2 = uMin + ((float) (i + 1) / segments) * uRange;

            float cos1 = Mth.cos(theta1), sin1 = Mth.sin(theta1);
            float cos2 = Mth.cos(theta2), sin2 = Mth.sin(theta2);

            float lx1 = radius * cos1, lz1 = radius * sin1;
            float lx2 = radius * cos2, lz2 = radius * sin2;

            // 调用 QuadRenderer 的带旋转版本
            QuadRenderer.render(consumer,
                    lx1, 0, lz1,         // 右下
                    lx1, height, lz1,    // 右上
                    lx2, height, lz2,    // 左上
                    lx2, 0, lz2,         // 左下
                    cx, cy, cz, rotation,
                    r, g, b, a, light,
                    u1, vMin, u1, vMax, u2, vMax, u2, vMin);
        }
    }

    // ARGB 便利重载
    public static void renderSide(VertexConsumer consumer,
                                  float cx, float cy, float cz,
                                  float radius, float height, int segments,
                                  Quaternionf rotation, int argb, int light,
                                  float uMin, float vMin, float uMax, float vMax) {
        renderSide(consumer, cx, cy, cz, radius, height, segments, rotation,
                (argb >> 16 & 255) / 255f ,(argb >> 8 & 255) / 255f,(argb & 255) / 255f, (argb >> 24) / 255f,
                light, uMin, vMin, uMax, vMax);
    }
}
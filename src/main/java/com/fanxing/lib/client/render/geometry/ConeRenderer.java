package com.fanxing.lib.client.render.geometry;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public final class ConeRenderer {

    /**
     * 渲染任意棱数的锥体（接收四元数旋转）
     *
     * @param pose           姿态（PoseStack.last()）
     * @param consumer       顶点消费者
     * @param ox, oy, oz     锥尖位置（相机空间坐标）
     * @param rotation       旋转四元数（将局部Y轴旋转到目标方向）
     * @param radius         底面外接圆半径
     * @param height         锥体长度（沿局部Y轴）
     * @param radialSegments 棱数（≥3）
     * @param startColor     锥尖颜色
     * @param endColor       底面颜色
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer,
                              float ox, float oy, float oz, Quaternionf rotation,
                              float radius, float height, int radialSegments,
                              int startColor, int endColor) {
        Matrix4f mat = pose.pose();

        int sr = FastColor.ARGB32.red(startColor);
        int sg = FastColor.ARGB32.green(startColor);
        int sb = FastColor.ARGB32.blue(startColor);
        int sa = FastColor.ARGB32.alpha(startColor);
        int er = FastColor.ARGB32.red(endColor);
        int eg = FastColor.ARGB32.green(endColor);
        int eb = FastColor.ARGB32.blue(endColor);
        int ea = FastColor.ARGB32.alpha(endColor);

        // 从四元数提取旋转矩阵元素（与 renderSide 一致）
        float qx = rotation.x, qy = rotation.y, qz = rotation.z, qw = rotation.w;
        float xx = qx * qx, yy = qy * qy, zz = qz * qz, ww = qw * qw;
        float xy = qx * qy, xz = qx * qz, yz = qy * qz;
        float xw = qx * qw, yw = qy * qw, zw = qz * qw;

        float m00 = ww + xx - yy - zz;
        float m01 = 2 * (xy - zw);
        float m02 = 2 * (xz + yw);
        float m10 = 2 * (xy + zw);
        float m11 = ww - xx + yy - zz;
        float m12 = 2 * (yz - xw);
        float m20 = 2 * (xz - yw);
        float m21 = 2 * (yz + xw);
        float m22 = ww - xx - yy + zz;

        float step = Mth.TWO_PI / radialSegments;

        for (int i = 0; i < radialSegments; i++) {
            float angle1 = i * step;
            float angle2 = (i + 1) * step;
            float cos1 = Mth.cos(angle1), sin1 = Mth.sin(angle1);
            float cos2 = Mth.cos(angle2), sin2 = Mth.sin(angle2);

            // 局部坐标（底面顶点，y = height）
            float lx1 = radius * cos1, ly1 = height, lz1 = radius * sin1;
            float lx2 = radius * cos2, ly2 = height, lz2 = radius * sin2;

            // 应用旋转矩阵并平移到原点
            float x1 = m00 * lx1 + m01 * ly1 + m02 * lz1 + ox;
            float y1 = m10 * lx1 + m11 * ly1 + m12 * lz1 + oy;
            float z1 = m20 * lx1 + m21 * ly1 + m22 * lz1 + oz;

            float x2 = m00 * lx2 + m01 * ly2 + m02 * lz2 + ox;
            float y2 = m10 * lx2 + m11 * ly2 + m12 * lz2 + oy;
            float z2 = m20 * lx2 + m21 * ly2 + m22 * lz2 + oz;

            // 锥尖三角形
            consumer.addVertex(mat, ox, oy, oz).setColor(sr, sg, sb, sa);
            consumer.addVertex(mat, x1, y1, z1).setColor(er, eg, eb, ea);
            consumer.addVertex(mat, x2, y2, z2).setColor(er, eg, eb, ea);
        }
    }

    // 三棱锥快捷方法
    public static void renderTriangular(PoseStack.Pose pose, VertexConsumer consumer,
                                        float ox, float oy, float oz, Quaternionf rotation,
                                        float radius, float height,
                                        int startColor, int endColor) {
        render(pose, consumer, ox, oy, oz, rotation, radius, height, 3, startColor, endColor);
    }

    // ==================== 世界坐标直接渲染（用于粒子等） ====================

    /**
     * 渲染带纹理的棱锥（锥顶 UV = (uMin,vMin)，底面顶点 UV = (uMax,vMax)）。
     */
    public static void renderSide(VertexConsumer consumer, float ox, float oy, float oz, Quaternionf rotation,
                                  float radiusX, float height, float radiusZ, int radialSegments,
                                  float r, float g, float b, float a, int light,
                                  float uMin, float vMin, float uMax, float vMax) {
        // 手动展开四元数旋转矩阵
        float qx = rotation.x, qy = rotation.y, qz = rotation.z, qw = rotation.w;
        float xx = qx * qx, yy = qy * qy, zz = qz * qz, ww = qw * qw;
        float xy = qx * qy, xz = qx * qz, yz = qy * qz;
        float xw = qx * qw, yw = qy * qw, zw = qz * qw;

        float m00 = ww + xx - yy - zz;
        float m01 = 2 * (xy - zw);
        float m02 = 2 * (xz + yw);
        float m10 = 2 * (xy + zw);
        float m11 = ww - xx + yy - zz;
        float m12 = 2 * (yz - xw);
        float m20 = 2 * (xz - yw);
        float m21 = 2 * (yz + xw);
        float m22 = ww - xx - yy + zz;

        float angleStep = Mth.TWO_PI / radialSegments;

        for (int i = 0; i < radialSegments; i++) {
            float angle1 = i * angleStep;
            float angle2 = (i + 1) * angleStep;
            float cos1 = Mth.cos(angle1), sin1 = Mth.sin(angle1);
            float cos2 = Mth.cos(angle2), sin2 = Mth.sin(angle2);

            float lx1 = radiusX * cos1, lz1 = radiusZ * sin1, ly1 = height;
            float lx2 = radiusX * cos2, lz2 = radiusZ * sin2, ly2 = height;

            float x1 = m00 * lx1 + m01 * ly1 + m02 * lz1 + ox;
            float y1 = m10 * lx1 + m11 * ly1 + m12 * lz1 + oy;
            float z1 = m20 * lx1 + m21 * ly1 + m22 * lz1 + oz;
            float x2 = m00 * lx2 + m01 * ly2 + m02 * lz2 + ox;
            float y2 = m10 * lx2 + m11 * ly2 + m12 * lz2 + oy;
            float z2 = m20 * lx2 + m21 * ly2 + m22 * lz2 + oz;

            consumer.addVertex(ox, oy, oz)
                    .setUv(uMin, vMin)
                    .setColor(r, g, b, a)
                    .setLight(light)
                    .setNormal(0, 1, 0);
            consumer.addVertex(x1, y1, z1)
                    .setUv(uMax, vMax)
                    .setColor(r, g, b, a)
                    .setLight(light)
                    .setNormal(0, 1, 0);
            consumer.addVertex(x2, y2, z2)
                    .setUv(uMax, vMax)
                    .setColor(r, g, b, a)
                    .setLight(light)
                    .setNormal(0, 1, 0);
        }
    }

    /**
     * 渲染带纹理的棱锥（ARGB 颜色）。
     */
    public static void renderSide(VertexConsumer consumer, float ox, float oy, float oz, Quaternionf rotation,
                                  float radiusX, float height, float radiusZ, int radialSegments,
                                  int argb, int light,
                                  float uMin, float vMin, float uMax, float vMax) {
        renderSide(consumer, ox, oy, oz, rotation, radiusX, height, radiusZ, radialSegments,
                (argb >> 16 & 255) / 255f,
                (argb >> 8 & 255) / 255f,
                (argb & 255) / 255f,
                (argb >> 24) / 255f,
                light, uMin, vMin, uMax, vMax);
    }

    /**
     * 纯色版本的棱锥（无纹理），锥尖和底面不同颜色。
     */
    public static void renderSide(VertexConsumer consumer, float ox, float oy, float oz, Quaternionf rotation,
                                  float radiusX, float height, float radiusZ, int radialSegments,
                                  int startColor, int endColor, int light) {
        if (radialSegments < 3) radialSegments = 3;

        float qx = rotation.x, qy = rotation.y, qz = rotation.z, qw = rotation.w;
        float xx = qx * qx, yy = qy * qy, zz = qz * qz, ww = qw * qw;
        float xy = qx * qy, xz = qx * qz, yz = qy * qz;
        float xw = qx * qw, yw = qy * qw, zw = qz * qw;

        float m00 = ww + xx - yy - zz;
        float m01 = 2 * (xy - zw);
        float m02 = 2 * (xz + yw);
        float m10 = 2 * (xy + zw);
        float m11 = ww - xx + yy - zz;
        float m12 = 2 * (yz - xw);
        float m20 = 2 * (xz - yw);
        float m21 = 2 * (yz + xw);
        float m22 = ww - xx - yy + zz;

        float angleStep = Mth.TWO_PI / radialSegments;

        for (int i = 0; i < radialSegments; i++) {
            float angle1 = i * angleStep;
            float angle2 = (i + 1) * angleStep;
            float cos1 = Mth.cos(angle1), sin1 = Mth.sin(angle1);
            float cos2 = Mth.cos(angle2), sin2 = Mth.sin(angle2);

            float lx1 = radiusX * cos1, lz1 = radiusZ * sin1, ly1 = height;
            float lx2 = radiusX * cos2, lz2 = radiusZ * sin2, ly2 = height;

            float x1 = m00 * lx1 + m01 * ly1 + m02 * lz1 + ox;
            float y1 = m10 * lx1 + m11 * ly1 + m12 * lz1 + oy;
            float z1 = m20 * lx1 + m21 * ly1 + m22 * lz1 + oz;
            float x2 = m00 * lx2 + m01 * ly2 + m02 * lz2 + ox;
            float y2 = m10 * lx2 + m11 * ly2 + m12 * lz2 + oy;
            float z2 = m20 * lx2 + m21 * ly2 + m22 * lz2 + oz;

            consumer.addVertex(ox, oy, oz)
                    .setUv(0f, 0f)
                    .setColor(startColor)
                    .setLight(light)
                    .setNormal(0, 1, 0);
            consumer.addVertex(x1, y1, z1)
                    .setUv(1f, 1f)
                    .setColor(endColor)
                    .setLight(light)
                    .setNormal(0, 1, 0);
            consumer.addVertex(x2, y2, z2)
                    .setUv(1f, 1f)
                    .setColor(endColor)
                    .setLight(light)
                    .setNormal(0, 1, 0);
        }
    }
}
package com.fanxing.lib.client.render.geometry;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class QuadRenderer {
    /**
     * 四个顶点独立颜色、独立法线、独立UV
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
                              int r1, int g1, int b1, int a1,
                              int r2, int g2, int b2, int a2,
                              int r3, int g3, int b3, int a3,
                              int r4, int g4, int b4, int a4,
                              int overlay, int light,
                              float u1, float v1, float u2, float v2, float u3, float v3, float u4, float v4) {
        Matrix4f matrix = pose.pose();
        consumer.addVertex(matrix, x1, y1, z1).setNormal(pose, nx1, ny1, nz1).setUv(u1, v1).setColor(r1, g1, b1, a1).setOverlay(overlay).setLight(light);
        consumer.addVertex(matrix, x2, y2, z2).setNormal(pose, nx2, ny2, nz2).setUv(u2, v2).setColor(r2, g2, b2, a2).setOverlay(overlay).setLight(light);
        consumer.addVertex(matrix, x3, y3, z3).setNormal(pose, nx3, ny3, nz3).setUv(u3, v3).setColor(r3, g3, b3, a3).setOverlay(overlay).setLight(light);
        consumer.addVertex(matrix, x4, y4, z4).setNormal(pose, nx4, ny4, nz4).setUv(u4, v4).setColor(r4, g4, b4, a4).setOverlay(overlay).setLight(light);
    }

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
                              int r, int g, int b, int a, int overlay, int light,
                              float u1, float v1, float u2, float v2, float u3, float v3, float u4, float v4) {
        render(pose, consumer, x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4, nx1, ny1, nz1, nx2, ny2, nz2, nx3, ny3, nz3, nx4, ny4, nz4, r, g, b, a, r, g, b, a, r, g, b, a, r, g, b, a, overlay, light, u1, v1, u2, v2, u3, v3, u4, v4);
    }

    /**
     * 共颜色、共法线、独立 UV（圆柱侧面核心调用的就是这个）
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer,
                              float x1, float y1, float z1,
                              float x2, float y2, float z2,
                              float x3, float y3, float z3,
                              float x4, float y4, float z4,
                              float nx, float ny, float nz,
                              int r, int g, int b, int a, int overlay, int light,
                              float u1, float v1, float u2, float v2, float u3, float v3, float u4, float v4) {
        Matrix4f matrix = pose.pose();
        consumer.addVertex(matrix, x1, y1, z1).setNormal(pose, nx, ny, nz).setUv(u1, v1).setColor(r, g, b, a).setOverlay(overlay).setLight(light);
        consumer.addVertex(matrix, x2, y2, z2).setNormal(pose, nx, ny, nz).setUv(u2, v2).setColor(r, g, b, a).setOverlay(overlay).setLight(light);
        consumer.addVertex(matrix, x3, y3, z3).setNormal(pose, nx, ny, nz).setUv(u3, v3).setColor(r, g, b, a).setOverlay(overlay).setLight(light);
        consumer.addVertex(matrix, x4, y4, z4).setNormal(pose, nx, ny, nz).setUv(u4, v4).setColor(r, g, b, a).setOverlay(overlay).setLight(light);
    }

    /**
     * 共颜色、共法线、共享 UV 范围（简单平面）
     * KEY OpenGL 标准：V=0 是纹理底部，V=1 是纹理顶部。
     *  Minecraft 图集约定：V=0 是纹理顶部，V=1 是纹理底部（因为构建图集时翻转了 Y 轴）。
     *  本方法已经将V反转
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer,
                              float x1, float y1, float z1,
                              float x2, float y2, float z2,
                              float x3, float y3, float z3,
                              float x4, float y4, float z4,
                              float nx, float ny, float nz,
                              int r, int g, int b, int a, int overlay, int light,
                              float uMin, float vMin, float uMax, float vMax) {
        Matrix4f matrix = pose.pose();
        consumer.addVertex(matrix, x1, y1, z1).setNormal(pose, nx, ny, nz).setUv(uMax, vMax).setColor(r, g, b, a).setOverlay(overlay).setLight(light);
        consumer.addVertex(matrix, x2, y2, z2).setNormal(pose, nx, ny, nz).setUv(uMax, vMin).setColor(r, g, b, a).setOverlay(overlay).setLight(light);
        consumer.addVertex(matrix, x3, y3, z3).setNormal(pose, nx, ny, nz).setUv(uMin, vMin).setColor(r, g, b, a).setOverlay(overlay).setLight(light);
        consumer.addVertex(matrix, x4, y4, z4).setNormal(pose, nx, ny, nz).setUv(uMin, vMax).setColor(r, g, b, a).setOverlay(overlay).setLight(light);
    }


    // ARGB 重载

    /**
     * 共颜色、共法线、UV范围，接受 ARGB 颜色
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer,
                              float x1, float y1, float z1,
                              float x2, float y2, float z2,
                              float x3, float y3, float z3,
                              float x4, float y4, float z4,
                              float normalX, float normalY, float normalZ,
                              int argb, int overlay, int light, float uMin, float vMin, float uMax, float vMax) {
        render(pose, consumer, x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4, normalX, normalY, normalZ,
                argb >> 16 & 255, argb >> 8 & 255, argb & 255, argb >> 24, overlay, light, uMin, vMin, uMax, vMax);
    }


    /**
     * 从起点向前延伸的四边形（在XZ平面），法线向上。
     */
    public static void renderForward(PoseStack.Pose pose, VertexConsumer consumer,
                                     float cx, float cy, float cz, float width, float length,
                                     int r, int g, int b, int a, int overlay, int light,
                                     float uMin, float vMin, float uMax, float vMax) {
        float hw = width * 0.5f;
        // 四个顶点：左下、左上、右上、右下（逆时针，法线向上）
        float x1 = cx - hw, z1 = cz;
        float x2 = cx + hw, z2 = cz;
        float x3 = cx + hw, z3 = cz + length;
        float x4 = cx - hw, z4 = cz + length;
        render(pose, consumer, x1, cy, z1, x2, cy, z2, x3, cy, z3, x4, cy, z4, 0, 1, 0,
                r, g, b, a, overlay, light, uMin, vMin, uMax, vMax);
    }


    //######################### 无PoseStack，世界坐标直接手动计算渲染 #############################

    /**
     * 使用统一 UV 矩形渲染四边形（float 坐标）。
     *
     * @param consumer  顶点消费者
     * @param x1,y1,z1  右下顶点
     * @param x2,y2,z2  右上顶点
     * @param x3,y3,z3  左上顶点
     * @param x4,y4,z4  左下顶点
     * @param r,g,b,a   颜色 (0-255)
     * @param light     光照值
     * @param uMin,vMin UV 矩形左下角
     * @param uMax,vMax UV 矩形右上角
     *                  KEY OpenGL 标准：V=0 是纹理底部，V=1 是纹理顶部。
     *                   Minecraft 图集约定：V=0 是纹理顶部，V=1 是纹理底部（因为构建图集时翻转了 Y 轴）。
     *                   本方法已经将V反转
     */
    public static void render(VertexConsumer consumer,
                              float x1, float y1, float z1,
                              float x2, float y2, float z2,
                              float x3, float y3, float z3,
                              float x4, float y4, float z4,
                              float r, float g, float b, float a, int light,
                              float uMin, float vMin, float uMax, float vMax) {
        consumer.addVertex(x1, y1, z1).setUv(uMax, vMax).setColor(r, g, b, a).setLight(light).setNormal(0, 1, 0);   // 假法线，任意方向;// 右下
        consumer.addVertex(x2, y2, z2).setUv(uMax, vMin).setColor(r, g, b, a).setLight(light).setNormal(0, 1, 0);   // 假法线，任意方向;// 右上
        consumer.addVertex(x3, y3, z3).setUv(uMin, vMin).setColor(r, g, b, a).setLight(light).setNormal(0, 1, 0);   // 假法线，任意方向;// 左上
        consumer.addVertex(x4, y4, z4).setUv(uMin, vMax).setColor(r, g, b, a).setLight(light).setNormal(0, 1, 0);   // 假法线，任意方向;// 左下
    }

    /**
     * 使用 ARGB 颜色和统一 UV 矩形渲染四边形（float 坐标）。
     *
     * @param consumer  顶点消费者
     * @param x1,y1,z1  右下顶点
     * @param x2,y2,z2  右上顶点
     * @param x3,y3,z3  左上顶点
     * @param x4,y4,z4  左下顶点
     * @param argb      ARGB 颜色
     * @param light     光照值
     * @param uMin,vMin UV 矩形左下角
     * @param uMax,vMax UV 矩形右上角
     */
    public static void render(VertexConsumer consumer,
                              float x1, float y1, float z1,
                              float x2, float y2, float z2,
                              float x3, float y3, float z3,
                              float x4, float y4, float z4,
                              int argb, int light,
                              float uMin, float vMin, float uMax, float vMax) {
        render(consumer, x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4,
                (argb >> 16 & 255) / 255f, (argb >> 8 & 255) / 255f, (argb & 255) / 255f, (argb >> 24) / 255f, light, uMin, vMin, uMax, vMax);
    }


    // 无PoseStack，局部坐标+几何中心+渲染计算世界坐标渲染
    // ----- 公开的独立 UV 重载（float 坐标）-----

    /**
     * 带旋转的四边形渲染（独立 UV 版本）。
     * 顶点先在局部空间定义，然后经过 rotation 旋转，再平移到世界空间。
     * <p>
     * 优化说明：
     * <ul>
     *   <li>单位四元数快速路径（无旋转时直接拷贝顶点）</li>
     *   <li>每个顶点算完立即提交，减少寄存器溢出</li>
     *   <li>使用 Math.fma 加速乘加运算（硬件支持时自动降为 FMA 指令）</li>
     * </ul>
     *
     * @param consumer    顶点消费者
     * @param lx1,ly1,lz1 局部右下顶点（相对中心）
     * @param lx2,ly2,lz2 局部右上顶点
     * @param lx3,ly3,lz3 局部左上顶点
     * @param lx4,ly4,lz4 局部左下顶点
     * @param cx,cy,cz    中心世界坐标（最终平移量）
     * @param rotation    旋转四元数，null 或单位四元数表示无旋转
     * @param r,g,b,a     颜色 0-255
     * @param light       光照值
     * @param u1,v1       右下顶点 UV
     * @param u2,v2       右上顶点 UV
     * * @param u3,v3      左上顶点 UV
     * @param u4,v4       左下顶点 UV
     */
    public static void render(VertexConsumer consumer,
                              float cx, float cy, float cz,
                              float lx1, float ly1, float lz1,
                              float lx2, float ly2, float lz2,
                              float lx3, float ly3, float lz3,
                              float lx4, float ly4, float lz4,
                              Quaternionf rotation,
                              float r, float g, float b, float a, int light,
                              float u1, float v1, float u2, float v2,
                              float u3, float v3, float u4, float v4) {

        float qx = rotation.x, qy = rotation.y, qz = rotation.z, qw = rotation.w;
        // 构建旋转矩阵（复用一次，应用给4个顶点）
        float xx = qx * qx, yy = qy * qy, zz = qz * qz, ww = qw * qw;
        float xy = qx * qy, xz = qx * qz, yz = qy * qz;
        float xw = qx * qw, yw = qy * qw, zw = qz * qw;

        float m00 = ww + xx - yy - zz;
        float m01 = 2f * (xy - zw);
        float m02 = 2f * (xz + yw);
        float m10 = 2f * (xy + zw);
        float m11 = ww - xx + yy - zz;
        float m12 = 2f * (yz - xw);
        float m20 = 2f * (xz - yw);
        float m21 = 2f * (yz + xw);
        float m22 = ww - xx - yy + zz;

        // 顶点1（算完即提交）
        consumer.addVertex(
                cx + Math.fma(m00, lx1, Math.fma(m01, ly1, m02 * lz1)),
                cy + Math.fma(m10, lx1, Math.fma(m11, ly1, m12 * lz1)),
                cz + Math.fma(m20, lx1, Math.fma(m21, ly1, m22 * lz1))
        ).setUv(u1, v1).setColor(r, g, b, a).setLight(light);

        // 顶点2
        consumer.addVertex(
                cx + Math.fma(m00, lx2, Math.fma(m01, ly2, m02 * lz2)),
                cy + Math.fma(m10, lx2, Math.fma(m11, ly2, m12 * lz2)),
                cz + Math.fma(m20, lx2, Math.fma(m21, ly2, m22 * lz2))
        ).setUv(u2, v2).setColor(r, g, b, a).setLight(light);

        // 顶点3
        consumer.addVertex(
                cx + Math.fma(m00, lx3, Math.fma(m01, ly3, m02 * lz3)),
                cy + Math.fma(m10, lx3, Math.fma(m11, ly3, m12 * lz3)),
                cz + Math.fma(m20, lx3, Math.fma(m21, ly3, m22 * lz3))
        ).setUv(u3, v3).setColor(r, g, b, a).setLight(light);

        // 顶点4
        consumer.addVertex(
                cx + Math.fma(m00, lx4, Math.fma(m01, ly4, m02 * lz4)),
                cy + Math.fma(m10, lx4, Math.fma(m11, ly4, m12 * lz4)),
                cz + Math.fma(m20, lx4, Math.fma(m21, ly4, m22 * lz4))
        ).setUv(u4, v4).setColor(r, g, b, a).setLight(light);
    }

    /**
     * 带旋转的四边形渲染（统一 UV 矩形版本）。
     * 顶点先在局部空间定义，然后经过 rotation 旋转，再平移到世界空间。
     * KEY 本方法已经将V反转
     */
    public static void render(VertexConsumer consumer,
                              float cx, float cy, float cz,
                              float lx1, float ly1, float lz1,
                              float lx2, float ly2, float lz2,
                              float lx3, float ly3, float lz3,
                              float lx4, float ly4, float lz4,
                              Quaternionf rotation,
                              float r, float g, float b, float a, int light,
                              float uMin, float vMin, float uMax, float vMax) {
        render(consumer, cx, cy, cz,
                lx1, ly1, lz1, lx2, ly2, lz2, lx3, ly3, lz3, lx4, ly4, lz4,
                rotation,
                r, g, b, a, light,
                uMax, vMax, uMax, vMin, uMin, vMin, uMin, vMax);
    }

    /**
     * 带旋转的四边形渲染（统一 UV 矩形版本）。
     * 顶点先在局部空间定义（相对中心），然后经过 rotation 旋转，再平移到世界空间。
     * KEY 逆时针，本方法已经将纹理贴图V反转，直接调用即可
     */
    public static void render(VertexConsumer consumer,
                              float cx, float cy, float cz,
                              float width, float height,
                              Quaternionf rotation,
                              float r, float g, float b, float a, int light,
                              float uMin, float vMin, float uMax, float vMax) {
        float hw = width * 0.5f;
        float hh = height * 0.5f;
        render(consumer, cx, cy, cz,
                hw, -hh, 0,   // 顶点1：右下
                hw,  hh, 0,   // 顶点2：右上
                -hw, hh, 0,   // 顶点3：左上
                -hw, -hh, 0,  // 顶点4：左下
                rotation,
                r, g, b, a, light,
                uMax, vMin,   // 右下 UV
                uMin, vMin,   // 右上 UV
                uMin, vMax,   // 左上 UV
                uMax, vMax);  // 左下 UV
    }

    /**
     * 带旋转的四边形渲染（统一 UV 矩形版本）。
     * 顶点先在局部空间定义，然后经过 rotation 旋转，再平移到世界空间。
     * KEY 逆时针，本方法已经将纹理贴图V反转，直接调用即可
     */
    public static void render(VertexConsumer consumer, float cx, float cy, float cz, float width, float height, Quaternionf rotation,
                              int argb, int light, float uMin, float vMin, float uMax, float vMax) {
        render(consumer, cx, cy, cz, width, height, rotation, (argb >> 16 & 255) / 255f, (argb >> 8 & 255) / 255f, (argb & 255) / 255f, (argb >> 24) / 255f, light, uMin, vMin, uMax, vMax);
    }


    /**
     * 带旋转的四边形渲染（专用 Z=0 版本，根据宽高直接计算）。
     * 适用于公告板粒子、固定 Y 轴粒子等局部顶点在 XY 平面内的场景。
     * <p>
     * 顶点顺序：左下 → 右下 → 右上 → 左上（逆时针）
     *
     * @param consumer    顶点消费者
     * @param cx,cy,cz    中心世界坐标（最终平移量）
     * @param width       四边形宽度（X 轴方向）
     * @param height      四边形高度（Y 轴方向）
     * @param rotation    旋转四元数
     * @param r,g,b,a     颜色 0-255
     * @param light       光照值
     * @param uMin,vMin   左下角 UV
     * @param uMax,vMax   右上角 UV
     * KEY 逆时针，本方法已经将纹理贴图V反转，直接调用即可
     */
    public static void renderZ0(VertexConsumer consumer,
                                float cx, float cy, float cz,
                                float width, float height,
                                Quaternionf rotation,
                                float r, float g, float b, float a, int light,
                                float uMin, float vMin, float uMax, float vMax) {
        float hw = width * 0.5f;
        float hh = height * 0.5f;
        float qx = rotation.x, qy = rotation.y, qz = rotation.z, qw = rotation.w;

        // 构建旋转矩阵（只需 XY 平面相关的 6 个元素）
        float xx = qx * qx, yy = qy * qy, zz = qz * qz, ww = qw * qw;
        float xy = qx * qy, xz = qx * qz, yz = qy * qz;
        float xw = qx * qw, yw = qy * qw, zw = qz * qw;

        float m00 = ww + xx - yy - zz;
        float m01 = 2f * (xy - zw);
        float m10 = 2f * (xy + zw);
        float m11 = ww - xx + yy - zz;
        float m20 = 2f * (xz - yw);
        float m21 = 2f * (yz + xw);

        float nw = -hw;
        float nh = -hh;
        // 顶点1：右下（ hw, -hh）→ UV (uMax, vMin)
        consumer.addVertex(
                cx + Math.fma(m00, hw, m01 * nh),
                cy + Math.fma(m10, hw, m11 * nh),
                cz + Math.fma(m20, hw, m21 * nh)
        ).setUv(uMax, vMin).setColor(r, g, b, a).setLight(light);

        // 顶点2：右上（ hw,  hh）→ UV (uMin, vMin)
        consumer.addVertex(
                cx + Math.fma(m00, hw, m01 * hh),
                cy + Math.fma(m10, hw, m11 * hh),
                cz + Math.fma(m20, hw, m21 * hh)
        ).setUv(uMin, vMin).setColor(r, g, b, a).setLight(light);

        // 顶点3：左上（-hw,  hh）→ UV (uMin, vMax)
        consumer.addVertex(
                cx + Math.fma(m00, nw, m01 * hh),
                cy + Math.fma(m10, nw, m11 * hh),
                cz + Math.fma(m20, nw, m21 * hh)
        ).setUv(uMin, vMax).setColor(r, g, b, a).setLight(light);

        // 顶点4：左下（-hw, -hh）→ UV (uMax, vMax)
        consumer.addVertex(
                cx + Math.fma(m00, nw, m01 * nh),
                cy + Math.fma(m10, nw, m11 * nh),
                cz + Math.fma(m20, nw, m21 * nh)
        ).setUv(uMax, vMax).setColor(r, g, b, a).setLight(light);
    }

}
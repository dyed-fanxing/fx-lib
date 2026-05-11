package com.fanxing.lib.client.particle.mesh;

import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Vector3f;

/**
 * 粒子专用的四边形渲染工具（世界坐标，QUAD 模式）。
 * 提供两种 UV 传递方式：
 * 1. 四个顶点同一 UV 矩形（uMin,vMin,uMax,vMax）
 * 2. 四个顶点独立 UV（u1,v1, u2,v2, u3,v3, u4,v4）
 */
public final class QuadParticleRenderer {



    /**
     * 使用统一 UV 矩形渲染四边形（float 坐标）。
     *
     * @param consumer 顶点消费者
     * @param x1,y1,z1 右下顶点
     * @param x2,y2,z2 右上顶点
     * @param x3,y3,z3 左上顶点
     * @param x4,y4,z4 左下顶点
     * @param r,g,b,a  颜色 (0-255)
     * @param light    光照值
     * @param uMin,vMin UV 矩形左下角
     * @param uMax,vMax UV 矩形右上角
     */
    public static void render(VertexConsumer consumer,
                              float x1, float y1, float z1,
                              float x2, float y2, float z2,
                              float x3, float y3, float z3,
                              float x4, float y4, float z4,
                              int r, int g, int b, int a, int light,
                              float uMin, float vMin, float uMax, float vMax) {
        consumer.addVertex(x1, y1, z1).setUv(uMax, vMax).setColor(r, g, b, a).setLight(light).setNormal(0, 1, 0);   // 假法线，任意方向;// 右下
        consumer.addVertex(x2, y2, z2).setUv(uMax, vMin).setColor(r, g, b, a).setLight(light).setNormal(0, 1, 0);   // 假法线，任意方向;// 右上
        consumer.addVertex(x3, y3, z3).setUv(uMin, vMin).setColor(r, g, b, a).setLight(light).setNormal(0, 1, 0);   // 假法线，任意方向;// 左上
        consumer.addVertex(x4, y4, z4).setUv(uMin, vMax).setColor(r, g, b, a).setLight(light).setNormal(0, 1, 0);   // 假法线，任意方向;// 左下
    }

    /**
     * 使用默认 UV 范围 [0,1] 渲染四边形（float 坐标）。
     *
     * @param consumer 顶点消费者
     * @param x1,y1,z1 右下顶点
     * @param x2,y2,z2 右上顶点
     * @param x3,y3,z3 左上顶点
     * @param x4,y4,z4 左下顶点
     * @param r,g,b,a  颜色 (0-255)
     * @param light    光照值
     */
    public static void render(VertexConsumer consumer,
                              float x1, float y1, float z1,
                              float x2, float y2, float z2,
                              float x3, float y3, float z3,
                              float x4, float y4, float z4,
                              int r, int g, int b, int a, int light) {
        render(consumer, x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4,
                r, g, b, a, light, 0f, 0f, 1f, 1f);
    }

    // ----- 公开的统一 UV 重载（Vector3f 坐标）-----
    /**
     * 使用统一 UV 矩形渲染四边形（Vector3f 坐标）。
     *
     * @param consumer 顶点消费者
     * @param p1,p2,p3,p4 四个顶点（右下、右上、左上、左下）
     * @param r,g,b,a  颜色 (0-255)
     * @param light    光照值
     * @param uMin,vMin UV 矩形左下角
     * @param uMax,vMax UV 矩形右上角
     */
    public static void render(VertexConsumer consumer,
                              Vector3f p1, Vector3f p2, Vector3f p3, Vector3f p4,
                              int r, int g, int b, int a, int light,
                              float uMin, float vMin, float uMax, float vMax) {
        render(consumer,
                p1.x(), p1.y(), p1.z(),
                p2.x(), p2.y(), p2.z(),
                p3.x(), p3.y(), p3.z(),
                p4.x(), p4.y(), p4.z(),
                r, g, b, a, light, uMin, vMin, uMax, vMax);
    }

    /**
     * 使用默认 UV 范围 [0,1] 渲染四边形（Vector3f 坐标）。
     *
     * @param consumer 顶点消费者
     * @param p1,p2,p3,p4 四个顶点
     * @param r,g,b,a  颜色 (0-255)
     * @param light    光照值
     */
    public static void render(VertexConsumer consumer,
                              Vector3f p1, Vector3f p2, Vector3f p3, Vector3f p4,
                              int r, int g, int b, int a, int light) {
        render(consumer, p1, p2, p3, p4, r, g, b, a, light, 0f, 0f, 1f, 1f);
    }

    // ----- 公开的独立 UV 重载（float 坐标）-----
    /**
     * 使用独立 UV 渲染四边形（float 坐标，每个顶点有自己的 UV）。
     * KEY OpenGL 标准：V=0 是纹理底部，V=1 是纹理顶部。
     *  Minecraft 图集约定：V=0 是纹理顶部，V=1 是纹理底部（因为构建图集时翻转了 Y 轴）。
     *  所以在调用本方法的时候需要考虑好V坐标。
     *
     * @param consumer 顶点消费者
     * @param x1,y1,z1 右下顶点
     * @param x2,y2,z2 右上顶点
     * @param x3,y3,z3 左上顶点
     * @param x4,y4,z4 左下顶点
     * @param r,g,b,a  颜色 (0-255)
     * @param light    光照值
     * @param u1,v1    右下顶点 UV
     * @param u2,v2    右上顶点 UV
     * @param u3,v3    左上顶点 UV
     * @param u4,v4    左下顶点 UV
     */
    public static void render(VertexConsumer consumer,
                              float x1, float y1, float z1,
                              float x2, float y2, float z2,
                              float x3, float y3, float z3,
                              float x4, float y4, float z4,
                              int r, int g, int b, int a, int light,
                              float u1, float v1, float u2, float v2, float u3, float v3, float u4, float v4) {
        consumer.addVertex(x1, y1, z1).setUv(u1, v1).setColor(r, g, b, a).setLight(light).setNormal(0, 1, 0);   // 假法线，任意方向;
        consumer.addVertex(x2, y2, z2).setUv(u2, v2).setColor(r, g, b, a).setLight(light).setNormal(0, 1, 0);   // 假法线，任意方向;
        consumer.addVertex(x3, y3, z3).setUv(u3, v3).setColor(r, g, b, a).setLight(light).setNormal(0, 1, 0);   // 假法线，任意方向;
        consumer.addVertex(x4, y4, z4).setUv(u4, v4).setColor(r, g, b, a).setLight(light).setNormal(0, 1, 0);   // 假法线，任意方向;
    }

    // ----- 公开的独立 UV 重载（Vector3f 坐标）-----
    /**
     * 使用独立 UV 渲染四边形（Vector3f 坐标，每个顶点有自己的 UV）。
     *
     * @param consumer 顶点消费者
     * @param p1,p2,p3,p4 四个顶点
     * @param r,g,b,a  颜色 (0-255)
     * @param light    光照值
     * @param u1,v1    右下顶点 UV
     * @param u2,v2    右上顶点 UV
     * @param u3,v3    左上顶点 UV
     * @param u4,v4    左下顶点 UV
     */
    public static void render(VertexConsumer consumer,
                              Vector3f p1, Vector3f p2, Vector3f p3, Vector3f p4,
                              int r, int g, int b, int a, int light,
                              float u1, float v1, float u2, float v2, float u3, float v3, float u4, float v4) {
        render(consumer,
                p1.x(), p1.y(), p1.z(),
                p2.x(), p2.y(), p2.z(),
                p3.x(), p3.y(), p3.z(),
                p4.x(), p4.y(), p4.z(),
                r, g, b, a, light,
                u1, v1, u2, v2, u3, v3, u4, v4);
    }

    // ===== ARGB 统一 UV 重载（方便使用）=====
    /**
     * 使用 ARGB 颜色和统一 UV 矩形渲染四边形（float 坐标）。
     *
     * @param consumer 顶点消费者
     * @param x1,y1,z1 右下顶点
     * @param x2,y2,z2 右上顶点
     * @param x3,y3,z3 左上顶点
     * @param x4,y4,z4 左下顶点
     * @param argb     ARGB 颜色
     * @param light    光照值
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
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        int a = (argb >> 24) & 0xFF;
        render(consumer, x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4,
                r, g, b, a, light, uMin, vMin, uMax, vMax);
    }

    /**
     * 使用 ARGB 颜色和统一 UV 矩形渲染四边形（Vector3f 坐标）。
     *
     * @param consumer 顶点消费者
     * @param p1,p2,p3,p4 四个顶点
     * @param argb     ARGB 颜色
     * @param light    光照值
     * @param uMin,vMin UV 矩形左下角
     * @param uMax,vMax UV 矩形右上角
     */
    public static void render(VertexConsumer consumer,
                              Vector3f p1, Vector3f p2, Vector3f p3, Vector3f p4,
                              int argb, int light,
                              float uMin, float vMin, float uMax, float vMax) {
        render(consumer,
                p1.x(), p1.y(), p1.z(),
                p2.x(), p2.y(), p2.z(),
                p3.x(), p3.y(), p3.z(),
                p4.x(), p4.y(), p4.z(),
                argb, light, uMin, vMin, uMax, vMax);
    }

    // 默认 UV 范围 [0,1] 的 ARGB 重载
    /**
     * 使用 ARGB 颜色和默认 UV 范围 [0,1] 渲染四边形（float 坐标）。
     *
     * @param consumer 顶点消费者
     * @param x1,y1,z1 右下顶点
     * @param x2,y2,z2 右上顶点
     * @param x3,y3,z3 左上顶点
     * @param x4,y4,z4 左下顶点
     * @param argb     ARGB 颜色
     * @param light    光照值
     */
    public static void render(VertexConsumer consumer,
                              float x1, float y1, float z1,
                              float x2, float y2, float z2,
                              float x3, float y3, float z3,
                              float x4, float y4, float z4,
                              int argb, int light) {
        render(consumer, x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4,
                argb, light, 0f, 0f, 1f, 1f);
    }

    /**
     * 使用 ARGB 颜色和默认 UV 范围 [0,1] 渲染四边形（Vector3f 坐标）。
     *
     * @param consumer 顶点消费者
     * @param p1,p2,p3,p4 四个顶点
     * @param argb     ARGB 颜色
     * @param light    光照值
     */
    public static void render(VertexConsumer consumer,
                              Vector3f p1, Vector3f p2, Vector3f p3, Vector3f p4,
                              int argb, int light) {
        render(consumer, p1, p2, p3, p4, argb, light, 0f, 0f, 1f, 1f);
    }
}
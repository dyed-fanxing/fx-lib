package com.fanxing.lib.client.render.shape;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.FastColor;
import org.joml.Vector3f;

public class CubeRenderer {

    /**
     * 几何中心点渲染，UV缩放和偏移
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer, Vector3f center,
                              float length, float width, float height,
                              int r, int g, int b, int a, int overlay, int light,
                              float uScale, float vScale, float uOffset, float vOffset) {
        float l = length * 0.5f;
        float w = width * 0.5f;
        float h = height * 0.5f;
        float cx = center.x();
        float cy = center.y();
        float cz = center.z();
        // 最终 UV = (原始范围 0-1) * uScale + uOffset
        float uMin = uOffset;
        float uMax = uScale + uOffset;
        float vMin = vOffset;
        float vMax = vScale + vOffset;
        // 前面 (Z-)
        QuadRenderer.render(pose, consumer,
                new Vector3f(-l + cx, -w + cy, -h + cz),
                new Vector3f(-l + cx,  w + cy, -h + cz),
                new Vector3f( l + cx,  w + cy, -h + cz),
                new Vector3f( l + cx, -w + cy, -h + cz),
                0, 0, -1,
                r, g, b, a, overlay, light, uMin, vMin, uMax, vMax);
        // 后面 (Z+)
        QuadRenderer.render(pose, consumer,
                new Vector3f(-l + cx, -w + cy,  h + cz),
                new Vector3f( l + cx, -w + cy,  h + cz),
                new Vector3f( l + cx,  w + cy,  h + cz),
                new Vector3f(-l + cx,  w + cy,  h + cz),
                0, 0, 1,
                r, g, b, a, overlay, light, uMin, vMin, uMax, vMax);
        // 左面 (X-)
        QuadRenderer.render(pose, consumer,
                new Vector3f(-l + cx, -w + cy, -h + cz),
                new Vector3f(-l + cx, -w + cy,  h + cz),
                new Vector3f(-l + cx,  w + cy,  h + cz),
                new Vector3f(-l + cx,  w + cy, -h + cz),
                -1, 0, 0,
                r, g, b, a, overlay, light, uMin, vMin, uMax, vMax);
        // 右面 (X+)
        QuadRenderer.render(pose, consumer,
                new Vector3f( l + cx, -w + cy, -h + cz),
                new Vector3f( l + cx,  w + cy, -h + cz),
                new Vector3f( l + cx,  w + cy,  h + cz),
                new Vector3f( l + cx, -w + cy,  h + cz),
                1, 0, 0,
                r, g, b, a, overlay, light, uMin, vMin, uMax, vMax);
        // 上面 (Y+)
        QuadRenderer.render(pose, consumer,
                new Vector3f(-l + cx,  w + cy, -h + cz),
                new Vector3f(-l + cx,  w + cy,  h + cz),
                new Vector3f( l + cx,  w + cy,  h + cz),
                new Vector3f( l + cx,  w + cy, -h + cz),
                0, 1, 0,
                r, g, b, a, overlay, light, uMin, vMin, uMax, vMax);
        // 下面 (Y-)
        QuadRenderer.render(pose, consumer,
                new Vector3f(-l + cx, -w + cy, -h + cz),
                new Vector3f( l + cx, -w + cy, -h + cz),
                new Vector3f( l + cx, -w + cy,  h + cz),
                new Vector3f(-l + cx, -w + cy,  h + cz),
                0, -1, 0,
                r, g, b, a, overlay, light, uMin, vMin, uMax, vMax);
    }


    /**
     * 立方体：UV缩放
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer, Vector3f center, float length, float width, float height,
                              int r, int g, int b, int a, int overlay, int light, float uScale, float vScale) {
        render(pose, consumer, center, length, width, height, r, g, b, a, overlay, light, uScale, vScale, 1f, 1f);
    }

    /**
     * 立方体
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer, Vector3f center, float length, float width, float height,
                              int r, int g, int b, int a, int overlay, int light) {
        render(pose, consumer, center, length, width, height, r, g, b, a, overlay, light, 1f, 1f, 1f, 1f);
    }

    /**
     * 端面中心向前渲染，UV 缩放、偏移
     */
    public static void renderFromBackCenter(PoseStack.Pose pose, VertexConsumer consumer, float length, float width, float height,
                                            int r, int g, int b, int a, int overlay, int light,
                                            float uScale, float vScale, float uOffset, float vOffset) {
        float halfW = width * 0.5f;
        Vector3f[] v = new Vector3f[8];
        v[0] = new Vector3f(-halfW, 0, 0);
        v[1] = new Vector3f( halfW, 0, 0);
        v[2] = new Vector3f( halfW, 0, length);
        v[3] = new Vector3f(-halfW, 0, length);
        v[4] = new Vector3f(-halfW, height, 0);
        v[5] = new Vector3f( halfW, height, 0);
        v[6] = new Vector3f( halfW, height, length);
        v[7] = new Vector3f(-halfW, height, length);
        // 直接计算 UV 边界（最终 UV = uOffset ~ uScale+uOffset）
        float uMin = uOffset;
        float uMax = uScale + uOffset;
        float vMin = vOffset;
        float vMax = vScale + vOffset;
        // 前 (Z+)
        QuadRenderer.render(pose, consumer, v[3], v[2], v[6], v[7], 0, 0, 1,
                r, g, b, a, overlay, light, uMin, vMin, uMax, vMax);
        // 后 (Z-)
        QuadRenderer.render(pose, consumer, v[0], v[1], v[5], v[4], 0, 0, -1,
                r, g, b, a, overlay, light, uMin, vMin, uMax, vMax);
        // 左 (X-)
        QuadRenderer.render(pose, consumer, v[0], v[4], v[7], v[3], -1, 0, 0,
                r, g, b, a, overlay, light, uMin, vMin, uMax, vMax);
        // 右 (X+)
        QuadRenderer.render(pose, consumer, v[1], v[2], v[6], v[5], 1, 0, 0,
                r, g, b, a, overlay, light, uMin, vMin, uMax, vMax);
        // 上 (Y+)
        QuadRenderer.render(pose, consumer, v[4], v[5], v[6], v[7], 0, 1, 0,
                r, g, b, a, overlay, light, uMin, vMin, uMax, vMax);
        // 下 (Y-)
        QuadRenderer.render(pose, consumer, v[0], v[3], v[2], v[1], 0, -1, 0,
                r, g, b, a, overlay, light, uMin, vMin, uMax, vMax);
    }

    /**
     * 立方体：UV缩放
     */
    public static void renderFromBackCenter(PoseStack.Pose pose, VertexConsumer consumer, float length, float width, float height,
                                            int r, int g, int b, int a, int overlay, int light, float uScale, float vScale) {
        renderFromBackCenter(pose, consumer, length, width, height, r, g, b, a, overlay, light, uScale, vScale, 1f, 1f);
    }

    /**
     * 立方体
     */
    public static void renderFromBackCenter(PoseStack.Pose pose, VertexConsumer consumer, float length, float width, float height,
                                            int r, int g, int b, int a, int overlay, int light) {
        renderFromBackCenter(pose, consumer, length, width, height, r, g, b, a, overlay, light, 1f, 1f);
    }



    /**
     * 以一个顶点为基准渲染线框
     */
    public static void renderOutlineByVertex(PoseStack.Pose pose, VertexConsumer consumer,float length, float width, float height,int r, int g, int b, int a) {
        Vector3f[] vertices = new Vector3f[8];
        vertices[0] = new Vector3f(0, 0, 0);
        vertices[1] = new Vector3f(length, 0, 0);
        vertices[2] = new Vector3f(length, 0, width);
        vertices[3] = new Vector3f(0, 0, width);
        vertices[4] = new Vector3f(0, height, 0);
        vertices[5] = new Vector3f(length, height, 0);
        vertices[6] = new Vector3f(length, height, width);
        vertices[7] = new Vector3f(0, height, width);
        renderLineBox(pose, consumer, vertices, r, g, b, a);
    }
    /**
     * 以几何中心为基准渲染线框
     */
    public static void renderOutline(PoseStack.Pose pose, VertexConsumer consumer,float length, float width, float height,int r, int g, int b, int a) {
        float halfW = width * 0.5f;
        Vector3f[] vertices = new Vector3f[8];
        vertices[0] = new Vector3f(-halfW, 0, 0);
        vertices[1] = new Vector3f(halfW, 0, 0);
        vertices[2] = new Vector3f(halfW, 0, length);
        vertices[3] = new Vector3f(-halfW, 0, length);
        vertices[4] = new Vector3f(-halfW, height, 0);
        vertices[5] = new Vector3f(halfW, height, 0);
        vertices[6] = new Vector3f(halfW, height, length);
        vertices[7] = new Vector3f(-halfW, height, length);
        renderLineBox(pose, consumer, vertices, r, g, b, a);
    }
    /**
     * 绘制立方体线框（通用方法）
     * @param vertices 8个顶点，顺序：底面4个（逆时针），顶面4个（对应底面顶点向上偏移）
     */
    private static void renderLineBox(PoseStack.Pose pose, VertexConsumer consumer, Vector3f[] vertices,
                                      int r, int g, int b, int a) {
        int[][] edges = {
                {0,1}, {1,2}, {2,3}, {3,0}, // 底面
                {4,5}, {5,6}, {6,7}, {7,4}, // 顶面
                {0,4}, {1,5}, {2,6}, {3,7}  // 垂直棱
        };
        for (int[] edge : edges) {
            LineRenderer.render(pose, consumer, vertices[edge[0]], vertices[edge[1]], r, g, b, a);
        }
    }









    // ARGB 重载（带偏移）
    public static void render(PoseStack.Pose pose, VertexConsumer consumer, Vector3f center, float length, float width, float height,
                              int argb, int overlay, int light, float uScale, float vScale, float uOffset, float vOffset) {
        render(pose, consumer, center, length, width, height,
                FastColor.ARGB32.red(argb), FastColor.ARGB32.green(argb), FastColor.ARGB32.blue(argb), FastColor.ARGB32.alpha(argb), overlay, light, uScale, vScale, uOffset, vOffset);
    }

    // ARGB 重载（无偏移，默认 uScale=1, vScale=1）
    public static void render(PoseStack.Pose pose, VertexConsumer consumer, Vector3f center, float length, float width, float height,
                              int argb, int overlay, int light) {
        render(pose, consumer, center, length, width, height, argb, overlay, light, 1f, 1f, 0f, 0f);
    }

    // ARGB 重载（带缩放，无偏移）
    public static void render(PoseStack.Pose pose, VertexConsumer consumer, Vector3f center, float length, float width, float height,
                              int argb, int overlay, int light, float uScale, float vScale) {
        render(pose, consumer, center, length, width, height, argb, overlay, light, uScale, vScale, 0f, 0f);
    }




    // ARGB 重载（带偏移）
    public static void renderFromBackCenter(PoseStack.Pose pose, VertexConsumer consumer, float length, float width, float height,
                                            int argb, int overlay, int light, float uScale, float vScale, float uOffset, float vOffset) {
        renderFromBackCenter(pose, consumer, length, width, height,
                FastColor.ARGB32.red(argb), FastColor.ARGB32.green(argb), FastColor.ARGB32.blue(argb), FastColor.ARGB32.alpha(argb), overlay, light, uScale, vScale, uOffset, vOffset);
    }

    // ARGB 重载（无偏移，默认 uScale=1, vScale=1）
    public static void renderFromBackCenter(PoseStack.Pose pose, VertexConsumer consumer, float length, float width, float height,
                                            int argb, int overlay, int light) {
        renderFromBackCenter(pose, consumer, length, width, height, argb, overlay, light, 1f, 1f, 0f, 0f);
    }

    // ARGB 重载（带缩放，无偏移）
    public static void renderFromBackCenter(PoseStack.Pose pose, VertexConsumer consumer, float length, float width, float height,
                                            int argb, int overlay, int light, float uScale, float vScale) {
        renderFromBackCenter(pose, consumer, length, width, height, argb, overlay, light, uScale, vScale, 0f, 0f);
    }

}

package com.fanxing.lib.client.render.shape;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class CylinderRenderer {

    /**
     * 圆柱体：竖向，UV缩放、偏移
     *
     * @param pose           姿态
     * @param bufferSource   缓冲源
     * @param sideRenderType 侧面渲染器（TRIANGLE_STRIP）
     * @param capRenderType  顶底面渲染器（TRIANGLE）
     * @param start          起点，底部中心点
     * @param radius         半径
     * @param height         高度
     * @param segments       分段数
     * @param uScale         UV横向缩放
     * @param vScale         UV纵向缩放
     * @param uOffset        U方向偏移
     * @param vOffset        V方向偏移
     */
    public static void render(PoseStack.Pose pose, MultiBufferSource bufferSource, RenderType sideRenderType, RenderType capRenderType, Vector3f start, float radius, float height, int segments,
                              int r, int g, int b, int a, int overlay, int light, float uScale, float vScale, float uOffset, float vOffset) {
        VertexConsumer sideConsumer = bufferSource.getBuffer(sideRenderType);
        renderSideStrip(pose, sideConsumer, start, radius, height, segments, r, g, b, a, overlay, light, uScale, vScale, uOffset, vOffset);
        VertexConsumer capConsumer = bufferSource.getBuffer(capRenderType);
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
    public static void render(PoseStack.Pose pose, MultiBufferSource bufferSource, RenderType sideRenderType, RenderType capRenderType, Vector3f start, float radius, float height, int segments,
                              int r, int g, int b, int a, int overlay, int light, float uScale, float vScale) {
        render(pose, bufferSource, sideRenderType, capRenderType, start, radius, height, segments, r, g, b, a, overlay, light, uScale, vScale, 0f, 0f);
    }

    /**
     * 圆柱体：默认 UV 缩放和偏移
     */
    public static void render(PoseStack.Pose pose, MultiBufferSource bufferSource, RenderType sideRenderType, RenderType capRenderType, Vector3f start, float radius, float height, int segments,
                              int r, int g, int b, int a, int overlay, int light) {
        render(pose, bufferSource, sideRenderType, capRenderType, start, radius, height, segments, r, g, b, a, overlay, light, 1f, 1f, 0f, 0f);
    }

    /**
     * 圆柱侧面：竖向，UV缩放、偏移、条带渲染
     * KEY 无法重复使用同一个bufferSource获取的同一个RenderType去提交顶点，否则会报not building
     * 根本原因是 STRIP图元不能连续添加顶点，FAN同理
     * TRIANGLE_STRIP(5, 3, 1, true),
     * TRIANGLE_FAN(6, 3, 1, true),
     */
    public static void renderSideStrip(PoseStack.Pose pose, VertexConsumer consumer, Vector3f start, float radius, float height, int segments,
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


    // ==================== ARGB 重载 ====================

    public static void render(PoseStack.Pose pose, MultiBufferSource bufferSource, RenderType sideRenderType, RenderType capRenderType,
                              Vector3f start, float radius, float height, int segments,
                              int argb, int overlay, int light, float uScale, float vScale, float uOffset, float vOffset) {
        render(pose, bufferSource, sideRenderType, capRenderType, start, radius, height, segments,
                FastColor.ARGB32.red(argb), FastColor.ARGB32.green(argb), FastColor.ARGB32.blue(argb), FastColor.ARGB32.alpha(argb), overlay, light, uScale, vScale, uOffset, vOffset);
    }

    public static void render(PoseStack.Pose pose, MultiBufferSource bufferSource, RenderType sideRenderType, RenderType capRenderType,
                              Vector3f start, float radius, float height, int segments,
                              int argb, int overlay, int light, float uScale, float vScale) {
        render(pose, bufferSource, sideRenderType, capRenderType, start, radius, height, segments, argb, overlay, light, uScale, vScale, 0f, 0f);
    }

    public static void render(PoseStack.Pose pose, MultiBufferSource bufferSource, RenderType sideRenderType, RenderType capRenderType,
                              Vector3f start, float radius, float height, int segments,
                              int argb, int overlay, int light) {
        render(pose, bufferSource, sideRenderType, capRenderType, start, radius, height, segments, argb, overlay, light, 1f, 1f, 0f, 0f);
    }

    public static void renderSideStrip(PoseStack.Pose pose,VertexConsumer consumer,
                              Vector3f start, float radius, float height, int segments,
                              int argb, int overlay, int light) {
        renderSideStrip(pose, consumer, start, radius, height, segments, FastColor.ARGB32.red(argb), FastColor.ARGB32.green(argb), FastColor.ARGB32.blue(argb), FastColor.ARGB32.alpha(argb), overlay, light, 1f, 1f, 0f, 0f);
    }





    /**
     * 圆柱侧面：使用四边形模式（QUADS），通过 QuadRenderer.render 渲染每个四边形段
     * KEY 虽然条带渲染顶点更少，GPU性能更高，但是无法复用，每一个都要获取新的RenderType
     * 在图形顶点数量相对较少，而图形数量多的时候，反复的创建RenderType和获取新的bufferSource，会导致CPU开销变大
     * 而使用可复用的图元，CPU使用缓存，开销很小，而由于顶点数量相对不是很多，所以综合性能更好
     *
     * @param pose     姿态矩阵
     * @param consumer 顶点消费者（需支持 QUADS 模式）
     * @param start    底部中心点
     * @param radius   半径
     * @param height   高度
     * @param segments 分段数（四边形数量）
     * @param r,g,b,a  颜色
     * @param overlay  叠加纹理
     * @param light    光照
     * @param uScale   U方向缩放
     * @param vScale   V方向缩放
     * @param uOffset  U方向偏移
     * @param vOffset  V方向偏移
     */
    public static void renderSide(PoseStack.Pose pose, VertexConsumer consumer,
                                  Vector3f start, float radius, float height, int segments,
                                  int r, int g, int b, int a, int overlay, int light,
                                  float uScale, float vScale, float uOffset, float vOffset) {
        float step = Mth.TWO_PI / segments;
        for (int i = 0; i < segments; i++) {
            float theta1 = i * step;
            float theta2 = (i + 1) * step;
            float cos1 = Mth.cos(theta1);
            float sin1 = Mth.sin(theta1);
            float cos2 = Mth.cos(theta2);
            float sin2 = Mth.sin(theta2);

            float x1 = start.x() + radius * cos1;
            float z1 = start.z() + radius * sin1;
            float x2 = start.x() + radius * cos2;
            float z2 = start.z() + radius * sin2;
            float yBottom = start.y();
            float yTop = start.y() + height;
            // 法线（径向）
            float nx1 = cos1, nz1 = sin1;
            float nx2 = cos2, nz2 = sin2;
            float ny = 0;
            // UV 坐标：u 沿圆周 0~1 连续，v 沿高度 0~1 连续
            float u1 = ((float) i / segments) * uScale + uOffset;
            float u2 = ((float) (i + 1) / segments) * uScale + uOffset;
            float vBottom = 0 * vScale + vOffset;   // 底部 v = 0
            float vTop = 1 * vScale + vOffset;      // 顶部 v = 1

            // 注意顶点顺序应为逆时针（从外部看）
            QuadRenderer.render(pose, consumer,
                    x1, yBottom, z1,   // 底部左
                    x2, yBottom, z2,   // 底部右
                    x2, yTop, z2,      // 顶部右
                    x1, yTop, z1,      // 顶部左
                    nx1, ny, nz1,      // 底部左法线
                    nx2, ny, nz2,      // 底部右法线
                    nx2, ny, nz2,      // 顶部右法线
                    nx1, ny, nz1,      // 顶部左法线
                    r, g, b, a, overlay, light,
                    u1, vBottom, u2, vBottom, u2, vTop, u1, vTop);
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
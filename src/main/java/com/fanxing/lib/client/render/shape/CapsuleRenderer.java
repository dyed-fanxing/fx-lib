package com.fanxing.lib.client.render.shape;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

public class CapsuleRenderer {
    /**
     * 胶囊体：竖向，带UV缩放，支持纹理滚动偏移（vOffset），指定底部中心点
     * 侧面使用三角条带TRIANGLE_STRIP渲染，半球使用四边形QUAD渲染，适合个体数量少，但顶点数量多时
     * @param pose                  姿态
     * @param bufferSource          缓冲区来源
     * @param sideRenderType        圆柱侧面渲染类型（TRIANGLE_STRIP）
     * @param hemisphereRenderType  半球渲染类型（QUAD）
     * @param start                 胶囊体底部中心点（下半球最低点）
     * @param radius                半径
     * @param length                圆柱部分长度（不含半球）
     * @param segments              分段数
     * @param r,g,b,a               颜色
     * @param overlay               覆盖层
     * @param light                 光照
     * @param uScale                UV横向缩放
     * @param vScale                UV纵向缩放
     * @param uOffset               u滚动
     * @param vOffset               v滚动
     */
    public static void render(PoseStack.Pose pose, MultiBufferSource bufferSource, RenderType sideRenderType, RenderType hemisphereRenderType, Vector3f start, float radius, float length, int segments,
                              int r, int g, int b, int a, int overlay, int light, float uScale, float vScale, float uOffset, float vOffset) {
        int latSegments = segments / 2;
        float deltaTheta = Mth.TWO_PI / segments;
        float deltaPhi = Mth.PI / latSegments;
        float[] ringRadius = new float[latSegments + 1];
        float[] ringY = new float[latSegments + 1];
        for (int i = 0; i <= latSegments; i++) {
            float phi = i * deltaPhi - Mth.HALF_PI;
            float cosPhi = Mth.cos(phi);
            float sinPhi = Mth.sin(phi);
            ringRadius[i] = radius * cosPhi;
            ringY[i] = radius * sinPhi;
        }
        ringY[latSegments / 2] = 0;
        ringRadius[latSegments / 2] = radius;
        // KEY 默认使用共享缓冲区，所以必须按照顺序获取
        CylinderRenderer.renderSideStrip(pose, bufferSource.getBuffer(sideRenderType), start, radius, length, segments, r, g, b, a, overlay, light, uScale, vScale, uOffset, vOffset);
        VertexConsumer hemiSphereBuffer = bufferSource.getBuffer(hemisphereRenderType);
        SphereRenderer.renderHemisphere(pose, hemiSphereBuffer, start, radius, 0.5f, 0f, segments, latSegments, ringRadius, ringY, deltaTheta, r, g, b, a, overlay, light, uScale, vScale, uOffset, vOffset);
        SphereRenderer.renderHemisphere(pose, hemiSphereBuffer, start, radius, -0.5f, length, segments, latSegments, ringRadius, ringY, deltaTheta, r, g, b, a, overlay, light, uScale, vScale, uOffset, vOffset);
    }

    /**
     * 胶囊体：竖向，带UV缩放，无滚动
     */
    public static void render(PoseStack.Pose pose, MultiBufferSource bufferSource,RenderType sideRenderType, RenderType hemisphereRenderType, Vector3f start, float radius, float length, int segments,
                              int r, int g, int b, int a, int overlay, int light, float uScale, float vScale) {
        render(pose,bufferSource, sideRenderType, hemisphereRenderType, start, radius, length, segments, r, g, b, a, overlay, light, uScale, vScale, 0f, 0f);
    }

    /**
     * 胶囊体：竖向（使用默认 UV 缩放 1.0），底部在原点
     */
    public static void render(PoseStack.Pose pose,MultiBufferSource bufferSource,RenderType sideRenderType, RenderType hemisphereRenderType, Vector3f start, float radius, float length, int segments,
                              int r, int g, int b, int a, int overlay, int light) {
        render(pose, bufferSource,sideRenderType, hemisphereRenderType, start, radius, length, segments, r, g, b, a, overlay, light, 1f, 1f);
    }





    // ==================== ARGB 重载 ====================

    /**
     * 胶囊体：竖向，带UV缩放和滚动偏移，指定底部中心点，接受 ARGB
     */
    public static void render(PoseStack.Pose pose,MultiBufferSource bufferSource,RenderType sideRenderType, RenderType hemisphereRenderType,Vector3f start, float radius, float length, int segments,
                              int argb, int overlay, int light, float uScale, float vScale, float uOffset, float vOffset) {
        render(pose, bufferSource,sideRenderType, hemisphereRenderType, start, radius, length, segments,
                FastColor.ARGB32.red(argb), FastColor.ARGB32.green(argb), FastColor.ARGB32.blue(argb), FastColor.ARGB32.alpha(argb), overlay, light, uScale, vScale, uOffset, vOffset);
    }

    /**
     * 胶囊体：竖向，带UV缩放，接受单个 ARGB 颜色
     */
    public static void render(PoseStack.Pose pose,MultiBufferSource bufferSource,RenderType sideRenderType, RenderType hemisphereRenderType, Vector3f start, float radius, float length, int segments,
                              int argb, int overlay, int light, float uScale, float vScale) {
        render(pose,bufferSource,sideRenderType, hemisphereRenderType, start, radius, length, segments,
                FastColor.ARGB32.red(argb), FastColor.ARGB32.green(argb), FastColor.ARGB32.blue(argb), FastColor.ARGB32.alpha(argb), overlay, light, uScale, vScale);
    }

    /**
     * 胶囊体：竖向（使用默认 UV 缩放 1.0），接受 ARGB
     */
    public static void render(PoseStack.Pose pose, MultiBufferSource bufferSource,RenderType sideRenderType, RenderType hemisphereRenderType, Vector3f start, float radius, float length, int segments,
                              int argb, int overlay, int light) {
        render(pose,bufferSource,sideRenderType, hemisphereRenderType, start, radius, length, segments, argb, overlay, light, 1f, 1f);
    }










































    /**
     * 胶囊体：竖向，带UV缩放，支持纹理滚动偏移（vOffset），指定底部中心点
     * 使用同一个渲染类型，QUAD图元渲染，单个胶囊体顶点数量中等，适合个体比较多的情况下
     * @param consumer           顶点消费者（QUAD）
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer,Vector3f start, float radius, float length, int segments,
                              int r, int g, int b, int a, int overlay, int light, float uScale, float vScale, float uOffset, float vOffset) {
        int latSegments = segments / 2;
        float deltaTheta = Mth.TWO_PI / segments;
        float deltaPhi = Mth.PI / latSegments;
        float[] ringRadius = new float[latSegments + 1];
        float[] ringY = new float[latSegments + 1];
        for (int i = 0; i <= latSegments; i++) {
            float phi = i * deltaPhi - Mth.HALF_PI;
            float cosPhi = Mth.cos(phi);
            float sinPhi = Mth.sin(phi);
            ringRadius[i] = radius * cosPhi;
            ringY[i] = radius * sinPhi;
        }
        ringY[latSegments / 2] = 0;
        ringRadius[latSegments / 2] = radius;
        CylinderRenderer.renderSide(pose, consumer, start, radius, length, segments, r, g, b, a, overlay, light, uScale, vScale, uOffset, vOffset);
        SphereRenderer.renderHemisphere(pose, consumer, start, radius, 0.5f, 0f, segments, latSegments, ringRadius, ringY, deltaTheta, r, g, b, a, overlay, light, uScale, vScale, uOffset, vOffset);
        SphereRenderer.renderHemisphere(pose, consumer, start, radius, -0.5f, length, segments, latSegments, ringRadius, ringY, deltaTheta, r, g, b, a, overlay, light, uScale, vScale, uOffset, vOffset);
    }

    /**
     * 胶囊体：竖向，带UV缩放，无滚动偏移
     * @param consumer 顶点消费者（QUAD）
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer, Vector3f start, float radius, float length, int segments,
                              int r, int g, int b, int a, int overlay, int light, float uScale, float vScale) {
        render(pose, consumer, start, radius, length, segments, r, g, b, a, overlay, light, uScale, vScale, 0f, 0f);
    }

    /**
     * 胶囊体：竖向，默认UV缩放（1.0），无滚动
     * @param consumer 顶点消费者（QUAD）
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer, Vector3f start, float radius, float length, int segments,
                              int r, int g, int b, int a, int overlay, int light) {
        render(pose, consumer, start, radius, length, segments, r, g, b, a, overlay, light, 1f, 1f, 0f, 0f);
    }

    // ==================== ARGB 重载 ====================

    /**
     * 胶囊体：竖向，带UV缩放和滚动偏移，接受 ARGB 颜色
     * @param consumer 顶点消费者（QUAD）
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer, Vector3f start, float radius, float length, int segments,
                              int argb, int overlay, int light, float uScale, float vScale, float uOffset, float vOffset) {
        render(pose, consumer, start, radius, length, segments,
                FastColor.ARGB32.red(argb), FastColor.ARGB32.green(argb), FastColor.ARGB32.blue(argb), FastColor.ARGB32.alpha(argb),
                overlay, light, uScale, vScale, uOffset, vOffset);
    }

    /**
     * 胶囊体：竖向，带UV缩放，无滚动，接受 ARGB 颜色
     * @param consumer 顶点消费者（QUAD）
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer, Vector3f start, float radius, float length, int segments,
                              int argb, int overlay, int light, float uScale, float vScale) {
        render(pose, consumer, start, radius, length, segments, argb, overlay, light, uScale, vScale, 0f, 0f);
    }

    /**
     * 胶囊体：竖向，默认UV缩放（1.0），无滚动，接受 ARGB 颜色
     * @param consumer 顶点消费者（QUAD）
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer, Vector3f start, float radius, float length, int segments,
                              int argb, int overlay, int light) {
        render(pose, consumer, start, radius, length, segments, argb, overlay, light, 1f, 1f, 0f, 0f);
    }
}
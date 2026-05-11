package com.fanxing.lib.client.render.shape;

import com.fanxing.lib.client.render.data.RingLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

import java.util.List;

public class RingRenderer {

    /**
     * 核心渲染方法：连接相邻两个 RingLayer 生成侧面。
     * 环默认位于 XY 平面（法线朝 Z，即环面垂直于 Z 轴），顶点坐标 (r*cos, r*sin, zOffset)。
     * 纹理 U 沿角度方向，V 沿 Z 轴方向（从最小 zOffset 到最大 zOffset 线性映射）。
     *
     * @param pose       姿态（已平移到中心并应用朝向）
     * @param consumer   顶点消费者
     * @param layers     层列表（至少2层）
     * @param startAngle 起始弧度
     * @param endAngle   结束弧度
     * @param segments   完整圆的分段数（内部按角度比例自动缩放）
     * @param uMin       纹理 U 最小值（对应 startAngle）
     * @param uMax       纹理 U 最大值（对应 endAngle）
     * @param vMin       纹理 V 最小值（对应最小 zOffset）
     * @param vMax       纹理 V 最大值（对应最大 zOffset）
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer, List<RingLayer> layers,
                              float startAngle, float endAngle, int segments,
                              float uMin, float uMax, float vMin, float vMax) {
        if (layers == null || layers.size() < 2) return;

        float angleLength = endAngle - startAngle;
        int segCount = Math.max(1, (int) (segments * (angleLength / Mth.TWO_PI)));
        float deltaAngle = angleLength / segCount;

        // 计算全局 Z 范围（用于纹理 V 坐标）
        float minZ = layers.getFirst().zOffset;
        float maxZ = layers.getFirst().zOffset;
        for (RingLayer layer : layers) {
            minZ = Math.min(minZ, layer.zOffset);
            maxZ = Math.max(maxZ, layer.zOffset);
        }
        if (minZ == maxZ) maxZ = minZ + 1; // 避免除零

        for (int idx = 0; idx < layers.size() - 1; idx++) {
            RingLayer bottom = layers.get(idx);
            RingLayer top = layers.get(idx + 1);

            float r1 = bottom.radius;
            float r2 = top.radius;
            float z1 = bottom.zOffset;
            float z2 = top.zOffset;
            int c1 = bottom.color;
            int c2 = top.color;

            // 纹理 V 坐标（基于 Z 线性插值）
            float v1 = vMin + (z1 - minZ) / (maxZ - minZ) * (vMax - vMin);
            float v2 = vMin + (z2 - minZ) / (maxZ - minZ) * (vMax - vMin);

            int ir = (c1 >> 16) & 0xFF, ig = (c1 >> 8) & 0xFF, ib = c1 & 0xFF, ia = (c1 >> 24) & 0xFF;
            int or = (c2 >> 16) & 0xFF, og = (c2 >> 8) & 0xFF, ob = c2 & 0xFF, oa = (c2 >> 24) & 0xFF;

            for (int i = 0; i < segCount; i++) {
                float anglePrev = startAngle + i * deltaAngle;
                float angleCurr = startAngle + (i + 1) * deltaAngle;
                float cosPrev = Mth.cos(anglePrev), sinPrev = Mth.sin(anglePrev);
                float cosCurr = Mth.cos(angleCurr), sinCurr = Mth.sin(angleCurr);

                // 底部两点（z = z1）
                float x1b = r1 * cosPrev, y1b = r1 * sinPrev;
                float x2b = r1 * cosCurr, y2b = r1 * sinCurr;
                // 顶部两点（z = z2）
                float x1t = r2 * cosPrev, y1t = r2 * sinPrev;
                float x2t = r2 * cosCurr, y2t = r2 * sinCurr;

                // 法线（径向向外，在 XY 平面内）
                float nxPrev = cosPrev, nyPrev = sinPrev, nzPrev = 0f;
                float nxCurr = cosCurr, nyCurr = sinCurr, nzCurr = 0f;

                float u1 = uMin + (anglePrev - startAngle) / angleLength * (uMax - uMin);
                float u2 = uMin + (angleCurr - startAngle) / angleLength * (uMax - uMin);

                // 四边形：底部左、底部右、顶部右、顶部左（逆时针，从外侧看）
                QuadRenderer.render(pose, consumer,
                        x1b, y1b, z1,
                        x2b, y2b, z1,
                        x2t, y2t, z2,
                        x1t, y1t, z2,
                        nxPrev, nyPrev, nzPrev,
                        nxCurr, nyCurr, nzCurr,
                        nxCurr, nyCurr, nzCurr,
                        nxPrev, nyPrev, nzPrev,
                        ir, ig, ib, ia,
                        ir, ig, ib, ia,
                        or, og, ob, oa,
                        or, og, ob, oa,
                        0, 0,   // overlay, light
                        u1, v1, u2, v1, u2, v2, u1, v2
                );
            }
        }
    }

    // -------------------- 便捷重载（保持签名一致） --------------------

    public static void render(PoseStack.Pose pose, VertexConsumer consumer, List<RingLayer> layers,
                              float startAngle, float endAngle, int segments, float vMin, float vMax) {
        render(pose, consumer, layers, startAngle, endAngle, segments, 0f, 1f, vMin, vMax);
    }

    public static void render(PoseStack.Pose pose, VertexConsumer consumer, List<RingLayer> layers,
                              int segments, float uMin, float uMax, float vMin, float vMax) {
        render(pose, consumer, layers, 0f, Mth.TWO_PI, segments, uMin, uMax, vMin, vMax);
    }

    public static void render(PoseStack.Pose pose, VertexConsumer consumer, List<RingLayer> layers,
                              int segments, float vMin, float vMax) {
        render(pose, consumer, layers, 0f, Mth.TWO_PI, segments, 0f, 1f, vMin, vMax);
    }

    public static void render(PoseStack.Pose pose, VertexConsumer consumer, List<RingLayer> layers, int segments) {
        render(pose, consumer, layers, segments, 0f, 1f);
    }

    // -------------------- 世界坐标系版本（无 PoseStack）------------------
    // 环默认位于 XY 平面，顶点 (center.x + r*cos, center.y + r*sin, center.z + zOffset)
    // 注意：此版本使用三角形条带（TRIANGLE_STRIP），需渲染类型支持
    public static void render(VertexConsumer consumer, Vector3f center, List<RingLayer> layers,
                              float startAngle, float endAngle, int segments, int light,
                              float uMin, float vMin, float uMax, float vMax) {
        if (layers == null || layers.size() < 2) return;
        float angleLen = endAngle - startAngle;
        int segCount = Math.max(1, (int) (segments * (angleLen / Mth.TWO_PI)));
        float delta = angleLen / segCount;
        float cx = center.x(), cy = center.y(), cz = center.z();

        // 计算全局 Z 范围
        float minZ = layers.getFirst().zOffset;
        float maxZ = layers.getFirst().zOffset;
        for (RingLayer layer : layers) {
            minZ = Math.min(minZ, layer.zOffset);
            maxZ = Math.max(maxZ, layer.zOffset);
        }
        if (minZ == maxZ) maxZ = minZ + 1;

        for (int i = 0; i < layers.size() - 1; i++) {
            RingLayer bottom = layers.get(i);
            RingLayer top = layers.get(i + 1);
            float r1 = bottom.radius, r2 = top.radius;
            float z1 = bottom.zOffset, z2 = top.zOffset;
            int c1 = bottom.color, c2 = top.color;
            int r1c = (c1 >> 16) & 0xFF, g1c = (c1 >> 8) & 0xFF, b1c = c1 & 0xFF, a1c = (c1 >> 24) & 0xFF;
            int r2c = (c2 >> 16) & 0xFF, g2c = (c2 >> 8) & 0xFF, b2c = c2 & 0xFF, a2c = (c2 >> 24) & 0xFF;

            float v_bottom = vMin + (z1 - minZ) / (maxZ - minZ) * (vMax - vMin);
            float v_top    = vMin + (z2 - minZ) / (maxZ - minZ) * (vMax - vMin);

            for (int j = 0; j <= segCount; j++) {
                float angle = startAngle + j * delta;
                float cos = Mth.cos(angle), sin = Mth.sin(angle);
                float x = cx + r1 * cos;
                float y = cy + r1 * sin;
                float x2 = cx + r2 * cos;
                float y2 = cy + r2 * sin;
                float u = uMin + (angle - startAngle) / angleLen * (uMax - uMin);
                consumer.addVertex(x, y, cz + z1).setUv(u, v_bottom).setColor(r1c, g1c, b1c, a1c).setLight(light);
                consumer.addVertex(x2, y2, cz + z2).setUv(u, v_top).setColor(r2c, g2c, b2c, a2c).setLight(light);
            }
        }
    }

    public static void render(VertexConsumer consumer, List<RingLayer> layers, float startAngle, float endAngle, int segments,
                              int light, float uMin, float vMin, float uMax, float vMax) {
        render(consumer, new Vector3f(), layers, startAngle, endAngle, segments, light, uMin, vMin, uMax, vMax);
    }

    public static void render(VertexConsumer consumer, Vector3f center, List<RingLayer> layers, float startAngle, float endAngle, int segments,
                              int light, float uMax, float vMax) {
        render(consumer, center, layers, startAngle, endAngle, segments, light, 0f, 0f, uMax, vMax);
    }

    public static void render(VertexConsumer consumer, Vector3f center, List<RingLayer> layers, int segments,
                              int light, float uMin, float vMin, float uMax, float vMax) {
        render(consumer, center, layers, 0f, Mth.TWO_PI, segments, light, uMin, vMin, uMax, vMax);
    }

    public static void render(VertexConsumer consumer, Vector3f center, List<RingLayer> layers, int segments,
                              int light, float uMax, float vMax) {
        render(consumer, center, layers, 0f, Mth.TWO_PI, segments, light, 0f, 0f, uMax, vMax);
    }

    public static void render(VertexConsumer consumer, Vector3f center, List<RingLayer> layers, int segments, int light) {
        render(consumer, center, layers, segments, light, 1f, 1f);
    }
}
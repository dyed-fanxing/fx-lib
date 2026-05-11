package com.fanxing.lib.client.particle.mesh;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.fanxing.lib.client.render.data.RingLayer;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public final class RingFanParticleRenderer {
    /**
     * 默认面向Z轴的竖立扇环
     */
    public static void render(VertexConsumer consumer, Vector3f center, List<RingLayer> layers,
                              float startAngle, float endAngle, int segments,
                              Quaternionf rotation,
                              float scaleX, float scaleY, float scaleZ,
                              int light,
                              float uMin, float vMin, float uMax, float vMax,
                              float alpha) { // 新增透明度乘数 (0~1)
        if (layers == null || layers.size() < 2) return;

        float angleLen = endAngle - startAngle;
        int segCount = Math.max(1, (int) (segments * (angleLen / Mth.TWO_PI)));
        float delta = angleLen / segCount;
        float cx = center.x(), cy = center.y(), cz = center.z();

        float minZ = layers.getFirst().zOffset;
        float maxZ = layers.getLast().zOffset;
        float totalDepth = maxZ - minZ;
        if (totalDepth == 0) totalDepth = 1;

        for (int l = 0; l < layers.size() - 1; l++) {
            RingLayer bottom = layers.get(l);
            RingLayer top = layers.get(l + 1);

            float r_b = bottom.radius, r_t = top.radius;
            float z_b = bottom.zOffset, z_t = top.zOffset;
            int c_b = bottom.color, c_t = top.color;
            int r_bc = (c_b >> 16) & 0xFF, g_bc = (c_b >> 8) & 0xFF, b_bc = c_b & 0xFF, a_bc_orig = (c_b >> 24) & 0xFF;
            int r_tc = (c_t >> 16) & 0xFF, g_tc = (c_t >> 8) & 0xFF, b_tc = c_t & 0xFF, a_tc_orig = (c_t >> 24) & 0xFF;

            // 应用全局透明度乘数
            int a_bc = (int)(a_bc_orig * alpha);
            int a_tc = (int)(a_tc_orig * alpha);

            float v_bottom = vMin + (z_b - minZ) / totalDepth * (vMax - vMin);
            float v_top    = vMin + (z_t - minZ) / totalDepth * (vMax - vMin);

            for (int i = 0; i < segCount; i++) {
                float angle1 = startAngle + i * delta;
                float angle2 = angle1 + delta;
                float u1 = uMin + (angle1 - startAngle) / angleLen * (uMax - uMin);
                float u2 = uMin + (angle2 - startAngle) / angleLen * (uMax - uMin);

                float cos1 = Mth.cos(angle1), sin1 = Mth.sin(angle1);
                float cos2 = Mth.cos(angle2), sin2 = Mth.sin(angle2);

                Vector3f b1_local = new Vector3f(r_b * cos1 * scaleX, r_b * sin1 * scaleY, z_b * scaleZ);
                Vector3f b2_local = new Vector3f(r_b * cos2 * scaleX, r_b * sin2 * scaleY, z_b * scaleZ);
                Vector3f t1_local = new Vector3f(r_t * cos1 * scaleX, r_t * sin1 * scaleY, z_t * scaleZ);
                Vector3f t2_local = new Vector3f(r_t * cos2 * scaleX, r_t * sin2 * scaleY, z_t * scaleZ);

                Vector3f b1 = b1_local.rotate(rotation).add(cx, cy, cz);
                Vector3f b2 = b2_local.rotate(rotation).add(cx, cy, cz);
                Vector3f t1 = t1_local.rotate(rotation).add(cx, cy, cz);
                Vector3f t2 = t2_local.rotate(rotation).add(cx, cy, cz);

                consumer.addVertex(b2.x(), b2.y(), b2.z())
                        .setUv(u2, v_bottom)
                        .setColor(r_bc, g_bc, b_bc, a_bc)
                        .setLight(light);
                consumer.addVertex(t2.x(), t2.y(), t2.z())
                        .setUv(u2, v_top)
                        .setColor(r_tc, g_tc, b_tc, a_tc)
                        .setLight(light);
                consumer.addVertex(t1.x(), t1.y(), t1.z())
                        .setUv(u1, v_top)
                        .setColor(r_tc, g_tc, b_tc, a_tc)
                        .setLight(light);
                consumer.addVertex(b1.x(), b1.y(), b1.z())
                        .setUv(u1, v_bottom)
                        .setColor(r_bc, g_bc, b_bc, a_bc)
                        .setLight(light);
            }
        }
    }

    // 便捷重载省略（你自己有）
}
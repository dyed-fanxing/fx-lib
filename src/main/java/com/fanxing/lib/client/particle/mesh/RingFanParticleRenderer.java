package com.fanxing.lib.client.particle.mesh;

import com.fanxing.lib.client.render.data.RingLayer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.Mth;
import org.joml.Math;
import org.joml.Quaternionf;

import java.util.List;

public final class RingFanParticleRenderer {


    public static void render(VertexConsumer consumer,
                              float cx, float cy, float cz,
                              List<RingLayer> layers,
                              float[] layerDist,          // 预计算的累积归一化距离
                              float startAngle, float endAngle, int segments,
                              Quaternionf rotation,
                              float scaleX, float scaleY, float scaleZ,
                              int light,
                              float uMin, float vMin, float uMax, float vMax,
                              float alpha) {
        float angleLen = endAngle - startAngle;
        int segCount = Math.max(1, (int) (segments * (angleLen / Mth.TWO_PI)));
        float delta = angleLen / segCount;

        // 预计算旋转矩阵（同前）
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

        int layerCount = layers.size();
        for (int l = 0; l < layerCount - 1; l++) {
            RingLayer bottom = layers.get(l);
            RingLayer top    = layers.get(l + 1);

            float r_b = bottom.radius, r_t = top.radius;
            float z_b = bottom.zOffset, z_t = top.zOffset;

            int c_b = bottom.color, c_t = top.color;
            int r_bc = (c_b >> 16) & 0xFF, g_bc = (c_b >> 8) & 0xFF, b_bc = c_b & 0xFF;
            int a_bc_orig = (c_b >> 24) & 0xFF;
            int r_tc = (c_t >> 16) & 0xFF, g_tc = (c_t >> 8) & 0xFF, b_tc = c_t & 0xFF;
            int a_tc_orig = (c_t >> 24) & 0xFF;

            int a_bc = (int)(a_bc_orig * alpha);
            int a_tc = (int)(a_tc_orig * alpha);

            // 使用预计算的归一化距离
            float v_b = vMin + layerDist[l]   * (vMax - vMin);
            float v_t = vMin + layerDist[l+1] * (vMax - vMin);

            for (int i = 0; i < segCount; i++) {
                float angle1 = startAngle + i * delta;
                float angle2 = angle1 + delta;
                float u1 = uMin + (angle1 - startAngle) / angleLen * (uMax - uMin);
                float u2 = uMin + (angle2 - startAngle) / angleLen * (uMax - uMin);

                float cos1 = Mth.cos(angle1), sin1 = Mth.sin(angle1);
                float cos2 = Mth.cos(angle2), sin2 = Mth.sin(angle2);

                float b1x = r_b * cos1 * scaleX;
                float b1y = r_b * sin1 * scaleY;
                float b1z = z_b * scaleZ;
                float b2x = r_b * cos2 * scaleX;
                float b2y = r_b * sin2 * scaleY;
                float b2z = z_b * scaleZ;
                float t1x = r_t * cos1 * scaleX;
                float t1y = r_t * sin1 * scaleY;
                float t1z = z_t * scaleZ;
                float t2x = r_t * cos2 * scaleX;
                float t2y = r_t * sin2 * scaleY;
                float t2z = z_t * scaleZ;

                float B2x = Math.fma(m00, b2x, Math.fma(m01, b2y, m02 * b2z)) + cx;
                float B2y = Math.fma(m10, b2x, Math.fma(m11, b2y, m12 * b2z)) + cy;
                float B2z = Math.fma(m20, b2x, Math.fma(m21, b2y, m22 * b2z)) + cz;

                float T2x = Math.fma(m00, t2x, Math.fma(m01, t2y, m02 * t2z)) + cx;
                float T2y = Math.fma(m10, t2x, Math.fma(m11, t2y, m12 * t2z)) + cy;
                float T2z = Math.fma(m20, t2x, Math.fma(m21, t2y, m22 * t2z)) + cz;

                float T1x = Math.fma(m00, t1x, Math.fma(m01, t1y, m02 * t1z)) + cx;
                float T1y = Math.fma(m10, t1x, Math.fma(m11, t1y, m12 * t1z)) + cy;
                float T1z = Math.fma(m20, t1x, Math.fma(m21, t1y, m22 * t1z)) + cz;

                float B1x = Math.fma(m00, b1x, Math.fma(m01, b1y, m02 * b1z)) + cx;
                float B1y = Math.fma(m10, b1x, Math.fma(m11, b1y, m12 * b1z)) + cy;
                float B1z = Math.fma(m20, b1x, Math.fma(m21, b1y, m22 * b1z)) + cz;

                consumer.addVertex(B2x, B2y, B2z).setUv(u2, v_b)
                        .setColor(r_bc, g_bc, b_bc, a_bc).setLight(light);
                consumer.addVertex(T2x, T2y, T2z).setUv(u2, v_t)
                        .setColor(r_tc, g_tc, b_tc, a_tc).setLight(light);
                consumer.addVertex(T1x, T1y, T1z).setUv(u1, v_t)
                        .setColor(r_tc, g_tc, b_tc, a_tc).setLight(light);
                consumer.addVertex(B1x, B1y, B1z).setUv(u1, v_b)
                        .setColor(r_bc, g_bc, b_bc, a_bc).setLight(light);
            }
        }
    }

}
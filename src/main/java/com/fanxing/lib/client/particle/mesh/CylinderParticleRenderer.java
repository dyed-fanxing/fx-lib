package com.fanxing.lib.client.particle.mesh;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

/**
 * 圆柱体粒子渲染工具（四边形模式，UV 边界传参）。
 * 所有 UV 变换由粒子类计算边界值后传入，工具类不负责 scale/offset 计算。
 * 注意：此渲染器要求渲染类型使用 VertexFormat.Mode.QUADS。
 */
public final class CylinderParticleRenderer {

    /**
     * 圆柱侧面：每个扇区一个独立四边形（QUADS），避免条带连接问题。
     *
     * @param consumer  顶点消费者（必须支持 VertexFormat.Mode.QUADS）
     * @param start     圆柱底部中心世界坐标
     * @param radius    半径
     * @param height    高度（沿 Y 轴）
     * @param segments  圆周分段数（至少为 1）
     * @param r,g,b,a   颜色 (0-255)
     * @param light     光照值
     * @param uMin,vMin 纹理左下角 UV（对应 theta=0, Y=0）
     * @param uMax,vMax 纹理右上角 UV（对应 theta=2PI, Y=height）
     */
    public static void renderSide(VertexConsumer consumer, Vector3f start, float radius, float height, int segments,
                                  int r, int g, int b, int a, int light,
                                  float uMin, float vMin, float uMax, float vMax) {
        if (segments <= 0) return;
        float step = Mth.TWO_PI / segments;
        float cx = start.x(), cy = start.y(), cz = start.z();

        for (int i = 0; i < segments; i++) {
            float theta1 = i * step;
            float theta2 = theta1 + step;
            float u1 = uMin + ((float) i / segments) * (uMax - uMin);
            float u2 = uMin + ((float) (i + 1) / segments) * (uMax - uMin);

            float cos1 = Mth.cos(theta1), sin1 = Mth.sin(theta1);
            float cos2 = Mth.cos(theta2), sin2 = Mth.sin(theta2);

            // 四个顶点（左下、右下、右上、左上），逆时针顺序
            // 左下（当前角度，底部）
            float x1b = cx + radius * cos1;
            float z1b = cz + radius * sin1;
            // 右下（下个角度，底部）
            float x2b = cx + radius * cos2;
            float z2b = cz + radius * sin2;
            // 右上（下个角度，顶部）
            float x2t = x2b;
            float z2t = z2b;
            float y2t = cy + height;
            // 左上（当前角度，顶部）
            float x1t = x1b;
            float z1t = z1b;
            float y1t = cy + height;

            // 顶点顺序：左下 -> 右下 -> 右上 -> 左上
            consumer.addVertex(x1b, cy, z1b).setUv(u1, vMin).setColor(r, g, b, a).setLight(light);
            consumer.addVertex(x2b, cy, z2b).setUv(u2, vMin).setColor(r, g, b, a).setLight(light);
            consumer.addVertex(x2t, y2t, z2t).setUv(u2, vMax).setColor(r, g, b, a).setLight(light);
            consumer.addVertex(x1t, y1t, z1t).setUv(u1, vMax).setColor(r, g, b, a).setLight(light);
        }
    }

    // ==================== ARGB 重载 ====================
    public static void renderSide(VertexConsumer consumer, Vector3f start, float radius, float height, int segments,
                                  int argb, int light,
                                  float uMin, float vMin, float uMax, float vMax) {
        renderSide(consumer, start, radius, height, segments,
                FastColor.ARGB32.red(argb), FastColor.ARGB32.green(argb),
                FastColor.ARGB32.blue(argb), FastColor.ARGB32.alpha(argb),
                light, uMin, vMin, uMax, vMax);
    }
}
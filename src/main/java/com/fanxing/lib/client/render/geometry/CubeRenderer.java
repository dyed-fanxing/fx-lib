package com.fanxing.lib.client.render.geometry;

import com.fanxing.lib.util.phys.OBB;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.FastColor;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public class CubeRenderer {

    /**
     * 几何中心点渲染（立方体各面法线正确，颜色统一）。
     *
     * @param pose      姿态矩阵
     * @param consumer  顶点消费者（QUADS 模式）
     * @param cx,cy,cz  几何中心世界坐标
     * @param length    X 轴方向长度
     * @param width     Z 轴方向宽度（原 width 实为 Z 向）
     * @param height    Y 轴方向高度
     * @param r,g,b,a   颜色 (0-255)
     * @param overlay   叠加纹理
     * @param light     光照
     * @param uMin,vMin UV 左下角
     * @param uMax,vMax UV 右上角
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer,
                              float cx, float cy, float cz,
                              float length, float width, float height,
                              int r, int g, int b, int a, int overlay, int light,
                              float uMin, float vMin, float uMax, float vMax) {
        float l = length * 0.5f;
        float w = width * 0.5f;
        float h = height * 0.5f;

        // 前面 (Z-)
        QuadRenderer.render(pose, consumer,
                -l + cx, -h + cy, -w + cz,   // 左下
                l + cx, -h + cy, -w + cz,   // 右下
                l + cx,  h + cy, -w + cz,   // 右上
                -l + cx,  h + cy, -w + cz,   // 左上
                0, 0, -1,
                r, g, b, a, overlay, light, uMin, vMin, uMax, vMax);

        // 后面 (Z+)
        QuadRenderer.render(pose, consumer,
                -l + cx, -h + cy,  w + cz,
                l + cx, -h + cy,  w + cz,
                l + cx,  h + cy,  w + cz,
                -l + cx,  h + cy,  w + cz,
                0, 0, 1,
                r, g, b, a, overlay, light, uMin, vMin, uMax, vMax);

        // 左面 (X-)
        QuadRenderer.render(pose, consumer,
                -l + cx, -h + cy, -w + cz,
                -l + cx, -h + cy,  w + cz,
                -l + cx,  h + cy,  w + cz,
                -l + cx,  h + cy, -w + cz,
                -1, 0, 0,
                r, g, b, a, overlay, light, uMin, vMin, uMax, vMax);

        // 右面 (X+)
        QuadRenderer.render(pose, consumer,
                l + cx, -h + cy, -w + cz,
                l + cx,  h + cy, -w + cz,
                l + cx,  h + cy,  w + cz,
                l + cx, -h + cy,  w + cz,
                1, 0, 0,
                r, g, b, a, overlay, light, uMin, vMin, uMax, vMax);

        // 上面 (Y+)
        QuadRenderer.render(pose, consumer,
                -l + cx,  h + cy, -w + cz,
                -l + cx,  h + cy,  w + cz,
                l + cx,  h + cy,  w + cz,
                l + cx,  h + cy, -w + cz,
                0, 1, 0,
                r, g, b, a, overlay, light, uMin, vMin, uMax, vMax);

        // 下面 (Y-)
        QuadRenderer.render(pose, consumer,
                -l + cx, -h + cy, -w + cz,
                l + cx, -h + cy, -w + cz,
                l + cx, -h + cy,  w + cz,
                -l + cx, -h + cy,  w + cz,
                0, -1, 0,
                r, g, b, a, overlay, light, uMin, vMin, uMax, vMax);
    }

    // ARGB 便利重载
    public static void render(PoseStack.Pose pose, VertexConsumer consumer,
                              float cx, float cy, float cz,
                              float length, float width, float height,
                              int argb, int overlay, int light,
                              float uMin, float vMin, float uMax, float vMax) {
        render(pose, consumer, cx, cy, cz, length, width, height,
                argb >> 16 & 255 ,argb >> 8 & 255,argb & 255, argb >> 24,
                overlay, light, uMin, vMin, uMax, vMax);
    }

    /**
     * 端面中心向前渲染（起点为背面中心，向 Z+ 延伸）。
     * 用于实体的“向前”特效，无须传入中心点，起点即当前 PoseStack 原点。
     */
    public static void renderFromBackCenter(PoseStack.Pose pose, VertexConsumer consumer,
                                            float length, float width, float height,
                                            int r, int g, int b, int a, int overlay, int light,
                                            float uMin, float vMin, float uMax, float vMax) {
        float halfW = width * 0.5f;
        float halfH = height * 0.5f;

        // 前 (Z+)
        QuadRenderer.render(pose, consumer,
                -halfW, -halfH, length,    // 左下
                halfW, -halfH, length,    // 右下
                halfW,  halfH, length,    // 右上
                -halfW,  halfH, length,    // 左上
                0, 0, 1,
                r, g, b, a, overlay, light, uMin, vMin, uMax, vMax);
        // 后 (Z=0)
        QuadRenderer.render(pose, consumer,
                -halfW, -halfH, 0,
                halfW, -halfH, 0,
                halfW,  halfH, 0,
                -halfW,  halfH, 0,
                0, 0, -1,
                r, g, b, a, overlay, light, uMin, vMin, uMax, vMax);
        // 左 (X-)
        QuadRenderer.render(pose, consumer,
                -halfW, -halfH, 0,
                -halfW, -halfH, length,
                -halfW,  halfH, length,
                -halfW,  halfH, 0,
                -1, 0, 0,
                r, g, b, a, overlay, light, uMin, vMin, uMax, vMax);
        // 右 (X+)
        QuadRenderer.render(pose, consumer,
                halfW, -halfH, 0,
                halfW,  halfH, 0,
                halfW,  halfH, length,
                halfW, -halfH, length,
                1, 0, 0,
                r, g, b, a, overlay, light, uMin, vMin, uMax, vMax);
        // 上 (Y+)
        QuadRenderer.render(pose, consumer,
                -halfW,  halfH, 0,
                -halfW,  halfH, length,
                halfW,  halfH, length,
                halfW,  halfH, 0,
                0, 1, 0,
                r, g, b, a, overlay, light, uMin, vMin, uMax, vMax);
        // 下 (Y-)
        QuadRenderer.render(pose, consumer,
                -halfW, -halfH, 0,
                halfW, -halfH, 0,
                halfW, -halfH, length,
                -halfW, -halfH, length,
                0, -1, 0,
                r, g, b, a, overlay, light, uMin, vMin, uMax, vMax);
    }
    // ARGB 便利重载
    public static void renderFromBackCenter(PoseStack.Pose pose, VertexConsumer consumer,
                                            float length, float width, float height,
                                            int argb, int overlay, int light,
                                            float uMin, float vMin, float uMax, float vMax) {
        renderFromBackCenter(pose, consumer, length, width, height,
                FastColor.ARGB32.red(argb), FastColor.ARGB32.green(argb),
                FastColor.ARGB32.blue(argb), FastColor.ARGB32.alpha(argb),
                overlay, light, uMin, vMin, uMax, vMax);
    }

    // ==================== 线框渲染（保留，但已改为 float 坐标） ====================
    /**
     * 以几何中心为基准渲染线框。
     * @param vertices 8 个顶点顺序：[底面(0-3逆时针), 顶面(4-7对应)]
     */
    public static void renderLineBox(PoseStack.Pose pose, VertexConsumer consumer,
                                     float[][] vertices, int r, int g, int b, int a) {
        int[][] edges = {
                {0,1}, {1,2}, {2,3}, {3,0}, // 底面
                {4,5}, {5,6}, {6,7}, {7,4}, // 顶面
                {0,4}, {1,5}, {2,6}, {3,7}  // 垂直棱
        };
        for (int[] edge : edges) {
            float[] v1 = vertices[edge[0]];
            float[] v2 = vertices[edge[1]];
            LineRenderer.render(pose, consumer, v1[0], v1[1], v1[2], v2[0], v2[1], v2[2], r, g, b, a);
        }
    }

    public static void renderOutline(PoseStack.Pose pose, VertexConsumer consumer,
                                     float length, float width, float height,
                                     int r, int g, int b, int a) {
        float halfW = width * 0.5f;
        float[][] verts = {
                {-halfW, 0, 0}, {halfW, 0, 0}, {halfW, 0, length}, {-halfW, 0, length},
                {-halfW, height, 0}, {halfW, height, 0}, {halfW, height, length}, {-halfW, height, length}
        };
        renderLineBox(pose, consumer, verts, r, g, b, a);
    }

    public static void renderOBBOutline(PoseStack.Pose pose, VertexConsumer consumer, OBB obb,
                                        float r, float g, float b, float a) {
        Vec3[] v = obb.getVertices();
        float[][] verts = new float[8][3];
        for (int i = 0; i < 8; i++) {
            verts[i][0] = (float) v[i].x;
            verts[i][1] = (float) v[i].y;
            verts[i][2] = (float) v[i].z;
        }
        int[][] edges = {
                {0,1}, {1,5}, {5,4}, {4,0}, // 底面
                {2,3}, {3,7}, {7,6}, {6,2}, // 顶面
                {0,2}, {1,3}, {4,6}, {5,7}  // 垂直棱
        };
        for (int[] edge : edges) {
            float[] v1 = verts[edge[0]];
            float[] v2 = verts[edge[1]];
            LineRenderer.render(pose, consumer, v1[0], v1[1], v1[2], v2[0], v2[1], v2[2], r, g, b, a);
        }
    }









































    // ========== 几何中心渲染（修正后） ==========
    public static void render(VertexConsumer consumer,
                              float cx, float cy, float cz,
                              Quaternionf rotation,
                              float width, float height, float depth,
                              float r, float g, float b, float a, int light,
                              float uMin, float vMin, float uMax, float vMax) {
        float hw = width * 0.5f;
        float hh = height * 0.5f;
        float hd = depth * 0.5f;

        // 前 (Z+) —— 原顺序正确，不变
        QuadRenderer.render(consumer,
                -hw, -hh,  hd,  // 左下前
                hw, -hh,  hd,  // 右下前
                hw,  hh,  hd,  // 右上前
                -hw,  hh,  hd,  // 左上前
                cx, cy, cz, rotation,
                r, g, b, a, light, uMin, vMin, uMax, vMax);

        // 后 (Z-) —— 反转顺序
        QuadRenderer.render(consumer,
                -hw, -hh, -hd,  // 左下后
                -hw,  hh, -hd,  // 左上后
                hw,  hh, -hd,  // 右上后
                hw, -hh, -hd,  // 右下后
                cx, cy, cz, rotation,
                r, g, b, a, light, uMin, vMin, uMax, vMax);

        // 左 (X-) —— 原顺序正确，不变
        QuadRenderer.render(consumer,
                -hw, -hh, -hd,  // 左下后
                -hw, -hh,  hd,  // 左下前
                -hw,  hh,  hd,  // 左上前
                -hw,  hh, -hd,  // 左上后
                cx, cy, cz, rotation,
                r, g, b, a, light, uMin, vMin, uMax, vMax);

        // 右 (X+) —— 反转顺序
        QuadRenderer.render(consumer,
                hw, -hh, -hd,  // 右下后
                hw,  hh, -hd,  // 右上后
                hw,  hh,  hd,  // 右上前
                hw, -hh,  hd,  // 右下前
                cx, cy, cz, rotation,
                r, g, b, a, light, uMin, vMin, uMax, vMax);

        // 上 (Y+) —— 反转顺序
        QuadRenderer.render(consumer,
                -hw,  hh, -hd,  // 左上后
                -hw,  hh,  hd,  // 左上前
                hw,  hh,  hd,  // 右上前
                hw,  hh, -hd,  // 右上后
                cx, cy, cz, rotation,
                r, g, b, a, light, uMin, vMin, uMax, vMax);

        // 下 (Y-) —— 原顺序正确，不变
        QuadRenderer.render(consumer,
                -hw, -hh, -hd,  // 左下后
                hw, -hh, -hd,  // 右下后
                hw, -hh,  hd,  // 右下前
                -hw, -hh,  hd,  // 左下前
                cx, cy, cz, rotation,
                r, g, b, a, light, uMin, vMin, uMax, vMax);
    }

    public static void render(VertexConsumer consumer,
                              float cx, float cy, float cz,
                              Quaternionf rotation,
                              float width, float height, float depth,
                              int argb, int light,
                              float uMin, float vMin, float uMax, float vMax) {
        float r = FastColor.ARGB32.red(argb) / 255f;
        float g = FastColor.ARGB32.green(argb) / 255f;
        float b = FastColor.ARGB32.blue(argb) / 255f;
        float a = FastColor.ARGB32.alpha(argb) / 255f;
        render(consumer, cx, cy, cz, rotation, width, height, depth, r, g, b, a, light, uMin, vMin, uMax, vMax);
    }

    // ========== 向前渲染（修正后） ==========
    public static void renderForward(VertexConsumer consumer,
                                     float px, float py, float pz,
                                     Quaternionf rotation,
                                     float width, float height, float length,
                                     float r, float g, float b, float a, int light,
                                     float uMin, float vMin, float uMax, float vMax) {
        float hw = width * 0.5f;
        float hh = height * 0.5f;

        // 前表面 (Z=0) —— 现在用反转后的顺序
        QuadRenderer.render(consumer,
                -hw, -hh, 0,       // 左下前
                -hw,  hh, 0,       // 左上前
                hw,  hh, 0,        // 右上前
                hw, -hh, 0,        // 右下前
                px, py, pz, rotation,
                r, g, b, a, light, uMin, vMin, uMax, vMax);

        // 后表面 (Z=+length) —— 现在用原来的顺序
        QuadRenderer.render(consumer,
                hw, -hh, length,   // 右下后
                hw,  hh, length,   // 右上后
                -hw, hh, length,   // 左上后
                -hw, -hh, length,   // 左下后
                px, py, pz, rotation,
                r, g, b, a, light, uMin, vMin, uMax, vMax);

        // 左表面 (X=-hw) —— 反转顺序
        QuadRenderer.render(consumer,
                -hw, -hh, 0,      // 左下前
                -hw, -hh, length, // 左下后
                -hw,  hh, length, // 左上后
                -hw,  hh, 0,      // 左上前
                px, py, pz, rotation,
                r, g, b, a, light, uMin, vMin, uMax, vMax);

        // 右表面 (X=hw) —— 原顺序正确，不变
        QuadRenderer.render(consumer,
                hw, -hh, 0,
                hw,  hh, 0,
                hw,  hh, length,
                hw, -hh, length,
                px, py, pz, rotation,
                r, g, b, a, light, uMin, vMin, uMax, vMax);

        // 上表面 (Y=hh) —— 反转顺序
        QuadRenderer.render(consumer,
                -hw, hh, 0,       // 左上前
                -hw, hh, length,  // 左上后
                hw,  hh, length,  // 右上后
                hw,  hh, 0,       // 右上前
                px, py, pz, rotation,
                r, g, b, a, light, uMin, vMin, uMax, vMax);

        // 下表面 (Y=-hh) —— 原顺序正确，不变
        QuadRenderer.render(consumer,
                -hw, -hh, 0,
                hw, -hh, 0,
                hw, -hh, length,
                -hw, -hh, length,
                px, py, pz, rotation,
                r, g, b, a, light, uMin, vMin, uMax, vMax);
    }

    public static void renderForward(VertexConsumer consumer,
                                     float px, float py, float pz,
                                     Quaternionf rotation,
                                     float width, float height, float length,
                                     int argb, int light,
                                     float uMin, float vMin, float uMax, float vMax) {
        float r = FastColor.ARGB32.red(argb) / 255f;
        float g = FastColor.ARGB32.green(argb) / 255f;
        float b = FastColor.ARGB32.blue(argb) / 255f;
        float a = FastColor.ARGB32.alpha(argb) / 255f;
        renderForward(consumer, px, py, pz, rotation, width, height, length, r, g, b, a, light, uMin, vMin, uMax, vMax);
    }
}
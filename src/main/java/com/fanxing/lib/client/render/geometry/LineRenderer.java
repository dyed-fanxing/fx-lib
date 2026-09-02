package com.fanxing.lib.client.render.geometry;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.FastColor;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class LineRenderer {
    public static void render(PoseStack.Pose pose, VertexConsumer consumer,
                              float x1, float y1, float z1,
                              float x2, float y2, float z2,
                              float r, float g, float b, float a) {
        Matrix4f matrix = pose.pose();
        consumer.addVertex(matrix, x1, y1, z1).setNormal(pose, 0, 1, 0).setColor(r, g, b, a);
        consumer.addVertex(matrix, x2, y2, z2).setNormal(pose, 0, 1, 0).setColor(r, g, b, a);
    }

    public static void render(PoseStack.Pose pose, VertexConsumer consumer,
                              float x1, float y1, float z1,
                              float x2, float y2, float z2,
                              int r, int g, int b, int a) {
        render(pose, consumer, x1, y1,z1,x2,y2,z2, r / 255f, g / 255f, b / 255f, a / 255f);
    }



    public static void render(PoseStack.Pose pose, VertexConsumer consumer,
                              float x1, float y1, float z1,
                              float x2, float y2, float z2,
                              int argb) {
        render(pose, consumer, x1, y1,z1,x2,y2,z2,(argb >> 16 & 255) / 255f ,(argb >> 8 & 255) / 255f,(argb & 255) / 255f, (argb >> 24) / 255f);
    }
}

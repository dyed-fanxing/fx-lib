package com.fanxing.corelib.client.render.shape;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class LineRenderer {
    public static void render(PoseStack.Pose pose, VertexConsumer consumer, Vector3f p1, Vector3f p2, float r, float g, float b, float a) {
        Matrix4f matrix = pose.pose();
        consumer.addVertex(matrix, p1.x, p1.y, p1.z).setNormal(pose, 0, 1, 0).setColor(r, g, b, a);
        consumer.addVertex(matrix, p2.x, p2.y, p2.z).setNormal(pose, 0, 1, 0).setColor(r, g, b, a);
    }

    public static void render(PoseStack.Pose pose, VertexConsumer consumer, Vector3f p1, Vector3f p2, int r, int g, int b, int a) {
        render(pose, consumer, p1, p2, r / 255f, g / 255f, b / 255f, a / 255f);
    }
}

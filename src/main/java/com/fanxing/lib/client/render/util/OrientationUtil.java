package com.fanxing.lib.client.render.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * @author dyed_fanxing
 * @date 2026/5/6 16:57
 */
public class OrientationUtil {
    /**
     * 始终面向相机（全向 Billboard）
     *
     * @param poseStack 姿态栈
     * @param center    物体中心（世界坐标）
     */
    public static void billboard(PoseStack poseStack, Vector3f center) {
        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        Vector3f dir = cameraPos.toVector3f().sub(center).normalize();
        Quaternionf rot = new Quaternionf().rotateTo(new Vector3f(0, 1, 0), dir);
        poseStack.mulPose(rot);
    }

    /**
     * 垂直面向相机（只绕 Y 轴旋转，保持直立）
     *
     * @param poseStack 姿态栈
     * @param center    物体中心
     */
    public static void billboardVertical(PoseStack poseStack, Vector3f center) {
        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        Vector3f toCamera = cameraPos.toVector3f().sub(center);
        float angle = (float) Math.atan2(toCamera.x, toCamera.z);
        poseStack.mulPose(new Quaternionf().rotateY(angle));
    }

    /**
     * 使物体法线指向移动方向
     *
     * @param poseStack 姿态栈
     * @param velocity  速度向量
     */
    public static void directional(PoseStack poseStack, Vector3f velocity) {
        if (velocity.lengthSquared() > 1e-6f) {
            Vector3f velDir = new Vector3f(velocity).normalize();
            Quaternionf rot = new Quaternionf().rotateTo(new Vector3f(0, 1, 0), velDir);
            poseStack.mulPose(rot);
        }
    }
}

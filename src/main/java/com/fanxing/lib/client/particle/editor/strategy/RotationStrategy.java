package com.fanxing.lib.client.particle.editor.strategy;

import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;

/**
 * 旋转属性接口，提供插值后的旋转四元数。
 */
@FunctionalInterface
public interface RotationStrategy {
    void getLerpQuaternion(Camera camera,float partialTick,Quaternionf out);
    default void tick(){}

    /**
     * 恒定角速度旋转（支持三轴独立角速度），可指定初始旋转。
     */
    class AngularVelocity implements RotationStrategy {
        public float yawSpeed = 0f;   // 绕 Y 轴角速度（弧度/秒）
        public float pitchSpeed = 0f; // 绕 X 轴角速度（弧度/秒）
        public float rollSpeed = 0f;  // 绕 Z 轴角速度（弧度/秒）

        private final Quaternionf target;   // 目标旋转（当前帧结束时的值）
        private final Quaternionf last;     // 上一帧的目标值

        // 无参构造，初始单位四元数
        public AngularVelocity() {
            this(new Quaternionf(0, 0, 0, 1));
        }

        // 通过欧拉角（角度）指定初始旋转
        public AngularVelocity(float yaw, float pitch, float roll) {
            this(new Quaternionf().rotateY(yaw * Mth.DEG_TO_RAD).rotateX(pitch* Mth.DEG_TO_RAD).rotateZ(roll* Mth.DEG_TO_RAD));
        }

        // 直接指定初始四元数
        public AngularVelocity(Quaternionf initialRotation) {
            this.target = new Quaternionf(initialRotation);
            this.last = new Quaternionf(initialRotation);
        }

        public AngularVelocity(float yaw, float pitch, float roll, float yawSpeed, float pitchSpeed, float rollSpeed) {
            this(new Quaternionf().rotateY(yaw* Mth.DEG_TO_RAD).rotateX(pitch* Mth.DEG_TO_RAD).rotateZ(roll* Mth.DEG_TO_RAD));
            this.yawSpeed = yawSpeed* Mth.DEG_TO_RAD;
            this.pitchSpeed = pitchSpeed* Mth.DEG_TO_RAD;
            this.rollSpeed = rollSpeed* Mth.DEG_TO_RAD;
        }

        public void tick() {
            last.set(target);
            target.rotateY(yawSpeed);
            target.rotateX(pitchSpeed);
            target.rotateZ(rollSpeed);
        }
        // 角度
        public void setSpeed(float yawSpeed, float pitchSpeed, float rollSpeed) {
            this.yawSpeed = yawSpeed* Mth.DEG_TO_RAD;
            this.pitchSpeed = pitchSpeed* Mth.DEG_TO_RAD;
            this.rollSpeed = rollSpeed* Mth.DEG_TO_RAD;
        }
        @Override
        public void getLerpQuaternion(Camera camera,float partialTick,Quaternionf out) {
            out.set(last).slerp(target, partialTick);
        }
    }


    RotationStrategy LOOKAT_XYZ = (camera, partialTick,out) -> out.set(camera.rotation());
    RotationStrategy LOOKAT_Y = (camera,partialTick,out) -> out.set(0.0F,camera.rotation().y,0.0F,camera.rotation().w);

}
package com.fanxing.lib.client.particle;

import com.fanxing.lib.client.particle.property.RotationStrategy;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

/**
 * 基础粒子基类。
 * 提供：
 * - 插值位置与旋转四元数
 * - 面向相机模式或自定义旋转
 * - 尺寸字段供子类使用
 */
public abstract class BaseParticle extends Particle {
    protected float lerpX;
    protected float lerpY;
    protected float lerpZ;
    protected RotationStrategy rotationStrategy = null;
    // 直接存储固定旋转（单位四元数）
    protected Quaternionf rotation = new Quaternionf(0, 0, 0, 1);


    public BaseParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz) {
        super(level, x, y, z);
        this.xd = vx;
        this.yd = vy;
        this.zd = vz;
    }

    public void setRotationProperty(RotationStrategy rotationProperty) {
        this.rotationStrategy = rotationProperty;
    }

    /**
     * 获取插值后的世界坐标（用于渲染）。
     */
    protected void calLerpPos(Camera camera, float partialTick) {
        Vec3 cameraPos = camera.getPosition();
        lerpX = (float) (Mth.lerp(partialTick, this.xo, this.x) - cameraPos.x);
        lerpY = (float) (Mth.lerp(partialTick, this.yo, this.y) - cameraPos.y);
        lerpZ = (float) (Mth.lerp(partialTick, this.zo, this.z) - cameraPos.z);
    }

    @Override
    public void tick() {
        super.tick();
        if (rotationStrategy != null) rotationStrategy.tick();
    }


    /**
     * 模板方法：计算最终旋转四元数，获取世界中心，然后调用子类的几何渲染。
     */
    @Override
    public final void render(@NotNull VertexConsumer consumer, @NotNull Camera camera, float partialTick) {
        calLerpPos(camera, partialTick);
        if (rotationStrategy != null) rotationStrategy.getLerpQuaternion(camera, partialTick, rotation);
        preRender(consumer, camera, partialTick);
        render(consumer, partialTick);
    }


    /**
     * 子类实现此方法，使用世界坐标直接渲染几何体。
     * 可以调用已有的几何渲染器（如 QuadParticleRenderer、RingParticleRenderer），
     * 这些渲染器接受世界坐标的顶点。
     *
     * @param consumer    顶点消费者
     * @param partialTick 帧间插值（可用于额外的插值需求）
     */
    public abstract void render(@NotNull VertexConsumer consumer, float partialTick);

    public void preRender(VertexConsumer consumer, Camera camera, float partialTick) {
    }

    public float getProgress(float partialTick) {
        return Math.min((age + partialTick) / lifetime, 1f);
    }

    @Override
    public void setAlpha(float alpha) {
        super.setAlpha(alpha);
    }

    public int getAge() {
        return age;
    }

    public double getXd() {
        return xd;
    }

    public double getYd() {
        return yd;
    }

    public double getZd() {
        return zd;
    }

    public void setColor(int color) {
        super.setColor(FastColor.ARGB32.red(color) / 255f, FastColor.ARGB32.green(color) / 255f, FastColor.ARGB32.blue(color) / 255f);
    }

    public void setColor(int r, int g, int b) {
        this.setColor(r / 255f, g / 255f, b / 255f);
    }

}
package com.fanxing.lib.client.particle.editor.base;

import com.fanxing.lib.client.particle.editor.strategy.PositionStrategy;
import com.fanxing.lib.client.particle.editor.strategy.RotationStrategy;
import com.fanxing.lib.client.particle.editor.strategy.UVStrategy;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.FastColor;
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
public abstract class AbstractPropertyParticle extends Particle {
    // 需要的属性字段
    public double lerpX;
    public double lerpY;
    public double lerpZ;
    public float lerpU0,lerpV0,lerpU1,lerpV1;
    protected final Quaternionf rotation = new Quaternionf(0, 0, 0, 1);

    protected RotationStrategy rotationStrategy = null;
    // 直接存储固定旋转（单位四元数）
    protected PositionStrategy positionStrategy = null;
    protected UVStrategy uvStrategy = null;
    ParticleRenderType renderType;

    public AbstractPropertyParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz) {
        super(level, x, y, z);
        this.xd = vx;
        this.yd = vy;
        this.zd = vz;
    }

    public void setRotationProperty(RotationStrategy rotationProperty) {
        this.rotationStrategy = rotationProperty;
    }


    @Override
    public void tick() {
        super.tick();
        if(rotationStrategy != null) rotationStrategy.tick();
        if(positionStrategy != null) positionStrategy.tick(this);
        if(uvStrategy != null) uvStrategy.tick(this);

    }
    public void calaLerpPosition(float partialTick) {
        if(positionStrategy == null) {
            lerpX = (float) x;
            lerpY = (float) y;
            lerpZ = (float) z;
        }else positionStrategy.applyLerpPosition(this,partialTick);
    }

    /**
     * 模板方法：计算最终旋转四元数，获取世界中心，然后调用子类的几何渲染。
     */
    @Override
    public final void render(@NotNull VertexConsumer consumer, @NotNull Camera camera, float partialTick) {
        Vec3 cameraPos = camera.getPosition();
        float cx = (float) (lerpX - cameraPos.x);
        float cy = (float) (lerpY - cameraPos.y);
        float cz = (float) (lerpZ - cameraPos.z);
        if(rotationStrategy != null) rotationStrategy.getLerpQuaternion(camera, partialTick, rotation);
        if(uvStrategy != null) uvStrategy.applyLerpUV(this,partialTick);

        render(consumer,cx,cy,cz, partialTick);
    }



    /**
     * 渲染网格，子类实现此方法，使用世界坐标直接渲染网格。
     * 可以调用已有的几何渲染器（如 QuadParticleRenderer、RingParticleRenderer），
     * 这些渲染器接受世界坐标的顶点。
     *
     * @param consumer    顶点消费者
     * @param partialTick 帧间插值（可用于额外的插值需求）
     */
    public abstract void render(@NotNull VertexConsumer consumer,float centerX, float centerY, float centerZ, float partialTick);



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

    public double getX(){
        return x;
    }
    public double getY(){
        return y;
    }
    public double getZ(){
        return z;
    }
    public double getXo(){
        return xo;
    }
    public double getYo(){
        return yo;
    }
    public double getZo(){
        return zo;
    }


    public void setVel(double dx, double dy, double dz) {
        this.xd = dx;
        this.yd = dy;
        this.zd = dz;
    }

    public void setColor(int color) {
        super.setColor(FastColor.ARGB32.red(color) / 255f, FastColor.ARGB32.green(color) / 255f, FastColor.ARGB32.blue(color) / 255f);
    }

    public void setColor(int r, int g, int b) {
        this.setColor(r / 255f, g / 255f, b / 255f);
    }


    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return renderType;
    }


    public void setPositionStrategy(PositionStrategy strategy) {
        this.positionStrategy = strategy;
    }
    public PositionStrategy getPositionStrategy() {
        return this.positionStrategy;
    }

    public void setRotationStrategy(RotationStrategy rotationStrategy) {
        this.rotationStrategy = rotationStrategy;
    }
    public RotationStrategy getRotationStrategy() {
        return rotationStrategy;
    }
    public void setRenderType(ParticleRenderType renderType) {
        this.renderType = renderType;
    }
    public void setUVStrategy(UVStrategy uvStrategy) {
        this.uvStrategy = uvStrategy;
    }

    public UVStrategy getUVStrategy() {
        return uvStrategy;
    }


}
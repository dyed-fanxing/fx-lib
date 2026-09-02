package com.fanxing.lib.client.particle.tree;

import com.fanxing.lib.client.particle.AbstractParticle;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 实体跟随粒子基类。一般用于跟随实体的根粒子
 * 数据源为实体，支持曲线路径和局部偏移。
 * @author dyed_fanxing
 * @since 2026/7/3 00:00
 */
public abstract class FollowEntityParticle extends AbstractParticle {

    private static final Logger log = LoggerFactory.getLogger(FollowEntityParticle.class);
    protected Entity target;
    protected boolean inheritRotation = false;

    public FollowEntityParticle(ClientLevel level, double lx, double ly, double lz, @NotNull Entity target) {
        super(level, lx, ly, lz);
        this.target = target;
        this.hasPhysics = false;
        this.gravity = 0F;
        this.friction = 1F;
        this.speedUpWhenYMotionIsBlocked = false;
        this.lifetime = Integer.MAX_VALUE;
    }


    public void setTarget(Entity target) {
        this.target = target;
    }

    public void setInheritRotation(boolean inheritRotation) {
        this.inheritRotation = inheritRotation;
    }

    @Override
    public void tick() {
        if (shouldRemove()) {
            this.remove();
            return;
        }
        age++;
    }

    @Override
    protected boolean shouldRemove() {
        return super.shouldRemove() || !target.isAlive();
    }


    // ============================================================
    // ★ 变换模板：缩放 → 旋转 → 位移
    // ============================================================

    /**
     * 由 Mixin 在渲染前调用
     */
    public void transform(float partialTick, Camera camera) {
        // ★ 第一步：缩放
        applyScale(partialTick);
        // ★ 第二步：旋转
        applyRotation(partialTick, camera);
        // ★ 第三步：位移
        applyTranslation(partialTick);
        // ★ 第四步：应用父变换
        applyParentTransform(partialTick);
    }

    @Override
    protected void applyTranslation(float partialTick) {
        float t = easePosition(getProgress(partialTick));
        worldX = Mth.lerp(t,this.xo, this.x);
        worldY = Mth.lerp(t,this.yo, this.y);
        worldZ = Mth.lerp(t,this.zo, this.z);
    }

    protected float easePosition(float t) {
        return t;
    }

    protected void applyParentTransform(float partialTick) {
        float yaw = -target.getViewYRot(partialTick) * Mth.DEG_TO_RAD;
        float pitch = target.getViewXRot(partialTick) * Mth.DEG_TO_RAD;
        // ★ 优化1：继承旋转时，直接旋转四元数（这是唯一的重复计算，但无法避免）
        if (inheritRotation) {
            rotation.rotateYXZ(yaw, pitch, 0);
        }
        // ★ 优化2：全部用 float，避免 double 转换
        double lx = worldX, ly = worldY, lz = worldZ;

        double cosP = Mth.cos(pitch);
        double sinP = Mth.sin(pitch);
        double cosY = Mth.cos(yaw);
        double sinY = Mth.sin(yaw);

        // ★ 优化4：使用 FMA 减少乘法+加法指令
        // Pitch 旋转
        double ry = Math.fma(ly, cosP, -lz * sinP);
        double rz = Math.fma(ly, sinP, lz * cosP);
        ly = ry;
        lz = rz;
        // Yaw 旋转
        double rx = Math.fma(lx, cosY, lz * sinY);
        double rz2 = Math.fma(lz, cosY, -lx * sinY);
        lx = rx;
        lz = rz2;

        Vec3 pos = getAnchorPosition(partialTick);
        worldX = lx+pos.x;
        worldY = ly+pos.y;
        worldZ = lz+pos.z;
    }

    protected Vec3 getAnchorPosition(float partialTick) {
        return target.getEyePosition(partialTick);
    }

}
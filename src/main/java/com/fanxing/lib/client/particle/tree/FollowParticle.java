package com.fanxing.lib.client.particle.tree;

import com.fanxing.lib.client.particle.AbstractParticle;
import com.fanxing.lib.client.render.instance.format.InstanceFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;
import org.joml.Quaternionf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * 跟随粒子基类。
 * 位置来自曲线路径（起点→终点）或父粒子跟随。
 *
 * @author dyed_fanxing
 * @since 2026/7/2 22:00
 */
public abstract class FollowParticle extends AbstractParticle {

    private static final Logger log = LoggerFactory.getLogger(FollowParticle.class);
    protected AbstractParticle parent = ZERO_PARENT;
    protected boolean inheritRotation = false;

    public FollowParticle(ClientLevel level,double lsx,double lsy,double lsz,double lex, double ley, double lez) {
        super(level, lsx, lsy, lsz);
        setEndPos(lex,ley,lez);
        this.hasPhysics = false;
        this.speedUpWhenYMotionIsBlocked = false;
        this.gravity = 0F;
        this.friction = 1F;
    }

    public FollowParticle(ClientLevel level,double lsx,double lsy,double lsz,double lex, double ley, double lez, AbstractParticle parent) {
        this(level, lsx, lsy, lsz,lex,ley,lez);
        setParent(parent);
    }

    public void setStartPos(double lsx, double lsy, double lsz){
        this.xo = lsx; this.yo = lsy; this.zo = lsz;
        setPos(this.xo, this.yo, this.zo);
    }
    public void setEndPos(double lex, double ley, double lez){
        this.x = lex;this.y = ley;this.z = lez;
    }
    public void setParent(@NotNull AbstractParticle parent) {
        this.parent = parent;
        depth = (byte) (parent.getDepth()+1);
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
        return super.shouldRemove() || !parent.isAlive();
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
        applyParentTransform();
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
    protected void applyParentTransform() {
        // 1. 继承父缩放
        scaleX *= parent.scaleX;
        scaleY *= parent.scaleY;
        scaleZ *= parent.scaleZ;
        Quaternionf pq = parent.rotation;
        // 2. 是否继承父旋转
        if(inheritRotation) this.rotation.mul(pq);
        // 3. 应用父旋转
        float vx = (float) worldX, vy = (float) worldY, vz = (float) worldZ;
        float qx = pq.x, qy = pq.y, qz = pq.z, qw = pq.w;
        // 第一次叉积 (q × v)
        float cx = Math.fma(qy, vz, -qz * vy);
        float cy = Math.fma(qz, vx, -qx * vz);
        float cz = Math.fma(qx, vy, -qy * vx);
        // 第二次叉积 + 标量部分: v' = (q × v) × q⁻¹ + qw²·v
        // 这里已经包含标量部分，结果就是旋转后的向量，不需要再加原始 v
        float rx = Math.fma(qy, cz, Math.fma(-qz, cy, qw * cx));
        float ry = Math.fma(qz, cx, Math.fma(-qx, cz, qw * cy));
        float rz = Math.fma(qx, cy, Math.fma(-qy, cx, qw * cz));
        // ★ 最终结果：合并加法（3次FMA）
        worldX = Math.fma(2.0f, rx, vx + parent.worldX);
        worldY = Math.fma(2.0f, ry, vy + parent.worldY);
        worldZ = Math.fma(2.0f, rz, vz + parent.worldZ);
    }


    private FollowParticle() {
        super(null, 0, 0, 0);
    }

    public static final FollowParticle ZERO_PARENT = new FollowParticle() {
        {
            this.scaleX = 1f;
            this.scaleY = 1f;
            this.scaleZ = 1f;
            this.worldX = 0;
            this.worldY = 0;
            this.worldZ = 0;
            this.lifetime = Integer.MAX_VALUE;
        }
        @Override
        protected void buildVertices(VertexConsumer consumer, float cx, float cy, float cz, Quaternionf rotation, float partialTick) {}

        @Override
        public @NotNull ParticleRenderType getRenderType() {
            return ParticleRenderType.NO_RENDER;
        }
    };
}
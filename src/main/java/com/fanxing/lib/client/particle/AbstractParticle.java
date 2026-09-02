package com.fanxing.lib.client.particle;

import com.fanxing.lib.client.render.instance.format.InstanceFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;
import org.joml.Quaternionf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * 粒子基类。
 * 粒子执行顺序，由粒子引擎先tick -> transform -> render
 * 变换顺序：先缩放 → 再旋转 → 最后位移。
 */
public abstract class AbstractParticle extends Particle implements LayerParticle{

    private static final Logger log = LoggerFactory.getLogger(AbstractParticle.class);
    // 世界变换（输出）
    public double worldX, worldY, worldZ;
    public float scaleX = 1f, scaleY = 1f, scaleZ = 1f;
    public final Quaternionf rotation = new Quaternionf();


    protected int layerID;
    protected int orderInLayer;

    protected byte depth = 0;
    // ===== 构造器 =====
    public AbstractParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
    }

    /**
     * 直接赋值速度，阻止原版粒子随机化速度，使其更自由可控
     */
    public AbstractParticle(ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
        super(level, x, y, z, dx, dy, dz);
        this.xd = dx;
        this.yd = dy;
        this.zd = dz;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (shouldRemove()) {
            this.remove();
            return;
        }
        age++;
        this.yd -= 0.04 * this.gravity;
        this.move(this.xd, this.yd, this.zd);
        if (this.speedUpWhenYMotionIsBlocked && this.y == this.yo) {
            this.xd *= 1.1;
            this.zd *= 1.1;
        }
        this.xd *= this.friction;
        this.yd *= this.friction;
        this.zd *= this.friction;
        if (this.onGround) {
            this.xd *= 0.7F;
            this.zd *= 0.7F;
        }
    }
    protected boolean shouldRemove() {
        return  this.age >= this.lifetime;
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
        setSize(scaleX,scaleY);
        // ★ 第二步：旋转
        applyRotation(partialTick, camera);
        // ★ 第三步：位移
        applyTranslation(partialTick);
    }

    // ===== 第一步：缩放（子类重写） =====
    protected void applyScale(float partialTick) {
    }

    // ===== 第二步：旋转（子类重写） =====
    protected void applyRotation(float partialTick, Camera camera) {
    }

    // ===== 第三步：位移（子类重写） =====
    protected void applyTranslation(float partialTick) {
        worldX = Mth.lerp(partialTick,this.xo, this.x);
        worldY = Mth.lerp(partialTick,this.yo, this.y);
        worldZ = Mth.lerp(partialTick,this.zo, this.z);
    }
    // ============================================================
    // 渲染
    // ============================================================

    @Override
    public void render(@NotNull VertexConsumer consumer, Camera camera, float partialTick) {
        Vec3 camPos = camera.getPosition();
        float cx = (float) (worldX - camPos.x);
        float cy = (float) (worldY - camPos.y);
        float cz = (float) (worldZ - camPos.z);
        applyColor(partialTick);
        buildVertices(consumer, cx, cy, cz, rotation, partialTick);
    }

    protected abstract void buildVertices(VertexConsumer consumer, float cx, float cy, float cz, Quaternionf rotation, float partialTick);

    protected void applyColor(float partialTick) {
    }


    // ============================================================
    // 视锥剔除
    // ============================================================
    @Override
    public @NotNull AABB getRenderBoundingBox(float partialTick) {
        float hsx = scaleX * 0.5f;
        float hsy = scaleY * 0.5f;
        float hsz = scaleZ * 0.5f;
        return new AABB(worldX - hsx, worldY - hsy, worldZ - hsz, worldX + hsx, worldY + hsy, worldZ + hsz);
    }

    // ★ 默认实现 LayerParticle 接口
    @Override
    public int getLayerID() { return layerID; }
    @Override
    public int getOrderInLayer() { return orderInLayer; }

    public void setLayerID(int layerID) {
        this.layerID = layerID;
    }
    public void setOrderInLayer(int orderInLayer) {
        this.orderInLayer = orderInLayer;
    }

    public byte getDepth() {
        return depth;
    }

    // ★ 判断是否需要透明混合
    public boolean isLayerEnabled() {
        return layerID != 0;
    }

    // ============================================================
    // 工具
    // ============================================================

    public float getProgress(float partialTick) {
        return Math.min((age + partialTick) / lifetime, 1f);
    }

    public void setColor(int color) {
        super.setColor(FastColor.ARGB32.red(color) / 255f, FastColor.ARGB32.green(color) / 255f, FastColor.ARGB32.blue(color) / 255f);
        setAlpha(FastColor.ARGB32.alpha(color) / 255f);
    }

    public void setColor(int r, int g, int b) {
        super.setColor(r / 255f, g / 255f, b / 255f);
    }
    public void initRoll(float roll){
        this.oRoll = this.roll = roll;
    }
    public void setPos(Vec3 pos) {
        super.setPos(pos.x, pos.y, pos.z);
    }
    public void initPos(Vec3 pos) {
        super.setPos(pos.x, pos.y, pos.z);
        this.xo = pos.x;
        this.yo = pos.y;
        this.zo = pos.z;
    }

    @Override
    public Vec3 getPos() {
        return new Vec3(worldX, worldY, worldZ);
    }

    @Override
    public @NotNull String toString() {
        String var10000 = this.getClass().getSimpleName();
        return var10000 + ", Pos (" + this.worldX + "," + this.worldY + "," + this.worldZ + "), RGBA (" + this.rCol + "," + this.gCol + "," + this.bCol + "," + this.alpha + "), Age " + this.age;
    }
}
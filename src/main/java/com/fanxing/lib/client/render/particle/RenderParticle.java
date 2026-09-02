package com.fanxing.lib.client.render.particle;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.Objects;

/**
 * 手动渲染粒子的数据载体。
 * <p>
 * 不继承原版 {@link net.minecraft.client.particle.Particle}，
 * 没有独立生命周期、物理运算或 tick 逻辑，
 * 仅用于在手动渲染管线中传递粒子的世界坐标、纹理 UV、颜色和排序键。
 * </p>
 *
 * <p>排序键（{@code layerID} 和 {@code orderInLayer}）规则与原版 {@code ParticleEngineMixin} 保持一致：
 * <ul>
 *   <li><b>layerID</b>：特效组标识，不同光束/特效之间通过它隔离，距离排序优先。</li>
 *   <li><b>orderInLayer</b>：组内层级（外层=1 先画，内层=3 后画），同组内强制按此顺序排列。</li>
 * </ul>
 * </p>
 *
 * @author dyed_fanxing
 * @since 2026/6/23 15:30
 */
public class RenderParticle {
    /**
     * 纹理精灵
     */
    public TextureAtlasSprite sprite;

    /** 世界坐标，用于渲染 */
    public float x,y,z;
    /** 局部坐标，用于变换计算 */
    public float localX, localY, localZ;

    public float size;
    /** ARGB 颜色（例如 {@code 0x80FF3400} 表示半透明暗红） */
    public int color;

    /** 特效图层 ID */
    public int layerID;
    /** 图层内绘制顺序（值小的先画，外层 → 内层） */
    public int orderInLayer;

    public float roll;

    public RenderParticle(TextureAtlasSprite sprite, float localX, float localY, float localZ, float size, int color, int layerID, int orderInLayer) {
        this.sprite = sprite;
        this.localX = localX;this.localY = localY;this.localZ = localZ;

        this.size = size;
        this.color = color;
        this.layerID = layerID;
        this.orderInLayer = orderInLayer;
    }

    public RenderParticle(TextureAtlasSprite sprite, float size, int color, int layerID, int orderInLayer) {
        this.sprite = sprite;
        this.size = size;
        this.color = color;
        this.layerID = layerID;
        this.orderInLayer = orderInLayer;
    }

    public RenderParticle(TextureAtlasSprite sprite, float size, int color) {
        this.sprite = sprite;
        this.size = size;
        this.color = color;
    }


    public void setLocalPos(float localX, float localY, float localZ) {
        this.localX = localX;
        this.localY = localY;
        this.localZ = localZ;
    }
    public void setWorldPos(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public void setRoll(float roll) {
        this.roll = roll;
    }

    /**
     * 使用预计算的三个轴向量（dir, right, up）进行刚性变换。
     * @param sx, sy, sz 起点的世界坐标
     * @param matrix3f 长度为9的数组，依次为 (dx,dy,dz, rx,ry,rz, ux,uy,uz)
     */
    public void rotate(float sx, float sy, float sz, float[] matrix3f) {
        this.x = sx + matrix3f[0] * localZ + matrix3f[3] * localX + matrix3f[6] * localY;
        this.y = sy + matrix3f[1] * localZ + matrix3f[4] * localX + matrix3f[7] * localY;
        this.z = sz + matrix3f[2] * localZ + matrix3f[5] * localX + matrix3f[8] * localY;
    }
    public void rotate(float sx, float sy, float sz,
                       float dx, float dy, float dz,
                       float rx, float ry, float rz,
                       float ux, float uy, float uz) {
        this.x = sx + dx * localZ + rx * localX + ux * localY;
        this.y = sy + dy * localZ + ry * localX + uy * localY;
        this.z = sz + dz * localZ + rz * localX + uz * localY;
    }

    @Override
    public String toString() {
        return "RenderParticle{" +
                "sprite=" + sprite +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", localX=" + localX +
                ", localY=" + localY +
                ", localZ=" + localZ +
                ", size=" + size +
                ", color=" + color +
                ", layerID=" + layerID +
                ", orderInLayer=" + orderInLayer +
                '}';
    }
}

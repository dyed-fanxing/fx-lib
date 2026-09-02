package com.fanxing.lib.client.render.particle;


import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.phys.Vec3;

/**
 * 曲线粒子
 * @author dyed_fanxing
 * @since 2026/6/23 16:57
 */
public class CurveParticle extends RenderParticle {
    // 将local作为起点
    public float endX, endY, endZ;

    public CurveParticle(TextureAtlasSprite sprite, float localX, float localY, float localZ, float size, int color, int layerID, int orderInLayer) {
        super(sprite, localX, localY, localZ, size, color, layerID, orderInLayer);
    }

    public CurveParticle(TextureAtlasSprite sprite, float size, int color, int layerID, int orderInLayer) {
        super(sprite, size, color, layerID, orderInLayer);
    }

    public CurveParticle(TextureAtlasSprite sprite, float size, int color) {
        super(sprite, size, color);
    }

    public void setEasePos(float startX, float startY, float startZ, float endX, float endY, float endZ) {
        this.localX = startX;
        this.localY = startY;
        this.localZ = startZ;
        this.endX = endX;
        this.endY = endY;
        this.endZ = endZ;
    }

    public void rotateLerp(float progress, float sx, float sy, float sz, float[] axes) {
        float lx = localX + (endX - localX) * progress;
        float ly = localY + (endY - localY) * progress;
        float lz = localZ + (endZ - localZ) * progress;

        this.x = sx + axes[0] * lz + axes[3] * lx + axes[6] * ly;
        this.y = sy + axes[1] * lz + axes[4] * lx + axes[7] * ly;
        this.z = sz + axes[2] * lz + axes[5] * lx + axes[8] * ly;
    }
    public void rotateLerp(float progress,
                           float sx, float sy, float sz,
                           float dx, float dy, float dz,
                           float rx, float ry, float rz,
                           float ux, float uy, float uz) {
        float lx = localX + (endX - localX) * progress;
        float ly = localY + (endY - localY) * progress;
        float lz = localZ + (endZ - localZ) * progress;
        this.x = sx + dx * lz + rx * lx + ux * ly;
        this.y = sy + dy * lz + ry * lx + uy * ly;
        this.z = sz + dz * lz + rz * lx + uz * ly;
    }

    @Override
    public String toString() {
        return "CurveParticle{" +
                "sprite=" + sprite +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", localX=" + localX +
                ", localY=" + localY +
                ", localZ=" + localZ +
                ", endX=" + endX +
                ", endY=" + endY +
                ", endZ=" + endZ +
                ", size=" + size +
                ", color=" + color +
                ", layerID=" + layerID +
                ", orderInLayer=" + orderInLayer +
                '}';
    }
}
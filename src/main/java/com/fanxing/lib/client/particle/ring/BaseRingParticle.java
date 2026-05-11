package com.fanxing.lib.client.particle.ring;

import com.fanxing.lib.ConfigFxLib;
import com.fanxing.lib.client.particle.BaseParticle;
import com.fanxing.lib.client.particle.mesh.RingFanParticleRenderer;
import com.fanxing.lib.client.render.data.RingLayer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

/**
 * 环形粒子基类，使用 ParticleRingRenderer 进行三角形条带渲染。
 * 子类需提供 RingLayer 列表，并可重写获取角度、分段、旋转的方法。
 * 纹理 UV 边界可通过 setUV 动态修改（例如实现滚动）。
 */
public abstract class BaseRingParticle extends BaseParticle {
    protected List<RingLayer> layers;
    protected float startAngle;
    protected float endAngle;

    protected int segments;

    protected FloatUnaryOperator uMin = t -> 0f;
    protected FloatUnaryOperator vMin = t -> 0f;
    protected FloatUnaryOperator uMax = t -> 1f;
    protected FloatUnaryOperator vMax = t -> 1f;
    protected FloatUnaryOperator xScale = t -> 1f;
    protected FloatUnaryOperator yScale = t -> 1f;
    protected FloatUnaryOperator zScale = t -> 1f;

    protected FloatUnaryOperator alphaFactory = t -> 1f;


    public BaseRingParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, List<RingLayer> layers, float startAngle, float endAngle) {
        super(level, x, y, z, vx, vy, vz);
        this.layers = layers;
        this.startAngle = startAngle;
        this.endAngle = endAngle;
        segments = ConfigFxLib.Client.SEGMENTS.getAsInt();
    }
    public BaseRingParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, List<RingLayer> layers) {
        this(level, x, y, z, vx, vy, vz,layers,0f,Mth.TWO_PI);
    }


    public void setColor(int color) {
        setColor(FastColor.ARGB32.red(color), FastColor.ARGB32.green(color), FastColor.ARGB32.blue(color));
        setAlpha(FastColor.ARGB32.alpha(color));
    }


    protected List<RingLayer> getLayers() {
        return layers;
    }

    @Override
    public void render(@NotNull VertexConsumer consumer, Vector3f center, Quaternionf rotation, float partialTick) {
        float progress = getProgress(partialTick);
        setAlpha(alphaFactory.apply(progress));
        RingFanParticleRenderer.render(consumer, center, getLayers(), startAngle, endAngle, segments, rotation,
                xScale.apply(progress),yScale.apply(progress),zScale.apply(progress), getLightColor(partialTick),
                uMin.apply(progress),vMin.apply(progress), uMax.apply(progress),vMax.apply(progress),alpha);
    }


    public void setStartAngle(float startAngle) {
        this.startAngle = startAngle;
    }

    public void setEndAngle(float endAngle) {
        this.endAngle = endAngle;
    }

    public void setUV(FloatUnaryOperator uMin, FloatUnaryOperator uMax, FloatUnaryOperator vMin, FloatUnaryOperator vMax) {
        this.uMin = uMin;this.uMax = uMax;this.vMin = vMin;this.vMax = vMax;
    }
    public void setUV(FloatUnaryOperator ...uv) {
        uMin = uv[0];vMin = uv[1];uMax = uv[2];vMax = uv[3];
    }

    public void setScale(FloatUnaryOperator ...scale) {
        this.xScale = scale[0];
        this.yScale = scale[1];
        this.zScale = scale[2];
    }
    public void setScaleXZ(FloatUnaryOperator xScale,FloatUnaryOperator zScale) {
        this.xScale = xScale;
        this.zScale = zScale;
    }
    public void setScale(FloatUnaryOperator scale) {
        this.xScale = scale;
        this.yScale = scale;
        this.zScale = scale;
    }
    public void setAlphaFactory(FloatUnaryOperator alphaFactory) {
        this.alphaFactory = alphaFactory;
    }
}
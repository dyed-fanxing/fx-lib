//package com.fanxing.lib.client.particle.kinds.ring;
//
//import com.fanxing.lib.client.particle.AbstractParticle;
//import com.fanxing.lib.client.render.data.RingLayer;
//import net.minecraft.client.multiplayer.ClientLevel;
//import net.minecraft.util.Mth;
//
//import java.util.List;
//
///**
// * 环形粒子基类，使用 ParticleRingRenderer 进行三角形条带渲染。
// * 子类需提供 RingLayer 列表，并可重写获取角度、分段、旋转的方法。
// * 纹理 UV 边界可通过 setUV 动态修改（例如实现滚动）。
// */
//public class RingParticle extends AbstractParticle {
//    protected final List<RingLayer> layers;
//    protected float startAngle;
//    protected float endAngle;
//    protected float[] layerDist;
//
//    public RingParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, List<RingLayer> layers, float startAngle, float endAngle) {
//        super(level, x, y, z, vx, vy, vz);
//        this.layers = layers;
//        this.startAngle = startAngle;
//        this.endAngle = endAngle;
//        if (layers == null || layers.size() < 2) return;
//        int n = layers.size();
//        layerDist = new float[n];
//        layerDist[0] = 0f;
//        float totalDist = 0f;
//        for (int i = 1; i < n; i++) {
//            RingLayer prev = layers.get(i - 1);
//            RingLayer curr = layers.get(i);
//            float dr = curr.radius - prev.radius;
//            float dz = curr.zOffset - prev.zOffset;
//            totalDist += (float) Math.sqrt(dr * dr + dz * dz);
//            layerDist[i] = totalDist;
//        }
//        if (totalDist > 0) {
//            float inv = 1f / totalDist;
//            for (int i = 0; i < n; i++) {
//                layerDist[i] *= inv;
//            }
//        }
//    }
//
//    public RingParticle(ClientLevel level, double x, double y, double z, List<RingLayer> layers, float startAngle, float endAngle) {
//        this(level, x, y, z, 0, 0, 0, layers, startAngle, endAngle);
//    }
//    public RingParticle(ClientLevel level, double x, double y, double z,List<RingLayer> layers) {
//        this(level, x, y, z, 0, 0, 0,layers,0f,Mth.TWO_PI);
//
//    }
//
////    @Override
////    public void renderGeometry(@NotNull VertexConsumer consumer, float cx, float cy, float cz, Quaternionf rotation, float xScale, float yScale, float zScale, float partialTick, float u0, float v0, float u1, float v1) {
////        RingFanParticleRenderer.render(consumer,cx,cy,cz,layers,layerDist,startAngle,endAngle,ConfigFxLib.Client.SEGMENTS.getAsInt(),rotation,xScale,yScale,zScale, LightTexture.FULL_BRIGHT,alpha,u0,v0,u1,v1);
////    }
//
//
//    protected List<RingLayer> getLayers() {
//        return layers;
//    }
//}
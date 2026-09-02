package com.fanxing.lib.client.particle.instance;


import com.fanxing.lib.client.particle.AbstractParticle;
import com.fanxing.lib.client.render.instance.format.InstanceFormat;
import com.fanxing.lib.client.render.instance.format.ParticleInstanceFormat;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

/**
 * 专门用于 GPU 实例化渲染的基础粒子类。
 * 继承自 {@link AbstractParticle}，具备将自身数据写入 SSBO 的能力。
 * 实际使用时可以直接实例化或作为基类进一步扩展。
 * @author dyed_fanxing
 * @since 2026/7/6 16:28
 */
public class GpuParticle extends AbstractParticle {

    protected TextureAtlasSprite sprite;

    public GpuParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
    }

    public GpuParticle(ClientLevel level, double x, double y, double z,
                       double dx, double dy, double dz) {
        super(level, x, y, z, dx, dy, dz);
    }

    public void setSprite(TextureAtlasSprite sprite) {
        this.sprite = sprite;
    }

    /**
     * 将粒子自身的数据按照指定的 {@link InstanceFormat} 写入 ByteBuffer。
     * 默认使用 {@link ParticleInstanceFormat}，如果传入其他格式，则按对应格式写入。
     */
    public void writeInstanceData(ByteBuffer buffer, InstanceFormat format) {
        float u0 = 0, v0 = 0, u1 = 1, v1 = 1;
        if (sprite != null) {
            u0 = sprite.getU0();
            v0 = sprite.getV0();
            u1 = sprite.getU1();
            v1 = sprite.getV1();
        }
        format.writePosition(buffer, (float) worldX, (float) worldY, (float) worldZ);
        format.writeRotation(buffer, rotation);
        format.writeScale(buffer, scaleX, scaleY, scaleZ);
        format.writeColor(buffer, rCol, gCol, bCol, alpha);
        format.writeUV(buffer, u0, v0, u1, v1);
    }

    @Override
    public void render(com.mojang.blaze3d.vertex.@NotNull VertexConsumer consumer,
                       net.minecraft.client.Camera camera, float partialTick) {
        // 不执行任何操作 —— 渲染由 InstancedRenderer 接管
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return com.fanxing.lib.client.particle.rendertypes.ParticleRenderTypesFxLib.PARTICLE_TRANSLUCENT;
    }

    @Override
    protected void buildVertices(com.mojang.blaze3d.vertex.VertexConsumer consumer,
                                 float cx, float cy, float cz,
                                 org.joml.Quaternionf rotation, float partialTick) {
        // 不使用传统 VBO 方式提交顶点
    }
}
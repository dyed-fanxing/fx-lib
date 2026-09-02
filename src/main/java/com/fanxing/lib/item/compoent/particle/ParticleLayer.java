package com.fanxing.lib.item.compoent.particle;

import com.fanxing.lib.util.math.ease.EaseType;
import com.fanxing.lib.util.math.random.RandomType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author dyed_fanxing
 * @since 2026/5/25 11:24
 */

public class ParticleLayer {
    public RandomType lifetime;
    public MeshType mesh;
    public ParticleRenderType renderType;
    public PositionProperty position;
    public RotationProperty rotation;
    public ScaleProperty scale;
    public String alphaCurve;
    public List<ParticleLayer> particles;
    public List<EmitterBehavior> emitters;

    public ParticleLayer() {
        lifetime = new RandomType.AvgAmpInt();
        mesh = MeshType.Quad.INSTANCE;
        renderType = new ParticleRenderType();
        position = new PositionProperty.Fixed();
        rotation = new RotationProperty.Fixed();
        scale = new ScaleProperty.Fixed();
        alphaCurve = EaseType.EASES.keySet().iterator().next();
        particles = new ArrayList<>();
        emitters = new ArrayList<>();
    }
    // 全参构造（可选）
    public ParticleLayer(RandomType lifetime, MeshType mesh, ParticleRenderType renderType,
                         PositionProperty position, RotationProperty rotation,
                         ScaleProperty scale, String alphaCurve,
                         List<ParticleLayer> particles, List<EmitterBehavior> emitters) {
        this.lifetime = lifetime;
        this.mesh = mesh;
        this.renderType = renderType;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        this.alphaCurve = alphaCurve;
        this.particles = particles;
        this.emitters = emitters;
    }

    // Codec 使用 getter/setter 映射
    public static final Codec<ParticleLayer> CODEC = Codec.lazyInitialized(() ->
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            RandomType.CODEC.fieldOf("lifetime").forGetter(l -> l.lifetime),
                            MeshType.CODEC.fieldOf("mesh").forGetter(l -> l.mesh),
                            ParticleRenderType.CODEC.fieldOf("renderType").forGetter(l -> l.renderType),
                            PositionProperty.CODEC.fieldOf("position").forGetter(l -> l.position),
                            RotationProperty.CODEC.fieldOf("rotation").forGetter(l -> l.rotation),
                            ScaleProperty.CODEC.fieldOf("scale").forGetter(l -> l.scale),
                            Codec.STRING.fieldOf("alphaCurve").forGetter(l -> l.alphaCurve),
                            ParticleLayer.CODEC.listOf().fieldOf("particles").forGetter(l -> l.particles),
                            EmitterBehavior.CODEC.listOf().fieldOf("emitters").forGetter(l -> l.emitters)
                    ).apply(instance, ParticleLayer::new)
            )
    );

    public static final StreamCodec<ByteBuf, ParticleLayer> STREAM_CODEC =
            ByteBufCodecs.fromCodec(CODEC);


    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof ParticleLayer that)) return false;
        return Objects.equals(lifetime, that.lifetime) && Objects.equals(mesh, that.mesh) && Objects.equals(renderType, that.renderType) && Objects.equals(position, that.position) && Objects.equals(rotation, that.rotation) && Objects.equals(scale, that.scale) && Objects.equals(alphaCurve, that.alphaCurve) && Objects.equals(particles, that.particles) && Objects.equals(emitters, that.emitters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lifetime,mesh,renderType,position,rotation,scale,alphaCurve,particles,emitters);
    }

    @Override
    public String toString() {
        return "ParticleLayer{" +
                "lifetime=" + lifetime +
                ", mesh=" + mesh +
                ", renderType=" + renderType +
                ", position=" + position +
                ", rotation=" + rotation +
                ", scale=" + scale +
                ", alphaCurve='" + alphaCurve + '\'' +
                ", particles=" + particles +
                ", emitters=" + emitters +
                '}';
    }
}
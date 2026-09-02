package com.fanxing.lib.item.compoent.particle;

import com.fanxing.lib.client.render.data.RingLayer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Objects;

/**
 * 网格类型密封接口，每种网格有自己的参数。
 * @author dyed_fanxing
 * @since 2026/5/25 11:29
 */
public sealed interface MeshType permits MeshType.Quad, MeshType.Cylinder, MeshType.Sphere, MeshType.Ring {
    Codec<MeshType> CODEC = Codec.STRING.dispatch(MeshType::type, type -> switch (type) {
        case "quad" -> Quad.CODEC;
        case "cylinder" -> Cylinder.CODEC;
        case "sphere" -> Sphere.CODEC;
        case "ring" -> Ring.CODEC;
        default -> throw new IllegalStateException("Unexpected value: " + type);
    });
    String type();

    // 四边形网格（无额外参数）
    final class Quad implements MeshType {
        public static final Quad INSTANCE = new Quad();
        private Quad() {}
        public static final MapCodec<Quad> CODEC = MapCodec.unit(INSTANCE);
        @Override public String type() { return "quad"; }
    }

    // 圆柱体网格参数
    final class Cylinder implements MeshType {
        public float radius;
        public float height;
        public int segments;
        public Cylinder() {}
        public Cylinder(float radius, float height, int segments) {
            this.radius = radius;
            this.height = height;
            this.segments = segments;
        }
        public static final MapCodec<Cylinder> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.FLOAT.fieldOf("radius").forGetter(c -> c.radius),
                        Codec.FLOAT.fieldOf("height").forGetter(c -> c.height),
                        Codec.INT.fieldOf("segments").forGetter(c -> c.segments)
                ).apply(instance, Cylinder::new)
        );
        @Override public String type() { return "cylinder"; }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Cylinder that)) return false;
            return Float.compare(that.radius, radius) == 0 &&
                    Float.compare(that.height, height) == 0 &&
                    segments == that.segments;
        }
        @Override
        public int hashCode() {
            return Objects.hash(radius, height, segments);
        }
        @Override
        public String toString() {
            return "Cylinder{radius=" + radius + ", height=" + height + ", segments=" + segments + "}";
        }
    }

    // 球体网格参数
    final class Sphere implements MeshType {
        public float radius;
        public int segments;
        public Sphere() {}
        public Sphere(float radius, int segments) {
            this.radius = radius;
            this.segments = segments;
        }
        public static final MapCodec<Sphere> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.FLOAT.fieldOf("radius").forGetter(s -> s.radius),
                        Codec.INT.fieldOf("segments").forGetter(s -> s.segments)
                ).apply(instance, Sphere::new)
        );
        @Override public String type() { return "sphere"; }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Sphere that)) return false;
            return Float.compare(that.radius, radius) == 0 && segments == that.segments;
        }
        @Override
        public int hashCode() {
            return Objects.hash(radius, segments);
        }
        @Override
        public String toString() {
            return "Sphere{radius=" + radius + ", segments=" + segments + "}";
        }
    }

    // 环形网格参数（多层环）
    final class Ring implements MeshType {
        public List<RingLayer> layers;
        public Ring() {}
        public Ring(List<RingLayer> layers) { this.layers = layers; }
        public static final MapCodec<Ring> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        RingLayer.CODEC.listOf().fieldOf("layers").forGetter(r -> r.layers)
                ).apply(instance, Ring::new)
        );
        @Override public String type() { return "ring"; }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Ring ring)) return false;
            return Objects.equals(layers, ring.layers);
        }
        @Override
        public int hashCode() {
            return Objects.hashCode(layers);
        }
        @Override
        public String toString() {
            return "Ring{layers=" + layers + "}";
        }
    }
}
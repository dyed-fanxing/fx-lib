package com.fanxing.lib.item.compoent.particle;

import com.fanxing.lib.client.particle.rendertypes.BlendMode;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.util.Locale;
import java.util.Objects;

/**
 * 粒子渲染类型配置，包含纹理、混合模式、包裹模式、深度遮罩、剔除、着色器名称。
 * 着色器名称对应 GameRenderer 中注册的着色器（如 "particle", "beacon_beam" 等）。
 */
public final class ParticleRenderType {
    public String shader = "rendertype_beacon_beam";   // 着色器名称，例如 "particle", "beacon_beam"
    public ResourceLocation texture;
    public BlendMode blend = BlendMode.TRANSPARENT_ADDITIVE;
    public Filter filter = Filter.LINEAR;
    public Wrap wrap = Wrap.CLAMP;
    public boolean depthTest = true;
    public boolean depthMask = false;
    public UVProperty uv = new UVProperty.Fixed();
    public Cull cull = Cull.NONE;

    public ParticleRenderType() {
    }

    public ParticleRenderType(String shader,ResourceLocation texture, BlendMode blend,Filter filter, Wrap wrap,
                              boolean depthTest,boolean depthMask, UVProperty uv,Cull cull) {
        this.shader = shader;
        this.texture = texture;
        this.blend = blend;
        this.filter = filter;
        this.wrap = wrap;
        this.depthTest = depthTest;
        this.depthMask = depthMask;
        this.uv = uv;
        this.cull = cull;
    }

    public static final Codec<ParticleRenderType> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("shader").forGetter(t -> t.shader),
                    ResourceLocation.CODEC.fieldOf("texture").forGetter(t -> t.texture),
                    BlendMode.CODEC.fieldOf("blend").forGetter(t -> t.blend),
                    Filter.CODEC.fieldOf("filter").forGetter(t -> t.filter),
                    Wrap.CODEC.fieldOf("wrap").forGetter(t -> t.wrap),
                    Codec.BOOL.fieldOf("depthTest").forGetter(t -> t.depthTest),
                    Codec.BOOL.fieldOf("depthMask").forGetter(t -> t.depthMask),
                    UVProperty.CODEC.fieldOf("uv").forGetter(l -> l.uv),
                    Cull.CODEC.fieldOf("cull").forGetter(t -> t.cull)
                    ).apply(instance, ParticleRenderType::new)
    );


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ParticleRenderType that)) return false;

        return depthTest == that.depthTest && depthMask == that.depthMask && Objects.equals(shader, that.shader) && Objects.equals(texture, that.texture) && blend == that.blend && filter == that.filter && wrap == that.wrap && Objects.equals(uv, that.uv) && cull == that.cull;
    }

    @Override
    public int hashCode() {
        return Objects.hash(shader,texture,blend,filter,wrap,depthTest,depthMask,uv,cull);
    }

    @Override
    public String toString() {
        return "ParticleRenderType{" +
                "shader='" + shader + '\'' +
                ", texture=" + texture +
                ", blend=" + blend +
                ", filter=" + filter +
                ", wrap=" + wrap +
                ", depthTest=" + depthTest +
                ", depthMask=" + depthMask +
                ", uv=" + uv +
                ", cull=" + cull +
                '}';
    }

    // WrapMode 枚举保持不变
    public enum Wrap implements StringRepresentable {
        CLAMP(GL11.GL_CLAMP),
        REPEAT(GL11.GL_REPEAT);
        final int glValue;
        Wrap(int gl) { this.glValue = gl; }
        @Override
        public @NotNull String getSerializedName() { return name().toLowerCase(Locale.ROOT); }
        @Override
        public String toString() { return getSerializedName();}
        public static final Codec<Wrap> CODEC = StringRepresentable.fromEnum(Wrap::values);
    }
    // WrapMode 枚举保持不变
    public enum Filter implements StringRepresentable {
        NEAREST(GL11.GL_NEAREST),
        LINEAR(GL11.GL_LINEAR);
        final int glValue;
        Filter(int gl) { this.glValue = gl; }
        @Override
        public @NotNull String getSerializedName() { return name().toLowerCase(Locale.ROOT); }

        @Override
        public String toString() { return getSerializedName();}

        public static final Codec<Filter> CODEC = StringRepresentable.fromEnum(Filter::values);
    }
    public enum Cull implements StringRepresentable {
        NONE(-1),
        BACK(GL11.GL_BACK),
        FRONT(GL11.GL_FRONT);
        final int glValue;
        Cull(int gl) { this.glValue = gl; }
        @Override
        public @NotNull String getSerializedName() { return name().toLowerCase(Locale.ROOT); }
        @Override
        public String toString() { return getSerializedName();}
        public static final Codec<Cull> CODEC = StringRepresentable.fromEnum(Cull::values);
    }
}
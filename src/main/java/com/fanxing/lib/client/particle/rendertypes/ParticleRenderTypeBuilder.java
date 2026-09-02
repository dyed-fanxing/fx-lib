package com.fanxing.lib.client.particle.rendertypes;

import com.fanxing.lib.client.particle.advanced.material.Material;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureManager;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

/**
 * 动态粒子渲染类型构建器。
 * 可设置纹理、混合模式、纹理包裹模式（CLAMP/REPEAT）、深度遮罩、背面剔除、顶点格式、Shader 等。
 *
 * @author dyed_fanxing
 * @since 2026/5/24 21:05
 */
public class ParticleRenderTypeBuilder {
    private final Material material;
    private BlendMode blend = BlendMode.TRANSPARENT_ADDITIVE;
    private int filter = GL11.GL_NEAREST;
    private boolean depthMask = false;
    private int cull = GL11.GL_BACK;
    private static final Map<String, ParticleRenderType> CACHE = new HashMap<>();

    public ParticleRenderTypeBuilder(Material material) {
        this.material = material;
    }

    public ParticleRenderTypeBuilder blend(BlendMode blend) {
        this.blend = blend;
        return this;
    }

    public ParticleRenderTypeBuilder nearest() {
        this.filter = GL11.GL_NEAREST;
        return this;
    }

    public ParticleRenderTypeBuilder linear() {
        this.filter = GL11.GL_LINEAR;
        return this;
    }

    public ParticleRenderTypeBuilder enableWriteDepth() {
        this.depthMask = true;
        return this;
    }

    public ParticleRenderTypeBuilder disableWriteDepth() {
        this.depthMask = false;
        return this;
    }

    public ParticleRenderTypeBuilder disableCull() {
        this.cull = -1;
        return this;
    }

    public ParticleRenderTypeBuilder frontCull() {
        this.cull = GL11.GL_FRONT;
        return this;
    }

    public ParticleRenderTypeBuilder backCull() {
        this.cull = GL11.GL_BACK;
        return this;
    }




    public ParticleRenderType buildQuads() {
        return CACHE.computeIfAbsent(this + "Quads", k -> new SortableParticleRenderType() {
            @Override
            public @NotNull BufferBuilder begin(@NotNull Tesselator tesselator, @NotNull TextureManager textureManager) {
                material.apply(textureManager,filter);
                blend.apply();
                // 深度遮罩/写入深度
                RenderSystem.depthMask(depthMask);
                if (cull == -1) RenderSystem.disableCull();
                else {
                    RenderSystem.enableCull();
                    GL11.glCullFace(cull);
                }
                return tesselator.begin(VertexFormat.Mode.QUADS, material.getShaderSupplier().get().getVertexFormat());
            }

            @Override
            public boolean isTranslucent() {
                return blend != BlendMode.NO_TRANSPARENT;
            }

            @Override
            public int getSortOrder() {
                return blend.ordinal();
            }
            @Override
            public String toString() {
                return "ParticleRenderTypeBuilder{" +
                        "material=" + material +
                        ", blend=" + blend +
                        ", filter=" + filter +
                        ", depthMask=" + depthMask +
                        ", cull=" + cull +
                        ", shaderSupplier=" + material.getShaderSupplier().get() +
                        '}';
            }
        });
    }

    public ParticleRenderType buildTriangles() {
        return CACHE.computeIfAbsent(this + "Triangles", k -> new SortableParticleRenderType() {
                    @Override
                    public @NotNull BufferBuilder begin(@NotNull Tesselator tesselator, @NotNull TextureManager textureManager) {
                        material.apply(textureManager,filter);
                        // 混合模式
                        RenderSystem.enableBlend();
                        blend.apply();
                        // 深度遮罩/写入深度
                        RenderSystem.depthMask(depthMask);
                        // 背面剔除
                        if (cull == -1) RenderSystem.disableCull();
                        else {
                            RenderSystem.enableCull();
                            GL11.glCullFace(cull);
                        }
                        return tesselator.begin(VertexFormat.Mode.TRIANGLES, material.getShaderSupplier().get().getVertexFormat());
                    }

                    @Override
                    public boolean isTranslucent() {
                        return blend != BlendMode.NO_TRANSPARENT;
                    }
                    @Override
                    public int getSortOrder() {
                        return blend.ordinal();
                    }
                    @Override
                    public String toString() {
                        return "ParticleRenderTypeBuilder{" +
                                "material=" + material +
                                ", blend=" + blend +
                                ", filter=" + filter +
                                ", depthMask=" + depthMask +
                                ", cull=" + cull +
                                ", shaderSupplier=" + material.getShaderSupplier().get() +
                                '}';
                    }
                }
        );
    }

    @Override
    public String toString() {
        return "ParticleRenderTypeBuilder{" +
                "material=" + material +
                ", blend=" + blend +
                ", filter=" + filter +
                ", depthMask=" + depthMask +
                ", cull=" + cull +
                ", shaderSupplier=" + material.getShaderSupplier().get() +
                '}';
    }
}

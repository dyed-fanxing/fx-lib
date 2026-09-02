package com.fanxing.lib.client.particle.advanced.material;

import com.fanxing.lib.client.Shaders;
import com.fanxing.lib.client.render.ResourceLocations;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.util.function.Supplier;

/**
 * @author dyed_fanxing
 * @since 2026/6/4 13:06
 */
public class ParticleMaterial implements Material {
    protected ResourceLocation mainTex = ResourceLocations.WHITE_TEXTURE;
    protected int wrap = GL30.GL_CLAMP;
    public ParticleMaterial clamp() {
        this.wrap = GL11.GL_CLAMP;
        return this;
    }

    public ParticleMaterial repeat() {
        this.wrap = GL11.GL_REPEAT;
        return this;
    }

    public ParticleMaterial() {}

    public ParticleMaterial(ResourceLocation mainTex) {
        this.mainTex = mainTex;
    }
    public Supplier<ShaderInstance> getShaderSupplier(){
        return Shaders::getParticleShader;
    }

    @Override
    public void bindTexture(TextureManager textureManager,int filter) {
        bindTexture(textureManager,mainTex,0,wrap,filter);
    }

    @Override
    public String toString() {
        return "ParticleMaterial{" +
                "mainTex=" + mainTex +
                '}';
    }
}

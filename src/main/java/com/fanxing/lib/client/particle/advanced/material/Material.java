package com.fanxing.lib.client.particle.advanced.material;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.util.function.Supplier;

/**
 * 材质：包含一个着色器程序和一组纹理（纹理单元 -> 纹理路径）
 * @author dyed_fanxing
 * @since 2026/6/4 12:47
 */
public interface Material {


    Supplier<ShaderInstance> getShaderSupplier();
    default void apply(TextureManager textureManager,int filter){
        RenderSystem.setShader(getShaderSupplier());
        bindTexture(textureManager,filter);
        RenderSystem.activeTexture(org.lwjgl.opengl.GL13.GL_TEXTURE0);
    }
    void bindTexture(TextureManager textureManager,int filter);



    default void bindTexture(TextureManager textureManager, ResourceLocation texture,int slot,int filter){
        bindTexture(textureManager,texture,slot,GL30.GL_REPEAT,filter);
    }

    default void bindTexture(TextureManager textureManager, ResourceLocation texture,int slot, int wrap, int filter){
        AbstractTexture tex = textureManager.getTexture(texture);
        RenderSystem.setShaderTexture(slot,tex.getId());
        GL11.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, wrap);
        GL11.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, wrap);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filter);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter);
    }


}
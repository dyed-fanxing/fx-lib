package com.fanxing.lib.client.shader;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;

import java.io.IOException;


/**
 * @author dyed_fanxing
 * @since 2026/6/1 11:30
 */
public class RampCutoutShaderInstance extends ShaderInstance {
    public RampCutoutShaderInstance(ResourceProvider provider, ResourceLocation location, VertexFormat format) throws IOException {
        super(provider, location, format);
    }
    public void setIntensity(float value) {
            markDirty();
    }
}

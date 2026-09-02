package com.fanxing.lib.client.particle.advanced.material;

import com.fanxing.lib.client.Shaders;
import com.fanxing.lib.client.render.ResourceLocations;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL30;

import java.util.function.Supplier;

/**
 * @author dyed_fanxing
 * @since 2026/6/4 13:01
 */
public class RampCutoutMaterial implements Material {
    private final ResourceLocation mainTex;       // 主纹理
    private final ResourceLocation coloredTex;    // 着色纹理
    private ResourceLocation maskTex = ResourceLocations.BLACK_TEXTURE;       // 遮罩纹理

    public RampCutoutMaterial(ResourceLocation mainTex, ResourceLocation coloredTex) {
        this.mainTex = mainTex;
        this.coloredTex = coloredTex;
    }

    public RampCutoutMaterial(ResourceLocation mainTex, ResourceLocation coloredTex, ResourceLocation maskTex) {
        this.mainTex = mainTex;
        this.coloredTex = coloredTex;
        this.maskTex = maskTex;
    }

    @Override
    public void bindTexture(TextureManager textureManager, int filter) {
        bindTexture(textureManager, mainTex, 0, GL30.GL_REPEAT, filter);
        bindTexture(textureManager, coloredTex, 3, GL30.GL_REPEAT, filter);
        bindTexture(textureManager, maskTex, 4, GL30.GL_REPEAT, filter);
    }

    public Supplier<ShaderInstance> getShaderSupplier() {
        return Shaders::getRampCutoutShader;
    }

    @Override
    public String toString() {
        return "RampCutoutMaterial{" +
                "mainTex=" + mainTex +
                ", coloredTex=" + coloredTex +
                ", maskTex=" + maskTex +
                '}';
    }
}

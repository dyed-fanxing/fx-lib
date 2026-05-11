package com.fanxing.lib.client;

import com.fanxing.lib.FxLib;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;

@EventBusSubscriber(modid = FxLib.MOD_ID, value = Dist.CLIENT)
public class Shaders {
    private static ShaderInstance whiteEntityShader;
    private static ShaderInstance flyBasicShader;
    private static ShaderInstance topFadeShader;

    private static ShaderInstance entityTranslucentEmissiveAdjustableShader;
    public static ShaderInstance screenShader;
    public static ShaderInstance depthDebugShader;

    public static ShaderInstance getWhiteEntityShader() {
        return whiteEntityShader;
    }
    public static ShaderInstance getFlyBasicShader() {
        return flyBasicShader;
    }
    public static ShaderInstance getTopFadeShader() {
        return topFadeShader;
    }
    public static ShaderInstance getEntityTranslucentEmissiveAdjustableShader() { return entityTranslucentEmissiveAdjustableShader;}
    public static ShaderInstance getScreenShader() { return screenShader;}

    public static ShaderInstance getDepthDebugShader() {
        return depthDebugShader;
    }

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(
                        event.getResourceProvider(),
                        ResourceLocation.fromNamespaceAndPath(FxLib.MOD_ID, "white_entity"),
                        DefaultVertexFormat.NEW_ENTITY
                ),
                shader -> whiteEntityShader = shader
        );
        event.registerShader(new ShaderInstance(
                        event.getResourceProvider(),
                        ResourceLocation.fromNamespaceAndPath(FxLib.MOD_ID, "fly_basic"),
                        DefaultVertexFormat.NEW_ENTITY
                ),
                shader -> flyBasicShader = shader);
        event.registerShader(new ShaderInstance(
                        event.getResourceProvider(),
                        ResourceLocation.fromNamespaceAndPath(FxLib.MOD_ID, "top_fade"),
                        DefaultVertexFormat.NEW_ENTITY
                ),
                shader -> topFadeShader = shader);
        event.registerShader(new ShaderInstance(
                        event.getResourceProvider(),
                        ResourceLocation.fromNamespaceAndPath(FxLib.MOD_ID, "entity_translucent_emissive_adjustable"),
                        DefaultVertexFormat.NEW_ENTITY
                ),
                shader -> entityTranslucentEmissiveAdjustableShader = shader);
        event.registerShader(new ShaderInstance(
                event.getResourceProvider(),
                ResourceLocation.fromNamespaceAndPath(FxLib.MOD_ID, "screen"),
                DefaultVertexFormat.NEW_ENTITY
        ), shader -> screenShader = shader);
        event.registerShader(new ShaderInstance(
                event.getResourceProvider(),
                ResourceLocation.fromNamespaceAndPath(FxLib.MOD_ID, "screen"),
                DefaultVertexFormat.NEW_ENTITY
        ), shader -> screenShader = shader);
        event.registerShader(new ShaderInstance(
                event.getResourceProvider(),
                ResourceLocation.fromNamespaceAndPath(FxLib.MOD_ID, "depth_debug"),
                DefaultVertexFormat.NEW_ENTITY
        ), shader -> depthDebugShader = shader);
    }

}
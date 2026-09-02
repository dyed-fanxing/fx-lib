package com.fanxing.lib.client;

import com.fanxing.lib.FxLib;
import com.fanxing.lib.client.render.instance.InstancedShaderProgram;
import com.fanxing.lib.client.render.instance.format.ParticleInstanceFormat;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;

@EventBusSubscriber(modid = FxLib.MOD_ID, value = Dist.CLIENT)
public class Shaders {
    public static ShaderInstance particleShader;
    public static ShaderInstance rampCutoutShader;

    public static ShaderInstance getParticleShader() {
        return particleShader;
    }
    public static ShaderInstance getRampCutoutShader() {
        return rampCutoutShader;
    }



    // 实例化着色器（手动初始化）
    public static InstancedShaderProgram particleInstancedShader;

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(
                event.getResourceProvider(),
                ResourceLocation.fromNamespaceAndPath(FxLib.MOD_ID, "particle"),
                DefaultVertexFormat.PARTICLE
        ), shader -> particleShader = shader);
        event.registerShader(new ShaderInstance(
                event.getResourceProvider(),
                ResourceLocation.fromNamespaceAndPath(FxLib.MOD_ID, "ramp_cutout"),
                DefaultVertexFormat.PARTICLE
        ), shader -> rampCutoutShader = shader);
    }
    // 在客户端设置阶段初始化实例化着色器
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
//            particleInstancedShader = InstancedShaderProgram.create(FxLib.MOD_ID,"particle", ParticleInstanceFormat.FLOATS_PER_PARTICLE);
        });
    }
}
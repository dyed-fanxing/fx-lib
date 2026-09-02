package com.fanxing.lib.mixin;

import com.fanxing.lib.client.particle.AbstractParticle;
import com.fanxing.lib.client.particle.rendertypes.ParticleRenderTypesFxLib;
import com.fanxing.lib.client.render.instance.InstancedRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.ClientHooks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.function.Predicate;

@Mixin(ParticleEngine.class)
public abstract class ParticleEngineMixin {

    @Shadow @Final
    public Map<ParticleRenderType, Queue<Particle>> particles;

    @Shadow @Final
    public Map<ResourceLocation, ParticleEngine.MutableSpriteSet> spriteSets;

    @Unique
    private ArrayList<AbstractParticle>[] fx_lib$depthBuckets;

    @Unique
    private static final int MAX_DEPTH = 32;

    // 你自己的无上限实例化粒子容器
    @Unique
    private final List<AbstractParticle> fx_lib$instancedParticles = new ArrayList<>();

    // ============================================================
    // 1. 初始化深度桶
    // ============================================================
    @Inject(method = "<init>", at = @At("TAIL"))
    private void initDepthBuckets(ClientLevel level, TextureManager textureManager, CallbackInfo ci) {
        //noinspection unchecked
        this.fx_lib$depthBuckets = new ArrayList[MAX_DEPTH];
        for (int i = 0; i < MAX_DEPTH; i++) {
            this.fx_lib$depthBuckets[i] = new ArrayList<>();
        }
    }

    // ============================================================
    // 2. 拦截 add：AbstractParticle 进入深度桶 + 自己的容器，跳过原版队列
    // ============================================================
    @Inject(method = "add", at = @At("HEAD"), cancellable = true)
    private void onAddParticle(Particle particle, CallbackInfo ci) {
        if (particle instanceof AbstractParticle ap) {
            // 按深度存储，用于后续 transform
            this.fx_lib$depthBuckets[ap.getDepth()].add(ap);
            // 放入自己的渲染容器（无上限）
            fx_lib$instancedParticles.add(ap);
            ci.cancel(); // 阻止原版的队列、粒子组计数等流程
        }
    }

    // ============================================================
    // 3. 每 tick 清理死亡粒子
    // ============================================================
    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        // 清理深度桶
        for (int i = 0; i < MAX_DEPTH; i++) {
            fx_lib$depthBuckets[i].removeIf(p -> !p.isAlive());
        }
        // 清理渲染容器
        fx_lib$instancedParticles.removeIf(p -> !p.isAlive());
    }

    // ============================================================
    // 4. clearParticles 时清空所有自定义结构
    // ============================================================
    @Inject(method = "clearParticles", at = @At("HEAD"))
    private void onClearParticles(CallbackInfo ci) {
        for (int i = 0; i < MAX_DEPTH; i++) {
            this.fx_lib$depthBuckets[i].clear();
        }
        fx_lib$instancedParticles.clear();
    }

    // ============================================================
    // 5. 渲染前按深度顺序计算 transform（父先子后）
    // ============================================================
    @Inject(
            method = "render(Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/culling/Frustum;Ljava/util/function/Predicate;)V",
            at = @At("HEAD")
    )
    private void computeDepthParticles(LightTexture light, Camera camera, float partialTick,
                                       Frustum frustum, Predicate<ParticleRenderType> predicate,
                                       CallbackInfo ci) {
        for (int depth = 0; depth < MAX_DEPTH; depth++) {
            for (AbstractParticle p : this.fx_lib$depthBuckets[depth]) {
                p.transform(partialTick, camera);
            }
        }
    }

    // ============================================================
    // 6. 渲染末尾：GPU 排序 + 实例化绘制
    // ============================================================
    @Inject(
            method = "render(Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/culling/Frustum;Ljava/util/function/Predicate;)V",
            at = @At("TAIL")
    )
    private void renderInstancedParticles(LightTexture light, Camera camera, float partialTick,
                                          Frustum frustum, Predicate<ParticleRenderType> predicate,
                                          CallbackInfo ci) {
        if (!InstancedRenderer.isInitialized() || fx_lib$instancedParticles.isEmpty()) return;

        boolean hasLayer = false;
        for (AbstractParticle p : fx_lib$instancedParticles) {
            if (p.getLayerID() > 0) {
                hasLayer = true;
                break;
            }
        }

        InstancedRenderer.render(camera, fx_lib$instancedParticles, frustum, hasLayer);
    }

    // ============================================================
    // 7. 自定义渲染类型顺序（兼容你的 ParticleRenderTypesFxLib）
    // ============================================================
    @Redirect(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/neoforged/neoforge/client/ClientHooks;makeParticleRenderTypeComparator(Ljava/util/List;)Ljava/util/Comparator;"
            )
    )
    private Comparator<ParticleRenderType> injectCustomRenderOrder(List<ParticleRenderType> originalOrder) {
        List<ParticleRenderType> newOrder = new ArrayList<>(originalOrder);
        newOrder.add(ParticleRenderTypesFxLib.PARTICLE_TRANSLUCENT);
        newOrder.add(ParticleRenderTypesFxLib.PARTICLE_TRANSLUCENT_NO_CULL);
        newOrder.add(ParticleRenderTypesFxLib.PARTICLE_ADDITIVE);
        return ClientHooks.makeParticleRenderTypeComparator(newOrder);
    }
}
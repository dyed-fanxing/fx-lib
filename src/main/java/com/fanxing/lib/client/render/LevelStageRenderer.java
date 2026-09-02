package com.fanxing.lib.client.render;

import com.fanxing.lib.FxLib;
import com.fanxing.lib.client.render.effect.Effect;
import com.fanxing.lib.client.render.entity.EntityEffectStageRender;
import com.fanxing.lib.client.render.particle.RenderParticle;
import com.fanxing.lib.client.render.particle.RenderParticleRenderer;
import com.fanxing.lib.client.render.type.RenderTypesFxLib;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sakpeipei
 * @since 2025/11/17 16:13
 * 自定义特效渲染处理器
 */
@EventBusSubscriber(modid = FxLib.MOD_ID, value = Dist.CLIENT)
public class LevelStageRenderer {
    private final static List<Effect> EFFECTS = new ArrayList<>();
    private static final Logger log = LoggerFactory.getLogger(LevelStageRenderer.class);

    @SubscribeEvent
    private static void onClientTick(ClientTickEvent.Post event) {
        if (EFFECTS.isEmpty()) {
            return;
        }

        EFFECTS.forEach(Effect::tick);
        EFFECTS.removeIf(Effect::isRemoved);
    }

    @SubscribeEvent
    private static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;

        PoseStack poseStack = event.getPoseStack();
        Camera camera = event.getCamera();
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

//        // ─── 第一部分：渲染自定义特效（Effect） ───
//        if (!EFFECTS.isEmpty()) {
//            Frustum frustum = event.getFrustum();
//            Vec3 cameraPos = camera.getPosition();
//
//            poseStack.pushPose();
//            poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
//            for (Effect effect : EFFECTS) {
//                if (!effect.isRemoved() && effect.shouldRender(frustum, cameraPos.x, cameraPos.y, cameraPos.z)) {
//                    effect.render(poseStack, partialTick, bufferSource, camera,
//                            event.getModelViewMatrix(), event.getProjectionMatrix());
//                }
//            }
//            poseStack.popPose();
//        }
        // ─── 第二部分：渲染实现了 EntityEffectStageRender 的实体 ───
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null) {
            EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
            for (Entity entity : level.entitiesForRendering()) {
                EntityRenderer<? super Entity> renderer = dispatcher.getRenderer(entity);
                if (renderer instanceof EntityEffectStageRender effectRenderer) {
                    poseStack.pushPose();
                    poseStack.translate(
                            entity.getX() - camera.getPosition().x,
                            entity.getY() - camera.getPosition().y,
                            entity.getZ() - camera.getPosition().z
                    );
                    effectRenderer.renderEffect(entity, poseStack, bufferSource, partialTick);
                    poseStack.popPose();
                }
            }
        }
        // ─── 第三部分：渲染所有手动粒子（全局排序） ───
        // 渲染 EntityEffectStageRender 实体 ...
        // ─── 开启光照层（模拟原版 ParticleEngine） ───
        LightTexture lightTexture = Minecraft.getInstance().gameRenderer.lightTexture();
        lightTexture.turnOnLightLayer();

        // 提交所有手动粒子
        RenderParticleRenderer.render();

        // 统一提交所有绘制（包括粒子）
        bufferSource.endBatch();

        // ─── 关闭光照层 ───
        lightTexture.turnOffLightLayer();
        bufferSource.endBatch();
    }

    public static void addDecoration(Effect effect) {
        EFFECTS.add(effect);
    }
}
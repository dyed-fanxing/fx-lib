package com.fanxing.lib.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;


/**
 * @author dyed_fanxing
 * @since 2026/6/16 16:11
 * KEY 将实体渲染特效延迟到粒子阶段，防止半透明特效无法覆盖后出现的其他半透明实体等bug
 *  因为一般特效都是最后渲染的，所以得把特效放在粒子阶段或粒子阶段之后
 */
public interface EntityEffectStageRender<T extends Entity> {
    void renderEffect(T entity, PoseStack poseStack, MultiBufferSource bufferSource, float partialTick);
}

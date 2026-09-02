package com.fanxing.lib.client.particle.rendertypes;

import net.minecraft.client.particle.ParticleRenderType;

/**
 * @author dyed_fanxing
 * @since 2026/6/3 14:18
 */
public abstract class SortableParticleRenderType implements ParticleRenderType {
    /**
     * 返回排序优先级，数值越小越先渲染。
     */
    public abstract int getSortOrder();
}
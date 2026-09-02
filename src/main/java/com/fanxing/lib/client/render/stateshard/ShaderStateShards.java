package com.fanxing.lib.client.render.stateshard;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;

/**
 * @author dyed_fanxing
 * @since 2026/6/22 22:07
 */
public class ShaderStateShards {
    public static final RenderStateShard.ShaderStateShard PARTICLE_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getParticleShader);
}

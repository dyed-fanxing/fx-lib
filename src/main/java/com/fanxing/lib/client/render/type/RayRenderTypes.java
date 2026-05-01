package com.fanxing.lib.client.render.type;

import com.fanxing.lib.client.render.ResourceLocations;
import net.minecraft.client.renderer.RenderType;

/**
 * @author dyed_fanxing
 * @date 2026/5/1 11:27
 */
public interface RayRenderTypes {
    RenderType RAY = BeamRenderType.ENERGY_BEAM_TRIANGLE.apply(ResourceLocations.WHITE_TEXTURE);
}

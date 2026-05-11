package com.fanxing.lib.client.vertex;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;

/**
 * @author dyed_fanxing
 * @date 2026/5/11 11:50
 */
public interface VertexFormatFxLib {
    VertexFormat POSITION_COLOR_TEX = VertexFormat.builder()
            .add("Position", VertexFormatElement.POSITION)
            .add("Color", VertexFormatElement.COLOR)
            .add("UV0", VertexFormatElement.UV0)
            .build();
}

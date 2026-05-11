package com.fanxing.lib.client.particle.norender;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;

import javax.swing.text.html.parser.Entity;

/**
 * @author dyed_fanxing
 * @date 2026/5/9 18:47
 */
public class TrackEntityParticle extends NoRenderParticle {
    protected Entity entity;
    public TrackEntityParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
    }



}



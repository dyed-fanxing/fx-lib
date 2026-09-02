package com.fanxing.lib.client.particle.kinds.norender;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/**
 * @author dyed_fanxing
 * @date 2026/5/9 18:47
 */
public class TrackEntityParticle extends NoRenderParticle {
    protected Entity entity;
    public TrackEntityParticle(ClientLevel level, double x, double y, double z,Entity entity) {
        super(level, x, y, z);
        this.entity = entity;
    }
    public void setPos(Vec3 pos) {
        super.setPos(pos.x, pos.y, pos.z);
    }
    public void setPosAndOld(Vec3 pos) {
        super.setPos(pos.x, pos.y, pos.z);
        this.xo = pos.x;
        this.yo = pos.y;
        this.zo = pos.z;
    }
}



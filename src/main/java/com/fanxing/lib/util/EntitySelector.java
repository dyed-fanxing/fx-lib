package com.fanxing.lib.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.animal.FlyingAnimal;

public class EntitySelector {
    /**
     * 目标是否正在飞行
     */
    public static boolean isFlying(LivingEntity t) {
        return !t.onGround() && (t.isNoGravity() || t instanceof FlyingAnimal || t instanceof FlyingMob);
    }

    /**
     * 能否攻击实体，排除拥有者和拥有者的实体
     */
    public static boolean canHitEntityNotOwner(LivingEntity owner,LivingEntity target){
        return target!=owner && !target.isRemoved() && !(target instanceof TraceableEntity traceable && traceable.getOwner() == owner);

    }
}

package com.fanxing.corelib.entity.capability;

import com.fanxing.corelib.phys.OBB;
import net.minecraft.world.phys.AABB;

/**
 * 需要OBB碰撞的实体
 */
public interface OBBHolder {
    void updateOBB();
    default OBB getOBB(){
        return getOBB(1.0F);
    }
    OBB getOBB(float partialTicks);
    /**
     * 重写这个方法，以获取OBB的下的AABB渲染剔除碰撞箱
     */
    AABB getBoundingBoxForCulling();

}

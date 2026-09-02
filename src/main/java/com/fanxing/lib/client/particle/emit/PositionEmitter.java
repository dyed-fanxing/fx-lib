package com.fanxing.lib.client.particle.emit;

/**
 * 位置发射器，位置 + 运动方向
 * @author dyed_fanxing
 * @since 2026/6/22 13:38
 */
@FunctionalInterface
public interface PositionEmitter {
    void accept(float x, float y, float z, float dirX, float dirY, float dirZ);
}
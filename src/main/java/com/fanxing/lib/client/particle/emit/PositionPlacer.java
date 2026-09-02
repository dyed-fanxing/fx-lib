package com.fanxing.lib.client.particle.emit;

/**
 * 位置放置器
 * @author dyed_fanxing
 * @since 2026/6/19 15:57
 */
@FunctionalInterface
public interface PositionPlacer  {
    void accept(float x, float y, float z);
}
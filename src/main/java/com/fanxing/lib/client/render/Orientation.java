package com.fanxing.lib.client.render;

/**
 * @author dyed_fanxing
 * @date 2026/5/6 16:36
 */
public enum Orientation {
    GROUND,        // 平躺，法线向上 (固定Y)
    BILLBOARD,     // 始终面向相机
    BILLBOARD_Z,   // 绕Z轴旋转的告示牌（垂直面向相机）
    DIRECTIONAL,   // 法线指向移动方向
    FIXED          // 使用外部固定旋转
}
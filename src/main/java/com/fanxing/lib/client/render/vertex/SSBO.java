package com.fanxing.lib.client.render.vertex;

import java.nio.ByteBuffer;

/**
 * SSBO 数据布局的抽象描述，相当于原版 {@code VertexFormat} 但用于 SSBO。
 * 每个具体的 SSBO 子类对应一种着色器输入布局。
 * @author dyed_fanxing
 * @since 2026/7/5 23:50
 */
public abstract class SSBO {

    /** 每个实例占用的 float 数量 */
    public abstract int getFloatsPerInstance();

    /**
     * 将单个实例的数据写入映射后的 ByteBuffer 当前位置。
     * 调用者负责管理缓冲区偏移，本方法只负责写入。
     */
    public abstract void writeInstance(ByteBuffer buffer, Object instance);
}
package com.fanxing.lib.client.particle;

/**
 * 复合图层粒子
 * @author dyed_fanxing
 * @since 2026/6/20 21:44
 */
public interface LayerParticle {

    /**
     * 层内顺序
     */
    default int getOrderInLayer(){
        return 0;
    }
    /**
     * 标记是否属于同一层的特效粒子，详细排序规则看粒子引擎的混入ParticleEngineMixin
     */
    default int getLayerID(){
        return 0;
    }

    // ★ 判断是否需要透明混合
    default boolean isLayerEnabled() {
        return getLayerID() != 0;
    }

}

package com.fanxing.lib.client.particle.util;

/**
 * @author dyed_fanxing
 * @since 2026/6/20 22:33
 */
public class LayerParticleIDGenerator {
    public static int AUTO_INCREASE_ID = 0;
    public static int next() { return AUTO_INCREASE_ID++; }
}

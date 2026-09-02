package com.fanxing.lib.client.steup;

import com.fanxing.lib.client.render.instance.InstanceModelRegistry;

/**
 * @author dyed_fanxing
 * @since 2026/7/7 13:45
 */
public class InstanceModels {
    // 模型偏移信息（供实例创建时使用）
    public static InstanceModelRegistry.ModelSlice QUAD = InstanceModelRegistry.registerModel(new float[]{
            -1, -1, 0,
            1, -1, 0,
            1,  1, 0,
            -1,  1, 0
    }, new int[]{0, 1, 2, 0, 2, 3});
    public static InstanceModelRegistry.ModelSlice CUBE = InstanceModelRegistry.registerModel(new float[]{
            -1, -1, -1,   1, -1, -1,   1,  1, -1,  -1,  1, -1,
            -1, -1,  1,   1, -1,  1,   1,  1,  1,  -1,  1,  1
    }, new int[]{
            0,1,2, 0,2,3, 4,5,6, 4,6,7,
            0,1,5, 0,5,4, 2,3,7, 2,7,6,
            0,3,7, 0,7,4, 1,2,6, 1,6,5
    });

    public static void register() {
    }
}

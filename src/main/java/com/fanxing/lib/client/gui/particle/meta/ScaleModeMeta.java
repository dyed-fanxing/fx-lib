package com.fanxing.lib.client.gui.particle.meta;


import com.fanxing.lib.FxLib;
import java.util.List;
/**
 * @author dyed_fanxing
 * @date 2026/5/22 16:48
 */
public enum ScaleModeMeta implements ModeMeta {
    FIXED("fixed", List.of(
            "gui.fx_lib.scale.ratio_x",
            "gui.fx_lib.scale.ratio_y",
            "gui.fx_lib.scale.ratio_z")),
    ACCELERATION("acceleration", List.of(
            "gui.fx_lib.scale.ratio_x", "gui.fx_lib.scale.ratio_y", "gui.fx_lib.scale.ratio_z",
            "gui.fx_lib.scale.vel_x", "gui.fx_lib.scale.vel_y", "gui.fx_lib.scale.vel_z",
            "gui.fx_lib.scale.acc_x", "gui.fx_lib.scale.acc_y", "gui.fx_lib.scale.acc_z")),
    EASE("ease", List.of(
            "gui.fx_lib.scale.start_ratio_x", "gui.fx_lib.scale.start_ratio_y", "gui.fx_lib.scale.start_ratio_z",
            "gui.fx_lib.scale.end_ratio_x", "gui.fx_lib.scale.end_ratio_y", "gui.fx_lib.scale.end_ratio_z",
            "util.fx_lib.math.ease_type"));

    public static final String TRANSLATION_KEY = "gui." + FxLib.MOD_ID + ".scale.mode";
    private final String translationKey;
    private final List<String> params;

    ScaleModeMeta(String key, List<String> params) {
        this.translationKey = TRANSLATION_KEY + "." + key;
        this.params = params;
    }

    @Override
    public String getTranslationKey() {
        return translationKey;
    }

    @Override
    public List<String> getParams() {
        return params;
    }
}
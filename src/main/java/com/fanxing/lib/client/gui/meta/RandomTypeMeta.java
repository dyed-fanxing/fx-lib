package com.fanxing.lib.client.gui.meta;

import com.fanxing.lib.FxLib;

/**
 * @author dyed_fanxing
 * @date 2026/5/13 15:39
 */
public class RandomTypeMeta {
    public static final String RANDOM_TRANSLATION_KEY_PREFIX = "util."+ FxLib.MOD_ID +".math.random.";
    public static final String MATH_TRANSLATION_KEY_PREFIX = "util."+ FxLib.MOD_ID +".math.";

    public static final String AVG_AMP = RANDOM_TRANSLATION_KEY_PREFIX + "avg_amp";

    public static final String AVG = MATH_TRANSLATION_KEY_PREFIX + "avg";
    public static final String AMP = MATH_TRANSLATION_KEY_PREFIX + "amp";
    public static final String ABS_AVG = MATH_TRANSLATION_KEY_PREFIX + "abs_avg";
    public static final String MIN = MATH_TRANSLATION_KEY_PREFIX + "min";
    public static final String MAX = MATH_TRANSLATION_KEY_PREFIX + "max";

    public static String getRandomFloatTranslationKey(String type){
        return RANDOM_TRANSLATION_KEY_PREFIX+type.replace("_float","");
    }

    public static String getRandomIntTranslationKey(String type){
        return RANDOM_TRANSLATION_KEY_PREFIX+type.replace("_int","");
    }
}

package com.fanxing.lib.client.gui;

import com.fanxing.lib.FxLib;

public enum ColorMode {
    RGB(new int[]{255,255,255},new String[]{"gui."+FxLib.MOD_ID+".color.red","gui."+FxLib.MOD_ID+".color.green","gui."+FxLib.MOD_ID+".color.blue"}),
    HSV(new int[]{360,100,100},new String[]{"gui."+FxLib.MOD_ID+".color_mode.hsv.hue","gui."+FxLib.MOD_ID+".color_mode.hsv.saturation","gui."+FxLib.MOD_ID+".color_mode.hsv.value"});

    public final int[] max;
    public final String[] names;
    ColorMode(int[] max, String[] names){
        this.max = max;
        this.names = names;

    }
}

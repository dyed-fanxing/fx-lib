package com.fanxing.lib.client.gui;

public enum ColorMode {
    RGB(new int[]{255,255,255}),
    HSV(new int[]{360,100,100});

    public final int[] max;
    ColorMode(int[] max){
        this.max = max;
    }
}

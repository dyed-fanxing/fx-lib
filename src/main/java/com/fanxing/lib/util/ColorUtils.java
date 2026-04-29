package com.fanxing.lib.util;

import net.minecraft.util.FastColor;

public class ColorUtils {
    public static int rgbaArrayToInt(int[] color) {
        return FastColor.ARGB32.color(color[3],color[0], color[1], color[2]);
    }
    public static int[] rgbaArrayToInt(int[][] color) {
        if (color == null) return null;
        int[] result = new int[color.length];
        for (int i = 0; i < color.length; i++) {
            result[i] = FastColor.ARGB32.color(color[i][3],color[i][0], color[i][1], color[i][2]);
        }
        return result;
    }

    public static int[] intToRgbaArray(int color) {
        return new int[]{FastColor.ARGB32.red(color),FastColor.ARGB32.green(color),FastColor.ARGB32.blue(color),FastColor.ARGB32.alpha(color)};
    }
    public static int[][] intToRgbaArray(int[] color) {
        if (color == null) return null;
        int[][] result = new int[color.length][];
        for (int i = 0; i < color.length; i++) {
            result[i] = intToRgbaArray(color[i]);
        }
        return result;
    }
    public static int[][] intToRgbaArrays(int... color) {
        return intToRgbaArray(color);
    }

    public static float[] intToFloat(int[] color){
        return new float[]{color[0]/255F, color[1]/255F, color[2]/255F, color[3]/255F};
    }
    // 将二维 int 颜色数组转换为二维 float 数组
    public static float[][] intToFloat(int[][] color) {
        if (color == null) return null;
        float[][] result = new float[color.length][];
        for (int i = 0; i < color.length; i++) {
            result[i] = intToFloat(color[i]);
        }
        return result;
    }





    // HSV to RGB (返回 0xFFRRGGBB)
    public static int hsvToRgb(float hue, float saturation, float value) {
        int i = (int)(hue * 6);
        float f = hue * 6 - i;
        float p = value * (1 - saturation);
        float q = value * (1 - f * saturation);
        float t = value * (1 - (1 - f) * saturation);
        float r, g, b;
        switch (i % 6) {
            case 0: r = value; g = t; b = p; break;
            case 1: r = q; g = value; b = p; break;
            case 2: r = p; g = value; b = t; break;
            case 3: r = p; g = q; b = value; break;
            case 4: r = t; g = p; b = value; break;
            default: r = value; g = p; b = q; break;
        }
        return 0xFF000000 | ((int)(r * 255) << 16) | ((int)(g * 255) << 8) | (int)(b * 255);
    }
    // HSV to RGB (返回 0xFFRRGGBB)
    public static int hsvToRgb(float[] hsv) {
        return hsvToRgb(hsv[0],hsv[1],hsv[2]);
    }

    // RGB to HSV (输出 hue 0-1, saturation 0-1, value 0-1)
    public static float[] rgbToHsv(int r, int g, int b) {
        float rf = r / 255f, gf = g / 255f, bf = b / 255f;
        float max = Math.max(rf, Math.max(gf, bf));
        float min = Math.min(rf, Math.min(gf, bf));
        float h, s, v = max;
        float d = max - min;
        s = (max == 0) ? 0 : d / max;
        if (max == min) {
            h = 0;
        } else {
            if (max == rf) h = (gf - bf) / d + (gf < bf ? 6 : 0);
            else if (max == gf) h = (bf - rf) / d + 2;
            else h = (rf - gf) / d + 4;
            h /= 6;
        }
        return new float[]{h, s, v};
    }
    // RGB to HSV (输出 hue 0-1, saturation 0-1, value 0-1)
    public static float[] rgbToHsv(int rgb) {
        int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
        return rgbToHsv(r,g,b);
    }

    // HSL to RGB (hue 0-1, saturation 0-1, lightness 0-1)
    public static int hslToRgb(float h, float s, float l) {
        float r, g, b;
        if (s == 0) {
            r = g = b = l;
        } else {
            float q = l < 0.5f ? l * (1 + s) : l + s - l * s;
            float p = 2 * l - q;
            r = hueToRgb(p, q, h + 1/3f);
            g = hueToRgb(p, q, h);
            b = hueToRgb(p, q, h - 1/3f);
        }
        return 0xFF000000 | ((int)(r * 255) << 16) | ((int)(g * 255) << 8) | (int)(b * 255);
    }

    private static float hueToRgb(float p, float q, float t) {
        if (t < 0) t += 1;
        if (t > 1) t -= 1;
        if (t < 1/6f) return p + (q - p) * 6 * t;
        if (t < 1/2f) return q;
        if (t < 2/3f) return p + (q - p) * (2/3f - t) * 6;
        return p;
    }

    // RGB to HSL
    public static float[] rgbToHsl(int r, int g, int b) {
        float rf = r / 255f, gf = g / 255f, bf = b / 255f;
        float max = Math.max(rf, Math.max(gf, bf));
        float min = Math.min(rf, Math.min(gf, bf));
        float h, s, l = (max + min) / 2;
        if (max == min) {
            h = s = 0;
        } else {
            float d = max - min;
            s = l > 0.5f ? d / (2 - max - min) : d / (max + min);
            if (max == rf) h = (gf - bf) / d + (gf < bf ? 6 : 0);
            else if (max == gf) h = (bf - rf) / d + 2;
            else h = (rf - gf) / d + 4;
            h /= 6;
        }
        return new float[]{h, s, l};
    }


    // 添加一个辅助方法：将 ARGB 颜色转换为 ABGR
    public static int argbToAbgr(int argb) {
        int a = (argb >> 24) & 0xFF;
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        return (a << 24) | (b << 16) | (g << 8) | r;
    }

}

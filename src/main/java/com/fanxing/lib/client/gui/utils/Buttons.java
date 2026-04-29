package com.fanxing.lib.client.gui.utils;

import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * 按钮创建工具类，提供常用按钮的快速创建方法。
 */
public class Buttons {

    /**
     * 创建一个删除按钮（14x14）
     *
     * @param onPress 点击回调
     * @return ImageButton 实例
     */
    public static ImageButton delete(Runnable onPress) {
        WidgetSprites CROSS_SPRITES = new WidgetSprites(
                ResourceLocation.withDefaultNamespace("widget/cross_button"),
                ResourceLocation.withDefaultNamespace("widget/cross_button_highlighted")
        );
        return new ImageButton(0, 0, 14, 14, CROSS_SPRITES, btn -> onPress.run(), Component.empty());
    }

    /**
     * 创建一个带工具提示的删除按钮
     *
     * @param onPress 点击回调
     * @param tooltip 悬停提示文本
     * @return ImageButton 实例
     */
    public static ImageButton delete(Runnable onPress, Component tooltip) {
        ImageButton button = delete(onPress);
        button.setTooltip(net.minecraft.client.gui.components.Tooltip.create(tooltip));
        return button;
    }
}
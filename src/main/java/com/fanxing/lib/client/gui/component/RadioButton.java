package com.fanxing.lib.client.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

/**
 * @author dyed_fanxing
 * @date 2026/5/13 18:14
 */
public class RadioButton extends Button implements RadioOption<String> {
    boolean selected;
    public RadioButton(int x, int y, int width, int height, Component component, OnPress onPress, CreateNarration createNarration) {
        super(x,y,width,height, component, onPress, createNarration);
    }
    public RadioButton(int x, int y, int width, int height, Component component, OnPress onPress) {
        super(x,y,width,height, component, onPress, Button.DEFAULT_NARRATION);
    }
    public RadioButton(int width, int height, Component component, OnPress onPress) {
        super(0,0,width,height, component, onPress, Button.DEFAULT_NARRATION);
    }
    public RadioButton(Component component, OnPress onPress, CreateNarration createNarration) {
        super(0,0, Minecraft.getInstance().font.width(component), 20, component, onPress, createNarration);
    }

    @Override
    public String getValue() {
        return getMessage().getString();
    }

    @Override
    public boolean isHoveredOrFocused() {
        return super.isHoveredOrFocused() || selected;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }
}

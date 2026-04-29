package com.fanxing.lib.client.gui.component;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

/**
 * 二值状态切换按钮，点击切换 true/false 状态，并更新按钮文字。
 */
public class ToggleButton extends Button {
    private boolean state;
    private Component falseMessage;
    private Component trueMessage;
    private Component falseTooltip;
    private Component trueTooltip;

    public ToggleButton(int x, int y, int width, int height,boolean initialState,
                        Component falseMessage, Component trueMessage,
                        Component falseTooltip, Component trueTooltip,
                        OnPress onPress) {
        super(x, y, width, height, initialState ? trueMessage : falseMessage, onPress, DEFAULT_NARRATION);
        this.state = initialState;
        this.falseMessage = falseMessage;
        this.trueMessage = trueMessage;
        this.falseTooltip = falseTooltip;
        this.trueTooltip = trueTooltip;
        updateTooltip();
    }
    public ToggleButton(int width, boolean initialState,
                        Component falseMessage, Component trueMessage,
                        Component falseTooltip, Component trueTooltip,
                        OnPress onPress) {
        this(0, 0, width, DEFAULT_HEIGHT,initialState,  falseMessage, trueMessage, falseTooltip, trueTooltip, onPress);

    }
    public ToggleButton(int x, int y, int width, int height, OnPress onPress) {
        super(x, y, width, height, Component.empty(), onPress, DEFAULT_NARRATION);
    }

    public ToggleButton(int x, int y, int width, int height,boolean initialState,Component falseMessage, Component trueMessage,OnPress onPress) {
        this(x, y, width, height,initialState,  falseMessage, trueMessage, null, null, onPress);
    }


    public boolean getState() {
        return state;
    }

    public void setState(boolean newState) {
        if (this.state != newState) {
            this.state = newState;
            setMessage(state ? trueMessage : falseMessage);
            updateTooltip();
        }
    }

    public void toggle() {
        setState(!state);
    }

    private void updateTooltip() {
        if (state && trueTooltip != null) setTooltip(Tooltip.create(trueTooltip));
        else if (!state && falseTooltip != null) setTooltip(Tooltip.create(falseTooltip));
        else setTooltip(null);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        toggle();
        super.onClick(mouseX, mouseY);
    }
}
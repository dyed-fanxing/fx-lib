package com.fanxing.lib.client.gui.component;

import com.fanxing.lib.client.gui.Align;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class LabelEditBox extends AbstractWidget {
    private final Font font;
    private final EditBox editBox;
    private final Component label;
    private final int gap;          // 标签与输入框之间的间距
    private final Align mode;
    private int labelWidth;         // 输入框的宽度，如果=0，则默认计算使用剩余的宽度
    private int inputWidth;
    private int labelX;             // 标签文本相对于组件左边缘的偏移

    public LabelEditBox(Font font, int x, int y, int width, int labelWidth,int inputWidth, int height, int gap, Component label, EditBox editBox, Align mode) {
        super(x, y, width, height, label);
        this.font = font;
        this.label = label;
        this.editBox = editBox;
        this.labelWidth = labelWidth;
        this.inputWidth = inputWidth;
        this.gap = gap;
        this.mode = mode;
        updateLayout();
    }

    private void updateLayout() {
        int fontWidth = font.width(label.getString());
        inputWidth = inputWidth==0?width - labelWidth - gap:inputWidth; // 输入框可用宽度
        switch (mode) {
            case CENTER -> labelX = labelWidth - fontWidth;
            case SPREAD ->  labelX = 0;
        }
        editBox.setX(getX() + labelWidth + gap);
        editBox.setY(getY());
        editBox.setHeight(height);
        editBox.setWidth(inputWidth); // 根据模式计算宽度
        editBox.moveCursorToStart(false);
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        updateLayout();
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        updateLayout();
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int yOffset = (height - font.lineHeight) / 2;
        graphics.drawString(font, label, getX() + labelX, getY() + yOffset, 0xFFFFFF);
        editBox.renderWidget(graphics, mouseX, mouseY, partialTick);
    }

    // 事件转发（包括点击标签聚焦）
    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        // 判断是否点击在标签区域（整个 labelWidth 宽的区域）
        if (mx >= getX() && mx <= getX() + labelWidth && my >= getY() && my <= getY() + height) {
            editBox.setFocused(true);
            return true;
        }
        return editBox.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        return editBox.mouseReleased(mx, my, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return editBox.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return editBox.charTyped(codePoint, modifiers);
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        editBox.setFocused(focused);
    }

    @Override
    public boolean isFocused() {
        return editBox.isFocused();
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
        editBox.updateNarration(output);
    }

    // 便捷方法
    public String getText() { return editBox.getValue(); }
    public void setText(String text) { editBox.setValue(text); }
}
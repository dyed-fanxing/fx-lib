package com.fanxing.lib.client.gui.component;

import com.fanxing.lib.mixin.EditBoxAccessor;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

public class DoubleEditBox extends EditBox {
    private final double min, max, step;
    private final DecimalFormat format = new DecimalFormat("0.##");
    private final DoubleConsumer onValueChange;
    private boolean isValid = true;

    // 便捷构造器
    public DoubleEditBox(Font font, DoubleConsumer onValueChange) {
        this(font, 0, 0, 0, 20, Component.empty(), Double.MIN_VALUE, Double.MAX_VALUE, 0.05, 0.0, onValueChange);
    }
    public DoubleEditBox(Font font, double min, double max, double step, double initial, DoubleConsumer onValueChange) {
        this(font, 0, 0, 0, 20, Component.empty(), min, max, step, initial, onValueChange);
    }
    public DoubleEditBox(Font font, int x, int y, int width, int height, Component message,
                         double min, double max, double step, double initial, DoubleConsumer onValueChange) {
        super(font, x, y, width, height, message);
        this.min = min;
        this.max = max;
        this.step = step;
        this.onValueChange = onValueChange;
        // KEY 根据默认值直接更新宽度和高度，防止width和height=0的情况下，setValue失败
        this.width = Math.max(font.width(String.valueOf(initial)), width);
        this.height = Math.max(font.lineHeight,height);
        setValue(initial);
        setResponder(this::onTextChanged);
    }

    public void setValue(double value) {
        double clamped = Mth.clamp(value, min, max);
        setValue(format.format(clamped));
    }

    // KEY 静默设置，不触发回调，防止数据双向绑定时递归调用
    public void setValueSilently(double value) {
        EditBoxAccessor editBoxAccessor = (EditBoxAccessor)this;
        Consumer<String> responder = editBoxAccessor.responder();
        setResponder(null);
        setValue(value);
        setResponder(responder);
    }
    public double getDoubleValue() {
        try {
            return Double.parseDouble(getValue());
        } catch (NumberFormatException e) {
            return min;
        }
    }

    private void onTextChanged(String text) {
        try {
            double val = Double.parseDouble(text);
            if (val < min || val > max) throw new NumberFormatException();
            onValueChange.accept(val);
            isValid = true;
        } catch (NumberFormatException e) {
            isValid = false;
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (isFocused()) {
            if (keyCode == 262) { // 右箭头
                setValue(getDoubleValue() + step);
                return true;
            } else if (keyCode == 263) { // 左箭头
                setValue(getDoubleValue() - step);
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(graphics, mouseX, mouseY, partialTick);
        if (!isValid) {
            graphics.renderOutline(getX(), getY(), getWidth(), getHeight(), 0xFF5555);
        }
    }
}
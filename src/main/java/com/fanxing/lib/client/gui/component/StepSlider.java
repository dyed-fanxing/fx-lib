package com.fanxing.lib.client.gui.component;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.text.DecimalFormat;
import java.util.function.Consumer;

/**
 * 步进数值滑块，基于原版 AbstractSliderButton。
 * 键盘连续调节（鼠标聚焦后即可使用左右键），无焦点丢失。
 */
public class StepSlider extends AbstractSliderButton {
    protected double min;
    protected double max;
    protected double step;
    protected DecimalFormat format;
    protected Consumer<Double> onValueChange;

    /**
     * @param message 按钮初始显示文本（通常可为 Component.empty()，因为 updateMessage 会覆盖）
     */
    public StepSlider(int x, int y, int width, int height, Component message,
                      double min, double max, double step, double currentValue, int precision,
                      Consumer<Double> onValueChange) {
        super(x, y, width, height, message, 0.0);
        this.min = min;
        this.max = max;
        this.step = Math.abs(step);
        this.onValueChange = onValueChange;

        // 创建数值格式化器
        if (step == 0.0) {
            precision = Math.min(precision, 4);
            StringBuilder pattern = new StringBuilder("0");
            if (precision > 0) {
                pattern.append('.');
                pattern.append("0".repeat(precision));
            }
            this.format = new DecimalFormat(pattern.toString());
        } else if (Mth.equal(step, Math.floor(step))) {
            this.format = new DecimalFormat("0");
        } else {
            String stepStr = Double.toString(step);
            String pattern = stepStr.replaceAll("\\d", "0");
            this.format = new DecimalFormat(pattern);
        }

        this.value = (currentValue - min) / (max - min);
        this.value = Mth.clamp(this.value, 0.0, 1.0);
        updateMessage();
        if (onValueChange != null) onValueChange.accept(getValue());
    }

    public double getValue() {
        return Mth.lerp(this.value, min, max);
    }

    public void setValue(double value) {
        double newValue = (value - min) / (max - min);
        newValue = Mth.clamp(newValue, 0.0, 1.0);
        if (!Mth.equal(this.value, newValue)) {
            this.value = newValue;
            applyValue();
            updateMessage();
        }
    }

    public String getValueString() {
        return format.format(getValue());
    }

    @Override
    protected void updateMessage() {
        // 默认不修改消息，子类可重写
    }

    @Override
    protected void applyValue() {
        if (onValueChange != null) onValueChange.accept(getValue());
    }

    // ==================== 静态工厂方法 ====================

    /**
     * 创建一个使用“选项名: 值”格式（自动本地化冒号）的滑块，无需额外编写内部类。
     *
     * @param baseMessage 选项名称（例如 Component.translatable("gui.head_pivot_logic_eye_offset")）
     */
    public static StepSlider createWithOptionName(int x, int y, int width, int height,
                                                  double min, double max, double step, double init,
                                                  int precision, Component baseMessage, Consumer<Double> onValueChange) {
        return new StepSlider(x, y, width, height, Component.empty(), min, max, step, init, precision, onValueChange) {
            @Override
            protected void updateMessage() {
                Component valueText = Component.literal(getValueString());
                setMessage(CommonComponents.optionNameValue(baseMessage, valueText));
            }
        };
    }

    /**
     * 简化版本，步长=1，精度=0，自动显示选项名: 值
     */
    public static StepSlider createWithOptionName(int x, int y, int width, int height,
                                                  double min, double max, double init,
                                                  Component baseMessage, Consumer<Double> onValueChange) {
        return createWithOptionName(x, y, width, height, min, max, 1.0, init, 0, baseMessage, onValueChange);
    }
}
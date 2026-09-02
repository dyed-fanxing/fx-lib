package com.fanxing.lib.client.gui.component;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.function.DoubleConsumer;

public class DoubleEditBox extends NumberEditBox {
    protected final double min;
    protected final double max;
    protected DecimalFormat format = new DecimalFormat("0.#####");
    protected DoubleConsumer onValueChange;

    public DoubleEditBox(double initial,DoubleConsumer onValueChange,Font font) {
        this(0, 0, 0, 20, Component.empty(), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, initial, onValueChange,font);
    }
    public DoubleEditBox(int width,int height,Font font,DoubleConsumer onValueChange) {
        this(0, 0, width, height, Component.empty(), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,  0.0, onValueChange,font);
    }
    public DoubleEditBox(double min, double max,double initial, DoubleConsumer onValueChange,Font font) {
        this( 0, 0, 0, 20, Component.empty(), min, max,initial, onValueChange,font);
    }
    public DoubleEditBox(int x, int y, int width, int height, Component message,
                         double min, double max,double initial, DoubleConsumer onValueChange,Font font) {
        super(font, x, y, width, height, message);
        this.min = min;
        this.max = max;
        this.onValueChange = onValueChange;
        // KEY 根据默认值直接更新宽度和高度，防止width和height=0的情况下，setValue失败
        this.width = Math.max(font.width(String.valueOf(initial)), width);
        this.height = Math.max(font.lineHeight,height);
        setValueSilently(initial);
    }
    public DoubleEditBox precision(int precision) {
        format = new DecimalFormat("0."+ "#".repeat(precision));
        return  this;
    }
    public DoubleConsumer onValueChange(DoubleConsumer onValueChange) {
        this.onValueChange = onValueChange;
        return onValueChange;
    }
    public static DoubleEditBox create(String translationKey,float initial, DoubleConsumer consumer, Font font) {
        DoubleEditBox box = new DoubleEditBox(initial,consumer, font);
        MutableComponent hint = Component.translatable(translationKey);
        String tooltipString = translationKey + ".tooltip";
        if (I18n.exists(tooltipString)) box.setTooltip(Tooltip.create(Component.translatable(tooltipString)));
        box.setHint(hint);
        return box;
    }





    @Override
    protected boolean isValidChar(char c, String cur, int cursor) {
        if (c >= '0' && c <= '9') return true;
        if (c == '.') return !cur.contains(".");
        if (c == '-') return cursor == 0 && !cur.startsWith("-");
        return false;
    }

    @Override
    protected void onValueChange(@NotNull String text) {
        if (text.isEmpty() || text.equals("-")) {
            setValid(false);
            if (responder != null) responder.accept(text);
            return;
        }
        try {
            double val = Double.parseDouble(text);
            if (val >= min && val <= max) {
                setValid(true);
                onValueChange.accept(val);
            } else setValid(false);
        } catch (NumberFormatException e) {
            setValid(false);
        }

        if (responder != null) responder.accept(text);
    }


    public void setValue(double value) {
        double clamped = Mth.clamp(value, min, max);
        String formatted = format.format(clamped);
        if (!formatted.equals(getValue())) {
            super.setValue(formatted);
        }
    }

    public void setValueSilently(double value) {
        double clamped = Mth.clamp(value, min, max);
        String formatted = format.format(clamped);
        if (!formatted.equals(getValue())) {
            super.setValueSilently(formatted);
            setValid(true);
        }
    }

    protected void commit() {
        if (isValid()) super.setValueSilently(format.format(Double.parseDouble(getValue())));
        else setValue(0);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (isFocused() && keyCode == 257) {
            commit();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void setFocused(boolean focused) {
        if (!focused && isFocused()) commit();
        super.setFocused(focused);
    }

}
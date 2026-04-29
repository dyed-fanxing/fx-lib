package com.fanxing.lib.client.gui.component;

import com.fanxing.lib.mixin.EditBoxAccessor;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class IntegerEditBox extends EditBox {
    protected int min, max, step;
    protected final IntConsumer onValueChange;
    protected boolean wrap = false;
    private boolean insideCorrection = false;

    public IntegerEditBox(Font font, IntConsumer onValueChange) {
        this(font, 0, 0, 0, 0, Component.empty(), Integer.MIN_VALUE, Integer.MAX_VALUE, 1, 0, onValueChange);
    }

    public IntegerEditBox(Font font, int x, int y, int width, int height, Component message,
                          int min, int max, int step, int initial, IntConsumer onValueChange) {
        super(font, x, y, width, height, message);
        this.min = min;
        this.max = max;
        this.step = step;
        this.width = Math.max(font.width(String.valueOf(initial)), width);
        this.height = Math.max(font.lineHeight, height);
        this.onValueChange = onValueChange;
        setValueSilently(applyBoundary(initial));
        setResponder(this::onTextChanged);
    }

    public IntegerEditBox wrap(boolean wrap) {
        this.wrap = wrap;
        return this;
    }

    public IntegerEditBox range(int min, int max, int initial) {
        this.min = min;
        this.max = max;
        setValue(initial);
        return this;
    }

    public IntegerEditBox rangeSilently(int min, int max, int initial) {
        this.min = min;
        this.max = max;
        setValueSilently(initial);
        return this;
    }

    public IntegerEditBox step(int step) {
        this.step = step;
        return this;
    }

    private int applyBoundary(int value) {
        if (wrap) {
            int rangeLen = max - min;
            if (rangeLen <= 0) return min;
            int offset = (value - min) % rangeLen;
            if (offset < 0) offset += rangeLen;
            return min + offset;
        } else {
            return Mth.clamp(value, min, max);
        }
    }

    private String normalize(String text) {
        if (text == null || text.isEmpty()) return Integer.toString(min);
        try {
            int raw = Integer.parseInt(text);
            int bounded = applyBoundary(raw);
            return Integer.toString(bounded);
        } catch (NumberFormatException e) {
            return Integer.toString(min);
        }
    }

    public void setValue(int value) {
        int bounded = applyBoundary(value);
        super.setValue(Integer.toString(bounded));
    }

    public void setValueSilently(int value) {
        EditBoxAccessor accessor = (EditBoxAccessor) this;
        Consumer<String> oldResponder = accessor.responder();
        setResponder(null);
        setValue(value);
        setResponder(oldResponder);
    }

    public int getIntValue() {
        try {
            return Integer.parseInt(getValue());
        } catch (NumberFormatException e) {
            return min;
        }
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (!canConsumeInput()) return false;
        // 只允许数字和允许的负号，其余直接拒绝
        if (codePoint >= '0' && codePoint <= '9') {
            return super.charTyped(codePoint, modifiers);
        }
        if (min < 0 && codePoint == '-' && getCursorPosition() == 0 && !getValue().startsWith("-")) {
            return super.charTyped(codePoint, modifiers);
        }
        return false;
    }

    @Override
    public void insertText(String text) {
        // 过滤粘贴内容
        StringBuilder filtered = new StringBuilder();
        boolean canNeg = min < 0;
        boolean hasNeg = getValue().startsWith("-");
        int cursor = getCursorPosition();
        for (char c : text.toCharArray()) {
            if (c >= '0' && c <= '9') {
                filtered.append(c);
            } else if (canNeg && c == '-' && !hasNeg && cursor == 0 && filtered.indexOf("-") == -1) {
                filtered.append(c);
                hasNeg = true;
            }
        }
        if (!filtered.isEmpty()) {
            super.insertText(filtered.toString());
        }
    }

    // 核心：每次文本变化后，强制标准化
    private void onTextChanged(String text) {
        if (insideCorrection) return;
        insideCorrection = true;

        String normalized = normalize(text);
        if (!normalized.equals(text)) {
            setValueSilently(Integer.parseInt(normalized));
            setCursorPosition(normalized.length());
        } else {
            // 已经是标准形式，通知外部
            try {
                onValueChange.accept(Integer.parseInt(text));
            } catch (NumberFormatException ignored) {}
        }

        insideCorrection = false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (canConsumeInput()) {
            if (keyCode == 262) { // 右箭头
                setValue(getIntValue() + step);
                return true;
            }
            if (keyCode == 263) { // 左箭头
                setValue(getIntValue() - step);
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    // 失焦时再次确保（双保险）
    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (!focused) {
            String norm = normalize(getValue());
            if (!norm.equals(getValue())) {
                setValueSilently(Integer.parseInt(norm));
            }
        }
    }
}
package com.fanxing.lib.client.gui.component;

public interface RadioOption<T> {
    T getValue();
    void setSelected(boolean selected);
    boolean isSelected();
}
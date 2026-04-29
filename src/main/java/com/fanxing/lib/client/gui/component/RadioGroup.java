package com.fanxing.lib.client.gui.component;

import java.util.ArrayList;
import java.util.List;

public class RadioGroup<V, W extends RadioOption<V>> {
    private final List<W> options = new ArrayList<>();
    private W selected;

    public RadioGroup() {
    }

    public void addOption(W option) {
        options.add(option);
    }

    public void select(W option) {
        if (selected == option) return;
        if (selected != null) selected.setSelected(false);
        selected = option;
        selected.setSelected(true);
    }

    public W getSelected() {
        return selected;
    }

    public List<V> getValues() {
        return options.stream().map(W::getValue).toList();
    }
    public RadioGroup<V,W> selectFirst(){
        if (selected == null && !options.isEmpty()) {
            W first = options.getFirst();
            first.setSelected(true);
            this.selected = first;
        }
        return this;
    }

    public List<W> getOptions() {
        return options;
    }

    @Override
    public String toString() {
        return "RadioGroup{" +
                "options=" + options +
                ", selected=" + selected +
                '}';
    }
}
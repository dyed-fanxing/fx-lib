package com.fanxing.lib.client.gui.particle.form;


import com.fanxing.lib.client.gui.component.container.FormContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
/**
 * @author dyed_fanxing
 * @since 2026/5/24 14:42
 */
public class LabelMultiWidgetFormItem extends FormContainer.FormItem {
    protected final StringWidget label;
    protected final List<AbstractWidget> widgets;
    protected int gap = 5;
    protected int height = 20;

    public LabelMultiWidgetFormItem(StringWidget label, List<AbstractWidget> widgets,int gap,int height) {
        this.label = label;
        this.widgets = widgets;
        this.gap = gap;
        this.height = height;
        for (AbstractWidget widget : widgets) {
            widget.setHeight(height);
        }
        label.setHeight(height);
    }
    public LabelMultiWidgetFormItem(StringWidget label, List<AbstractWidget> widgets,int height) {
        this(label, widgets, 5,height);
    }
    public LabelMultiWidgetFormItem(StringWidget label, List<AbstractWidget> widgets) {
        this(label, widgets, 5,20);
    }
    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        List<GuiEventListener> list = new ArrayList<>();
        list.add(label);
        list.addAll(widgets);
        return list;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int index,int left,  int top, int width,
                       int mouseX, int mouseY, boolean isHovered,boolean isSelected, float partialTick) {
        label.setX(left);
        label.setY(top);
        label.render(graphics, mouseX, mouseY, partialTick);
        int currentX = left + label.getWidth() + gap;
        int remaining = left + width - currentX;
        int eachWidth = (remaining - gap * (widgets.size() - 1)) / widgets.size();
        for (AbstractWidget w : widgets) {
            w.setX(currentX);
            w.setY(top);
            w.setWidth(eachWidth);
            w.render(graphics, mouseX, mouseY, partialTick);
            currentX += eachWidth + gap;
        }
    }

    @Override
    public @NotNull List<? extends NarratableEntry> narratables() {
        List<NarratableEntry> list = new ArrayList<>();
        list.add(label);
        list.addAll(widgets);
        return list;
    }
}
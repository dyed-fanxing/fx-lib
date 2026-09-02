package com.fanxing.lib.client.gui.particle.entry;

import com.fanxing.lib.client.gui.component.entry.BaseContainerEntry;
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
 * @date 2026/5/22 20:18
 */
public class LabelMultiWidgetEntry extends BaseContainerEntry {
    private final StringWidget label;
    private final List<AbstractWidget> widgets;

    public LabelMultiWidgetEntry(StringWidget label, List<AbstractWidget> widgets) {
        this.label = label;
        this.widgets = widgets;
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        List<GuiEventListener> list = new ArrayList<>();
        list.add(label);
        list.addAll(widgets);
        return list;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
        label.setHeight(height);
        label.setX(left);
        label.setY(top);
        label.render(graphics, mouseX, mouseY, partialTick);

        int gap = 5;
        int currentX = left + label.getWidth() + gap;
        int remaining = left + width - currentX;
        int eachWidth = (remaining - gap * (widgets.size() - 1)) / widgets.size();
        for (AbstractWidget w : widgets) {
            w.setHeight(height);
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
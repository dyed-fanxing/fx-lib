package com.fanxing.lib.client.gui.particle.entry;

import com.fanxing.lib.client.gui.component.entry.BaseContainerEntry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LabelWidgetEntry extends BaseContainerEntry {
    private final StringWidget label;
    private final AbstractWidget widget;

    public LabelWidgetEntry(StringWidget label) {
        this.label = label;
        this.widget = null;
    }
    public LabelWidgetEntry(StringWidget label, AbstractWidget widget) {
        this.label = label;
        this.widget = widget;
    }
    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        if(widget == null) return List.of(label);
        return List.of(label, widget);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
        int gap = 5;
        label.setHeight(height);
        label.setX(left);
        label.setY(top);
        label.render(graphics, mouseX, mouseY, partialTick);
        if(widget != null) {
            widget.setHeight(height);
            widget.setWidth(width-label.getWidth()-gap);
            widget.setX(left + label.getWidth()+gap);
            widget.setY(top);
            widget.render(graphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public @NotNull List<? extends NarratableEntry> narratables() {
        return List.of(label,widget);
    }
}
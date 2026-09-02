package com.fanxing.lib.client.gui.particle.form;

import com.fanxing.lib.client.gui.component.container.FormContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import org.jetbrains.annotations.NotNull;

import java.util.List;
/**
 * @author dyed_fanxing
 * @since 2026/5/24 14:36
 */
public class LabelWidgetFormItem extends FormContainer.FormItem {
    protected final StringWidget label;
    protected final AbstractWidget widget;
    protected int gap = 5;
    protected int height = 20;
    public LabelWidgetFormItem(StringWidget label, AbstractWidget widget,int gap,int height) {
        this.label = label;
        this.widget = widget;
        this.gap = gap;
        this.height = height;
        widget.setHeight(height);
        label.setHeight(height);
    }
    public LabelWidgetFormItem(StringWidget label, AbstractWidget widget,int height) {
        this(label, widget, 5,height);
    }
    public LabelWidgetFormItem(StringWidget label, AbstractWidget widget) {
        this(label, widget, 5,20);
    }



    @Override
    public int getHeight() {
        return height;
    }
    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return List.of(label, widget);
    }
    @Override
    public void render(@NotNull GuiGraphics graphics, int index, int left,  int top,int width,
                       int mouseX, int mouseY, boolean isHovered,boolean isSelected, float partialTick) {
        label.setX(left);
        label.setY(top);
        label.render(graphics, mouseX, mouseY, partialTick);
        widget.setWidth(width - label.getWidth() - gap);
        widget.setX(left + label.getWidth() + gap);
        widget.setY(top);
        widget.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public @NotNull List<? extends NarratableEntry> narratables() {
        return List.of(label, widget);
    }

}

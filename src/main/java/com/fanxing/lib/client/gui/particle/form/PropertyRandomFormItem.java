package com.fanxing.lib.client.gui.particle.form;


import com.fanxing.lib.client.gui.component.Dropdown;
import com.fanxing.lib.client.gui.component.container.FormContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
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
public class PropertyRandomFormItem<T extends EditBox> extends FormContainer.FormItem {
    private final StringWidget label;
    private final Dropdown<String> dropdown;
    private List<T> editBoxes;
    protected int gap;
    protected int height;

    public PropertyRandomFormItem(StringWidget label,Dropdown<String> dropdown,List<T> editBoxes, int gap, int height) {
        this.label = label;
        this.dropdown = dropdown;
        this.editBoxes = editBoxes;
        this.gap = gap;
        this.height = height;
        label.setHeight(height);
        dropdown.setHeight(height);
        for (EditBox widget : editBoxes) {
            widget.setHeight(height);
        }
    }
    public PropertyRandomFormItem(StringWidget label,Dropdown<String> dropdown,List<T> editBoxes,int height) {
        this(label,dropdown, editBoxes, 5,height);
    }
    public PropertyRandomFormItem(StringWidget label,Dropdown<String> dropdown, List<T> editBoxes) {
        this(label,dropdown, editBoxes, 5,20);
    }
    public void setEditBoxes(List<T> editBoxes) {
        this.editBoxes = editBoxes;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        List<GuiEventListener> list = new ArrayList<>();
        list.add(label);
        list.add(dropdown);
        list.addAll(editBoxes);
        return list;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int index,int left,  int top, int width,
                       int mouseX, int mouseY, boolean isHovered,boolean isSelected, float partialTick) {
        int currentX = left;
        // 左侧标签
        label.setHeight(height);
        label.setX(currentX);
        label.setY(top);
        label.render(graphics, mouseX, mouseY, partialTick);
        currentX += label.getWidth() + gap;

        dropdown.setHeight(height);
        dropdown.setX(currentX);
        dropdown.setY(top); // 或 centerY，根据组件自身对齐方式决定
        dropdown.render(graphics, mouseX, mouseY, partialTick);
        currentX += dropdown.getWidth() + gap;

        // 右侧编辑框组（等分布局）
        int n = editBoxes.size();
        if (n == 0) return;
        int rightRemaining = left + width - currentX; // 剩余总宽度
        int eachWidth = (rightRemaining - gap * (n - 1)) / n;
        for (EditBox box : editBoxes) {
            box.setWidth(eachWidth);
            box.setHeight(height);
            box.setX(currentX);
            box.setY(top); // 或 centerY
            box.render(graphics, mouseX, mouseY, partialTick);
            currentX += eachWidth + gap;
        }
    }

    @Override
    public @NotNull List<? extends NarratableEntry> narratables() {
        List<NarratableEntry> list = new ArrayList<>();
        list.add(label);
        list.add(dropdown);
        list.addAll(editBoxes);
        return list;
    }
}
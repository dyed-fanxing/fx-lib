package com.fanxing.lib.client.gui.particle.entry;

import com.fanxing.lib.client.gui.component.DoubleEditBox;
import com.fanxing.lib.client.gui.component.Dropdown;
import com.fanxing.lib.client.gui.component.entry.BaseContainerEntry;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PropertyRandomEntry extends BaseContainerEntry {
    private final StringWidget label;
    private final Dropdown<String> randomMode;
    private List<DoubleEditBox> editBoxes;
    private final List<GuiEventListener> children = new ArrayList<>();
    public PropertyRandomEntry(int labelWidth, Component label, Font font, Dropdown<String> randomMode, List<DoubleEditBox> editBoxes) {
        this.label = new StringWidget(labelWidth,0,label, font).alignLeft();
        this.randomMode = randomMode;
        this.editBoxes = editBoxes;
        children.add(this.label);
        children.add(randomMode);
        children.addAll(editBoxes);
    }

    public void setEditBoxes(List<DoubleEditBox> newEditBoxes) {
        for (DoubleEditBox box : editBoxes) {
            children.remove(box);
        }
        editBoxes = newEditBoxes;
        children.addAll(editBoxes);
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return children;
    }

    @Override
    public void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
        int currentX = left;
        int gap = 5;
        // 左侧标签
        label.setHeight(height);
        label.setX(currentX);
        label.setY(top);
        label.render(graphics, mouseX, mouseY, partialTick);
        currentX += label.getWidth() + gap;

        randomMode.setHeight(height);
        randomMode.setX(currentX);
        randomMode.setY(top); // 或 centerY，根据组件自身对齐方式决定
        randomMode.render(graphics, mouseX, mouseY, partialTick);
        currentX += randomMode.getWidth() + gap;

        // 右侧编辑框组（等分布局）
        int n = editBoxes.size();
        if (n == 0) return;
        int rightRemaining = left + width - currentX; // 剩余总宽度
        int eachWidth = (rightRemaining - gap * (n - 1)) / n;
        for (DoubleEditBox box : editBoxes) {
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
        return List.of();
    }
}
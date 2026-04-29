package com.fanxing.lib.client.gui.component;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * 颜色色块组件，支持点击回调、悬停提示、选中高亮。
 */
public class ColorSwatch extends AbstractWidget implements RadioOption<Integer> {
    private int color;
    private final Consumer<ColorSwatch> onPress;
    private boolean selected;

    public ColorSwatch(int x, int y, int size, int color,Consumer<ColorSwatch> onPress) {
        this(x, y, size,size, color, onPress, null);
    }
    public ColorSwatch(int x, int y, int size, int color,Consumer<ColorSwatch> onPress,Component tooltip) {
        this(x, y, size,size, color, onPress, tooltip);
    }
    public ColorSwatch(int x, int y, int width,int height, int color, Consumer<ColorSwatch> onPress, Component tooltip) {
        super(x, y, width, height, Component.empty());
        this.color = color;
        this.onPress = onPress;
        if (tooltip != null) {
            setTooltip(Tooltip.create(tooltip));
        }
    }


    /**
     * 更新色块颜色
     */
    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public Integer getValue() {
        return color;
    }

    /**
     * 设置选中状态（高亮显示）
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        onPress.accept(this);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int x = getX(), y = getY(), w = width, h = height;
        graphics.fill(x, y , x + w, y + h, 0xFF000000 | color);
        // 绘制整体边框：根据选中状态和悬停状态
        boolean isHovered = isHoveredOrFocused();
        if (selected) {
            // 选中状态：白色加粗外框
            graphics.renderOutline(x - 1, y - 1, getWidth() + 2, getHeight() + 2, 0xFFFFFFFF);
            graphics.renderOutline(x, y, getWidth(), getHeight(), 0xFFFFFFFF);
        } else if (isHovered) {
            // 悬停状态：半透明白色光晕
            graphics.renderOutline(x - 1, y - 1, getWidth() + 2, getHeight() + 2, 0x88FFFFFF);
        } else {
            // 普通状态：黑色外框 + 灰色内框
            graphics.renderOutline(x, y, getWidth(), getHeight(), 0xFF000000);
            graphics.renderOutline(x + 1, y + 1, getWidth() - 2, getHeight() - 2, 0xFFA0A0A0);
        }
    }


    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, Component.translatable("gui.narrate.button", this.getMessage()));
    }
}
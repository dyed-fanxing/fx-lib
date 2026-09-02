package com.fanxing.lib.client.gui.component;



import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetTooltipHolder;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/**
 * @author dyed_fanxing
 * @date 2026/5/22 20:30
 * 支持滚动和 Tooltip 的文本组件。
 * 当文本宽度超过可用宽度时，会左右滚动显示完整文本。
 */
public class ScrollingStringWidget extends StringWidget {
    private final WidgetTooltipHolder tooltipHolder = new WidgetTooltipHolder();

    public ScrollingStringWidget(Component message, Font font) {
        super(message, font);
    }

    public ScrollingStringWidget(int width, int height, Component message, Font font) {
        super(width, height, message, font);
    }

    public ScrollingStringWidget(int x, int y, int width, int height, Component message, Font font) {
        super(x, y, width, height, message, font);
    }

    public void setTooltip(@Nullable Tooltip tooltip) {
        tooltipHolder.set(tooltip);
    }


    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // 滚动显示：使用组件完整宽度，无内边距
        int left = getX();
        int right = getX() + getWidth();
        AbstractWidget.renderScrollingString(graphics, getFont(), getMessage(), left, getY(), right, getY() + getHeight(), getColor());

        // 手动设置的 Tooltip
        tooltipHolder.refreshTooltipForNextRenderPass(isHovered(), isFocused(), getRectangle());
    }
}
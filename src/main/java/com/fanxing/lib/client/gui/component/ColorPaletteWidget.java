package com.fanxing.lib.client.gui.component;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 颜色样板（整体组件）：显示一组色块，支持水平或垂直排列，点击整个组件触发回调。
 * 用于预设方案选择，内部色块不可单独点击。
 */
public class ColorPaletteWidget extends AbstractWidget implements RadioOption<List<Integer>> {
    private final ScreenAxis orientation;
    private final int swatchWidth;    //每个色块的宽
    private final int swatchHeight;   //每个色块的高
    private final List<Integer> colors;
    private boolean selected;
    private final Consumer<ColorPaletteWidget> onPress;  // 整体点击回调

    /**
     * @param orientation 排列方向（水平或垂直）
     * @param swatchWidth  每个色块的宽度（像素）
     * @param swatchHeight  每个色块的高度（像素）
     * @param colors      颜色列表（决定色块数量）
     * @param onPress     点击整个组件时的回调
     */
    public ColorPaletteWidget(int x, int y, ScreenAxis orientation, int swatchWidth, int swatchHeight, List<Integer> colors, Consumer<ColorPaletteWidget> onPress, Component tooltip) {
        super(x, y,
                orientation == ScreenAxis.HORIZONTAL ? swatchWidth * colors.size() : swatchWidth,
                orientation == ScreenAxis.VERTICAL ? swatchHeight * colors.size() : swatchHeight,
                Component.empty());
        this.orientation = orientation;
        this.swatchWidth = swatchWidth;
        this.swatchHeight = swatchHeight;
        this.colors = new ArrayList<>(colors);
        this.onPress = onPress;
        this.setTooltip(Tooltip.create(tooltip));
        this.setMessage(tooltip);
    }
    public ColorPaletteWidget(int x, int y, ScreenAxis orientation, int swatchSize, List<Integer> colors, Consumer<ColorPaletteWidget> onPress, Component tooltip) {
        this(x,y,orientation,swatchSize,swatchSize,colors,onPress,tooltip);
    }

    public static ColorPaletteWidget horizontal(int x, int y, int swatchWidth, int swatchHeight, List<Integer> colors, Consumer<ColorPaletteWidget> onPress, Component tooltip) {
        return new ColorPaletteWidget(x, y, ScreenAxis.HORIZONTAL, swatchWidth,swatchHeight, colors, onPress,tooltip);
    }
    public static ColorPaletteWidget vertical(int x, int y, int swatchWidth, int swatchHeight, List<Integer> colors, Consumer<ColorPaletteWidget> onPress, Component tooltip) {
        return new ColorPaletteWidget(x, y, ScreenAxis.VERTICAL, swatchWidth,swatchHeight, colors, onPress,tooltip);
    }
    public static ColorPaletteWidget horizontal(int x, int y, int swatchSize, List<Integer> colors, Consumer<ColorPaletteWidget> onPress, Component tooltip) {
        return new ColorPaletteWidget(x, y, ScreenAxis.HORIZONTAL, swatchSize, colors, onPress,tooltip);
    }

    public static ColorPaletteWidget vertical(int x, int y, int swatchSize, List<Integer> colors, Consumer<ColorPaletteWidget> onPress, Component tooltip) {
        return new ColorPaletteWidget(x, y, ScreenAxis.VERTICAL, swatchSize, colors, onPress,tooltip);
    }

    /**
     * 更新指定索引的颜色值（用于外部修改整体颜色列表，例如应用预设后刷新）
     */
    public void setColor(int index, int color) {
        if (index >= 0 && index < colors.size()) {
            colors.set(index, color);
        }
    }

    @Override
    public List<Integer> getValue() {
        return colors;
    }

    @Override
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
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int x = getX();
        int y = getY();
        // 绘制所有色块
        for (int i = 0; i < colors.size(); i++) {
            int color = colors.get(i);
            if(orientation == ScreenAxis.HORIZONTAL){
                int currentX = x + i * swatchWidth;
                graphics.fill(currentX, y, currentX + swatchWidth, y + swatchHeight, 0xFF000000 | color);
            }else{
                int currentY = y + (orientation == ScreenAxis.VERTICAL ? i * swatchHeight : 0);
                graphics.fill(x, currentY, x + swatchWidth, currentY + swatchHeight, 0xFF000000 | color);
            }
        }
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
    protected void updateWidgetNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, Component.translatable("gui.fx_lib.color_scheme"));
    }



}
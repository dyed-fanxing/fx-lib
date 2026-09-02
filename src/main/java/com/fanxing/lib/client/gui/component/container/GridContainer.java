package com.fanxing.lib.client.gui.component.container;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 可滚动的网格容器，单元格不存储任何位置信息。
 * 所有尺寸和间距通过子类提供的抽象方法动态获取。
 * setX/setY/setWidth/setHeight 只处理容器自身。
 *
 * @param <C> 单元格类型，必须继承自 GridContainer.Cell
 */
public abstract class GridContainer<C extends GridContainer.Cell> extends AbstractScrollContainer {
    private static final Logger log = LoggerFactory.getLogger(GridContainer.class);
    protected final List<C> cells = new ArrayList<>();
    protected int cols = 1;
    protected C selected;
    protected C hovered;  // 新增：当前鼠标悬停的单元格

    public GridContainer(int x, int y, int width, int height, Component title) {
        super(x, y, width, height, title);
        setWidth(width);
    }

    // ========== 子类必须实现 ==========
    protected abstract int getCellWidth();
    protected abstract int getCellHeight();
    protected abstract int getSpacing();
    @Override
    protected double getScrollStep() {
        return (getCellHeight() + getSpacing()) / 2.0;
    }
    // ========== 只处理容器自身的 setter ==========
    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        int cellW = getCellWidth();
        int spacing = getSpacing();
        this.cols = Math.max(1, (width + spacing) / (cellW + spacing));
    }

    @Override
    protected int getContentHeight() {
        if (cells.isEmpty()) return 0;
        int rows = (cells.size() + cols - 1) / cols;
        return rows * (getCellHeight() + getSpacing());
    }

    // ========== 内容管理 ==========
    protected void replaceCells(Collection<C> newCells) {
        cells.clear();
        cells.addAll(newCells);
    }

    protected void clearCells() {
        cells.clear();
    }

    public C getSelected() { return selected; }
    public void setSelected(C cell) {
        this.selected = cell;
        ensureVisible(cell);
    }
    public C getHovered() {
        return hovered;
    }




    @Override
    public void setFocused(@Nullable GuiEventListener focused) {
        super.setFocused(focused);
        int i = this.cells.indexOf(focused);
        if (i >= 0) {
            C cell = this.cells.get(i);
            this.setSelected(cell);
            if (Minecraft.getInstance().getLastInputType().isKeyboard()) {
                this.ensureVisible(cell);
            }
        }

    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return cells;
    }

    @Override
    protected void renderContents(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.hovered = getCellAt(mouseX, mouseY);
        int scroll = (int) getScrollAmount();
        int cellW = getCellWidth();
        int cellH = getCellHeight();
        int spacing = getSpacing();
        int colWidth = cellW + spacing;
        int rowHeight = cellH + spacing;
        for (int i = 0; i < cells.size(); i++) {
            int row = i / cols;
            int col = i % cols;
            int cellLeft = col * colWidth;
            int cellTop = row * rowHeight;
            int screenLeft = getX() + cellLeft;
            int screenTop = getY() + cellTop - scroll;
            int screenRight = screenLeft + cellW;
            int screenBottom = screenTop + cellH;
            boolean isHovered = cells.get(i) == hovered;
            boolean isSelected = selected == cells.get(i);
            cells.get(i).render(graphics, i, cellW, cellH, screenTop, screenLeft, screenRight, screenBottom,
                    mouseX, mouseY, isHovered, isSelected, partialTick);

        }
    }
    private C getCellAt(double mouseX, double mouseY) {
        int scroll = (int) getScrollAmount();
        int cellW = getCellWidth();
        int cellH = getCellHeight();
        int spacing = getSpacing();
        int colWidth = cellW + spacing;
        int rowHeight = cellH + spacing;

        // 计算相对内容区域的 Y 坐标
        int relY = (int)(mouseY - getY()) + scroll;
        int row = relY / rowHeight;
        int col = (int)(mouseX - getX()) / colWidth;
        if (row < 0 || row >= (cells.size() + cols - 1) / cols) return null;
        if (col < 0 || col >= cols) return null;
        int index = row * cols + col;
        if (index >= cells.size()) return null;
        return cells.get(index);
    }


    private C findNeighbor(C current, ScreenDirection direction) {
        if (current == null) return cells.isEmpty() ? null : cells.getFirst();
        int idx = cells.indexOf(current);
        if (idx < 0) return null;
        int row = idx / cols, col = idx % cols;
        switch (direction) {
            case UP:    row--; break;
            case DOWN:  row++; break;
            case LEFT:  col--; break;
            case RIGHT: col++; break;
        }
        int newIdx = row * cols + col;
        if (newIdx < 0 || newIdx >= cells.size()) return null;
        return cells.get(newIdx);
    }

    private void ensureVisible(C cell) {
        int idx = cells.indexOf(cell);
        if (idx < 0) return;
        int row = idx / cols;
        int cellTop = row * (getCellHeight() + getSpacing());
        int cellBottom = cellTop + getCellHeight();
        int scroll = (int) getScrollAmount();
        if (cellTop - scroll < 0) setScrollAmount(cellTop);
        else if (cellBottom - scroll > getHeight()) setScrollAmount(cellBottom - getHeight());
    }

    // ========== 焦点导航（您的正确版本） ==========
    @Override
    public @Nullable ComponentPath nextFocusPath(@NotNull FocusNavigationEvent event) {
        if (cells.isEmpty()) return null;
        GuiEventListener focused = getFocused();
        if (focused == null) {
            C cell = this.getSelected();
            if (cell == null) cell = this.findNeighbor(null, event.getVerticalDirectionForInitialFocus());
            return cell == null ? null : ComponentPath.path(this, ComponentPath.leaf(cell));
        } else {
            if (event instanceof FocusNavigationEvent.TabNavigation(boolean forward)) {
                int currentIdx = selected != null ? cells.indexOf(selected) : -1;
                int nextIdx;
                if (currentIdx == -1) nextIdx = forward ? 0 : cells.size() - 1;
                else nextIdx = currentIdx + (forward ? 1 : -1);
                if (nextIdx < 0 || nextIdx >= cells.size()) return null;
                C nextCell = cells.get(nextIdx);
                setFocused(nextCell);
                ensureVisible(nextCell);
                return ComponentPath.leaf(nextCell);
            } else if (event instanceof FocusNavigationEvent.ArrowNavigation(ScreenDirection direction)) {
                C next = findNeighbor(selected, direction);
                if (next != null) {
                    setFocused(next);
                    ensureVisible(next);
                    return ComponentPath.leaf(next);
                }
            }
        }
        return null;
    }

    protected void narrateListElementPosition(NarrationElementOutput output, C cell) {
        int i = cells.indexOf(cell);
        if (i >= 0 && cells.size() > 1) {
            output.add(NarratedElementType.POSITION, Component.translatable("narrator.position.list", i + 1, cells.size()));
        }
    }
    @Override
    public void updateWidgetNarration(NarrationElementOutput output) {
        C hovered = getHovered(); // 需要实现 getHovered 方法，类似原版记录 hovered 单元格
        if (hovered != null) {
            narrateListElementPosition(output.nest(), hovered);
            output.add(NarratedElementType.TITLE, hovered.getNarration());
        } else {
            C selected = getSelected();
            if (selected != null) {
                narrateListElementPosition(output.nest(), selected);
                output.add(NarratedElementType.TITLE, selected.getNarration());
            }
        }
        if (isFocused()) {
            output.add(NarratedElementType.USAGE, Component.translatable("narration.selection.usage"));
        }
    }
    // ========== 事件处理 ==========
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (scrollbarVisible() && mouseX >= getX() + getWidth() - SCROLLBAR_WIDTH && mouseX <= getX() + getWidth() && mouseY >= getY() && mouseY <= getY() + getHeight()) {
            scrolling = true;
            return true;
        }
        C cell = getCellAt(mouseX, mouseY);
        if (cell != null) {
            if (cell.mouseClicked(mouseX, mouseY, button)) {
                this.setFocused(cell);
                this.setDragging(true);
                return true;
            }
            this.setFocused(cell);
            return true;
        }
        return false;
    }

    // ========== Cell 抽象类 ==========
    public abstract static class Cell implements GuiEventListener {
        /**
         * 渲染单元格（同时负责背景和前景）。
         * @param graphics      绘图上下文
         * @param index         单元格索引
         * @param top           顶边 Y 坐标（屏幕绝对坐标）
         * @param left          左边 X 坐标（屏幕绝对坐标）
         * @param right         右边 X 坐标（屏幕绝对坐标）
         * @param bottom        底边 Y 坐标（屏幕绝对坐标）
         * @param mouseX        鼠标 X 坐标
         * @param mouseY        鼠标 Y 坐标
         * @param isHovered     鼠标是否悬停
         * @param isSelected    是否被选中
         * @param partialTick   部分帧时间
         */
        public abstract void render(GuiGraphics graphics, int index,int width,int height, int top, int left, int right, int bottom,
                                    int mouseX, int mouseY, boolean isHovered, boolean isSelected, float partialTick);
        @Override public void setFocused(boolean focused) {}
        @Override public boolean isFocused() { return false; }
        public abstract Component getNarration();  // 新增：返回单元格的旁白文本
    }
}
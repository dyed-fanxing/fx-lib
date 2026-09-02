package com.fanxing.lib.client.gui.layout;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.AbstractLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.navigation.ScreenAxis;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FlexBoxLayout extends AbstractLayout {
    public enum JustifyContent { FLEX_START, CENTER, FLEX_END, SPACE_BETWEEN, SPACE_AROUND, SPACE_EVENLY }
    public enum AlignItems { FLEX_START, CENTER, FLEX_END }
    public enum FlexWrap { NOWRAP, WRAP }

    // ========== 子元素容器 ==========
    private final List<FlexItem> children = new ArrayList<>();

    private ScreenAxis direction = ScreenAxis.HORIZONTAL;
    private JustifyContent justifyContent = JustifyContent.SPACE_BETWEEN;
    private AlignItems alignItems = AlignItems.CENTER;
    private FlexWrap flexWrap = FlexWrap.NOWRAP;

    private int paddingTop = 0, paddingRight = 0, paddingBottom = 0, paddingLeft = 0;
    private int gap = 0;
    private int rowGap = 0;

    public FlexBoxLayout(int width, int height) {
        super(0, 0, width, height);
    }

    public FlexBoxLayout(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    // ========== 链式 API ==========
    public FlexBoxLayout padding(int all) {
        this.paddingTop = this.paddingRight = this.paddingBottom = this.paddingLeft = all;
        return this;
    }
    public FlexBoxLayout padding(int horizontal, int vertical) {
        this.paddingLeft = this.paddingRight = horizontal;
        this.paddingTop = this.paddingBottom = vertical;
        return this;
    }
    public FlexBoxLayout padding(int top, int right, int bottom, int left) {
        this.paddingTop = top;
        this.paddingRight = right;
        this.paddingBottom = bottom;
        this.paddingLeft = left;
        return this;
    }
    public FlexBoxLayout paddingTop(int top) { this.paddingTop = top; return this; }
    public FlexBoxLayout paddingRight(int right) { this.paddingRight = right; return this; }
    public FlexBoxLayout paddingBottom(int bottom) { this.paddingBottom = bottom; return this; }
    public FlexBoxLayout paddingLeft(int left) { this.paddingLeft = left; return this; }

    public FlexBoxLayout gap(int gap) {
        this.gap = gap;
        this.rowGap = gap;
        return this;
    }
    public FlexBoxLayout rowGap(int rowGap) {
        this.rowGap = rowGap;
        return this;
    }

    public FlexBoxLayout horizontal() { this.direction = ScreenAxis.HORIZONTAL; return this; }
    public FlexBoxLayout vertical() { this.direction = ScreenAxis.VERTICAL; return this; }
    public FlexBoxLayout justifyContent(JustifyContent j) { this.justifyContent = j; return this; }
    public FlexBoxLayout alignItems(AlignItems a) { this.alignItems = a; return this; }
    public FlexBoxLayout flexWrap(FlexWrap w) { this.flexWrap = w; return this; }

    // ========== 添加子元素 ==========
    /** 固定宽度子元素 */
    public FlexBoxLayout addChild(LayoutElement child) {
        children.add(new FlexItem(child, 0));   // flexGrow = 0
        return this;
    }

    /** 弹性子元素，flexGrow 表示占据剩余空间的比例 */
    public FlexBoxLayout addChild(LayoutElement child, float flexGrow) {
        children.add(new FlexItem(child, flexGrow));
        return this;
    }

    /** 移除子元素 */
    public void removeChild(LayoutElement child) {
        children.removeIf(item -> item.child == child);
    }

    // ========== 排列 ==========
    @Override
    public void arrangeElements() {
        // 递归排列嵌套弹性布局
        for (FlexItem item : children) {
            if (item.child instanceof FlexBoxLayout flex) {
                flex.arrangeElements();
            }
        }

        if (direction == ScreenAxis.HORIZONTAL) {
            if (flexWrap == FlexWrap.WRAP) arrangeHorizontalWrap();
            else arrangeHorizontalNoWrap();
        } else {
            if (flexWrap == FlexWrap.WRAP) arrangeVerticalWrap();
            else arrangeVerticalNoWrap();
        }
    }

    // ========== 水平不换行（支持弹性） ==========
    private void arrangeHorizontalNoWrap() {
        // 确定容器尺寸
        if (this.width <= 0) {
            int totalWidth = children.stream().mapToInt(it -> it.child.getWidth()).sum();
            this.width = totalWidth + (children.size() - 1) * gap + paddingLeft + paddingRight;
        }
        if (this.height <= 0) {
            int maxChildHeight = children.stream().mapToInt(it -> it.child.getHeight()).max().orElse(0);
            this.height = maxChildHeight + paddingTop + paddingBottom;
        }

        int contentX = getX() + paddingLeft;
        int contentY = getY() + paddingTop;
        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        // ---------- 第一步：弹性分配宽度 ----------
        float totalFlexGrow = 0f;
        for (FlexItem item : children) {
            if (item.flexGrow > 0) totalFlexGrow += item.flexGrow;
        }

        if (totalFlexGrow > 0) {
            // 所有子元素当前总宽度（包括固定和弹性）
            int totalChildWidth = children.stream().mapToInt(it -> it.child.getWidth()).sum();
            int totalGap = (children.size() - 1) * gap;
            int remainingSpace = Math.max(0, contentWidth - totalChildWidth - totalGap);

            int allocated = 0;
            FlexItem lastFlex = null;
            for (FlexItem item : children) {
                if (item.flexGrow > 0) lastFlex = item;
            }
            for (FlexItem item : children) {
                if (item.flexGrow > 0) {
                    int extra;
                    if (item == lastFlex) {
                        extra = remainingSpace - allocated; // 最后一个人拿剩余全部，消除舍入误差
                    } else {
                        extra = Math.round(remainingSpace * item.flexGrow / totalFlexGrow);
                        allocated += extra;
                    }
                    int newWidth = item.child.getWidth() + extra;
                    if (item.child instanceof AbstractWidget widget) widget.setWidth(newWidth);
                    else if (item.child instanceof FlexBoxLayout flex) flex.setWidth(newWidth);
                }
            }
        }

        // ---------- 第二步：计算最终总宽度和剩余空间 ----------
        int finalTotalWidth = children.stream().mapToInt(it -> it.child.getWidth()).sum();
        int remainingSpaceAfterFlex = Math.max(0, contentWidth - finalTotalWidth);

        // ---------- 第三步：按 justifyContent 分配剩余空间为间距 ----------
        int spacing = 0;
        int startX = contentX;

        switch (justifyContent) {
            case FLEX_START -> {
                spacing = gap;
                startX = contentX;
            }
            case CENTER -> {
                spacing = gap;
                startX = contentX + remainingSpaceAfterFlex / 2;
            }
            case FLEX_END -> {
                spacing = gap;
                startX = contentX + remainingSpaceAfterFlex;
            }
            case SPACE_BETWEEN -> {
                if (children.size() > 1) spacing = remainingSpaceAfterFlex / (children.size() - 1);
                else spacing = 0;
                startX = contentX;
            }
            case SPACE_AROUND -> {
                if (!children.isEmpty()) {
                    spacing = remainingSpaceAfterFlex / children.size();
                    startX = contentX + spacing / 2;
                }
            }
            case SPACE_EVENLY -> {
                if (!children.isEmpty()) {
                    spacing = remainingSpaceAfterFlex / (children.size() + 1);
                    startX = contentX + spacing;
                }
            }
        }

        // ---------- 第四步：放置 ----------
        int currentX = startX;
        for (FlexItem item : children) {
            int childY = getAlignedY(item.child, contentY, contentHeight);
            item.child.setPosition(currentX, childY);
            currentX += item.child.getWidth() + spacing;
        }
    }

    // ========== 水平换行布局（暂不处理弹性） ==========
    private void arrangeHorizontalWrap() {
        if (this.width <= 0) this.width = 0;
        int contentX = getX() + paddingLeft;
        int contentY = getY() + paddingTop;
        int contentWidth = getWidth() - paddingLeft - paddingRight;

        List<List<FlexItem>> lines = new ArrayList<>();
        List<Integer> lineHeights = new ArrayList<>();

        List<FlexItem> currentLine = new ArrayList<>();
        int currentLineWidth = 0;
        int currentLineHeight = 0;

        for (FlexItem item : children) {
            int childWidth = item.child.getWidth();
            int childHeight = item.child.getHeight();

            if (!currentLine.isEmpty() && currentLineWidth + childWidth + gap > contentWidth) {
                lines.add(currentLine);
                lineHeights.add(currentLineHeight);
                currentLine = new ArrayList<>();
                currentLineWidth = 0;
                currentLineHeight = 0;
            }
            currentLine.add(item);
            currentLineWidth += childWidth + gap;
            currentLineHeight = Math.max(currentLineHeight, childHeight);
        }
        if (!currentLine.isEmpty()) {
            lines.add(currentLine);
            lineHeights.add(currentLineHeight);
        }

        int currentY = contentY;
        for (int i = 0; i < lines.size(); i++) {
            List<FlexItem> line = lines.get(i);
            int lineHeight = lineHeights.get(i);
            int totalWidth = line.stream().mapToInt(it -> it.child.getWidth()).sum();
            int childCount = line.size();
            int spacing = gap;
            int startX = contentX;

            switch (justifyContent) {
                case FLEX_START: startX = contentX; spacing = gap; break;
                case CENTER:
                    startX = contentX + (contentWidth - totalWidth - (childCount - 1) * spacing) / 2;
                    break;
                case FLEX_END:
                    startX = contentX + contentWidth - totalWidth - (childCount - 1) * spacing;
                    break;
                case SPACE_BETWEEN:
                    if (childCount > 1) spacing = (contentWidth - totalWidth) / (childCount - 1);
                    else spacing = 0;
                    startX = contentX;
                    break;
                case SPACE_AROUND:
                    if (childCount > 0) {
                        int totalGap = contentWidth - totalWidth;
                        spacing = totalGap / childCount;
                        startX = contentX + spacing / 2;
                    }
                    break;
                case SPACE_EVENLY:
                    if (childCount > 0) {
                        int totalGap = contentWidth - totalWidth;
                        spacing = totalGap / (childCount + 1);
                        startX = contentX + spacing;
                    }
                    break;
            }

            int currentX = startX;
            for (FlexItem item : line) {
                int childY = getAlignedY(item.child, currentY, lineHeight);
                item.child.setPosition(currentX, childY);
                currentX += item.child.getWidth() + spacing;
            }
            currentY += lineHeight + rowGap;
        }

        if (this.height <= 0) {
            int totalHeight = currentY - getY() - rowGap + paddingBottom;
            this.height = totalHeight;
        }
    }

    // ========== 垂直不换行 ==========
    private void arrangeVerticalNoWrap() {
        if (this.height <= 0) {
            int totalChildHeight = children.stream().mapToInt(it -> it.child.getHeight()).sum();
            this.height = totalChildHeight + (children.size() - 1) * gap + paddingTop + paddingBottom;
        }
        if (this.width <= 0) {
            int maxChildWidth = children.stream().mapToInt(it -> it.child.getWidth()).max().orElse(0);
            this.width = maxChildWidth + paddingLeft + paddingRight;
        }

        int contentX = getX() + paddingLeft;
        int contentY = getY() + paddingTop;
        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        int currentY = contentY;
        for (FlexItem item : children) {
            int childX = getAlignedX(item.child, contentX, contentWidth);
            item.child.setPosition(childX, currentY);
            currentY += item.child.getHeight() + gap;
        }
    }

    // ========== 垂直换行布局 ==========
    private void arrangeVerticalWrap() {
        if (this.height <= 0) { /* 高度未指定 */ }
        int contentX = getX() + paddingLeft;
        int contentY = getY() + paddingTop;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        List<List<FlexItem>> columns = new ArrayList<>();
        List<Integer> columnWidths = new ArrayList<>();

        List<FlexItem> currentColumn = new ArrayList<>();
        int currentColumnHeight = 0;
        for (FlexItem item : children) {
            int childHeight = item.child.getHeight();
            if (!currentColumn.isEmpty() && currentColumnHeight + childHeight + gap > contentHeight) {
                columns.add(currentColumn);
                columnWidths.add(currentColumn.stream().mapToInt(it -> it.child.getWidth()).max().orElse(0));
                currentColumn = new ArrayList<>();
                currentColumnHeight = 0;
            }
            currentColumn.add(item);
            currentColumnHeight += childHeight + (currentColumn.isEmpty() ? 0 : gap);
        }
        if (!currentColumn.isEmpty()) {
            columns.add(currentColumn);
            columnWidths.add(currentColumn.stream().mapToInt(it -> it.child.getWidth()).max().orElse(0));
        }

        int currentX = contentX;
        for (int i = 0; i < columns.size(); i++) {
            List<FlexItem> column = columns.get(i);
            int columnWidth = columnWidths.get(i);
            int totalHeight = column.stream().mapToInt(it -> it.child.getHeight()).sum();
            int childCount = column.size();
            int spacing = gap;
            int startY = contentY;

            switch (justifyContent) {
                case FLEX_START: startY = contentY; spacing = gap; break;
                case CENTER:
                    startY = contentY + (contentHeight - totalHeight - (childCount - 1) * spacing) / 2;
                    break;
                case FLEX_END:
                    startY = contentY + contentHeight - totalHeight - (childCount - 1) * spacing;
                    break;
                case SPACE_BETWEEN:
                    if (childCount > 1) spacing = (contentHeight - totalHeight) / (childCount - 1);
                    else spacing = 0;
                    startY = contentY;
                    break;
                case SPACE_AROUND:
                    if (childCount > 0) {
                        int totalGap = contentHeight - totalHeight;
                        spacing = totalGap / childCount;
                        startY = contentY + spacing / 2;
                    }
                    break;
                case SPACE_EVENLY:
                    if (childCount > 0) {
                        int totalGap = contentHeight - totalHeight;
                        spacing = totalGap / (childCount + 1);
                        startY = contentY + spacing;
                    }
                    break;
            }

            int currentY = startY;
            for (FlexItem item : column) {
                int childX = getAlignedX(item.child, currentX, columnWidth);
                item.child.setPosition(childX, currentY);
                currentY += item.child.getHeight() + spacing;
            }
            currentX += columnWidth + rowGap;
        }

        if (this.width <= 0) {
            this.width = currentX - getX() - rowGap + paddingRight;
        }
    }

    // ========== 辅助方法 ==========
    private int getAlignedY(LayoutElement child, int contentY, int contentHeight) {
        return switch (alignItems) {
            case CENTER -> contentY + (contentHeight - child.getHeight()) / 2;
            case FLEX_END -> contentY + contentHeight - child.getHeight();
            default -> contentY;
        };
    }

    private int getAlignedX(LayoutElement child, int contentX, int contentWidth) {
        return switch (alignItems) {
            case CENTER -> contentX + (contentWidth - child.getWidth()) / 2;
            case FLEX_END -> contentX + contentWidth - child.getWidth();
            default -> contentX;
        };
    }

    @Override
    public void visitChildren(@NotNull Consumer<LayoutElement> consumer) {
        for (FlexItem item : children) {
            consumer.accept(item.child);
        }
    }

    public void setHeight(int height) {
        this.height = height;
    }
    public void setWidth(int width) {
        this.width = width;
    }

    public List<FlexItem> getChildren() {
        return children;
    }

    public List<GuiEventListener> getGuiEventListeners() {
        List<GuiEventListener> list = new ArrayList<>();
        for (FlexItem item : children) {
            if (item.child instanceof GuiEventListener listener) {
                list.add(listener);
            }
            // 如果子组件是容器（如 FlexBoxLayout），递归收集
            if (item.child instanceof FlexBoxLayout flex) {
                list.addAll(flex.getGuiEventListeners());
            }
        }
        return list;
    }

    public void clearChildren() {
        children.clear();
    }


    // ========== 极简弹性项目 ==========
    public record FlexItem(LayoutElement child, float flexGrow) {}
}
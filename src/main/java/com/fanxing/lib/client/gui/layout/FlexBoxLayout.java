package com.fanxing.lib.client.gui.layout;

import net.minecraft.client.gui.layouts.AbstractLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.navigation.ScreenAxis;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FlexBoxLayout extends AbstractLayout {
    public enum JustifyContent {
        FLEX_START, CENTER, FLEX_END, SPACE_BETWEEN, SPACE_AROUND, SPACE_EVENLY
    }
    public enum AlignItems { FLEX_START, CENTER, FLEX_END }
    public enum FlexWrap { NOWRAP, WRAP }

    private final List<LayoutElement> children = new ArrayList<>();
    private ScreenAxis direction = ScreenAxis.HORIZONTAL;
    private JustifyContent justifyContent = JustifyContent.SPACE_BETWEEN ;
    private AlignItems alignItems = AlignItems.CENTER;
    private FlexWrap flexWrap = FlexWrap.NOWRAP;

    // KEY 这里内边距是指CSS里的，是向内凹的，而不是追加在元素外的
    private int paddingTop = 0, paddingRight = 0, paddingBottom = 0, paddingLeft = 0;
    private int gap = 0;          // 主轴间距（行内或列内）
    private int rowGap = 0;       // 行间距（换行时使用，默认等于 gap）

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

    public FlexBoxLayout horizontal() {
        this.direction = ScreenAxis.HORIZONTAL;
        return this;
    }
    public FlexBoxLayout vertical() {
        this.direction = ScreenAxis.VERTICAL;
        return this;
    }
    public FlexBoxLayout justifyContent(JustifyContent justifyContent) {
        this.justifyContent = justifyContent;
        return this;
    }
    public FlexBoxLayout alignItems(AlignItems alignItems) {
        this.alignItems = alignItems;
        return this;
    }
    public FlexBoxLayout flexWrap(FlexWrap flexWrap) {
        this.flexWrap = flexWrap;
        return this;
    }

    public FlexBoxLayout addChild(LayoutElement child) {
        children.add(child);
        return this;
    }

    @Override
    public void arrangeElements() {
        // 递归排列嵌套布局
        for (LayoutElement child : children) {
            if (child instanceof FlexBoxLayout) {
                ((FlexBoxLayout) child).arrangeElements();
            }
        }
        if (direction == ScreenAxis.HORIZONTAL) {
            if (flexWrap == FlexWrap.WRAP) {
                arrangeHorizontalWrap();
            } else {
                arrangeHorizontalNoWrap();
            }
        } else {
            if (flexWrap == FlexWrap.WRAP) {
                arrangeVerticalWrap();
            } else {
                arrangeVerticalNoWrap();
            }
        }
    }

    // ========== 水平不换行（原逻辑） ==========
    private void arrangeHorizontalNoWrap() {
        if (this.width <= 0) {
            int totalChildWidth = children.stream().mapToInt(LayoutElement::getWidth).sum();
            int totalGap = (children.size() - 1) * gap;
            this.width = totalChildWidth + totalGap + paddingLeft + paddingRight;
        }
        if (this.height <= 0) {
            int maxChildHeight = children.stream().mapToInt(LayoutElement::getHeight).max().orElse(0);
            this.height = maxChildHeight + paddingTop + paddingBottom;
        }

        int contentX = getX() + paddingLeft;
        int contentY = getY() + paddingTop;
        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        int totalWidth = children.stream().mapToInt(LayoutElement::getWidth).sum();
        int childCount = children.size();
        if (childCount == 0) return;

        int spacing = 0;
        int startX = contentX;

        switch (justifyContent) {
            case FLEX_START:
                spacing = gap;
                startX = contentX;
                break;
            case CENTER:
                spacing = gap;
                int totalWidthWithGap = totalWidth + (childCount - 1) * spacing;
                startX = contentX + (contentWidth - totalWidthWithGap) / 2;
                break;
            case FLEX_END:
                spacing = gap;
                totalWidthWithGap = totalWidth + (childCount - 1) * spacing;
                startX = contentX + contentWidth - totalWidthWithGap;
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
        for (LayoutElement child : children) {
            int childY;
            if (alignItems == AlignItems.CENTER) {
                childY = contentY + (contentHeight - child.getHeight()) / 2;
            } else if (alignItems == AlignItems.FLEX_END) {
                childY = contentY + contentHeight - child.getHeight();
            } else {
                childY = contentY;
            }
            child.setPosition(currentX, childY);
            currentX += child.getWidth() + spacing;
        }
    }

    // ========== 水平换行布局 ==========
    private void arrangeHorizontalWrap() {
        // 先让容器宽度自适应（如果未指定）
        if (this.width <= 0) {
            // 不换行时宽度由内容决定，换行时宽度应该由父容器或外部决定，通常不会出现 width<=0，但为安全暂不处理
            this.width = 0;
        }
        // 高度自适应（如果未指定）会在布局后计算
        int contentX = getX() + paddingLeft;
        int contentY = getY() + paddingTop;
        int contentWidth = getWidth() - paddingLeft - paddingRight;

        List<List<LayoutElement>> lines = new ArrayList<>();
        List<Integer> lineHeights = new ArrayList<>();

        // 1. 分行
        List<LayoutElement> currentLine = new ArrayList<>();
        int currentLineWidth = 0;
        int currentLineHeight = 0;

        for (LayoutElement child : children) {
            int childWidth = child.getWidth();
            int childHeight = child.getHeight();

            if (!currentLine.isEmpty() && currentLineWidth + childWidth + gap > contentWidth) {
                // 换行
                lines.add(currentLine);
                lineHeights.add(currentLineHeight);
                currentLine = new ArrayList<>();
                currentLineWidth = 0;
                currentLineHeight = 0;
            }
            currentLine.add(child);
            currentLineWidth += childWidth + (currentLine.isEmpty() ? 0 : gap);
            currentLineHeight = Math.max(currentLineHeight, childHeight);
        }
        if (!currentLine.isEmpty()) {
            lines.add(currentLine);
            lineHeights.add(currentLineHeight);
        }

        // 2. 计算每行的位置并放置子元素
        int currentY = contentY;
        for (int i = 0; i < lines.size(); i++) {
            List<LayoutElement> line = lines.get(i);
            int lineHeight = lineHeights.get(i);
            int totalWidth = line.stream().mapToInt(LayoutElement::getWidth).sum();
            int childCount = line.size();
            int spacing = gap;
            int startX = contentX;

            // 根据 justifyContent 决定起始X（注意：换行时每行独立应用 justifyContent）
            switch (justifyContent) {
                case FLEX_START:
                    startX = contentX;
                    spacing = gap;
                    break;
                case CENTER:
                    spacing = gap;
                    startX = contentX + (contentWidth - totalWidth - (childCount - 1) * spacing) / 2;
                    break;
                case FLEX_END:
                    spacing = gap;
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
            for (LayoutElement child : line) {
                int childY;
                if (alignItems == AlignItems.CENTER) {
                    childY = currentY + (lineHeight - child.getHeight()) / 2;
                } else if (alignItems == AlignItems.FLEX_END) {
                    childY = currentY + lineHeight - child.getHeight();
                } else {
                    childY = currentY;
                }
                child.setPosition(currentX, childY);
                currentX += child.getWidth() + spacing;
            }
            currentY += lineHeight + rowGap;
        }

        // 3. 自适应高度（如果外部未设置）
        if (this.height <= 0) {
            int totalHeight = currentY - getY() - rowGap + paddingBottom;
            this.height = totalHeight;
        }
    }

    // ========== 垂直不换行（原逻辑） ==========
    private void arrangeVerticalNoWrap() {
        if (this.height <= 0) {
            int totalChildHeight = children.stream().mapToInt(LayoutElement::getHeight).sum();
            int totalGap = (children.size() - 1) * gap;
            this.height = totalChildHeight + totalGap + paddingTop + paddingBottom;
        }
        if (this.width <= 0) {
            int maxChildWidth = children.stream().mapToInt(LayoutElement::getWidth).max().orElse(0);
            this.width = maxChildWidth + paddingLeft + paddingRight;
        }

        int contentX = getX() + paddingLeft;
        int contentY = getY() + paddingTop;
        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        int totalHeight = children.stream().mapToInt(LayoutElement::getHeight).sum();
        int childCount = children.size();
        if (childCount == 0) return;

        int spacing = 0;
        int startY = contentY;

        switch (justifyContent) {
            case FLEX_START:
                spacing = gap;
                startY = contentY;
                break;
            case CENTER:
                spacing = gap;
                int totalHeightWithGap = totalHeight + (childCount - 1) * spacing;
                startY = contentY + (contentHeight - totalHeightWithGap) / 2;
                break;
            case FLEX_END:
                spacing = gap;
                totalHeightWithGap = totalHeight + (childCount - 1) * spacing;
                startY = contentY + contentHeight - totalHeightWithGap;
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
        for (LayoutElement child : children) {
            int childX;
            if (alignItems == AlignItems.CENTER) {
                childX = contentX + (contentWidth - child.getWidth()) / 2;
            } else if (alignItems == AlignItems.FLEX_END) {
                childX = contentX + contentWidth - child.getWidth();
            } else {
                childX = contentX;
            }
            child.setPosition(childX, currentY);
            currentY += child.getHeight() + spacing;
        }
    }

    // ========== 垂直换行布局（列换行） ==========
    private void arrangeVerticalWrap() {
        if (this.height <= 0) {
            // 高度未指定时暂不处理
        }
        int contentX = getX() + paddingLeft;
        int contentY = getY() + paddingTop;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        List<List<LayoutElement>> columns = new ArrayList<>();
        List<Integer> columnWidths = new ArrayList<>();

        // 分列
        List<LayoutElement> currentColumn = new ArrayList<>();
        int currentColumnHeight = 0;
        for (LayoutElement child : children) {
            int childHeight = child.getHeight();
            int childWidth = child.getWidth();
            if (!currentColumn.isEmpty() && currentColumnHeight + childHeight + gap > contentHeight) {
                columns.add(currentColumn);
                columnWidths.add(currentColumn.stream().mapToInt(LayoutElement::getWidth).max().orElse(0));
                currentColumn = new ArrayList<>();
                currentColumnHeight = 0;
            }
            currentColumn.add(child);
            currentColumnHeight += childHeight + (currentColumn.isEmpty() ? 0 : gap);
        }
        if (!currentColumn.isEmpty()) {
            columns.add(currentColumn);
            columnWidths.add(currentColumn.stream().mapToInt(LayoutElement::getWidth).max().orElse(0));
        }

        // 放置
        int currentX = contentX;
        for (int i = 0; i < columns.size(); i++) {
            List<LayoutElement> column = columns.get(i);
            int columnWidth = columnWidths.get(i);
            int totalHeight = column.stream().mapToInt(LayoutElement::getHeight).sum();
            int childCount = column.size();
            int spacing = gap;
            int startY = contentY;

            // 垂直方向的 justifyContent
            switch (justifyContent) {
                case FLEX_START:
                    startY = contentY;
                    spacing = gap;
                    break;
                case CENTER:
                    spacing = gap;
                    startY = contentY + (contentHeight - totalHeight - (childCount - 1) * spacing) / 2;
                    break;
                case FLEX_END:
                    spacing = gap;
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
            for (LayoutElement child : column) {
                int childX;
                if (alignItems == AlignItems.CENTER) {
                    childX = currentX + (columnWidth - child.getWidth()) / 2;
                } else if (alignItems == AlignItems.FLEX_END) {
                    childX = currentX + columnWidth - child.getWidth();
                } else {
                    childX = currentX;
                }
                child.setPosition(childX, currentY);
                currentY += child.getHeight() + spacing;
            }
            currentX += columnWidth + rowGap;
        }

        // 自适应宽度
        if (this.width <= 0) {
            this.width = currentX - getX() - rowGap + paddingRight;
        }
    }

    @Override
    public void visitChildren(@NotNull Consumer<LayoutElement> consumer) {
        children.forEach(consumer);
    }

    public List<LayoutElement> getChildren() {
        return children;
    }

    public void setHeight(int height) {
        this.height = height;
    }
    public void setWidth(int width) {
        this.width = width;
    }
}
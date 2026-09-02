package com.fanxing.lib.client.gui.component.container;

import com.fanxing.lib.client.gui.Scheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 可滚动的表单容器，每个表单项高度可独立指定。
 * 每个 FormItem 可以包含子控件，支持焦点导航。
 * 仿照原版 ContainerObjectSelectionList 设计。
 */
public abstract class FormContainer<E extends FormContainer.FormItem> extends AbstractScrollContainer {
    private static final Logger log = LoggerFactory.getLogger(FormContainer.class);
    protected final List<E> items = new ArrayList<>();
    protected E selected;
    protected E hovered;
    public static final int GAP = 4;
    public static final int PADDING = 2;
    public FormContainer(int x, int y, int width, int height, Component title) {
        super(x, y, width, height, title);
        log.info("y:{}",y);
    }
    // ========== 滚动与渲染 ==========
    // 上下两个内边距+内部的n-1个间距
    @Override
    protected int getContentHeight() {
        int total = GAP;
        for (E item : items) {
            total += item.getHeight()+GAP;
        }
        return total;
    }
    protected int getItemLeft(){
        return getX() + PADDING;
    }
    protected int getItemWidth(){
        return getWidth() - PADDING*2 - (scrollbarVisible()?SCROLLBAR_WIDTH:0);
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return items;
    }
    // ========== 内容管理 ==========
    protected void clearItems() {
        items.clear();
        selected = null;
        hovered = null;
        setScrollAmount(0);
    }
    protected void addItem(E item) {
        items.add(item);
    }
    protected void removeItem(E item) {
        items.remove(item);
        if (selected == item) selected = null;
        if (hovered == item) hovered = null;
    }
    public E getSelected() { return selected; }
    public void setSelected(E item) { this.selected = item; }

    @Override
    public void setFocused(@Nullable GuiEventListener focused) {
        if(getFocused() != focused){
            super.setFocused(focused);
            int i = this.items.indexOf(focused);
            if (i >= 0) {
                E e = this.items.get(i);
                this.setSelected(e);
                if (Minecraft.getInstance().getLastInputType().isKeyboard()) {
                    this.ensureVisible(e);
                }
            }
            if (focused == null) this.setSelected(null);
        }
    }

    // ========== 辅助方法 ==========
    @Nullable
    protected E getItemAtPosition(double mouseX, double mouseY) {
        int scroll = (int) getScrollAmount();
        int currentY = getY() - scroll + GAP; // 与渲染对齐
        for (E item : items) {
            int top = currentY;
            int bottom = top + item.getHeight();
            if (mouseY >= top && mouseY <= bottom && mouseX >= getX() && mouseX <= getX() + getWidth()) {
                return item;
            }
            currentY += item.getHeight() + GAP; // 也要加上间距
        }
        return null;
    }

    private void ensureVisible(E item) {
        int idx = items.indexOf(item);
        if (idx < 0) return;
        int itemTop = 0;
        for (int i = 0; i < idx; i++) {
            itemTop += items.get(i).getHeight();
        }
        int itemBottom = itemTop + item.getHeight();
        int scroll = (int) getScrollAmount();
        if (itemTop - scroll < 0) setScrollAmount(itemTop);
        else if (itemBottom - scroll > getHeight()) setScrollAmount(itemBottom - getHeight());
    }

    @Override
    protected void renderContents(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        E hovered = getItemAtPosition(mouseX, mouseY);
        int scroll = (int) getScrollAmount();
        int currentY = getY() - scroll + GAP;
        int itemLeft = getItemLeft();
        int itemWidth = getItemWidth();
        for (int i = 0; i < items.size(); i++) {
            E item = items.get(i);
            int height = item.getHeight();
            item.render(graphics, i,itemLeft,currentY,itemWidth,mouseX, mouseY, hovered == item,selected == item, partialTick);
            currentY += height+GAP;
        }
    }
    protected void renderBackground(GuiGraphics graphics) {
        graphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), Scheme.BG_COLOR_GREY);
    }

    // ========== 事件处理 ==========
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (scrollbarVisible() && mouseX >= getX() + getWidth() - SCROLLBAR_WIDTH && mouseX <= getX() + getWidth() && mouseY >= getY() && mouseY <= getY() + getHeight()) {
            scrolling = true;
            return true;
        }
        E item = getItemAtPosition(mouseX, mouseY);
        if (item != null) {
            if (item.mouseClicked(mouseX, mouseY, button)) {
                this.setFocused(item);
                this.setDragging(true);
                return true;
            }
            this.setFocused(item);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (getFocused() != null && getFocused().mouseReleased(mouseX, mouseY, button)) {
            return true;
        }
        scrolling = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (scrolling) {
            int contentHeight = getContentHeight();
            int viewHeight = getHeight();
            if (contentHeight > viewHeight) {
                double ratio = (double) viewHeight / contentHeight;
                int barHeight = Math.max(32, (int) (viewHeight * ratio));
                int maxY = getY() + viewHeight - barHeight;
                int newY = (int) Mth.clamp(mouseY, getY(), maxY);
                double percent = (newY - getY()) / (double) (viewHeight - barHeight);
                setScrollAmount(percent * getMaxScroll());
            }
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) return true;
        E item = getItemAtPosition(mouseX, mouseY);
        return item != null && item.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }


    @Nullable
    @Override
    public ComponentPath nextFocusPath(@NotNull FocusNavigationEvent event) {
        if (items.isEmpty()) return null;
        if (!(event instanceof FocusNavigationEvent.ArrowNavigation)) {
            return super.nextFocusPath(event);
        }
        FocusNavigationEvent.ArrowNavigation arrowEvent = (FocusNavigationEvent.ArrowNavigation) event;
        E focusedItem = (E) getFocused();
        // 水平方向键且当前焦点条目存在
        if (arrowEvent.direction().getAxis() == ScreenAxis.HORIZONTAL && focusedItem != null) {
            return ComponentPath.path(this, focusedItem.nextFocusPath(event));
        }
        // 垂直方向键（或当前无焦点条目）
        int targetChildIndex = -1;
        ScreenDirection direction = arrowEvent.direction();
        if (focusedItem != null) targetChildIndex = focusedItem.children().indexOf(focusedItem.getFocused());
        if (targetChildIndex == -1) {
            switch (direction) {
                case LEFT -> {
                    targetChildIndex = Integer.MAX_VALUE;
                    direction = ScreenDirection.DOWN;
                }
                case RIGHT ->{
                    targetChildIndex = 0;
                    direction = ScreenDirection.DOWN;
                }
                default -> targetChildIndex = 0;
            }
        }
        E nextItem = focusedItem;
        ComponentPath componentPath;
        do {
            // 直接使用简化的 nextEntry，每次传入当前查找的条目作为起始
            nextItem = nextEntry(direction, nextItem);
            if (nextItem == null) return null;
            componentPath = nextItem.focusPathAtIndex(arrowEvent, targetChildIndex);
        } while (componentPath == null);
        return ComponentPath.path(this, componentPath);
    }

    @Nullable
    private E nextEntry(ScreenDirection direction, @Nullable E current) {
        if (items.isEmpty()) return null;
        int step = direction == ScreenDirection.DOWN ? 1 : -1;
        int startIdx = (current == null) ? (step > 0 ? 0 : items.size() - 1) : items.indexOf(current) + step;
        int idx = Math.floorMod(startIdx, items.size());
        return items.get(idx);
    }
    @Nullable
    private E nextEntry(ScreenDirection direction) {
        return nextEntry(direction, selected);
    }

    // ========== 旁白支持 ==========
    @Override
    public void updateWidgetNarration(NarrationElementOutput output) {
        if (hovered != null) {
            hovered.updateNarration(output.nest());
            narratePosition(output, hovered);
        } else if (selected != null) {
            selected.updateNarration(output.nest());
            narratePosition(output, selected);
        }
        if (isFocused()) {
            output.add(NarratedElementType.USAGE, Component.translatable("narration.component_list.usage"));
        }
    }

    private void narratePosition(NarrationElementOutput output, E item) {
        int idx = items.indexOf(item);
        if (idx >= 0 && items.size() > 1) {
            output.add(NarratedElementType.POSITION, Component.translatable("narrator.position.object_list", idx + 1, items.size()));
        }
    }

    // ========== 内部抽象类 FormItem ==========
    public abstract static class FormItem implements ContainerEventHandler {
        @Nullable
        private GuiEventListener focused;
        @Nullable
        private NarratableEntry lastNarratable;
        protected boolean dragging;

        public abstract int getHeight();
        public abstract void render(GuiGraphics graphics, int index,int left,int top, int width,
                                    int mouseX, int mouseY, boolean isHovered,boolean isSelected, float partialTick);
        public abstract List<? extends NarratableEntry> narratables(); // 用于旁白

        @Override
        public boolean isDragging() { return dragging; }
        @Override
        public void setDragging(boolean dragging) { this.dragging = dragging; }
        @Nullable
        @Override
        public GuiEventListener getFocused() { return focused; }
        @Override
        public void setFocused(@Nullable GuiEventListener focused) {
            if (this.focused != null) {
                this.focused.setFocused(false);
            }

            if (focused != null) {
                focused.setFocused(true);
            }

            this.focused = focused;
        }


        @Nullable
        public ComponentPath focusPathAtIndex(FocusNavigationEvent event, int childIndex) {
            List<? extends GuiEventListener> children = children();
            if (children.isEmpty()) return null;
            // 归一化索引：如果 childIndex 超出范围或为 Integer.MAX_VALUE，取最后一个
            int index;
            if (childIndex < 0) index = 0;
            else if (childIndex >= children.size()) index = children.size() - 1;
            else index = childIndex;
            GuiEventListener child = children.get(index);
            ComponentPath path = child.nextFocusPath(event);
            if (path != null) return ComponentPath.path(this, path);
            return null;
        }

        @Nullable
        @Override
        public ComponentPath nextFocusPath(@NotNull FocusNavigationEvent event) {
            if (children().isEmpty()) return null;
            if (event instanceof FocusNavigationEvent.ArrowNavigation(ScreenDirection direction)) {
                int step = switch (direction) {
                    case LEFT -> -1;
                    case RIGHT -> 1;
                    default -> 0; // 上下键不处理，交给容器
                };
                if (step == 0) return null;
                int currentIdx = children().indexOf(getFocused());
                int startIdx = Mth.clamp(currentIdx + step, 0, children().size() - 1);
                for (int i = startIdx; i >= 0 && i < children().size(); i += step) {
                    GuiEventListener child = children().get(i);
                    ComponentPath path = child.nextFocusPath(event);
                    if (path != null) {
                        return ComponentPath.path(this, path);
                    }
                }
            }
            return ContainerEventHandler.super.nextFocusPath(event);
        }
        // 旁白
        public void updateNarration(NarrationElementOutput output) {
            List<? extends NarratableEntry> list = narratables();
            Screen.NarratableSearchResult result = Screen.findNarratableWidget(list, lastNarratable);
            if (result != null) {
                if (result.priority.isTerminal()) {
                    lastNarratable = result.entry;
                }
                if (list.size() > 1) {
                    output.add(NarratedElementType.POSITION, Component.translatable("narrator.position.object_list", result.index + 1, list.size()));
                    if (result.priority == NarratableEntry.NarrationPriority.FOCUSED) {
                        output.add(NarratedElementType.USAGE, Component.translatable("narration.component_list.usage"));
                    }
                }
                result.entry.updateNarration(output.nest());
            }
        }
    }
}
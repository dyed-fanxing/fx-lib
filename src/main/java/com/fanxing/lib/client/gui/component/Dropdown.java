package com.fanxing.lib.client.gui.component;

import com.fanxing.lib.client.gui.Scheme;
import com.fanxing.lib.client.gui.screen.PopupManageScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetTooltipHolder;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Dropdown<T> extends AbstractWidget {
    private static final Logger log = LoggerFactory.getLogger(Dropdown.class);
    private DropdownList list;
    private Component label;
    private final Consumer<T> onSelect;  // 统一回调
    private final PopupManageScreen popupScreen;


    // ==================== 构造器 ====================
    public Dropdown(int x, int y, int width, int height,  Consumer<T> onSelect, PopupManageScreen popupScreen) {
        super(x, y, width, height, Component.empty());
        this.onSelect = onSelect;
        this.popupScreen = popupScreen;
    }
    public Dropdown<T> list(int maxListHeight,int itemHeight,List<Dropdown.Entry<T>> entries, T initial){
        this.list = new DropdownList(width, maxListHeight, itemHeight, entries, initial);
        popupScreen.bind(this, list, popup -> {
            list.setX(getX());
            list.setY(getY() + getHeight());
        });
        return this;
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        list.setWidth(width);
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int x = getX(), y = getY(), w = getWidth(), h = getHeight(),r = getRight(),b = getBottom();
        graphics.fill(x, y, r, b, Scheme.BG_COLOR_BLACK);
        boolean opened = popupScreen.isOpen(list);
        int border = (isHoveredOrFocused() || opened) ? Scheme.BORDER_COLOR_HOVER : Scheme.BORDER_COLOR_NORMAL;
        graphics.fill(x, y, r, y + 1, border);
        graphics.fill(x, y, x + 1, b, border);
        graphics.fill(r - 1, y, r, b, border);
        if (!opened) graphics.fill(x, b - 1, r, b, border);
        Font font = Minecraft.getInstance().font;
        int textY = y + (h - 8) / 2;
        graphics.drawCenteredString(font, label.getString(), x + w / 2, textY, Scheme.TEXT_COLOR_HIGHLIGHT);
        String arrow = opened ? "⏶" : "⏷";
        int arrowX = r - font.width(arrow) - 4;
        graphics.drawString(font, arrow, arrowX, textY, Scheme.TEXT_COLOR_HIGHLIGHT);
    }
    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        popupScreen.popupMouseClicked(this);
        super.onClick(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (isActive() && CommonInputs.selected(keyCode)) {
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            popupScreen.popupKeyPressed(this);
            return true;
        }
        return false;
    }


    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
    }

    // ==================== 内部列表 ====================
    private class DropdownList extends ObjectSelectionList<Dropdown.Entry<T>> {
        public DropdownList(int width, int height, int itemHeight, List<Dropdown.Entry<T>> entries,T initial) {
            super(Minecraft.getInstance(), width, Math.min(entries.size()*itemHeight+4,height), 0, itemHeight);
            for (Dropdown.Entry<T> entry : entries) {
                addEntry(entry);
                if(entry.value == initial){
                    setSelected(entry);
                    Dropdown.this.label = entry.label;
                }
            }
        }

        @Override
        public int getRowLeft() {
            return getX();
        }

        @Override
        public int getRowWidth() {
            return getWidth();
        }

        @Override
        protected int getScrollbarPosition() {
            return getX() + getWidth() - 6;
        }
        @Override
        protected void renderListBackground(GuiGraphics graphics) {
            graphics.pose().translate(0, 0, 400);
            graphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), Scheme.BG_COLOR_BLACK);
        }

        @Override
        protected void renderDecorations(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
            graphics.pose().translate(0, 0, 400);
            int x = getX(), y = getY(),r = getRight(),b = getBottom();
            graphics.fill(x, y, r, y + 1, Scheme.BORDER_COLOR_HOVER);
            graphics.fill(x, y, x + 1, b, Scheme.BORDER_COLOR_HOVER);
            graphics.fill(r - 1, y, r, b, Scheme.BORDER_COLOR_HOVER);
            graphics.fill(x, b - 1, r, b, Scheme.BORDER_COLOR_HOVER);
        }

        @Override
        protected void renderSelection(GuiGraphics graphics, int top, int width, int height, int outerColor, int innerColor) {
            int left = getX() + (this.width - width) / 2;
            graphics.fill(left, top, left + width, top + height, Scheme.BG_COLOR_ITEM_SELECTED);
        }

        @Override
        protected void renderListSeparators(@NotNull GuiGraphics graphics) {
        }

        @Override
        protected Dropdown.Entry<T> nextEntry(@NotNull ScreenDirection direction, @NotNull Predicate<Dropdown.Entry<T>> predicate, @Nullable Dropdown.Entry<T> current) {
            if (getItemCount() == 0) return null;
            int step = switch (direction) {
                case UP -> -1;
                case DOWN -> 1;
                default -> 0;
            };
            if (step == 0) return current;
            int start = current == null ? (step > 0 ? 0 : getItemCount() - 1) : children().indexOf(current) + step;
            for (int i = 0; i < getItemCount(); i++) {
                int idx = Math.floorMod(start + i * step, getItemCount());
                Dropdown.Entry<T> e = getEntry(idx);
                if (predicate.test(e)) return e;
            }
            return current;
        }

        // ========== 统一处理选中 ==========
        @Override
        public boolean mouseClicked(double x, double y, int code) {
            // 让父类处理点击（调用 Dropdown.Entry<T>.mouseClicked）
            if (super.mouseClicked(x, y, code)) {
                // 已点击条目，关闭弹窗并回调
                Dropdown.Entry<T> clicked = getSelected();
                if (clicked != null) {
                    Dropdown.this.label = clicked.label;
                    onSelect.accept(clicked.value);
                }
                popupScreen.closeTop();
                return true;
            }
            return true; // 消费事件，防止穿透
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (keyCode == GLFW.GLFW_KEY_TAB) {
                popupScreen.closeTop();
                return true;
            }
            if (CommonInputs.selected(keyCode)) {
                // 已选择条目
                Dropdown.Entry<T> selected = getSelected();
                if (selected != null) {
                    Dropdown.this.label = selected.label;
                    onSelect.accept(selected.value);
                }
                popupScreen.closeTop();
                return true;
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }


    // ==================== 公开条目（纯数据） ====================
    public static class Entry<T> extends ObjectSelectionList.Entry<Entry<T>> {
        protected final Component label;
        protected final T value;
        private final WidgetTooltipHolder tooltip = new WidgetTooltipHolder();


        public Entry(Component label, T value) {
            this.label = label;
            this.value = value;
        }

        @Override
        public void renderBack(@NotNull GuiGraphics graphics, int index, int top, int left,
                               int width, int height, int mouseX, int mouseY,
                               boolean hovering, float partialTick) {
            graphics.pose().translate(0, 0, 400);
            if (hovering) graphics.fill(left, top, left + width, top + height, Scheme.BG_COLOR_ITEM_HOVER);
        }

        @Override
        public void render(@NotNull GuiGraphics graphics, int index, int top, int left, int width,
                           int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            graphics.pose().translate(0, 0, 400);
            Font font = Minecraft.getInstance().font;
            int leftBound = left + 2;
            int rightBound = left + width - 2;
            int color = Scheme.TEXT_COLOR_HIGHLIGHT;
            // 文本溢出时启用滚动（原版静态方法，自动裁剪和滚动）
            AbstractWidget.renderScrollingString(graphics, font, label, leftBound, top, rightBound, top + height, color);
            this.tooltip.refreshTooltipForNextRenderPass(hovering, isFocused(), getRectangle());
        }


        public void setTooltip(@Nullable Tooltip tooltip) {
            this.tooltip.set(tooltip);
        }


        @Override
        public void updateNarration(NarrationElementOutput output) {
            output.add(NarratedElementType.TITLE, this.getNarration());
            this.tooltip.updateNarration(output);
        }

        @Override
        public @NotNull Component getNarration() {
            return label;
        }
    }
}
package com.fanxing.lib.client.gui.component.container;

import com.fanxing.lib.client.gui.Scheme;
import com.fanxing.lib.client.gui.layout.FlexBoxLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * 标签页容器，继承 AbstractContainerWidget。
 * 每个标签页包含一个标签按钮和一个内容组件（任意 LayoutElement）。
 * 支持鼠标点击切换、左右键切换（焦点在标签按钮上时）、Tab 键自然聚焦内容区域。
 * @author dyed_fanxing
 * @since 2026/5/23 13:15
 */
public class TabNavigation extends AbstractContainerWidget {
    private static final Logger log = LoggerFactory.getLogger(TabNavigation.class);
    protected List<TabButton> tabButtons = new ArrayList<>();
    protected TabButton selected;
    protected boolean divideEqually=true;
    protected int scrollOffset = 0;            // 左对齐模式下的滚动偏移

    public TabNavigation(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
    }


    public TabNavigation addTab(TabButton tabButton) {
        tabButton.tabNavigation = this;
        tabButtons.add(tabButton);
        return this;
    }
    public TabNavigation addTab(Component label, Button.OnPress onPress) {
        TabButton tabButton = new TabButton(Button.builder(label,onPress));
        tabButton.tabNavigation = this;
        tabButtons.add(tabButton);
        return this;
    }

    public void init(){
        if(selected != null) selected.onPress();
        setSize(width,height);
    }
    public TabNavigation init(int index){
        setSize(width, height);
        selectTab(index);
        return this;
    }
    public void selectTab(int index){
        TabButton tabButton = tabButtons.get(index);
        tabButton.onPress();
        selected = tabButton;
    }
    public TabNavigation divideEqually() {
        this.divideEqually = true;
        return this;
    }
    public TabNavigation leftAlign() {
        this.divideEqually = false;
        return this;
    }

    @Override
    public void setSize(int width, int height) {
        setWidth(width);
        setHeight(height);
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        if(tabButtons.isEmpty()) return;
        if (divideEqually) {
            int buttonWidth = width / tabButtons.size();
            int remainder = width - buttonWidth * (tabButtons.size() - 1);
            for (int i = 0; i < tabButtons.size(); i++) {
                int w = i == tabButtons.size() - 1 ? remainder : buttonWidth;
                tabButtons.get(i).setWidth(w);
            }
        } else {
            int totalWidth = 0;
            for (TabButton btn : tabButtons) {
                totalWidth += btn.getWidth();
            }
            int maxScroll = Math.max(0, totalWidth - width);
            scrollOffset = Mth.clamp(scrollOffset, 0, maxScroll);
        }
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        for (TabButton btn : tabButtons) {
            btn.setHeight(height);
        }
    }
    @Override
    public void setX(int x) {
        super.setX(x);
        int currentX = x;
        for (TabButton btn : tabButtons) {
            btn.setX(currentX - scrollOffset);
            currentX += btn.getWidth();
        }
    }
    @Override
    public void setY(int y) {
        super.setY(y);
        for (TabButton btn : tabButtons) {
            btn.setY(y);
        }
    }

    @Nullable
    public ComponentPath nextFocusPath(@NotNull FocusNavigationEvent event) {
        if (!this.isFocused()) {
            return ComponentPath.path(this, ComponentPath.leaf(selected));
        }
        return event instanceof FocusNavigationEvent.TabNavigation ? null : super.nextFocusPath(event);
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // 先绘制内容区域边框（在按钮下方，避免被按钮覆盖）
        for (TabButton tabButton : tabButtons) {
            tabButton.render(graphics, mouseX, mouseY, partialTick);
        }
    }


    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return tabButtons;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (Screen.hasControlDown()) {
            if (keyCode >= 49 && keyCode <= 57) {
                int index = keyCode - 49;
                selectTab(Mth.clamp(index, 0, tabButtons.size() - 1));
                this.playDownSound(Minecraft.getInstance().getSoundManager());
                return true;
            }
            if (keyCode == 258) { // Tab
                int delta = Screen.hasShiftDown() ? -1 : 1;
                int curIdx = tabButtons.indexOf(selected);
                if (curIdx >= 0) {
                    selectTab(Math.floorMod(curIdx + delta, tabButtons.size()));
                    this.playDownSound(Minecraft.getInstance().getSoundManager());
                    return true;
                }
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public static class TabButton extends Button {
        public TabNavigation tabNavigation;
        public TabButton(Button.Builder builder) {
            super(builder);
        }

        @Override
        public void onClick(double x, double y) {
            super.onClick(x, y);
            tabNavigation.selected = this;
        }
        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (this.active && this.visible) {
                if (CommonInputs.selected(keyCode)) {
                    this.playDownSound(Minecraft.getInstance().getSoundManager());
                    tabNavigation.selected = this;
                    this.onPress();
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            int x = getX(), y = getY(), w = getWidth(), h = getHeight();
            // 背景
            graphics.fill(x, y, x + w, y + h, Scheme.BG_COLOR_GREY);
            boolean selected = tabNavigation.selected == this;
            int borderColor = isHoveredOrFocused()||selected ? Scheme.BORDER_COLOR_HOVER : Scheme.BORDER_COLOR_NORMAL;
//             上边框 (内部顶部)
            graphics.fill(x, y, x + w, y + 1, borderColor);
//             左边框 (内部左侧)
            graphics.fill(x, y, x+1, y + h, borderColor);
//             右边框 (内部右侧)
            graphics.fill(x + w-1, y, x + w, y + h, borderColor);
            // 下边框：非选中时绘制，选中时不绘制（改用下划线）
            Font font = Minecraft.getInstance().font;
            if (selected) renderFocusUnderline(graphics);
            else graphics.fill(x, y + h - 1, x + w, y + h, borderColor);
            // 文字渲染（保持原样）
            int i = this.getFGColor();
            this.renderString(graphics, font, i | Mth.ceil(this.alpha * 255.0F) << 24);
        }

        protected void renderFocusUnderline(GuiGraphics graphics) {
            int i = Math.max(width/3,20);
            int j = this.getX() + (this.getWidth() - i) / 2;
            int k = this.getY() + this.getHeight();
            graphics.fill(j, k-1, j + i, k, Scheme.BORDER_COLOR_SELECTED);
        }
    }
}
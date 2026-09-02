package com.fanxing.lib.client.gui.screen;


import com.google.common.annotations.VisibleForTesting;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;

/**
 * 需要处理弹窗的基类屏幕
 *
 * @author dyed_fanxing
 * @date 2026/5/16 14:23
 */
public abstract class PopupManageScreen extends Screen {
    private static final Logger log = LoggerFactory.getLogger(PopupManageScreen.class);
    private final Deque<PopupEntry> stack = new ArrayDeque<>();
    private final Map<GuiEventListener, PopupBind> bindings = new HashMap<>();
    

    protected PopupManageScreen(Component title) {
        super(title);
    }
    public void bind(GuiEventListener trigger, AbstractWidget popup, Consumer<AbstractWidget> onOpen) {
        bindings.put(trigger, new PopupBind(popup, onOpen));
    }
    public void bind(GuiEventListener trigger, AbstractWidget popup) {
        bind(trigger, popup, null);
    }

    protected <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidgetFirst(T popup) {
        this.renderables.addFirst(popup);
        this.children.addFirst(popup);
        this.narratables.addFirst(popup);
        return popup;
    }

    /**
     * 焦点发生变化时，被动处理
     * 1. 弹窗组件移动到其他组件 peek.popup != to
     *   1.1  to不是该弹窗的触发器 关闭
     *   1.2  to是该弹窗的触发器get(path) 不关闭
     * 2. 弹窗内部的组件变化 peek.popup == to 不关闭
     */
    @VisibleForTesting
    public void changeFocus(ComponentPath path) {
        this.clearFocus();
        path.applyFocus(true);
        if(!stack.isEmpty()){
            PopupEntry peek = stack.peek();
            GuiEventListener to = getFocused();
            if(peek.popup != to && get(path) != peek.trigger){
                log.debug("焦点发生变化，被动关闭弹窗：{}，当前聚焦的是：{}",peek,to);
                close0();
            }
        }
    }

    public GuiEventListener get(ComponentPath path) {
        if (path instanceof ComponentPath.Path path1) {
            return get(path1.childPath());
        }
        return path.component();
    }


    @Override
    public boolean mouseClicked(double x, double y, int code) {
        for(GuiEventListener child : this.children()) {
            if (child.mouseClicked(x, y, code)) {
                this.setFocused(child);
                // 这里仅仅负责处理聚焦栈顶的弹窗，栈顶则表示当前的弹窗和它的触发器
                // 在这里判断如果存在顶层弹窗，那么在这里直接获取当前触发器的路径，并和这个弹窗相关联（为了以后失焦恢复到触发器焦点做准备）
                if(!stack.isEmpty()){
                    PopupEntry peek = stack.peek();
                    peek.setTriggerPath(getCurrentFocusPath());
                    setInitialFocus(peek.popup);
                }
                if (code == 0) this.setDragging(true);
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        // 优先让栈顶弹窗处理滚轮
        if (!stack.isEmpty()) {
            PopupEntry top = stack.peek();
            AbstractWidget popup = top.popup;
            if (popup.isMouseOver(mouseX, mouseY)) {
                if (popup.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
                    return true;
                }
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    public GuiEventListener getRealFocused(GuiEventListener listener) {
        if(listener instanceof ContainerEventHandler container){
            return getRealFocused(container.getFocused());
        }
        return listener;
    }

    @Override
    public boolean keyPressed(int p_96552_, int p_96553_, int p_96554_) {
        if (p_96552_ == 256 && this.shouldCloseOnEsc()) {
            this.onClose();
            return true;
        } else if ( this.getFocused() != null && this.getFocused().keyPressed(p_96552_, p_96553_, p_96554_)) {
            // 这里仅仅负责处理聚焦栈顶的弹窗，栈顶则表示当前的弹窗和它的触发器
            // 在这里判断如果存在顶层弹窗，那么在这里直接获取当前触发器的路径，并和这个弹窗相关联（为了以后失焦恢复到触发器焦点做准备）
            if(!stack.isEmpty()){
                PopupEntry peek = stack.peek();
                log.debug("按键打开弹窗：{}",stack.peek());
                peek.setTriggerPath(getCurrentFocusPath());
                setInitialFocus(peek.popup);
                log.debug("设置初始聚焦：{}",peek.popup);
//                this.setFocused(peek.popup);
            }
            return true;
        } else {
            Object var10000;
            switch (p_96552_) {
                case 258:
                    var10000 = this.createTabEvent();
                    break;
                case 259:
                case 260:
                case 261:
                default:
                    var10000 = null;
                    break;
                case 262:
                    var10000 = this.createArrowEvent(ScreenDirection.RIGHT);
                    break;
                case 263:
                    var10000 = this.createArrowEvent(ScreenDirection.LEFT);
                    break;
                case 264:
                    var10000 = this.createArrowEvent(ScreenDirection.DOWN);
                    break;
                case 265:
                    var10000 = this.createArrowEvent(ScreenDirection.UP);
            }

            FocusNavigationEvent focusnavigationevent = (FocusNavigationEvent)var10000;
            if (focusnavigationevent != null) {
                ComponentPath componentpath = super.nextFocusPath(focusnavigationevent);
                if (componentpath == null && focusnavigationevent instanceof FocusNavigationEvent.TabNavigation) {
                    this.clearFocus();
                    componentpath = super.nextFocusPath(focusnavigationevent);
                }

                if (componentpath != null) {
                    this.changeFocus(componentpath);
                }
            }

            return false;
        }
    }



    /**
     * 负责点击触发，触发器的点击
     */
    public void popupMouseClicked(GuiEventListener listener){
        PopupBind bind = bindings.get(listener);
        Iterator<PopupEntry> it = stack.iterator();
        while (it.hasNext()) {
            PopupEntry entry = it.next();
            AbstractWidget popup = entry.popup;
            // 1. 点击的是弹窗本身（面板背景等）→ 聚焦
//            if (popup == listener) return;
            // 2. 点击的触发器在弹窗内部
            if (isInsidePopup(listener, popup) && bind != null) {
                // 有绑定（嵌套触发器）→ 打开新弹窗
                open0(bind, listener);
                return;
            }
            // 点击的是该弹窗的触发器（已打开）→ 关闭该弹窗
            if (entry.trigger == listener) {
                removeWidget(popup);
                it.remove();
                return;
            }
            //不是则关闭
            removeWidget(popup);
            it.remove();
        }
        // 栈被清空（平行触发或首次）→ 打开新弹窗
        if (bind != null) open0(bind, listener);
    }



    /**
     * 负责按键触发，
     * 在按键聚焦到触发器时，前面的不符合规则的弹窗已被被动关闭，所以此处只需要触发打开这个弹窗和关闭即可
     * 不需要像点击那样，需要先处理其他弹窗的关闭等逻辑
     */
    public void popupKeyPressed(GuiEventListener listener) {
        PopupBind bind = bindings.get(listener);
        if (bind != null){
            if(stack.isEmpty()) open0(bind, listener);
            else close0();
        }
    }



    /** 核心打开逻辑 */
    private void open0(PopupBind bind, GuiEventListener trigger) {
        if (bind.onOpen != null) bind.onOpen.accept(bind.popup);
        addRenderableWidget(bind.popup);
        stack.push(new PopupEntry(bind.popup, trigger));
    }

    /** 检查 listener 是否在弹窗内部（作为子组件） */
    private boolean isInsidePopup(GuiEventListener listener, AbstractWidget popup) {
        if (listener == popup) return true;
        if (popup instanceof ContainerEventHandler container) {
            for (GuiEventListener child : container.children()) {
                if (child == listener) return true;
                // 递归检查子容器
                if (child instanceof AbstractWidget childPopup && isInsidePopup(listener, childPopup)) return true;
            }
        }
        return false;
    }

    public void closeTop() {
        if (!stack.isEmpty()) close0();
    }
    protected void close0(){
        PopupEntry entry = stack.pop();
        changeFocus(entry.triggerPath);
        removeWidget(entry.popup);
    }
    /**
     * 优先让弹窗处理鼠标点击。
     * @return true 表示点击被弹窗消费（弹窗内点击条目或空白），false 表示点击不在任何弹窗内
     */
    public boolean tryPopupClick(double x, double y, int button) {
        if (stack.isEmpty()) return false;
        // 先检查是否有弹窗内部的条目点击
        Iterator<PopupEntry> it = stack.iterator();
        while (it.hasNext()) {
            PopupEntry entry = it.next();
            AbstractWidget popup = entry.popup;
            if (popup.isMouseOver(x, y)) {
                if (popup.mouseClicked(x, y, button)) {
                    return true; // 条目处理了点击
                }
                return true;
            }
            // 不包含鼠标 → 关闭此弹窗
            removeWidget(popup);
            it.remove();
        }
        return false;
    }
    
    public boolean isOpen(GuiEventListener popup){
        if (stack.isEmpty()) return false;
        for (PopupEntry entry : stack) {
            if(entry.popup == popup) return true;
        }
        return false;
    }


    public static class PopupEntry {
        protected AbstractWidget popup;
        protected GuiEventListener trigger;
        protected ComponentPath triggerPath;
        protected PopupEntry(AbstractWidget popup, GuiEventListener trigger) {
            this.popup = popup;
            this.trigger = trigger;
        }

        public void setTriggerPath(ComponentPath triggerPath) {
            this.triggerPath = triggerPath;
        }
    }

    // --- 内部记录类 ---
    public record PopupBind(AbstractWidget popup, @Nullable Consumer<AbstractWidget> onOpen) {}
}
//package com.fanxing.lib.client.gui.component.manager;
//
//import net.minecraft.client.gui.components.AbstractWidget;
//import net.minecraft.client.gui.components.events.ContainerEventHandler;
//import net.minecraft.client.gui.components.events.GuiEventListener;
//import org.jetbrains.annotations.Nullable;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.*;
//import java.util.function.Consumer;
//
//public class PopupManager {
//    private static final Logger log = LoggerFactory.getLogger(PopupManager.class);
//    private final Deque<PopupEntry> stack = new ArrayDeque<>();
//    private final Consumer<AbstractWidget> addWidget;
//    private final Consumer<AbstractWidget> removeWidget;
//    private final Map<GuiEventListener, PopupBind> bindings = new HashMap<>();
//    public PopupManager(Consumer<AbstractWidget> addWidget,
//                        Consumer<AbstractWidget> removeWidget,
//                        Consumer<GuiEventListener> focusWidget) {
//        this.addWidget = addWidget;
//        this.removeWidget = removeWidget;
//    }
//
//    public void bind(GuiEventListener trigger, AbstractWidget popup, Consumer<AbstractWidget> onOpen) {
//        bindings.put(trigger, new PopupBind(popup, onOpen));
//    }
//
//    public void bind(GuiEventListener trigger, AbstractWidget popup) {
//        bind(trigger, popup, null);
//    }
//
//    /**
//     * 任意弹窗触发器被激活时调用（鼠标点击、键盘回车等）。
//     * 统一处理弹窗的打开/关闭，仅负责点击的这个，而其他弹窗关闭则由屏幕焦点变化的时候被动关闭
//     */
//    public void trigger(GuiEventListener listener) {
//        PopupBind bind = bindings.get(listener);
//        Iterator<PopupEntry> it = stack.iterator();
//        while (it.hasNext()) {
//            PopupEntry entry = it.next();
//            AbstractWidget popup = entry.widget;
//
//            // 1. 点击的是弹窗本身（面板背景等）→ 聚焦
//            if (popup == listener) {
//                focusWidget.accept(popup);
//                return;
//            }
//
//            // 2. 点击的组件在弹窗内部
//            if (isInsidePopup(listener, popup)) {
//                if (bind == null) {
//                    // 没有绑定（普通子组件）→ 聚焦该弹窗
//                    focusWidget.accept(popup);
//                } else {
//                    // 有绑定（嵌套触发器）→ 打开新弹窗
//                    open0(bind, listener);
//                }
//                return;
//            }
//            // 3. 点击的是该弹窗的触发器（已打开）→ 关闭该弹窗
//            if (entry.trigger == listener) {
//                clearFocus(popup);
//                removeWidget.accept(popup);
//                it.remove();
//                return;
//            }
//            clearFocus(popup);
//            removeWidget.accept(popup);
//            it.remove();
//        }
//
//        // 栈被清空（平行触发或首次）→ 打开新弹窗
//        if (bind != null) open0(bind, listener);
//
//    }
//
//    /**
//     * 屏幕焦点变化时调用（被动焦点转移，如Tab键、方向键离开弹窗）。
//     */
//    public void onScreenFocusChanged(GuiEventListener to) {
//        Iterator<PopupEntry> it = stack.iterator();
//        while (it.hasNext()) {
//            PopupEntry entry = it.next();
//            AbstractWidget popup = entry.widget;
//            // 弹窗就是焦点、焦点已在弹窗内、焦点回到触发者 → 什么都不做
//            if (popup == to || isInsidePopup(to, popup) || entry.trigger == to) {
//                return;
//            }
//            // 无关 → 移除继续,并清除焦点
//            clearFocus(popup);
//            removeWidget.accept(popup);
//            it.remove();
//        }
//    }
//
//    /** 核心打开逻辑 */
//    private void open0(PopupBind bind, GuiEventListener trigger) {
//        if (bind.onOpen != null) bind.onOpen.accept(bind.popup);
//        addWidget.accept(bind.popup);
//        stack.push(new PopupEntry(bind.popup, trigger));
//    }
//
//    /** 检查 listener 是否在弹窗内部（作为子组件） */
//    private boolean isInsidePopup(GuiEventListener listener, AbstractWidget popup) {
//        if (listener == popup) return true;
//        if (popup instanceof ContainerEventHandler container) {
//            for (GuiEventListener child : container.children()) {
//                if (child == listener) return true;
//                // 递归检查子容器
//                if (child instanceof AbstractWidget widget && isInsidePopup(listener, widget)) return true;
//            }
//        }
//        return false;
//    }
//
//    public void closeAll() {
//        while (!stack.isEmpty()) close0();
//    }
//    public void closeTop() {
//        if (!stack.isEmpty()) close0();
//    }
//    protected void close0(){
//        PopupEntry entry = stack.pop();
//        clearFocus(entry.widget);
//        clearFocus(entry.trigger);
//        removeWidget.accept(entry.widget);
//    }
//
//    public void clearFocus(GuiEventListener listener) {
//        if(listener instanceof ContainerEventHandler container){
//            clearFocus(container.getFocused());
//            container.setFocused(null);
//        }
//        if(listener != null) listener.setFocused(false);
//    }
//
//
//
//    public boolean isTop(GuiEventListener widget) {
//        return !stack.isEmpty() && stack.peek().widget.equals(widget);
//    }
//
//    public void focusPopup(GuiEventListener popup) {
//        focusWidget.accept(popup);
//    }
//
//
//    /**
//     * 优先让弹窗处理鼠标点击。
//     * @return true 表示点击被弹窗消费（弹窗内点击条目或空白），false 表示点击不在任何弹窗内
//     */
//    public boolean tryPopupClick(double x, double y, int button) {
//        if (stack.isEmpty()) return false;
//        // 先检查是否有弹窗内部的条目点击
//        Iterator<PopupEntry> it = stack.iterator();
//        while (it.hasNext()) {
//            PopupEntry entry = it.next();
//            AbstractWidget popup = entry.widget;
//            if (popup.isMouseOver(x, y)) {
//                if (popup.mouseClicked(x, y, button)) {
//                    return true; // 条目处理了点击
//                }
//                return true;
//            }
//            // 不包含鼠标 → 关闭此弹窗
//            clearFocus(popup);
//            removeWidget.accept(popup);
//            it.remove();
//        }
//        return false;
//    }
//    /**
//     * 处理无子组件消费的点击（空白区域或弹窗内部空白）。
//     * 从栈顶向下遍历，保留第一个包含鼠标坐标的弹窗，移除它上面的所有弹窗。
//     * 如果鼠标不在任何弹窗内，则移除全部。
//     */
//    public void handleEmptyClick(double mouseX, double mouseY) {
//        Iterator<PopupEntry> it = stack.iterator();
//        while (it.hasNext()) {
//            PopupEntry entry = it.next();
//            if (entry.widget.isMouseOver(mouseX, mouseY)) return;
//            // 不包含鼠标 → 关闭此弹窗
//            clearFocus(entry.widget);
//            removeWidget.accept(entry.widget);
//            it.remove();
//        }
//    }
//
//
//    // --- 内部记录类 ---
//    public record PopupEntry(AbstractWidget widget, GuiEventListener trigger) {}
//    public record PopupBind(AbstractWidget popup, @Nullable Consumer<AbstractWidget> onOpen) {}
//}
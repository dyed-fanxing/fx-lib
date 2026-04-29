package com.fanxing.lib.client.gui.utils;

import com.fanxing.lib.client.gui.Placement;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.components.AbstractWidget;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * 悬浮辅助器：鼠标悬浮在目标组件上时，在指定位置显示一个浮动组件（如按钮）。
 * 热区为目标组件自身 + 右侧可扩展宽度（用于平滑过渡）。
 * 只有当鼠标先从目标组件进入热区时，热区才保持显示；单独在热区上不显示。
 */
public class FloatingPopupHelper<T extends AbstractWidget> {
    private final AbstractWidget floatingWidget;
    private final Map<T, Pair<Placement, Integer>> registry = new HashMap<>();
    private T activeTarget = null;  // 当前活跃的目标

    public FloatingPopupHelper(AbstractWidget floatingWidget) {
        this.floatingWidget = floatingWidget;
        floatingWidget.visible = false;
    }

    public void register(T target) {
        register(target, Placement.RIGHT, 3);
    }

    public void register(T target, Placement placement, int gap) {
        registry.put(target, Pair.of(placement, gap));
    }

    public void unregister(T target) {
        registry.remove(target);
        if (activeTarget == target) {
            activeTarget = null;
            floatingWidget.visible = false;
        }
    }

    public void update(double mouseX, double mouseY) {
        if (floatingWidget.visible && floatingWidget.isMouseOver(mouseX, mouseY)) {
            if (activeTarget != null) {
                updatePosition(activeTarget);
            }
            return;
        }

        // 2. 检测鼠标是否直接位于某个目标组件上
        T directTarget = null;
        for (Map.Entry<T, Pair<Placement, Integer>> entry : registry.entrySet()) {
            T w = entry.getKey();
            if (w.isMouseOver(mouseX, mouseY)) {
                directTarget = w;
                break;
            }
        }

        T newActive = null;
        if (directTarget != null) {
            newActive = directTarget;
        } else if (activeTarget != null) {
            // 3. 当前有活跃目标，检查鼠标是否在其热区内
            Pair<Placement, Integer> pair = registry.get(activeTarget);
            int gap = (pair != null) ? pair.getSecond() : 0;
            if (isMouseInHotZone(mouseX, mouseY, activeTarget, gap)) {
                newActive = activeTarget;
            }
        }

        // 更新状态
        if (newActive != activeTarget) {
            activeTarget = newActive;
            floatingWidget.visible = (activeTarget != null);
        }

        // 更新位置
        if (activeTarget != null) {
            updatePosition(activeTarget);
        }
    }

    private boolean isMouseInHotZone(double mx, double my, T w, int gap) {
        if (w.isMouseOver(mx, my)) return true;
        if (gap > 0) {
            int x = w.getX() + w.getWidth();
            int y = w.getY();
            return mx >= x && mx <= x + gap && my >= y && my <= y + w.getHeight();
        }
        return false;
    }

    private void updatePosition(T target) {
        Pair<Placement, Integer> pair = registry.get(target);
        Placement p = (pair != null) ? pair.getFirst() : Placement.RIGHT;
        int gap = (pair != null) ? pair.getSecond() : 0;
        int tx = target.getX(), ty = target.getY();
        int tw = target.getWidth(), th = target.getHeight();
        int bw = floatingWidget.getWidth(), bh = floatingWidget.getHeight();
        int x, y;

        switch (p) {
            case TOP_LEFT -> { x = tx - bw - gap; y = ty - bh - gap; }
            case TOP -> { x = tx + (tw - bw) / 2; y = ty - bh - gap; }
            case TOP_RIGHT -> { x = tx + tw + gap; y = ty - bh - gap; }
            case LEFT -> { x = tx - bw - gap; y = ty + (th - bh) / 2; }
            case BOTTOM_LEFT -> { x = tx - bw - gap; y = ty + th + gap; }
            case BOTTOM -> { x = tx + (tw - bw) / 2; y = ty + th + gap; }
            case BOTTOM_RIGHT -> { x = tx + tw + gap; y = ty + th + gap; }
            case CENTER -> { x = tx + (tw - bw) / 2; y = ty + (th - bh) / 2; }
            default -> { x = tx + tw + gap; y = ty + (th - bh) / 2; }
        }
        floatingWidget.setPosition(x, y);
    }

    @Nullable
    public T getActiveTarget() {
        return activeTarget;
    }

    public void clearHover() {
        activeTarget = null;
        floatingWidget.visible = false;
    }

    public void cleanup() {
        registry.clear();
        clearHover();
    }
}
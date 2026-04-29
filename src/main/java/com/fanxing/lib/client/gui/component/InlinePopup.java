package com.fanxing.lib.client.gui.component;

import com.fanxing.lib.client.gui.Placement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

/**
 * 包装任意组件，在组件指定方向外添加一个悬浮按钮，按钮占用布局空间（包装器尺寸会根据按钮方向扩展）。
 * 显示规则：鼠标必须先进入内容组件，然后移动到热区（目标与按钮之间的间隙区域）时按钮才显示；直接进入热区不显示。
 * 鼠标移动到按钮上时按钮保持显示。
 * 支持八个方向（LEFT, RIGHT, TOP, BOTTOM, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT）。
 * @param <T> 被包裹的组件类型（需继承 AbstractWidget）
 */
public class InlinePopup<T extends AbstractWidget> extends AbstractWidget {
    private final T content;
    private final ImageButton button;
    private final Placement placement;
    private final int gap;                     // 内容与按钮之间的间距（同时也是热区宽度/高度）
    private boolean wasInContent = false;

    public InlinePopup(int x, int y, T content, ImageButton button, Placement placement, int gap) {
        super(x, y,
                computeWidth(content, button, placement, gap),
                computeHeight(content, button, placement, gap),
                Component.empty());
        this.content = content;
        this.button = button;
        this.placement = placement;
        this.gap = gap;
        this.button.visible = false;
    }

    private static int computeWidth(AbstractWidget content, ImageButton button, Placement placement, int gap) {
        int cw = content.getWidth();
        int bw = button.getWidth();
        return switch (placement) {
            case LEFT, RIGHT -> cw + gap + bw;
            case TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT -> cw + bw + gap;
            default -> cw;
        };
    }

    private static int computeHeight(AbstractWidget content, ImageButton button, Placement placement, int gap) {
        int ch = content.getHeight();
        int bh = button.getHeight();
        return switch (placement) {
            case TOP, BOTTOM -> ch + gap + bh;
            case TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT -> ch + bh + gap;
            default -> ch;
        };
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // 计算内容组件的位置（根据按钮方向偏移）
        int contentX = getX();
        int contentY = getY();
        int bw = button.getWidth();
        int bh = button.getHeight();

        switch (placement) {
            case LEFT, BOTTOM_LEFT:
                contentX += bw + gap;
                break;
            case RIGHT, BOTTOM_RIGHT, BOTTOM:
                break;
            case TOP, TOP_RIGHT:
                contentY += bh + gap;
                break;
            case TOP_LEFT:
                contentX += bw + gap;
                contentY += bh + gap;
                break;
        }
        content.setPosition(contentX, contentY);
        content.render(graphics, mouseX, mouseY, partialTick);

        updateButtonState(mouseX, mouseY);

        if (button.visible) {
            int btnX = getButtonX();
            int btnY = getButtonY();
            button.setPosition(btnX, btnY);
            button.render(graphics, mouseX, mouseY, partialTick);
        }
    }

    private int getButtonX() {
        int cw = content.getWidth();
        int bw = button.getWidth();
        int contentX = content.getX();
        return switch (placement) {
            case LEFT, BOTTOM_LEFT, TOP_LEFT -> getX();
            case TOP, BOTTOM -> contentX + (cw - bw) / 2;
            default -> contentX + cw + gap;
        };
    }

    private int getButtonY() {
        int ch = content.getHeight();
        int bh = button.getHeight();
        int contentY = content.getY();
        return switch (placement) {
            case TOP, TOP_RIGHT, TOP_LEFT -> getY();
            case BOTTOM, BOTTOM_RIGHT, BOTTOM_LEFT -> contentY + ch + gap;
            default -> contentY + (ch - bh) / 2;
        };
    }

    private void updateButtonState(double mouseX, double mouseY) {
        boolean inContent = content.isMouseOver(mouseX, mouseY);
        boolean inHotZone = false;
        if (!inContent) {
            inHotZone = isMouseInHotZone(mouseX, mouseY);
        }
        boolean inButton = button.isMouseOver(mouseX, mouseY);

        if (inContent) {
            wasInContent = true;
            button.visible = true;  // 改为 true，悬浮目标时立即显示按钮
        } else if ((inHotZone || inButton) && wasInContent) {
            button.visible = true;
        } else {
            button.visible = false;
            if (!inHotZone && !inButton) {
                wasInContent = false;
            }
        }
    }

    private boolean isMouseInHotZone(double mx, double my) {
        int cx = content.getX(), cy = content.getY();
        int cw = content.getWidth(), ch = content.getHeight();
        return switch (placement) {
            case LEFT -> (mx >= cx - gap && mx < cx && my >= cy && my < cy + ch);
            case RIGHT -> (mx >= cx + cw && mx < cx + cw + gap && my >= cy && my < cy + ch);
            case TOP -> (mx >= cx && mx < cx + cw && my >= cy - gap && my < cy);
            case BOTTOM -> (mx >= cx && mx < cx + cw && my >= cy + ch && my < cy + ch + gap);
            case TOP_LEFT -> (mx >= cx - gap && mx < cx && my >= cy - gap && my < cy);
            case TOP_RIGHT -> (mx >= cx + cw && mx < cx + cw + gap && my >= cy - gap && my < cy);
            case BOTTOM_LEFT -> (mx >= cx - gap && mx < cx && my >= cy + ch && my < cy + ch + gap);
            case BOTTOM_RIGHT -> (mx >= cx + cw && mx < cx + cw + gap && my >= cy + ch && my < cy + ch + gap);
            default -> false;
        };
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int buttonIndex) {
        if (button.visible && button.isMouseOver(mouseX, mouseY)) {
            return button.mouseClicked(mouseX, mouseY, buttonIndex);
        }
        return content.mouseClicked(mouseX, mouseY, buttonIndex);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int buttonIndex) {
        if (button.visible && button.isMouseOver(mouseX, mouseY)) {
            return button.mouseReleased(mouseX, mouseY, buttonIndex);
        }
        return content.mouseReleased(mouseX, mouseY, buttonIndex);
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        // 内容组件的位置需要在 render 中动态计算，因为其位置依赖于包装器位置和方向偏移。
        // 这里无需手动设置内容位置。
    }

    public T getContent() {
        return content;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, Component.literal("悬浮弹出项"));
    }
}
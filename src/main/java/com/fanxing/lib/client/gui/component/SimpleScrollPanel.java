package com.fanxing.lib.client.gui.component;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class SimpleScrollPanel extends AbstractWidget implements Renderable {
    private LayoutElement content;
    private double scrollAmount = 0;
    private boolean draggingScrollbar = false;
    private final int scrollbarWidth = 6;
    private final int scrollbarColor = 0xFFAAAAAA;

    public SimpleScrollPanel(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
    }

    public void setContent(LayoutElement content) {
        this.content = content;
        // 初次设置时同步位置
        if (content != null) {
            content.setX(getX());
            content.setY(getY());
        }
    }

    private int getContentHeight() {
        return content == null ? 0 : content.getHeight();
    }

    private boolean scrollbarVisible() {
        return getContentHeight() > height;
    }

    private int getMaxScroll() {
        return Math.max(0, getContentHeight() - height);
    }

    private void setScrollAmount(double amount) {
        scrollAmount = Mth.clamp(amount, 0, getMaxScroll());
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        content.setX(x);
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        content.setY(y);
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // 背景
        graphics.fill(getX(), getY(), getX() + width, getY() + height, 0xFF222222);

        // 裁剪 + 平移
        graphics.enableScissor(getX(), getY(), getX() + width, getY() + height);
        graphics.pose().pushPose();
        // 平移整个画布，使内容向上滚动 scrollAmount 像素
        graphics.pose().translate(getX(), getY() - scrollAmount, 0);
        if (content != null) {
            // 注意：content 的渲染坐标是相对于其父组件的，而我们已经将原点平移到了 (getX(), getY() - scrollAmount)
            // 因此直接调用 content 的渲染即可，它会使用自己的坐标（例如 (0,0) 相对于面板左上角）
            // 但 content 可能不是 AbstractWidget，所以需要处理一下：如果是 AbstractWidget 就调用 render，否则通过 visitWidgets
            if (content instanceof AbstractWidget widget) {
                widget.render(graphics, mouseX, (int)(mouseY + scrollAmount), partialTick);
            } else {
                // 对于 LayoutElement 不是 AbstractWidget 的情况，遍历子组件渲染
                content.visitWidgets(w -> w.render(graphics, mouseX, (int)(mouseY + scrollAmount), partialTick));
            }
        }
        graphics.pose().popPose();
        graphics.disableScissor();

        // 滚动条
        if (scrollbarVisible()) {
            int barHeight = (int) ((float) height * height / getContentHeight());
            barHeight = Mth.clamp(barHeight, 32, height);
            int barY = getY() + (int) (scrollAmount / getMaxScroll() * (height - barHeight));
            graphics.fill(getX() + width - scrollbarWidth, barY, getX() + width, barY + barHeight, scrollbarColor);
        }
    }

    @Override
    public void visitWidgets(@NotNull Consumer<AbstractWidget> consumer) {
        content.visitWidgets(consumer);
        super.visitWidgets(consumer);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (isMouseOver(mouseX, mouseY)) {
            setScrollAmount(scrollAmount - scrollY * 20);
            return true;
        }
        return false;
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {}
}
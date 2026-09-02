package com.fanxing.lib.client.gui.component.container;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractScrollContainer extends AbstractContainerWidget {
    public static final ResourceLocation SCROLLER_SPRITE = ResourceLocation.withDefaultNamespace("widget/scroller");
    public static final ResourceLocation SCROLLER_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("widget/scroller_background");
    public static final int SCROLLBAR_WIDTH = 6;
    protected double scrollAmount = 0;
    protected boolean scrolling = false;

    public AbstractScrollContainer(int x, int y, int width, int height, Component title) {
        super(x, y, width, height, title);
    }

    protected abstract int getContentHeight();
    protected double getScrollAmount() { return scrollAmount; }
    public void setScrollAmount(double amount) {
        int maxScroll = Math.max(0, getContentHeight() - getHeight());
        this.scrollAmount = Mth.clamp(amount, 0, maxScroll);
    }
    protected int getMaxScroll() { return Math.max(0, getContentHeight() - getHeight()); }
    protected boolean scrollbarVisible() { return getContentHeight() > getHeight(); }

    // 新增：子类可重写此方法自定义滚动步长（像素）
    protected double getScrollStep() {
        return 16.0;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        setScrollAmount(getScrollAmount() - scrollY * getScrollStep());
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (scrollbarVisible() && mouseX >= getX() + getWidth() - SCROLLBAR_WIDTH && mouseX <= getX() + getWidth() && mouseY >= getY() && mouseY <= getY() + getHeight()) {
            scrolling = true;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
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
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        scrolling = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.enableScissor(getX(), getY(), getX() + getWidth(), getY() + getHeight());
        // 删除以下两行
        // graphics.pose().pushPose();
        // graphics.pose().translate(0, -getScrollAmount(), 0);
        // 直接渲染内容（renderContents 中已经手动处理了滚动偏移）
        renderContents(graphics, mouseX, mouseY, partialTick);
        // 删除 popPose
        // graphics.pose().popPose();
        graphics.disableScissor();

        if (scrollbarVisible()) {
            int x = getX() + getWidth() - SCROLLBAR_WIDTH;
            graphics.blitSprite(SCROLLER_BACKGROUND_SPRITE, x, getY(), SCROLLBAR_WIDTH, getHeight());
            int viewHeight = getHeight();
            int contentHeight = getContentHeight();
            double ratio = (double) viewHeight / contentHeight;
            int barHeight = Math.max(32, (int) (viewHeight * ratio));
            int barY = getY() + (int) ((getScrollAmount() / getMaxScroll()) * (viewHeight - barHeight));
            graphics.blitSprite(SCROLLER_SPRITE, x, barY, SCROLLBAR_WIDTH, barHeight);
        }
    }

    protected abstract void renderContents(GuiGraphics graphics, int mouseX, int mouseY, float partialTick);
}
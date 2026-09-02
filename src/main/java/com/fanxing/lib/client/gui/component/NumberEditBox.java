package com.fanxing.lib.client.gui.component;

import com.fanxing.lib.client.gui.Scheme;
import com.fanxing.lib.mixin.EditBoxAccessor;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public abstract class NumberEditBox extends EditBox {
    public static final WidgetSprites SPRITES = new WidgetSprites(
            ResourceLocation.withDefaultNamespace("widget/text_field"),
            ResourceLocation.withDefaultNamespace("widget/text_field_highlighted")
    );
    protected boolean valid = true; // 默认有效


    public NumberEditBox(Font font, int x, int y, int w, int h, Component component) {
        super(font, x, y, w, h, component);
    }

    /**
     * 判断单个字符是否允许输入。
     * 子类可重写此方法来改变过滤规则。
     * @param c 字符
     * @param currentValue 当前文本框内容
     * @param cursorPos 光标位置（插入前的光标位置）
     * @return true 允许输入
     */
    protected abstract boolean isValidChar(char c, String currentValue, int cursorPos);

    protected void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isValid() {
        return valid;
    }

    @Override
    public void insertText(String text) {
        StringBuilder filtered = new StringBuilder();
        String cur = getValue();
        int cursor = getCursorPosition();
        for (char ch : text.toCharArray()) {
            if (isValidChar(ch, cur, cursor + filtered.length())) {
                filtered.append(ch);
            }
        }
        if (filtered.isEmpty()) return;
        super.insertText(filtered.toString());

    }

    public void setValue(@NotNull String text) {
        if (this.filter.test(text)) {
            if (text.length() > this.maxLength) this.value = text.substring(0, this.maxLength);
            else this.value = text;
            this.moveCursorToEnd(false);
            this.setHighlightPos(this.cursorPos);
            this.onValueChange(text);
        }
    }
    public void setValueSilently(String text) {
        if (this.filter.test(text)) {
            if (text.length() > this.maxLength) this.value = text.substring(0, this.maxLength);
            else this.value = text;
            this.moveCursorToEnd(false);
            this.setHighlightPos(this.cursorPos);
        }
    }


    @Override
    protected void onValueChange(@NotNull String text) {
        if (this.responder != null) {
            this.responder.accept(text);
        }
    }

    // ========== 渲染（完全复用你之前的 renderWidget，未聚焦显示 hint:value） ==========
    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!isVisible()) return;
        EditBoxAccessor accessor = (EditBoxAccessor) this;
        Font font = accessor.getFont();

        // 背景
        if (accessor.isBordered()) {
            ResourceLocation sprite = SPRITES.get(isActive(), isFocused());
            graphics.blitSprite(sprite, getX(), getY(), getWidth(), getHeight());
        }

        int textColor = accessor.isEditable() ? accessor.getTextColor() : accessor.getTextColorUneditable();
        int textX = accessor.isBordered() ? getX() + 4 : getX();
        int textY = accessor.isBordered() ? getY() + (getHeight() - 8) / 2 : getY();

        if (isFocused()) {
            // 聚焦时：只绘制实际值（纯数字）
            String fullValue = value;
            int displayPos = accessor.getDisplayPos();
            String visible = font.plainSubstrByWidth(fullValue.substring(displayPos), getInnerWidth(accessor));
            int cursorRel = cursorPos - displayPos;
            boolean cursorInRange = cursorRel >= 0 && cursorRel <= visible.length();
            boolean blink = (Util.getMillis() - accessor.getFocusedTime()) / 300L % 2L == 0L && cursorInRange;

            int drawX = textX;
            int highlightRel = Mth.clamp(accessor.getHighlightPos() - displayPos, 0, visible.length());

            if (!visible.isEmpty()) {
                String before = cursorInRange ? visible.substring(0, cursorRel) : visible;
                drawX = graphics.drawString(font,
                        accessor.getFormatter().apply(before, displayPos),
                        textX, textY, textColor, accessor.isTextShadow());
            }

            boolean cursorAtEnd = cursorPos < fullValue.length() || fullValue.length() >= maxLength;
            int cursorX = drawX;
            if (!cursorInRange) {
                cursorX = cursorRel > 0 ? textX + getWidth() : textX;
            } else if (cursorAtEnd) {
                cursorX = drawX - 1;
                drawX--;
            }

            if (!visible.isEmpty() && cursorInRange && cursorRel < visible.length()) {
                graphics.drawString(font,
                        accessor.getFormatter().apply(visible.substring(cursorRel), cursorPos),
                        drawX, textY, textColor, accessor.isTextShadow());
            }

            if (blink) {
                if (cursorAtEnd) {
                    graphics.fill(RenderType.guiOverlay(), cursorX, textY - 1, cursorX + 1, textY + 9 + 1, 0xFF000000);
                } else {
                    graphics.drawString(font, "_", cursorX, textY, textColor, accessor.isTextShadow());
                }
            }

            if (highlightRel != cursorRel) {
                int selectStartX = textX + font.width(visible.substring(0, highlightRel));
                graphics.fill(RenderType.guiTextHighlight(), cursorX, textY - 1, selectStartX, textY + 9 + 1, 0x330000FF);
            }
        } else {
            Component hintComp = accessor.getHint();
            String value = getValue();
            int maxWidth = getInnerWidth(accessor);

            if (hintComp == null || value.isEmpty()) {
                // 无 hint 或值为空：只显示 value（可能滚动）
                if (!value.isEmpty()) {
                    renderScrollingString(graphics, font, value, textX, textY, maxWidth, textColor, accessor.isTextShadow());
                }
            } else {
                String hint = hintComp.getString();
                String suffix = ":" + value;
                int suffixWidth = font.width(suffix);
                int fullWidth = font.width(hint + suffix);

                if (fullWidth <= maxWidth) {
                    // 完整显示，不滚动
                    String hintPart = hint + ":";
                    int hintPartWidth = font.width(hintPart);
                    graphics.drawString(font, hintPart, textX, textY, Scheme.TEXT_COLOR_HINT, accessor.isTextShadow());
                    graphics.drawString(font, value, textX + hintPartWidth, textY, textColor, accessor.isTextShadow());
                } else if (suffixWidth > maxWidth) {
                    // 连 ":value" 都放不下，只显示 value（可能滚动）
                    renderScrollingString(graphics, font, value, textX, textY, maxWidth, textColor, accessor.isTextShadow());
                } else {
                    int colonWidth = font.width(":");
                    // hint 需要滚动，suffix 固定在右侧
                    int hintMaxWidth = maxWidth - suffixWidth;
                    renderScrollingString(graphics, font, hint, textX, textY, hintMaxWidth, Scheme.TEXT_COLOR_HINT, accessor.isTextShadow());
                    graphics.drawString(font, ":", textX+hintMaxWidth, textY, Scheme.TEXT_COLOR_HINT, accessor.isTextShadow());
                    graphics.drawString(font, value, textX + hintMaxWidth+colonWidth, textY, textColor, accessor.isTextShadow());
                }
            }
        }
        if (!valid) {
            graphics.renderOutline(getX(), getY(), getWidth(), getHeight(), 0xFFFF5555);
        }
    }
    /**
     * 渲染一个可能滚动的字符串，支持自定义颜色。
     * @param graphics GuiGraphics
     * @param font 字体
     * @param text 要渲染的文本
     * @param x 起始 X 坐标
     * @param y 起始 Y 坐标
     * @param maxWidth 最大可用宽度
     * @param color 文字颜色
     * @param shadow 是否阴影
     */
    public static void renderScrollingString(GuiGraphics graphics, Font font, String text,
                                              int x, int y, int maxWidth, int color, boolean shadow) {
        int textWidth = font.width(text);
        if (textWidth <= maxWidth) {
            graphics.drawString(font, text, x, y, color, shadow);
        } else {
            // 滚动
            int totalScroll = textWidth - maxWidth;
            double period = Math.max(totalScroll * 0.5, 3.0);
            long time = Util.getMillis();
            double t = (time % (long)(period * 1000)) / 1000.0;
            // 原版滚动曲线
            double progress = (Math.sin(Math.PI / 2 * Math.cos(2 * Math.PI * t / period)) + 1) / 2;
            int offset = (int) Math.round(progress * totalScroll);
            graphics.enableScissor(x, y, x + maxWidth, y + font.lineHeight);
            graphics.drawString(font, text, x - offset, y, color, shadow);
            graphics.disableScissor();
        }
    }

    protected int getInnerWidth(EditBoxAccessor accessor) {
        return accessor.isBordered() ? getWidth() - 8 : getWidth();
    }
}
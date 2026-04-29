package com.fanxing.lib.client.gui.renderer;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import com.mojang.blaze3d.systems.RenderSystem;

import static net.minecraft.client.gui.screens.Screen.*;

/**
 * 背景渲染器，为布局元素绘制背景和可选边框。
 * - 使用纹理背景时，只绘制上下边框（使用 HEADER_SEPARATOR / FOOTER_SEPARATOR 纹理），左右边框强制忽略。
 * - 使用纯色背景时，边框用纯色线条，可独立控制四边。
 */
public class BackgroundRenderer implements Renderable {
    private final LayoutElement target;
    private final int padding;
    private final int bgColor;
    private final int outlineColor;
    private final int innerColor;
    private final ResourceLocation texture;

    // 边框控制（纯色线条模式）; 纹理模式下只有 drawTop/drawBottom 用于控制分割线纹理
    private boolean drawTop = true, drawRight = true, drawBottom = true, drawLeft = true;
    private boolean drawInnerTop = false, drawInnerRight = false, drawInnerBottom = false, drawInnerLeft = false;

    public BackgroundRenderer(LayoutElement target, int padding, int bgColor, int outlineColor, int innerColor, ResourceLocation texture) {
        this.target = target;
        this.padding = padding;
        this.bgColor = bgColor;
        this.outlineColor = outlineColor;
        this.innerColor = innerColor;
        this.texture = texture;
    }

    public static BackgroundRenderer solid(LayoutElement target, int padding) {
        return new BackgroundRenderer(target, padding, 0x802C2C2C, 0xFFA0A0A0, 0xFF000000, null);
    }

    public static BackgroundRenderer textured(LayoutElement target, int padding) {
        return new BackgroundRenderer(target, padding, 0, 0xFFA0A0A0, 0xFF000000, MENU_BACKGROUND);
    }

    public static BackgroundRenderer textured(LayoutElement target, int padding, ResourceLocation texture) {
        return new BackgroundRenderer(target, padding, 0, 0xFFA0A0A0, 0xFF000000, texture);
    }

    // ========== 链式边框控制 ==========
    public BackgroundRenderer border(boolean top, boolean right, boolean bottom, boolean left) {
        this.drawTop = top;
        this.drawRight = right;
        this.drawBottom = bottom;
        this.drawLeft = left;
        return this;
    }

    public BackgroundRenderer borderVertical(boolean top, boolean bottom) {
        return border(top, false, bottom, false);
    }

    public BackgroundRenderer borderHorizontal(boolean left, boolean right) {
        return border(false, right, false, left);
    }

    public BackgroundRenderer noBorder() {
        return border(false, false, false, false);
    }

    // ========== 内边框控制（仅纯色模式有效） ==========
    public BackgroundRenderer innerBorder(boolean top, boolean right, boolean bottom, boolean left) {
        this.drawInnerTop = top;
        this.drawInnerRight = right;
        this.drawInnerBottom = bottom;
        this.drawInnerLeft = left;
        return this;
    }

    public BackgroundRenderer noInnerBorder() {
        return innerBorder(false, false, false, false);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (target.getHeight() <= 0) return;
        int x = target.getX() - padding;
        int y = target.getY() - padding;
        int w = target.getWidth() + padding * 2;
        int h = target.getHeight() + padding * 2;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // 1. 绘制背景
        if (texture != null) {
            int texW = 32, texH = 32;
            for (int i = 0; i < w; i += texW) {
                for (int j = 0; j < h; j += texH) {
                    graphics.blit(texture, x + i, y + j, 0, 0,
                            Math.min(texW, w - i), Math.min(texH, h - j), texW, texH);
                }
            }
        } else {
            graphics.fill(x, y, x + w, y + h, bgColor);
        }

        // 2. 绘制边框 / 分割线
        if (texture != null) {
            if (drawTop) {
                graphics.blit(HEADER_SEPARATOR, x, y, 0, 0, w, 2, 32, 2);
            }
            if (drawBottom) {
                graphics.blit(FOOTER_SEPARATOR, x, y + h - 2, 0, 0, w, 2, 32, 2);
            }
        } else {
            // 纯色模式：外边框（纯色线条）
            if (outlineColor != 0) {
                if (drawTop) graphics.fill(x, y, x + w, y + 1, outlineColor);
                if (drawBottom) graphics.fill(x, y + h - 1, x + w, y + h, outlineColor);
                if (drawLeft) graphics.fill(x, y, x + 1, y + h, outlineColor);
                if (drawRight) graphics.fill(x + w - 1, y, x + w, y + h, outlineColor);
            }
            // 内边框（纯色线条）
            if (innerColor != 0) {
                if (drawInnerTop) graphics.fill(x + 1, y + 1, x + w - 1, y + 2, innerColor);
                if (drawInnerBottom) graphics.fill(x + 1, y + h - 2, x + w - 1, y + h - 1, innerColor);
                if (drawInnerLeft) graphics.fill(x + 1, y + 1, x + 2, y + h - 1, innerColor);
                if (drawInnerRight) graphics.fill(x + w - 2, y + 1, x + w - 1, y + h - 1, innerColor);
            }
        }

        RenderSystem.disableBlend();
    }
}
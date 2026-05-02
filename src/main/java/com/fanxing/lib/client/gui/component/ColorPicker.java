package com.fanxing.lib.client.gui.component;

import com.fanxing.lib.FxLib;
import com.fanxing.lib.client.gui.ColorMode;
import com.fanxing.lib.util.ColorUtils;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

import static com.fanxing.lib.FxLib.MOD_ID;

/**
 * 颜色选择器：包含连续正方形色相边框、SV 方块、RGB/HSV 输入框、模式切换、透明度条。
 * 色相边框基于中轴线（厚度中心线）映射 0°~360°，指针严格沿中轴线移动，
 * 边框纹理采用投影算法保证角部色彩平滑过渡，完全连续无突变。
 * 透明度条位于左侧，高与调色器相同，宽20px。
 */
public class ColorPicker extends AbstractWidget {
    protected static final int GAP = 5;
    protected static final int BOX_HEIGHT = 20;
    protected static final int ALPHA_BAR_WIDTH = 20;
    protected static final int BORDER_THICKNESS = 20;      // 边框厚度
    protected static final int MIN_SV_SIZE = 100;
    private static final Logger log = LoggerFactory.getLogger(ColorPicker.class);

    protected int svSize;               // SV 方块边长
    protected int outerSize;            // 外正方形边长 = svSize + 2*(BORDER_THICKNESS+INNER_GAP)
    protected int pathLength;           // 中轴线正方形边长 = outerSize - BORDER_THICKNESS

    protected float[] hsv = new float[]{0f, 1f, 1f};
    protected int currentColor;
    protected int alpha = 255;
    protected final Consumer<Integer> onColorChange;

    protected ResourceLocation alphaBarTexture;   // 透明度条纹理
    protected ResourceLocation borderTexture;   // 整体色相边框纹理
    protected ResourceLocation squareTexture;   // SV 方块纹理
    protected final IntegerEditBox[] boxes = new IntegerEditBox[4];
    protected final CycleButton<ColorMode> mode;

    public ColorPicker(int x, int y, int width, int height, int init, Consumer<Integer> onColorChange) {
        super(x, y, width, height, Component.empty());
        this.onColorChange = onColorChange;
        this.currentColor = init;
        this.alpha = init >>> 24;

        outerSize = width - ALPHA_BAR_WIDTH - GAP;
        svSize = outerSize - 2 * (BORDER_THICKNESS + GAP);
        pathLength = outerSize - BORDER_THICKNESS;

        Font font = Minecraft.getInstance().font;
        int modeWidth = font.width(ColorMode.HSV.name()) + 10;


        hsv = ColorUtils.rgbToHsv(init);
        boxes[0] = new IntegerEditBox(font, 0, 0, 0, BOX_HEIGHT, Component.empty(),
                0, 255, 1, alpha, v -> updateFromBoxInput());
        boxes[0].setHint(Component.translatable("gui." + MOD_ID + ".color.alpha"));
        boxes[0].setTooltip(Tooltip.create(Component.translatable("gui." + MOD_ID + ".color.alpha")));
        // 先解析出正确的 hsv 和 alpha，供后续纹理生成使用

        // boxes[1-3] = R/G/B or H/S/V
        for (int i = 0; i < 3; i++) {
            boxes[i + 1] = new IntegerEditBox(font, 0, 0, 0, BOX_HEIGHT, Component.empty(),
                    0, ColorMode.HSV.max[i], 1, Math.round(hsv[i]*360), v -> updateFromBoxInput())
                    .wrap(i == 0);
            boxes[i + 1].setTooltip(Tooltip.create(Component.translatable(ColorMode.HSV.names[i])));
        }
        mode = CycleButton.<ColorMode>builder(mode -> Component.literal(mode.name()))
                .withValues(ColorMode.values()).withInitialValue(ColorMode.HSV).displayOnlyValue()
                .create(0, 0, modeWidth, BOX_HEIGHT, Component.empty(),
                        (btn, mode) -> updateMode());
        updateBorderTexture();
        updateSVSquareTexture();
        updateAlphaBarTexture();
//        updateMode();
        updateColorBox();
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        int totalWidth = outerSize + ALPHA_BAR_WIDTH + GAP;
        int modeWidth = mode.getWidth();
        int boxWidth = (totalWidth - modeWidth - 4 * GAP) / 4;
        int usedWidth = 4 * boxWidth + modeWidth + 4 * GAP;
        int extra = totalWidth - usedWidth;
        int lastGap = GAP + extra;
        int curX = x;
        for (int i = 0; i < 4; i++) {
            int gap = (i == 3) ? lastGap : GAP;
            boxes[i].setX(curX);
            boxes[i].setWidth(boxWidth);
            curX += boxWidth + gap;
        }
        mode.setX(curX);
        mode.setWidth(modeWidth);
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        int bottomBarY = y + outerSize + GAP;
        for (int i = 0; i < 4; i++) {
            boxes[i].setY(bottomBarY);
        }
        mode.setY(bottomBarY);
    }

    @Override
    public void visitWidgets(@NotNull Consumer<AbstractWidget> consumer) {
        for (IntegerEditBox box : boxes) consumer.accept(box);
        consumer.accept(mode);
        super.visitWidgets(consumer);
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // 左侧透明度条背景（棋盘格，fill 内部会 disable blend，所以在这里之后需要 re-enable）
        renderCheckerboard(graphics, getX(), getY(), ALPHA_BAR_WIDTH, outerSize);

        // 透明度条（需要 blend 叠加）
        RenderSystem.enableBlend();
        if (alphaBarTexture != null) {
            graphics.blit(alphaBarTexture, getX(), getY(), 0, 0, ALPHA_BAR_WIDTH, outerSize, ALPHA_BAR_WIDTH, outerSize);
        }
        // 透明度条上的指示器（水平线表示当前透明度）
        int alphaY = getY() + (int) (alpha * outerSize / 255f);
        graphics.renderOutline(getX(), alphaY - 1, ALPHA_BAR_WIDTH, 2, 0xFFFFFFFF);

        // renderOutline 也是 fill 实现的，又关了 blend，所以再次开启
        RenderSystem.enableBlend();
        // 右侧色相边框 + SV 方块（整体右移）
        if (borderTexture != null)
            graphics.blit(borderTexture, getPickerX(), getY(), 0, 0, outerSize, outerSize, outerSize, outerSize);
        if (squareTexture != null) {
            graphics.blit(squareTexture,
                    getPickerX() + BORDER_THICKNESS + GAP,
                    getY() + BORDER_THICKNESS + GAP,
                    0, 0, svSize, svSize, svSize, svSize);
        }
        RenderSystem.disableBlend();
        // SV 光标
        int svX = getPickerX() + BORDER_THICKNESS + GAP;
        int svY = getY() + BORDER_THICKNESS + GAP;
        int svCursorX = svX + (int) (hsv[1] * svSize);
        int svCursorY = svY + (int) ((1f - hsv[2]) * svSize);
        graphics.renderOutline(svCursorX - 2, svCursorY - 2, 4, 4, 0xFFFFFFFF);
        // 色相光标（沿中轴线）
        int[] cursor = getCursorPosFromHue();
        graphics.renderOutline(cursor[0] - 2, cursor[1] - 2, 4, 4, 0xFFFFFFFF);
    }

    /**
     * 绘制棋盘格，作为透明度条的背景
     */
    private void renderCheckerboard(GuiGraphics graphics, int x, int y, int width, int height) {
        int checkSize = 8;
        for (int row = 0; row < height; row += checkSize) {
            for (int col = 0; col < width; col += checkSize) {
                boolean isLight = ((row / checkSize) + (col / checkSize)) % 2 == 0;
                int color = isLight ? 0xFFCCCCCC : 0xFF888888;
                int endX = Math.min(x + col + checkSize, x + width);
                int endY = Math.min(y + row + checkSize, y + height);
                graphics.fill(x + col, y + row, endX, endY, color);
            }
        }
    }


    // ================== 鼠标交互 ==================
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (IntegerEditBox box : boxes) {
            if (box.isMouseOver(mouseX, mouseY)) return false;
        }
        if (mode.isMouseOver(mouseX, mouseY)) return false;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        updateFromPicker(mouseX, mouseY);
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        updateFromPicker(mouseX, mouseY);
    }

    private void updateFromPicker(double mouseX, double mouseY) {
        // 先检查是否点击了透明度条
        if (isOverAlphaBar(mouseX, mouseY)) {
            setAlphaFromMouse(mouseY);
            return;
        }

        float pos = getPosFromMouse(mouseX, mouseY);
        if (pos >= 0) {
            float t = pos / (4f * pathLength);
            t = Mth.clamp(t, 0f, 0.9999f);
            hsv[0] = t;
            currentColor = withAlpha(ColorUtils.hsvToRgb(hsv));
            updateColorBox();
            updateSVSquareTexture();
            updateAlphaBarTexture();
            onColorChange.accept(currentColor);
        } else {
            int svX = getPickerX() + BORDER_THICKNESS + GAP;
            int svY = getY() + BORDER_THICKNESS + GAP;
            if (mouseX >= svX && mouseX < svX + svSize && mouseY >= svY && mouseY < svY + svSize) {
                hsv[1] = (float) Mth.clamp((mouseX - svX) / svSize, 0, 1);
                hsv[2] = 1f - (float) Mth.clamp((mouseY - svY) / svSize, 0, 1);
                currentColor = withAlpha(ColorUtils.hsvToRgb(hsv));
                updateColorBox();
                updateSVSquareTexture();
                updateAlphaBarTexture();
                onColorChange.accept(currentColor);
            }
        }
    }


    private void updateMode() {
        // 根据当前颜色计算新模式下的正确值
        ColorMode mode = this.mode.getValue();
        int[] newValues = new int[3];
        if (mode == ColorMode.RGB) {
            newValues[0] = (currentColor >> 16) & 0xFF;
            newValues[1] = (currentColor >> 8) & 0xFF;
            newValues[2] = currentColor & 0xFF;
        } else { // HSV
            newValues[0] = Math.round(hsv[0] * 360);
            newValues[1] = Math.round(hsv[1] * 100);
            newValues[2] = Math.round(hsv[2] * 100);
        }
        for (int i = 0; i < 3; i++) {
            boxes[i + 1].setTooltip(Tooltip.create(Component.translatable(mode.names[i])));
            boxes[i + 1].rangeSilently(0, mode.max[i], newValues[i]);
            boxes[i + 1].wrap(mode == ColorMode.HSV && i == 0);
        }
    }

    private void setColorSilently(int rgb) {
        currentColor = rgb;
        alpha = (rgb >> 24) & 0xFF;
        hsv = ColorUtils.rgbToHsv(rgb);
        updateColorBox();
        updateSVSquareTexture();
        updateAlphaBarTexture();
    }

    private void updateFromBoxInput() {
        int[] vals = new int[4];
        for (int i = 0; i < 4; i++) {
            vals[i] = boxes[i].getIntValue();
        }
        alpha = vals[0];

        if (mode.getValue() == ColorMode.RGB) {
            currentColor = (alpha << 24) | (vals[1] << 16) | (vals[2] << 8) | vals[3];
            hsv = ColorUtils.rgbToHsv(vals[1], vals[2], vals[3]);
        } else {
            float h = vals[1] / 360f, s = vals[2] / 100f, v = vals[3] / 100f;
            currentColor = (alpha << 24) | (ColorUtils.hsvToRgb(h, s, v) & 0xFFFFFF);
            hsv[0] = h;
            hsv[1] = s;
            hsv[2] = v;
        }
        updateSVSquareTexture();
        updateAlphaBarTexture();
        onColorChange.accept(currentColor);
    }

    private void updateColorBox() {
        boxes[0].setValueSilently(alpha);
        if (mode.getValue() == ColorMode.HSV) {
            boxes[1].setValueSilently(Math.round(hsv[0] * 360));
            boxes[2].setValueSilently(Math.round(hsv[1] * 100));
            boxes[3].setValueSilently(Math.round(hsv[2] * 100));
        } else {
            int r = (currentColor >> 16) & 0xFF;
            int g = (currentColor >> 8) & 0xFF;
            int b = currentColor & 0xFF;
            boxes[1].setValueSilently(r);
            boxes[2].setValueSilently(g);
            boxes[3].setValueSilently(b);
        }
    }

    public void setColor(int rgb) {
        setColorSilently(rgb);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, Component.translatable("gui." + MOD_ID + ".color_picker"));
    }


    // ================== 纹理生成 ==================
    private void updateBorderTexture() {
        DynamicTexture tex = new DynamicTexture(outerSize, outerSize, false);
        NativeImage img = tex.getPixels();
        if (img == null) return;

        float totalLen = 4f * pathLength;   // 中轴线总周长
        int centerLineRadius = BORDER_THICKNESS / 2;
        int ax = centerLineRadius, ay = centerLineRadius;
        int bx = outerSize - centerLineRadius, by = centerLineRadius;
        int cx = outerSize - centerLineRadius, cy = outerSize - centerLineRadius;
        int dx = centerLineRadius, dy = outerSize - centerLineRadius;

        for (int y = 0; y < outerSize; y++) {
            for (int x = 0; x < outerSize; x++) {
                // 跳过内部 SV 方块区域（包括间隙）
                if (x >= BORDER_THICKNESS && x < outerSize - BORDER_THICKNESS &&
                        y >= BORDER_THICKNESS && y < outerSize - BORDER_THICKNESS) {
                    img.setPixelRGBA(x, y, 0);
                    continue;
                }
                float t = getProjectedT(x, y, ax, ay, bx, by, cx, cy, dx, dy, pathLength);
                if (t < 0) continue;
                int rgb = ColorUtils.hsvToRgb(t / totalLen, 1f, 1f);
                int abgr = ColorUtils.argbToAbgr(rgb);
                img.setPixelRGBA(x, y, abgr);
            }
        }

        tex.upload();
        if (borderTexture != null) {
            Minecraft.getInstance().getTextureManager().release(borderTexture);
        }
        borderTexture = Minecraft.getInstance().getTextureManager().register("color_picker_border", tex);
    }
    private float getProjectedT(int px, int py,
                                int ax, int ay, int bx, int by,
                                int cx, int cy, int dx, int dy,
                                int sideLen) {
        float t1 = projectOnSegment(px, py, ax, ay, bx, by, 0, sideLen);
        float t2 = projectOnSegment(px, py, bx, by, cx, cy, sideLen, 2 * sideLen);
        float t3 = projectOnSegment(px, py, cx, cy, dx, dy, 2 * sideLen, 3 * sideLen);
        float t4 = projectOnSegment(px, py, dx, dy, ax, ay, 3 * sideLen, 4 * sideLen);

        float d1 = distToSegmentSq(px, py, ax, ay, bx, by);
        float d2 = distToSegmentSq(px, py, bx, by, cx, cy);
        float d3 = distToSegmentSq(px, py, cx, cy, dx, dy);
        float d4 = distToSegmentSq(px, py, dx, dy, ax, ay);

        float minDist = Math.min(Math.min(d1, d2), Math.min(d3, d4));
        if (minDist == d1) return t1;
        if (minDist == d2) return t2;
        if (minDist == d3) return t3;
        return t4;
    }

    private void updateSVSquareTexture() {
        DynamicTexture tex = new DynamicTexture(svSize, svSize, false);
        NativeImage img = tex.getPixels();
        if (img != null) {
            for (int i = 0; i < svSize; i++) {
                for (int j = 0; j < svSize; j++) {
                    float s = i / (float) svSize;
                    float v = 1f - j / (float) svSize;
                    int rgb = ColorUtils.hsvToRgb(hsv[0], s, v);
                    int abgr = ColorUtils.argbToAbgr(rgb);
                    img.setPixelRGBA(i, j, abgr);
                }
            }
            tex.upload();
            if (squareTexture != null) {
                Minecraft.getInstance().getTextureManager().release(squareTexture);
            }
            squareTexture = Minecraft.getInstance().getTextureManager().register("color_picker_sv_square", tex);
        }
    }

    // ================== 透明度条纹理（顶部透明 -> 底部不透明） ==================
    private void updateAlphaBarTexture() {
        DynamicTexture tex = new DynamicTexture(ALPHA_BAR_WIDTH, outerSize, false);
        NativeImage img = tex.getPixels();
        if (img != null) {
            // 使用当前颜色（不含alpha）作为底色，预览当前颜色在不同透明度下的效果
            int baseRgb = currentColor & 0x00FFFFFF;
            for (int y = 0; y < outerSize; y++) {
                // y=0(顶)=透明(a=0)，y=outerSize-1(底)=不透明(a=255)
                int a = Mth.clamp(Math.round(y * 255f / (outerSize - 1)), 0, 255);
                int color = (a << 24) | baseRgb;       // 当前颜色+对应alpha
                int abgr = ColorUtils.argbToAbgr(color);
                for (int x = 0; x < ALPHA_BAR_WIDTH; x++) {
                    img.setPixelRGBA(x, y, abgr);
                }
            }
            tex.upload();
            if (alphaBarTexture != null) {
                Minecraft.getInstance().getTextureManager().release(alphaBarTexture);
            }
            alphaBarTexture = Minecraft.getInstance().getTextureManager().register("color_picker_alpha_bar", tex);
        }
    }

    // ================== 色相光标位置（基于中轴线） ==================
    private int[] getCursorPosFromHue() {
        float totalLen = 4f * pathLength;
        float pos = hsv[0] * totalLen;
        pos = Mth.clamp(pos, 0f, totalLen - 1e-4f);
        int centerLineRadius = BORDER_THICKNESS / 2;
        int startX = getPickerX() + centerLineRadius;
        int startY = getY() + centerLineRadius;
        int endX = getPickerX() + outerSize - centerLineRadius;
        int endY = getY() + outerSize - centerLineRadius;

        int x, y;
        if (pos < pathLength) {
            x = startX + Math.round(pos);
            y = startY;
        } else if (pos < 2 * pathLength) {
            x = endX;
            y = startY + Math.round(pos - pathLength);
        } else if (pos < 3 * pathLength) {
            x = endX - Math.round(pos - 2 * pathLength);
            y = endY;
        } else {
            x = startX;
            y = endY - Math.round(pos - 3 * pathLength);
        }
        return new int[]{x, y};
    }

    // ================== 鼠标坐标到色相参数（连续投影） ==================
    private float getPosFromMouse(double mouseX, double mouseY) {
        // 转换为相对于调色板区域左上角的局部坐标
        int px = (int) (mouseX - getPickerX());
        int py = (int) (mouseY - getY());
        if (px < 0 || px >= outerSize || py < 0 || py >= outerSize) return -1;

        // 排除内部区域（边框内部的大矩形，包括间隙和SV方块），防止SV区域影响色相
        if (px >= BORDER_THICKNESS && px <= outerSize - BORDER_THICKNESS &&
                py >= BORDER_THICKNESS && py <= outerSize - BORDER_THICKNESS) {
            return -1;
        }
        int centerLineRadius = BORDER_THICKNESS / 2;
        int ax = centerLineRadius, ay = centerLineRadius;
        int bx = outerSize - centerLineRadius, by = centerLineRadius;
        int cx = outerSize - centerLineRadius, cy = outerSize - centerLineRadius;
        int dx = centerLineRadius, dy = outerSize - centerLineRadius;

        float t1 = projectOnSegment(px, py, ax, ay, bx, by, 0, pathLength);
        float t2 = projectOnSegment(px, py, bx, by, cx, cy, pathLength, 2 * pathLength);
        float t3 = projectOnSegment(px, py, cx, cy, dx, dy, 2 * pathLength, 3 * pathLength);
        float t4 = projectOnSegment(px, py, dx, dy, ax, ay, 3 * pathLength, 4 * pathLength);

        float d1 = distToSegmentSq(px, py, ax, ay, bx, by);
        float d2 = distToSegmentSq(px, py, bx, by, cx, cy);
        float d3 = distToSegmentSq(px, py, cx, cy, dx, dy);
        float d4 = distToSegmentSq(px, py, dx, dy, ax, ay);

        float minDist = Math.min(Math.min(d1, d2), Math.min(d3, d4));
        if (minDist == d1) return t1;
        if (minDist == d2) return t2;
        if (minDist == d3) return t3;
        return t4;
    }


    // ================== 辅助方法（透明度条） ==================

    /**
     * 调色板区域的 X 坐标（透明度条右侧）
     */
    private int getPickerX() {
        return getX() + ALPHA_BAR_WIDTH + GAP;
    }

    /**
     * 将 alpha 合并到 rgb 中，返回 0xAARRGGBB
     */
    private int withAlpha(int rgb) {
        return (alpha << 24) | (rgb & 0x00FFFFFF);
    }

    /**
     * 鼠标是否悬停在透明度条区域
     */
    private boolean isOverAlphaBar(double mouseX, double mouseY) {
        return mouseX >= getX() && mouseX < getX() + ALPHA_BAR_WIDTH
                && mouseY >= getY() && mouseY < getY() + outerSize;
    }

    /**
     * 根据鼠标在透明度条上的位置设置 alpha 值
     */
    private void setAlphaFromMouse(double mouseY) {
        int relY = (int) (mouseY - getY());
        // 纹理：顶部(relY=0)=透明(a=0)，底部(relY=outerSize-1)=不透明(a=255)
        alpha = Mth.clamp(Math.round(relY * 255f / (outerSize - 1)), 0, 255);
        currentColor = withAlpha(currentColor);
        updateColorBox();
        onColorChange.accept(currentColor);
    }

    // 辅助：点到线段的投影参数（参数范围映射到 [tStart, tEnd]）
    private float projectOnSegment(int px, int py, int x1, int y1, int x2, int y2, float tStart, float tEnd) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float len2 = dx * dx + dy * dy;
        if (len2 == 0) return tStart;
        float t = ((px - x1) * dx + (py - y1) * dy) / len2;
        t = Mth.clamp(t, 0f, 1f);
        return tStart + t * (tEnd - tStart);
    }

    // 辅助：点到线段的距离平方
    private float distToSegmentSq(int px, int py, int x1, int y1, int x2, int y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float len2 = dx * dx + dy * dy;
        if (len2 == 0) return (px - x1) * (px - x1) + (py - y1) * (py - y1);
        float t = ((px - x1) * dx + (py - y1) * dy) / len2;
        if (t < 0) t = 0;
        else if (t > 1) t = 1;
        float projX = x1 + t * dx;
        float projY = y1 + t * dy;
        float dxp = px - projX;
        float dyp = py - projY;
        return dxp * dxp + dyp * dyp;
    }
}

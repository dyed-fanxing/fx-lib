package com.fanxing.lib.client.gui.component;

import com.fanxing.lib.client.gui.ColorMode;
import com.fanxing.lib.util.ColorUtils;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * 颜色选择器：包含连续正方形色相边框、SV 方块、RGB/HSV 输入框、模式切换。
 * 色相边框基于中轴线（厚度中心线）映射 0°~360°，指针严格沿中轴线移动，
 * 边框纹理采用投影算法保证角部色彩平滑过渡，完全连续无突变。
 */
public class ColorPicker extends AbstractWidget {
    protected static final int GAP = 5;
    protected static final int BOX_HEIGHT = 20;
    protected static final int BORDER_THICKNESS = 20;      // 边框厚度
    protected static final int MIN_SV_SIZE = 100;
    protected static final int MIN_OUTER_SIZE = MIN_SV_SIZE + 2 * BORDER_THICKNESS + 2 * GAP;

    protected int svSize;               // SV 方块边长
    protected int outerSize;            // 外正方形边长 = svSize + 2*(BORDER_THICKNESS+INNER_GAP)
    protected int pathLength;           // 中轴线正方形边长 = outerSize - BORDER_THICKNESS

    protected float[] hsv = new float[]{0f, 1f, 1f};
    protected int currentColor;
    protected final Consumer<Integer> onColorChange;

    protected ResourceLocation borderTexture;   // 整体色相边框纹理
    protected ResourceLocation squareTexture;   // SV 方块纹理
    protected final IntegerEditBox[] boxes = new IntegerEditBox[3];
    protected final CycleButton<ColorMode> mode;

    public ColorPicker(int x, int y, int width, int height, int init, Consumer<Integer> onColorChange) {
        super(x, y, width, height, Component.empty());
        this.onColorChange = onColorChange;
        this.currentColor = init;

        outerSize = Math.max(MIN_OUTER_SIZE, width);
        int neededHeight = outerSize + BOX_HEIGHT + GAP;
        if (neededHeight > height) setHeight(neededHeight);
        svSize = outerSize - 2 * (BORDER_THICKNESS + GAP);
        pathLength = outerSize - BORDER_THICKNESS;

        Font font = Minecraft.getInstance().font;
        int modeWidth = font.width(ColorMode.HSV.name()) + 10;

        for (int i = 0; i < 3; i++) {
            boxes[i] = new IntegerEditBox(font, 0, 0, 0, BOX_HEIGHT, Component.empty(),
                    0, ColorMode.HSV.max[i], 1, init, v -> updateFromBoxInput())
                    .wrap(i == 0);
        }
        mode = CycleButton.<ColorMode>builder(mode -> Component.literal(mode.name()))
                .withValues(ColorMode.values())
                .withInitialValue(ColorMode.HSV)
                .displayOnlyValue()
                .create(0, 0, modeWidth, BOX_HEIGHT, Component.empty(),
                        (btn, mode) -> updateMode());

        updateBorderTexture();
        updateSVSquareTexture();
        setColorSilently(init);
        updateMode();
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        int totalWidth = outerSize;
        int modeWidth = mode.getWidth();   // 使用当前实际宽度（可能已变化）
        int boxWidth = (totalWidth - modeWidth - 3 * GAP) / 3;
        int usedWidth = 3 * boxWidth + modeWidth + 3 * GAP;
        int extra = totalWidth - usedWidth;
        int lastGap = GAP + extra;
        int curX = x;
        for (int i = 0; i < 3; i++) {
            int gap = (i == 2) ? lastGap : GAP;
            boxes[i].setX(curX);
            boxes[i].setWidth(boxWidth);
            curX += boxWidth + gap;
        }
        mode.setX(curX);
        mode.setWidth(modeWidth); // 宽度保持不变
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        int bottomBarY = y + outerSize + GAP;
        for (int i = 0; i < 3; i++) {
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
        RenderSystem.enableBlend();
        if (borderTexture != null) {
            graphics.blit(borderTexture, getX(), getY(), 0, 0, outerSize, outerSize, outerSize, outerSize);
        }
        if (squareTexture != null) {
            graphics.blit(squareTexture,
                    getX() + BORDER_THICKNESS + GAP,
                    getY() + BORDER_THICKNESS + GAP,
                    0, 0, svSize, svSize, svSize, svSize);
        }
        RenderSystem.disableBlend();
        // SV 光标
        int svX = getX() + BORDER_THICKNESS + GAP;
        int svY = getY() + BORDER_THICKNESS + GAP;
        int svCursorX = svX + (int) (hsv[1] * svSize);
        int svCursorY = svY + (int) ((1f - hsv[2]) * svSize);
        graphics.renderOutline(svCursorX - 2, svCursorY - 2, 4, 4, 0xFFFFFFFF);
        // 色相光标（沿中轴线）
        int[] cursor = getCursorPosFromHue();
        graphics.renderOutline(cursor[0] - 2, cursor[1] - 2, 4, 4, 0xFFFFFFFF);
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
        float pos = getPosFromMouse(mouseX, mouseY);
        if (pos >= 0) {
            float t = pos / (4f * pathLength);
            t = Mth.clamp(t, 0f, 0.9999f);
            hsv[0] = t;
            currentColor = ColorUtils.hsvToRgb(hsv);
            updateColorBox();
            updateSVSquareTexture();
            onColorChange.accept(currentColor);
        } else {
            int svX = getX() + BORDER_THICKNESS + GAP;
            int svY = getY() + BORDER_THICKNESS + GAP;
            if (mouseX >= svX && mouseX < svX + svSize && mouseY >= svY && mouseY < svY + svSize) {
                hsv[1] = (float) Mth.clamp((mouseX - svX) / svSize, 0, 1);
                hsv[2] = 1f - (float) Mth.clamp((mouseY - svY) / svSize, 0, 1);
                currentColor = ColorUtils.hsvToRgb(hsv);
                updateColorBox();
                updateSVSquareTexture();
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
            boxes[i].setMessage(Component.literal(mode.name()));
            boxes[i].rangeSilently(0, mode.max[i], newValues[i]);
            boxes[i].wrap(mode == ColorMode.HSV && i == 0);
        }
    }

    private void setColorSilently(int rgb) {
        currentColor = rgb;
        hsv = ColorUtils.rgbToHsv(rgb);
        updateColorBox();
        updateSVSquareTexture();
    }

    private void updateFromBoxInput() {
        int[] vals = new int[3];
        for (int i = 0; i < 3; i++) {
            vals[i] = boxes[i].getIntValue();
        }
        currentColor = (vals[0] << 16) | (vals[1] << 8) | vals[2];
        if (mode.getValue() == ColorMode.RGB) hsv = ColorUtils.rgbToHsv(vals[0], vals[1], vals[2]);
        else {
            float h = vals[0] / 360f, s = vals[1] / 100f, v = vals[2] / 100f;
            currentColor = ColorUtils.hsvToRgb(h, s, v);
            hsv[0] = h;
            hsv[1] = s;
            hsv[2] = v;
        }
        updateSVSquareTexture();
        onColorChange.accept(currentColor);
    }

    private void updateColorBox() {
        if (mode.getValue() == ColorMode.HSV) {
            boxes[0].setValueSilently(Math.round(hsv[0] * 360));
            boxes[1].setValueSilently(Math.round(hsv[1] * 100));
            boxes[2].setValueSilently(Math.round(hsv[2] * 100));
        } else {
            int r = (currentColor >> 16) & 0xFF;
            int g = (currentColor >> 8) & 0xFF;
            int b = currentColor & 0xFF;
            boxes[0].setValueSilently(r);
            boxes[1].setValueSilently(g);
            boxes[2].setValueSilently(b);
        }
    }

    public void setColor(int rgb) {
        setColorSilently(rgb);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, Component.translatable("gui.fx_lib.color_picker"));
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
                int abgr = ColorUtils.argbToAbgr(0xFF000000 | rgb);
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
                    int abgr = ColorUtils.argbToAbgr(0xFF000000 | rgb);
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

    // ================== 色相光标位置（基于中轴线） ==================
    private int[] getCursorPosFromHue() {
        float totalLen = 4f * pathLength;
        float pos = hsv[0] * totalLen;
        pos = Mth.clamp(pos, 0f, totalLen - 1e-4f);
        int centerLineRadius = BORDER_THICKNESS / 2;
        int startX = getX() + centerLineRadius;
        int startY = getY() + centerLineRadius;
        int endX = getX() + outerSize - centerLineRadius;
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
        // 转换为相对于纹理左上角的局部坐标
        int px = (int) (mouseX - getX());
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
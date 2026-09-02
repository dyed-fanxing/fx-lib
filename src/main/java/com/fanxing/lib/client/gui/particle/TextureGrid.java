package com.fanxing.lib.client.gui.particle;

import com.fanxing.lib.client.gui.Scheme;
import com.fanxing.lib.client.gui.component.container.GridContainer;
import com.fanxing.lib.client.gui.particle.screen.TextureViewPopupScreen;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetTooltipHolder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TextureGrid extends GridContainer<TextureGrid.TextureCell> {
    private final Consumer<ResourceLocation> onSelect;
    protected int textureSize = 64;
    protected static final int PADDING = 4;
    protected static final int TEXT_LINE_HEIGHT = 8;
    protected static final int TEXT_LINE_SPACING = 3;  // 行间距
    protected static final int SPACING = 4; // 纹理与文本区域之间的额外边距
    public TextureGrid(int x, int y, int width, int height, Consumer<ResourceLocation> onSelect) {
        super(x, y, width, height, Component.empty());
        this.onSelect = onSelect;
    }

    public void updateTextures(List<ResourceLocation> textures) {
        List<TextureCell> newCells = new ArrayList<>();
        for (ResourceLocation loc : textures) {
            newCells.add(new TextureCell(loc));
        }
        replaceCells(newCells);
    }
    public void arrange(){
        setWidth(getWidth());
        setHeight(getHeight());
        setX(getX());
        setY(getY());
    }

    protected int getTextAreaHeight() {
        return 4 * TEXT_LINE_HEIGHT + 3 * TEXT_LINE_SPACING;
    }
    @Override
    protected int getCellWidth() { return PADDING + textureSize + PADDING; }
    @Override
    protected int getCellHeight() {
        return PADDING + textureSize + SPACING + getTextAreaHeight() + PADDING;
    }
    @Override
    protected int getSpacing() { return SPACING; }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (Screen.hasControlDown()) {
            int delta = scrollY > 0 ? 4 : -4;
            int newSize = Mth.clamp(textureSize + delta, 16, 256);
            if (newSize != textureSize) {
                textureSize = newSize;
                setWidth(getWidth());
                setHeight(getHeight());
                setX(getX());
                setY(getY());
            }
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    public void select(ResourceLocation texture) {
        for (TextureCell cell : cells) {
            if (cell.texture.equals(texture)) {
                setFocused(cell);
                return;
            }
        }
    }

    public class TextureCell extends Cell {
        private final ResourceLocation texture;
        private final String fileName;
        private final WidgetTooltipHolder tooltip = new WidgetTooltipHolder();
        private int imgWidth = 0, imgHeight = 0;

        public TextureCell(ResourceLocation texture) {
            this.texture = texture;
            String path = texture.getPath();
            this.fileName = path.substring(path.lastIndexOf('/') + 1);
            loadImageSize(); // 读取分辨率
        }

        private void loadImageSize() {
            var optResource = Minecraft.getInstance().getResourceManager().getResource(texture);
            if (optResource.isPresent()) {
                try (InputStream inputStream = optResource.get().open();
                     NativeImage nativeImage = NativeImage.read(inputStream)) {
                    this.imgWidth = nativeImage.getWidth();
                    this.imgHeight = nativeImage.getHeight();
                } catch (IOException e) {
                    e.printStackTrace();
                    this.imgWidth = this.imgHeight = -1;
                }
            } else {
                this.imgWidth = this.imgHeight = -1;
            }
        }

        @Override
        public void render(GuiGraphics graphics, int index, int width, int height, int top, int left, int right, int bottom,
                           int mouseX, int mouseY, boolean isHovered, boolean isSelected, float partialTick) {
            Font font = Minecraft.getInstance().font;
            if (isHovered || isSelected) {
                graphics.renderOutline(left, top, width, height, Scheme.BORDER_COLOR_SELECTED);
            }
            int centerX = left + width / 2;
            int iconX = centerX - textureSize / 2;
            int iconY = top + PADDING;
            RenderSystem.enableBlend();
            graphics.blit(texture, iconX, iconY, 0, 0, textureSize, textureSize, textureSize, textureSize);
            RenderSystem.disableBlend();
            int textTop = top + PADDING + textureSize + SPACING;
            int lineHeight = TEXT_LINE_HEIGHT;
            int spacing = TEXT_LINE_SPACING;
            int maxTextWidth = width - 4;

            // 文本区域,从区域顶部开始
            List<String> lines = wrapText(fileName, font, maxTextWidth);
            for (int i = 0; i < lines.size(); i++) {
                int lineY = textTop + i * (lineHeight + spacing);
                graphics.drawString(font, lines.get(i), centerX - font.width(lines.get(i)) / 2, lineY, 0xFFFFFF);
            }
            // 分辨率，固定第四行
            String resolution = (imgWidth > 0 && imgHeight > 0) ? (imgWidth + " x " + imgHeight) : "? x ?";
            int resY = textTop + 3 * (lineHeight + spacing);
            graphics.drawString(font, resolution, centerX - font.width(resolution) / 2, resY, 0xAAAAAA);

            // Tooltip：仅当文件名被截断时显示完整文件名
            boolean fileNameClipped = fileName.length() != lines.stream().mapToInt(String::length).sum();
            if (fileNameClipped && isHovered) {
                tooltip.set(Tooltip.create(Component.literal(fileName)));
                tooltip.refreshTooltipForNextRenderPass(true, isFocused(), getRectangle());
            } else tooltip.set(null);
        }

        private List<String> wrapText(String text, Font font, int maxWidth) {
            List<String> lines = new ArrayList<>();
            String remaining = text;
            while (!remaining.isEmpty() && lines.size() < 3) {
                String line = font.plainSubstrByWidth(remaining, maxWidth);
                if (line.isEmpty()) break;
                lines.add(line);
                remaining = remaining.substring(line.length());
            }
            if (!remaining.isEmpty() && lines.size() == 3) {
                String lastLine = lines.getLast();
                String truncated = font.plainSubstrByWidth(lastLine + "...", maxWidth);
                lines.set(lines.size() - 1, truncated);
            }
            return lines;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (Screen.hasControlDown() && button == 0) {
                Minecraft.getInstance().setScreen(new TextureViewPopupScreen(texture, Minecraft.getInstance().screen));
                return true;
            }
            if (button == 0) {
                onSelect.accept(texture);
                return true;
            }
            return false;
        }

        @Override
        public Component getNarration() {
            return Component.literal(fileName);
        }

        @Override
        public String toString() {
            return "TextureCell{" + "texture=" + texture + ", fileName='" + fileName + '\'' + '}';
        }
    }
}
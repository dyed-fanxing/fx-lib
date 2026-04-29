package com.fanxing.lib.client.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public abstract class EntityFreePreviewScreen extends Screen {
    protected final LivingEntity entity;
    protected int previewX, previewY, previewWidth, previewHeight;
    protected float zoom = 100F;

    public EntityFreePreviewScreen(Component title, LivingEntity entity) {
        super(title);
        this.entity = entity;
    }

    @Override
    protected void init() {
        super.init();
        int widthGap = this.width/8;
        previewX = widthGap*3;
        previewY = 0;
        previewWidth = widthGap*2;
        previewHeight = height;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        InventoryScreen.renderEntityInInventoryFollowsMouse(
                graphics,
                previewX, previewY,
                previewX + previewWidth, previewY + previewHeight,
                (int) zoom, 0.0625F,
                mouseX, mouseY,
                entity
        );
    }


    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (mouseX >= previewX && mouseX <= previewX + previewWidth &&
                mouseY >= previewY && mouseY <= previewY + previewHeight) {
            zoom += (float) scrollY;
            zoom = Mth.clamp(zoom, 10F, 100F);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }
}
package com.fanxing.lib.client.gui.component.entry;

import com.fanxing.lib.client.gui.layout.FlexBoxLayout;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 表单项 Entry：左侧固定宽度 FlexBoxLayout，右侧弹性宽度 FlexBoxLayout。
 * 左右内容通过 Consumer 函数在构造时定义。
 */
public class FlexEntry extends ContainerObjectSelectionList.Entry<FlexEntry> {
    private static final Logger log = LoggerFactory.getLogger(FlexEntry.class);
    private FlexBoxLayout flexLayout;

    public FlexEntry(FlexBoxLayout flexLayout) {
        this.flexLayout = flexLayout;
    }

    /** 替换右侧布局（用于动态切换内容） */
    public void setRightLayout(FlexBoxLayout flexLayout) {
        this.flexLayout = flexLayout;
    }

    public FlexBoxLayout getFlexLayout() {
        return flexLayout;
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return flexLayout.getGuiEventListeners();
    }



    @Override
    public void render(@NotNull GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
        boolean flag = false;
        if(flexLayout.getX() != left) {
            flexLayout.setX(left);
            flag = true;
        }
        if(flexLayout.getY() != top){
            flexLayout.setY(top);
            flag = true;
        }
        if(flexLayout.getWidth() != width) {
            flexLayout.setWidth(width);
            flag = true;
        }
        if(flexLayout.getHeight() != height) {
            flexLayout.setHeight(height);
            flag = true;
        }
        if(flag) {
            flexLayout.arrangeElements();
            log.info("flexLayout,width={},x:{}", flexLayout.getWidth(),flexLayout.getX());
            for (FlexBoxLayout.FlexItem child : flexLayout.getChildren()) {
                log.info("editBox,width={},x:{}", child.child().getWidth(),child.child().getX());
            }
        }
        flexLayout.visitWidgets(widget -> widget.render(graphics, mouseX, mouseY, partialTick));
    }


    @Override
    public @NotNull List<? extends NarratableEntry> narratables() {
        List<NarratableEntry> list = new ArrayList<>();
        for (GuiEventListener c : flexLayout.getGuiEventListeners()) {
            if (c instanceof NarratableEntry n) list.add(n);
        }
        return list;
    }
}
package com.fanxing.lib.client.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * 通用单行文本输入屏幕，支持自定义提示文本和输入框占位符。
 */
public class SimpleEditBoxScreen extends Screen {
    private final Screen parent;
    private final Component prompt;          // 输入框上方的提示文字
    private final Component hint;       // 输入框内部的占位符（可选）
    private final Consumer<String> onConfirm;
    private EditBox inputField;

    /**
     * 构造一个输入屏幕（无占位符）
     *
     * @param parent    父屏幕，取消或确认后返回
     * @param prompt    显示在输入框上方的提示文本
     * @param onConfirm 确认回调，参数为用户输入的字符串
     */
    public SimpleEditBoxScreen(Screen parent, Component prompt, Consumer<String> onConfirm) {
        this(parent, prompt, null, onConfirm);
    }

    /**
     * 构造一个输入屏幕（可指定输入框占位符）
     *
     * @param parent    父屏幕
     * @param prompt    显示在输入框上方的提示文本
     * @param hint 输入框为空时显示的占位提示（可为 null）
     * @param onConfirm 确认回调
     */
    public SimpleEditBoxScreen(Screen parent, Component prompt, Component hint, Consumer<String> onConfirm) {
        super(Component.empty());
        this.parent = parent;
        this.prompt = prompt;
        this.hint = hint;
        this.onConfirm = onConfirm;
    }

    @Override
    protected void init() {
        int centerX = width / 2;
        int centerY = height / 2;
        int fieldWidth = 200;
        int fieldHeight = 20;
        int buttonWidth = 80;

        // 输入框（初始内容为空）
        inputField = new EditBox(font, centerX - fieldWidth / 2, centerY - 30, fieldWidth, fieldHeight, Component.empty());
        inputField.setMaxLength(16);
        inputField.setHint(hint);   // 设置占位符提示
        addRenderableWidget(inputField);

        // 确认按钮
        Button confirmBtn = Button.builder(Component.translatable("gui.done"), btn -> {
            String value = inputField.getValue().trim();
            if (!value.isEmpty()) {
                onConfirm.accept(value);
                onClose();
            }
        }).size(buttonWidth, 20).build();
        confirmBtn.setPosition(centerX - buttonWidth - 10, centerY + 10);
        addRenderableWidget(confirmBtn);

        // 取消按钮
        Button cancelBtn = Button.builder(Component.translatable("gui.cancel"), btn -> onClose())
                .size(buttonWidth, 20).build();
        cancelBtn.setPosition(centerX + 10, centerY + 10);
        addRenderableWidget(cancelBtn);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // 先绘制父屏幕作为底层背景
        this.parent.render(graphics, mouseX, mouseY, partialTick);
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        // 绘制提示文本
        graphics.drawCenteredString(font, prompt, width / 2, height / 2 - 60, 0xFFFFFF);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // 按回车键触发确认
        if (keyCode == 257 || keyCode == 335) {
            String value = inputField.getValue().trim();
            if (!value.isEmpty()) {
                onConfirm.accept(value);
                onClose();
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
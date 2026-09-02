package com.fanxing.lib.mixin;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.checkerframework.checker.units.qual.A;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Mixin(EditBox.class)
public interface EditBoxAccessor {
    // 已有的
//    @Accessor("responder")
//    Consumer<String> responder();
    @Accessor("isEditable")
    boolean isEditable();
    @Accessor("hint")
    Component getHint();
    @Accessor("font")
    Font getFont();

    // 新增渲染所需
//    @Accessor("value")
//    String getValue();
//    @Accessor("cursorPos")
//    int getCursorPos();
    @Accessor("displayPos")
    int getDisplayPos();
    @Accessor("highlightPos")
    int getHighlightPos();
    @Accessor("bordered")
    boolean isBordered();
    @Accessor("textColor")
    int getTextColor();
    @Accessor("textColorUneditable")
    int getTextColorUneditable();
    @Accessor("suggestion")
    String getSuggestion();
    @Accessor("formatter")
    BiFunction<String, Integer, FormattedCharSequence> getFormatter();
    @Accessor("textShadow")
    boolean isTextShadow();
    @Accessor("focusedTime")
    long getFocusedTime();
//    @Accessor("maxLength")
//    int getMaxLength();
    @Accessor("canLoseFocus")
    boolean canLoseFocus();
//    @Accessor("filter")
//    Predicate<String> getFilter();



//    @Invoker("onValueChange")
//    void onValueChange(String text);
}
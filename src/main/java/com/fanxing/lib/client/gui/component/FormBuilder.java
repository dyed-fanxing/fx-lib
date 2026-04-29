package com.fanxing.lib.client.gui.component;

import com.fanxing.lib.client.gui.Align;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class FormBuilder {
    private final Font font;    // 字体
    private final int width;    // 表单宽度/单个LabeledInput最大宽度
    private final int height;   // 表单高度/单个LabeledInput最大高度
    private final int labelWidth;   // 表单高度/单个LabeledInput最大高度
    private final int inputWidth;   // 表单高度/单个LabeledInput最大高度
    private final int gap;      // 单个LabeledInput间距
    private final List<Component> labels = new ArrayList<>();
    private final List<EditBox> inputs = new ArrayList<>();
    private Align mode = Align.CENTER;

    public FormBuilder(Font font, int width, int labelWidth,int inputWidth, int height, int gap) {
        this.font = font;
        this.width = width;
        this.labelWidth = labelWidth;
        this.inputWidth = inputWidth;
        this.height = height;
        this.gap = gap;
    }

    public FormBuilder addRow(Component label, EditBox input) {
        labels.add(label);
        inputs.add(input);
        return this;
    }

    public FormBuilder mode(Align mode) {
        this.mode = mode;
        return this;
    }

    // 关键：返回一个垂直布局，内部已包含所有 LabelInput
    public LinearLayout buildLayout() {
        LinearLayout layout = LinearLayout.vertical().spacing(gap);
        layout.defaultCellSetting().alignHorizontallyLeft(); // 整体左对齐，具体 x 由外部 setPosition 决定
        for (int i = 0; i < labels.size(); i++) {
            // 注意：LabelInput 的构造函数需要 (x, y, width, labelWidth, height, gap, label, inputBox, mode)
            // 这里的 x, y 先传 0，因为布局会通过 setX/setY 重新定位
            LabelEditBox li = new LabelEditBox(
                    font, 0, 0, width, labelWidth,inputWidth, height, gap,
                    labels.get(i), inputs.get(i), mode
            );
            layout.addChild(li);
        }
        return layout;
    }
}
package com.fanxing.lib.client.gui.particle;

import com.fanxing.lib.client.gui.component.DoubleEditBox;
import com.fanxing.lib.client.gui.component.Dropdown;
import com.fanxing.lib.client.gui.component.entry.BaseContainerEntry;
import com.fanxing.lib.client.gui.particle.entry.LabelMultiWidgetEntry;
import com.fanxing.lib.client.gui.particle.entry.LabelWidgetEntry;
import com.fanxing.lib.client.gui.particle.screen.ParticleEditorScreen;
import com.fanxing.lib.client.gui.screen.PopupManageScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MeshVertexTabList extends PropertyTabList<BaseContainerEntry> {

    private static final Logger log = LoggerFactory.getLogger(MeshVertexTabList.class);
    private final Font font = Minecraft.getInstance().font;

    public MeshVertexTabList(int width, int height, int y, int itemHeight, ParticleEditorScreen particleEditor) {
        super(width, height, y, itemHeight,particleEditor);
        this.particleEditor = particleEditor;
        addMeshVertexRows();
    }

    private void addMeshVertexRows() {
        // 网格类型（下拉）
        addDropdownRow("gui.fx_lib.mesh_vertex.mesh_type",80,
                Arrays.asList("sprite", "ribbon", "trail", "ring", "cube", "sphere", "cylinder", "capsule", "cone"));

        // 网格参数（三个浮点数，用于尺寸/半径/分段等，实际应根据类型动态变化）
        addNumberTripleRow("gui.fx_lib.mesh_vertex.mesh_param",
                "gui.fx_lib.mesh_vertex.param_x",
                "gui.fx_lib.mesh_vertex.param_y",
                "gui.fx_lib.mesh_vertex.param_z");

        // 整体顶点颜色（颜色选择器，暂用文本框）
        addTextRow("gui.fx_lib.mesh_vertex.vertex_color_global");

        // 顶点颜色（逐顶点，复杂，暂用文本框占位）
        addTextRow("gui.fx_lib.mesh_vertex.vertex_color_per_vertex");
        // 剔除
        addDropdownRow("gui.fx_lib.mesh_vertex.cull",80, Arrays.asList("front", "back", "both"));
    }

    private void addDropdownRow(String labelKey,int dropdownWidth, List<String> options) {
        StringWidget label = new StringWidget(labelWidth(), 0, Component.translatable(labelKey), font);
        label.alignLeft();

        Dropdown<String> dropdown = new Dropdown<>(0, 0, dropdownWidth, itemHeight, selected -> {}, particleEditor);
        List<Dropdown.Entry<String>> entries = new ArrayList<>();
        for (String opt : options) {
            String transKey = labelKey + "." + opt;
            entries.add(new Dropdown.Entry<>(Component.translatable(transKey), opt));
        }
        dropdown.list(160, itemHeight, entries, options.getFirst());

        addEntry(new LabelWidgetEntry(label, dropdown));
    }

    private void addNumberTripleRow(String labelKey, String xKey, String yKey, String zKey) {
        StringWidget label = new StringWidget(labelWidth(), 0, Component.translatable(labelKey), font);
        label.alignLeft();

        DoubleEditBox xBox = new DoubleEditBox(0, itemHeight, font, v -> {});
        xBox.setHint(Component.translatable(xKey));
        DoubleEditBox yBox = new DoubleEditBox(0, itemHeight, font, v -> {});
        yBox.setHint(Component.translatable(yKey));
        DoubleEditBox zBox = new DoubleEditBox(0, itemHeight, font, v -> {});
        zBox.setHint(Component.translatable(zKey));

        List<AbstractWidget> widgets = List.of(xBox, yBox, zBox);
        addEntry(new LabelMultiWidgetEntry(label, widgets));
    }

    private void addTextRow(String labelKey) {
        StringWidget label = new StringWidget(labelWidth(), 0, Component.translatable(labelKey), font);
        label.alignLeft();

        DoubleEditBox textField = new DoubleEditBox(0, itemHeight, font, v -> {});
        textField.setHint(Component.translatable(labelKey + ".hint"));
        textField.setWidth(200);

        addEntry(new LabelWidgetEntry(label, textField));
    }
}
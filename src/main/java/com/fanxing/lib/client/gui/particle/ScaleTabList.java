package com.fanxing.lib.client.gui.particle;

import com.fanxing.lib.client.gui.component.Dropdown;
import com.fanxing.lib.client.gui.component.entry.BaseContainerEntry;
import com.fanxing.lib.client.gui.particle.entry.LabelWidgetEntry;
import com.fanxing.lib.client.gui.particle.screen.ParticleEditorScreen;
import com.fanxing.lib.item.compoent.particle.MeshType;
import com.fanxing.lib.item.compoent.particle.ScaleProperty;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.fanxing.lib.FxLib.MOD_ID;

public class ScaleTabList extends PropertyTabList<BaseContainerEntry> {

    private static final Logger log = LoggerFactory.getLogger(ScaleTabList.class);
    protected final Map<String, Pair<ScaleProperty, List<BaseContainerEntry>>> modePropertyEntriesMap = new LinkedHashMap<>();
    protected List<BaseContainerEntry> propertyEntries = new ArrayList<>();

    public static final String MODE_TRANSLATION_KEY = "gui." + MOD_ID + ".scale.mode";
    public static final String PROPERTY_TRANSLATION_KEY_PREFIX_3D = "gui." + MOD_ID + ".scale.";
    public static final String PROPERTY_TRANSLATION_KEY_PREFIX_2D = "gui." + MOD_ID + ".size.";

    public ScaleTabList(int width, int height, int y, int itemHeight, ParticleEditorScreen particleEditor) {
        super( width, height, y, itemHeight,particleEditor);
        addEntry(createModeEntry());
        switchMode(particleEditor.currLayer.scale.mode());
    }
    @Override
    protected int labelWidth() {
        return isMesh2D()?65:55;
    }
    private boolean isMesh2D() {
        return particleEditor.currLayer.mesh instanceof MeshType.Quad;
    }

    private LabelWidgetEntry createModeEntry() {
        String initial = particleEditor.currLayer.scale.mode();
        if (isMesh2D()) {
            modePropertyEntriesMap.put(ScaleProperty.FIXED, build2DFixedRows());
            modePropertyEntriesMap.put(ScaleProperty.ACCELERATION, build2DAccelerationRows());
            modePropertyEntriesMap.put(ScaleProperty.EASE, build2DEaseRows());
        } else {
            modePropertyEntriesMap.put(ScaleProperty.FIXED, build3DFixedRows());
            modePropertyEntriesMap.put(ScaleProperty.ACCELERATION, build3DAccelerationRows());
            modePropertyEntriesMap.put(ScaleProperty.EASE, build3DEaseRows());
        }

        Dropdown<String> dropdown = new Dropdown<>(0, 0, 100, itemHeight, this::switchMode, particleEditor);
        List<Dropdown.Entry<String>> modes = new ArrayList<>();
        for (String mode : modePropertyEntriesMap.keySet()) {
            modes.add(new Dropdown.Entry<>(Component.translatable(MODE_TRANSLATION_KEY + "." + mode), mode));
        }
        dropdown.list(84, itemHeight, modes, initial);
        StringWidget label = new StringWidget(labelWidth(), 0, Component.translatable(MODE_TRANSLATION_KEY), font);
        label.alignLeft();
        log.info("dropdown width:{}",dropdown.getWidth());
        return new LabelWidgetEntry(label, dropdown);
    }

    protected void switchMode(String selected) {
        for (BaseContainerEntry entry : propertyEntries) {
            removeEntry(entry);
        }
        Pair<ScaleProperty, List<BaseContainerEntry>> pair = modePropertyEntriesMap.get(selected);
        particleEditor.currLayer.scale = pair.getLeft();
        propertyEntries = pair.getRight();
        for (BaseContainerEntry entry : propertyEntries) {
            addEntry(entry);
        }
    }

    // ========== 2D 模式（将 X、Y 映射为宽度/高度，Z 忽略） ==========
    private Pair<ScaleProperty, List<BaseContainerEntry>> build2DFixedRows() {
        ScaleProperty.Fixed scale = ScaleProperty.FIXED.equals(particleEditor.currLayer.scale.mode()) && isMesh2D() ?
                (ScaleProperty.Fixed) particleEditor.currLayer.scale : new ScaleProperty.Fixed();
        List<BaseContainerEntry> rows = new ArrayList<>();
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX_2D+"scale_width", scale.scaleX, v -> scale.scaleX = v));
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX_2D+"scale_height", scale.scaleY, v -> scale.scaleY = v));
        return Pair.of(scale, rows);
    }

    private Pair<ScaleProperty, List<BaseContainerEntry>> build2DAccelerationRows() {
        ScaleProperty.Acceleration scale = ScaleProperty.ACCELERATION.equals(particleEditor.currLayer.scale.mode()) && isMesh2D() ?
                (ScaleProperty.Acceleration) particleEditor.currLayer.scale : new ScaleProperty.Acceleration();
        List<BaseContainerEntry> rows = new ArrayList<>();
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX_2D+"start_width", scale.startX, v -> scale.startX = v));
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX_2D+"start_height", scale.startY, v -> scale.startY = v));
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX_2D+"vel_width", scale.velX, v -> scale.velX = v));
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX_2D+"vel_height", scale.velY, v -> scale.velY = v));
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX_2D+"acc_width", scale.accX, v -> scale.accX = v));
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX_2D+"acc_height", scale.accY, v -> scale.accY = v));
        return Pair.of(scale, rows);
    }

    private Pair<ScaleProperty, List<BaseContainerEntry>> build2DEaseRows() {
        ScaleProperty.Ease scale = ScaleProperty.EASE.equals(particleEditor.currLayer.scale.mode()) && isMesh2D() ?
                (ScaleProperty.Ease) particleEditor.currLayer.scale : new ScaleProperty.Ease();
        List<BaseContainerEntry> rows = new ArrayList<>();
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX_2D+"start_width", scale.startX, v -> scale.startX = v));
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX_2D+"start_height", scale.startY, v -> scale.startY = v));
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX_2D+"end_width", scale.endX, v -> scale.endX = v));
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX_2D+"end_height", scale.endY, v -> scale.endY = v));
        rows.add(buildEasePropertyRow(scale.easeCurve, ease -> { scale.easeCurve = ease; particleEditor.rebuildParticle(); }));
        return Pair.of(scale, rows);
    }

    // ========== 3D 模式（标准 X/Y/Z） ==========
    private Pair<ScaleProperty, List<BaseContainerEntry>> build3DFixedRows() {
        ScaleProperty.Fixed scale = ScaleProperty.FIXED.equals(particleEditor.currLayer.scale.mode()) && !isMesh2D() ?
                (ScaleProperty.Fixed) particleEditor.currLayer.scale : new ScaleProperty.Fixed();
        List<BaseContainerEntry> rows = new ArrayList<>();
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX_3D+".scale_x", scale.scaleX, v -> scale.scaleX = v));
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX_3D+".scale_y", scale.scaleY, v -> scale.scaleY = v));
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX_3D+".scale_z", scale.scaleZ, v -> scale.scaleZ = v));
        return Pair.of(scale, rows);
    }

    private Pair<ScaleProperty, List<BaseContainerEntry>> build3DAccelerationRows() {
        ScaleProperty.Acceleration scale = ScaleProperty.ACCELERATION.equals(particleEditor.currLayer.scale.mode()) && !isMesh2D() ?
                (ScaleProperty.Acceleration) particleEditor.currLayer.scale : new ScaleProperty.Acceleration();
        List<BaseContainerEntry> rows = new ArrayList<>();
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX_3D+".start_x", scale.startX, v -> scale.startX = v));
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX_3D+".start_y", scale.startY, v -> scale.startY = v));
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX_3D+".start_z", scale.startZ, v -> scale.startZ = v));
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX_3D+".vel_x", scale.velX, v -> scale.velX = v));
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX_3D+".vel_y", scale.velY, v -> scale.velY = v));
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX_3D+".vel_z", scale.velZ, v -> scale.velZ = v));
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX_3D+".acc_x", scale.accX, v -> scale.accX = v));
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX_3D+".acc_y", scale.accY, v -> scale.accY = v));
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX_3D+".acc_z", scale.accZ, v -> scale.accZ = v));
        return Pair.of(scale, rows);
    }

    private Pair<ScaleProperty, List<BaseContainerEntry>> build3DEaseRows() {
        ScaleProperty.Ease scale = ScaleProperty.EASE.equals(particleEditor.currLayer.scale.mode()) && !isMesh2D() ?
                (ScaleProperty.Ease) particleEditor.currLayer.scale : new ScaleProperty.Ease();
        List<BaseContainerEntry> rows = new ArrayList<>();
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX_3D+".start_x", scale.startX, v -> scale.startX = v));
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX_3D+".start_y", scale.startY, v -> scale.startY = v));
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX_3D+".start_z", scale.startZ, v -> scale.startZ = v));
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX_3D+".end_x", scale.endX, v -> scale.endX = v));
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX_3D+".end_y", scale.endY, v -> scale.endY = v));
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX_3D+".end_z", scale.endZ, v -> scale.endZ = v));
        rows.add(buildEasePropertyRow(scale.easeCurve, ease -> { scale.easeCurve = ease; particleEditor.rebuildParticle(); }));
        return Pair.of(scale, rows);
    }
}
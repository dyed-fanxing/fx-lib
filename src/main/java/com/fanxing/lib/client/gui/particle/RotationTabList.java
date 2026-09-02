package com.fanxing.lib.client.gui.particle;

import com.fanxing.lib.client.gui.component.Dropdown;
import com.fanxing.lib.client.gui.component.entry.BaseContainerEntry;
import com.fanxing.lib.client.gui.particle.entry.LabelWidgetEntry;
import com.fanxing.lib.client.gui.particle.screen.ParticleEditorScreen;
import com.fanxing.lib.item.compoent.particle.RotationProperty;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.fanxing.lib.FxLib.MOD_ID;

public class RotationTabList extends PropertyTabList<BaseContainerEntry> {
    protected final Map<String, Pair<RotationProperty, List<BaseContainerEntry>>> modePropertyEntriesMap = new LinkedHashMap<>();
    protected List<BaseContainerEntry> propertyEntries = new ArrayList<>();

    public static final String MODE_TRANSLATION_KEY = "gui." + MOD_ID + ".rotation.mode";
    public static final String PROPERTY_TRANSLATION_KEY_PREFIX = "gui." + MOD_ID + ".rotation.";
    
    public RotationTabList(int width, int height, int y, int itemHeight, ParticleEditorScreen particleEditor) {
        super(width, height, y, itemHeight,particleEditor);
        addEntry(createModeEntry());
        switchMode(particleEditor.currLayer.rotation.mode());
    }

    @Override
    protected int labelWidth() {
        return 50;
    }

    private LabelWidgetEntry createModeEntry() {
        String initial = particleEditor.currLayer.rotation.mode();
        modePropertyEntriesMap.put(RotationProperty.FIXED, buildFixedRows());
        modePropertyEntriesMap.put(RotationProperty.ACCELERATION, buildAccelerationRows());
        modePropertyEntriesMap.put(RotationProperty.EASE, buildEaseRows());
        modePropertyEntriesMap.put(RotationProperty.FACING_CAMERA, buildFacingCameraRows());
        modePropertyEntriesMap.put(RotationProperty.FIXED_Y, buildFixedYRows());
        modePropertyEntriesMap.put(RotationProperty.FACING_MOVEMENT, buildFacingMovementRows());

        Dropdown<String> dropdown = new Dropdown<>(0, 0, 100, itemHeight, this::switchMode, particleEditor);
        List<Dropdown.Entry<String>> modes = new ArrayList<>();
        for (String mode : modePropertyEntriesMap.keySet()) {
            modes.add(new Dropdown.Entry<>(Component.translatable(MODE_TRANSLATION_KEY + "." + mode), mode));
        }
        dropdown.list(84, itemHeight, modes, initial);
        StringWidget label = new StringWidget(labelWidth(), 0, Component.translatable(MODE_TRANSLATION_KEY), font);
        label.alignLeft();
        return new LabelWidgetEntry(label, dropdown);
    }

    protected void switchMode(String selected) {
        for (BaseContainerEntry entry : propertyEntries) {
            removeEntry(entry);
        }
        Pair<RotationProperty, List<BaseContainerEntry>> pair = modePropertyEntriesMap.get(selected);
        particleEditor.currLayer.rotation = pair.getLeft();
        propertyEntries = pair.getRight();
        for (BaseContainerEntry entry : propertyEntries) {
            addEntry(entry);
        }
    }

    // Fixed 模式：angleX, angleY, angleZ
    private Pair<RotationProperty, List<BaseContainerEntry>> buildFixedRows() {
        RotationProperty.Fixed rot = RotationProperty.FIXED.equals(particleEditor.currLayer.rotation.mode()) ?
                (RotationProperty.Fixed) particleEditor.currLayer.rotation : new RotationProperty.Fixed();
        List<BaseContainerEntry> rows = new ArrayList<>();
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX+"angle_x", rot.angleX, v -> rot.angleX = v));
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX+"angle_y", rot.angleY, v -> rot.angleY = v));
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX+"angle_z", rot.angleZ, v -> rot.angleZ = v));
        return Pair.of(rot, rows);
    }

    // Acceleration 模式：angleX/Y/Z, velX/Y/Z, accX/Y/Z
    private Pair<RotationProperty, List<BaseContainerEntry>> buildAccelerationRows() {
        RotationProperty.Acceleration rot = RotationProperty.ACCELERATION.equals(particleEditor.currLayer.rotation.mode()) ?
                (RotationProperty.Acceleration) particleEditor.currLayer.rotation : new RotationProperty.Acceleration();
        List<BaseContainerEntry> rows = new ArrayList<>();
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX+"angle_x", rot.angleX, v -> rot.angleX = v));
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX+"angle_y", rot.angleY, v -> rot.angleY = v));
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX+"angle_z", rot.angleZ, v -> rot.angleZ = v));
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX+"vel_x", rot.velX, v -> rot.velX = v));
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX+"vel_y", rot.velY, v -> rot.velY = v));
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX+"vel_z", rot.velZ, v -> rot.velZ = v));
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX+"acc_x", rot.accX, v -> rot.accX = v));
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX+"acc_y", rot.accY, v -> rot.accY = v));
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX+"acc_z", rot.accZ, v -> rot.accZ = v));
        return Pair.of(rot, rows);
    }

    // Ease 模式：startAngleX/Y/Z, endAngleX/Y/Z + easeCurve
    private Pair<RotationProperty, List<BaseContainerEntry>> buildEaseRows() {
        RotationProperty.Ease rot = RotationProperty.EASE.equals(particleEditor.currLayer.rotation.mode()) ?
                (RotationProperty.Ease) particleEditor.currLayer.rotation : new RotationProperty.Ease();
        List<BaseContainerEntry> rows = new ArrayList<>();
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX+"start_x", rot.startAngleX, v -> rot.startAngleX = v));
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX+"start_y", rot.startAngleY, v -> rot.startAngleY = v));
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX+"start_z", rot.startAngleZ, v -> rot.startAngleZ = v));
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX+"end_x", rot.endAngleX, v -> rot.endAngleX = v));
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX+"end_y", rot.endAngleY, v -> rot.endAngleY = v));
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX+"end_z", rot.endAngleZ, v -> rot.endAngleZ = v));
        rows.add(buildEasePropertyRow(rot.easeCurve, ease -> { rot.easeCurve = ease; particleEditor.rebuildParticle(); }));
        return Pair.of(rot, rows);
    }
    // FacingCamera 无参数，显示一个提示标签
    private Pair<RotationProperty, List<BaseContainerEntry>> buildFacingCameraRows() {
        RotationProperty.FacingCamera rot = RotationProperty.FACING_CAMERA.equals(particleEditor.currLayer.rotation.mode()) ?
                (RotationProperty.FacingCamera) particleEditor.currLayer.rotation : RotationProperty.FacingCamera.INSTANCE;
        List<BaseContainerEntry> rows = new ArrayList<>();
        rows.add(new LabelWidgetEntry(new StringWidget(0, 0, Component.translatable(PROPERTY_TRANSLATION_KEY_PREFIX+"facing_camera.hint"), font)));
        return Pair.of(rot, rows);
    }

    // FixedY 模式：只有 angleY
    private Pair<RotationProperty, List<BaseContainerEntry>> buildFixedYRows() {
        RotationProperty.FixedY rot = RotationProperty.FIXED_Y.equals(particleEditor.currLayer.rotation.mode()) ?
                (RotationProperty.FixedY) particleEditor.currLayer.rotation : new RotationProperty.FixedY();
        List<BaseContainerEntry> rows = new ArrayList<>();
        rows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX+"angle_y", rot.angleY, v -> rot.angleY = v));
        return Pair.of(rot, rows);
    }

    // FacingMovement 无参数，显示一个提示标签
    private Pair<RotationProperty, List<BaseContainerEntry>> buildFacingMovementRows() {
        RotationProperty.FacingMovement rot = RotationProperty.FACING_MOVEMENT.equals(particleEditor.currLayer.rotation.mode()) ?
                (RotationProperty.FacingMovement) particleEditor.currLayer.rotation : RotationProperty.FacingMovement.INSTANCE;
        List<BaseContainerEntry> rows = new ArrayList<>();
        rows.add(new LabelWidgetEntry(new StringWidget(0, 0, Component.translatable(PROPERTY_TRANSLATION_KEY_PREFIX+"facing_movement.hint"), font)));
        return Pair.of(rot, rows);
    }

}
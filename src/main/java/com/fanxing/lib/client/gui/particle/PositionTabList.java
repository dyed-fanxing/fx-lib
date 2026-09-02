package com.fanxing.lib.client.gui.particle;

import com.fanxing.lib.FxLib;
import com.fanxing.lib.client.gui.component.Dropdown;
import com.fanxing.lib.client.gui.component.entry.BaseContainerEntry;
import com.fanxing.lib.client.gui.meta.MathMeta;
import com.fanxing.lib.client.gui.particle.entry.LabelWidgetEntry;
import com.fanxing.lib.client.gui.particle.screen.ParticleEditorScreen;
import com.fanxing.lib.item.compoent.particle.PositionProperty;
import com.fanxing.lib.util.math.ease.EaseType;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.fanxing.lib.FxLib.MOD_ID;

public class PositionTabList extends PropertyTabList<BaseContainerEntry> {

    protected final Map<String, Pair<PositionProperty,List<BaseContainerEntry>>> motionModePropertryEntriesMap = new LinkedHashMap<>(4);
    protected List<BaseContainerEntry> propertyEntries = new ArrayList<>();

    public static final String MODE_TRANSLATION_KEY = "gui." + MOD_ID + ".motion.mode";
    public static final String PROPERTY_TRANSLATION_KEY_PREFIX = "gui." + MOD_ID + ".motion.";
    public PositionTabList(int width, int height, int y, int itemHeight, ParticleEditorScreen particleEditor) {
        super(width, height, y, itemHeight,particleEditor);
        addEntry(createMotionEntry());
        switchMode(particleEditor.currLayer.position.mode());
    }

    private LabelWidgetEntry createMotionEntry() {
        String initial = particleEditor.currLayer.position.mode();
        motionModePropertryEntriesMap.put(PositionProperty.FIXED, buildFixedPropertyRows());
        motionModePropertryEntriesMap.put(PositionProperty.ACCELERATION, buildAccPropertyRows());
        motionModePropertryEntriesMap.put(PositionProperty.EASE, buildEasePropertyRows());
        motionModePropertryEntriesMap.put(PositionProperty.CAMERA_OFFSET, buildCameraOffsetPropertyRows());

        Dropdown<String> dropdown = new Dropdown<>(0, 0, 100, itemHeight, this::switchMode, particleEditor);
        List<Dropdown.Entry<String>> modes = new ArrayList<>();
        for (String mode : motionModePropertryEntriesMap.keySet()) {
            modes.add(new Dropdown.Entry<>(Component.translatable(MODE_TRANSLATION_KEY +"."+ mode), mode));
        }
        dropdown.list(84, itemHeight, modes, initial);
        StringWidget label = new StringWidget(labelWidth(), 0, Component.translatable(MODE_TRANSLATION_KEY), font);
        label.alignLeft();
        return new LabelWidgetEntry(label, dropdown);
    }
    protected void switchMode(String selected){
        for (BaseContainerEntry entry : propertyEntries) {
            removeEntry(entry);
        }
        Pair<PositionProperty, List<BaseContainerEntry>> propertyEntriesPair = motionModePropertryEntriesMap.get(selected);
        particleEditor.currLayer.position = propertyEntriesPair.getLeft();
        propertyEntries = propertyEntriesPair.getRight();
        for (BaseContainerEntry propertyEntry : propertyEntries) {
            addEntry(propertyEntry);
        }
    }

    protected Pair<PositionProperty,List<BaseContainerEntry>> buildFixedPropertyRows() {
        PositionProperty.Fixed pos = PositionProperty.FIXED.equals(particleEditor.currLayer.position.mode())?(PositionProperty.Fixed) particleEditor.currLayer.position:new PositionProperty.Fixed();
        List<BaseContainerEntry> propertyRows = new ArrayList<>();
        propertyRows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX+"pos_x",pos.x, randomValue -> pos.x = randomValue));
        propertyRows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX+"pos_y",pos.y, randomValue -> pos.y = randomValue));
        propertyRows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX+"pos_z",pos.z, randomValue -> pos.z = randomValue));
        return Pair.of(pos, propertyRows);
    }
    protected Pair<PositionProperty,List<BaseContainerEntry>> buildAccPropertyRows() {
        PositionProperty.Acceleration pos = PositionProperty.ACCELERATION.equals(particleEditor.currLayer.position.mode())?(PositionProperty.Acceleration) particleEditor.currLayer.position:new PositionProperty.Acceleration();
        List<BaseContainerEntry> propertyRows = new ArrayList<>();
        propertyRows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX+"pos_x",pos.x, randomValue -> pos.x = randomValue));
        propertyRows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX+"pos_y",pos.y, randomValue -> pos.y = randomValue));
        propertyRows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX+"pos_z",pos.z, randomValue -> pos.z = randomValue));
        propertyRows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX+"vel_x",pos.velX, randomValue -> pos.velX = randomValue));
        propertyRows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX+"vel_y",pos.velY, randomValue -> pos.velY = randomValue));
        propertyRows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX+"vel_z",pos.velZ, randomValue -> pos.velZ = randomValue));
        propertyRows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX+"acc_x",pos.accX, randomValue -> pos.accX = randomValue));
        propertyRows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX+"acc_y",pos.accY, randomValue -> pos.accY = randomValue));
        propertyRows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX+"acc_z",pos.accZ, randomValue -> pos.accZ = randomValue));
        return Pair.of(pos,propertyRows);
    }
    protected Pair<PositionProperty,List<BaseContainerEntry>> buildEasePropertyRows() {
        PositionProperty.Ease pos = PositionProperty.EASE.equals(particleEditor.currLayer.position.mode())?(PositionProperty.Ease) particleEditor.currLayer.position:new PositionProperty.Ease();
        List<BaseContainerEntry> propertyRows = new ArrayList<>();
        propertyRows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX+"start_x",pos.startX, randomValue -> pos.startX = randomValue));
        propertyRows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX+"start_y",pos.startY, randomValue -> pos.startY = randomValue));
        propertyRows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX+"start_z",pos.startZ, randomValue -> pos.startZ = randomValue));
        propertyRows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX+"end_x",pos.endX, randomValue -> pos.endX = randomValue));
        propertyRows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX+"end_y",pos.endY, randomValue -> pos.endY = randomValue));
        propertyRows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX+"end_z",pos.endZ, randomValue -> pos.endZ = randomValue));
        propertyRows.add(createEasePropertyRow(pos.easeCurve,ease -> {
            pos.easeCurve = ease;
            particleEditor.rebuildParticle();
        }));
        return Pair.of(pos,propertyRows);
    }
    protected Pair<PositionProperty,List<BaseContainerEntry>> buildCameraOffsetPropertyRows() {
        PositionProperty.CameraOffset pos = PositionProperty.CAMERA_OFFSET.equals(particleEditor.currLayer.position.mode())?(PositionProperty.CameraOffset) particleEditor.currLayer.position:new PositionProperty.CameraOffset();
        List<BaseContainerEntry> propertyRows = new ArrayList<>();
        propertyRows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX+"offset_x", pos.offsetX,randomValue -> pos.offsetX = randomValue));
        propertyRows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX+"offset_y", pos.offsetY,randomValue -> pos.offsetY = randomValue));
        propertyRows.add(buildEditBoxPropertyRow(PROPERTY_TRANSLATION_KEY_PREFIX+"offset_z", pos.offsetZ,randomValue -> pos.offsetZ = randomValue));
        return Pair.of(pos,propertyRows);
    }


    protected BaseContainerEntry createEasePropertyRow(String easeCurve,Consumer<String> update) {
        Dropdown<String> dropdown = new Dropdown<>(0, 0, 100, itemHeight, update, particleEditor);
        List<Dropdown.Entry<String>> modes = new ArrayList<>();
        for (String key : EaseType.EASES.keySet()) {
            modes.add(new Dropdown.Entry<>(Component.translatable(MathMeta.EASE_TRANSLATE_KEY+"."+key), key));
        }
        dropdown.list(204, itemHeight, modes,easeCurve);
        StringWidget label = new StringWidget(labelWidth(), 0, Component.translatable(MathMeta.EASE_TRANSLATE_KEY), font);
        label.alignLeft();
        return new LabelWidgetEntry(label, dropdown);
    }

}
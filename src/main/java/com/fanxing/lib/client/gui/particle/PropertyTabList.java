package com.fanxing.lib.client.gui.particle;


import com.fanxing.lib.client.gui.Scheme;
import com.fanxing.lib.client.gui.component.DoubleEditBox;
import com.fanxing.lib.client.gui.component.Dropdown;
import com.fanxing.lib.client.gui.component.IntegerEditBox;
import com.fanxing.lib.client.gui.component.entry.BaseContainerEntry;
import com.fanxing.lib.client.gui.meta.MathMeta;
import com.fanxing.lib.client.gui.meta.RandomTypeMeta;
import com.fanxing.lib.client.gui.particle.entry.LabelWidgetEntry;
import com.fanxing.lib.client.gui.particle.entry.PropertyRandomEntry;
import com.fanxing.lib.client.gui.particle.screen.ParticleEditorScreen;
import com.fanxing.lib.util.math.ease.EaseType;
import com.fanxing.lib.util.math.random.RandomType;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


/**
 * @author dyed_fanxing
 * @date 2026/5/21 00:43
 */
public abstract class PropertyTabList<E extends ContainerObjectSelectionList.Entry<E>> extends ContainerObjectSelectionList<E> {
    protected static final int PADDING = 3;
    protected static final int SCROLLBAR_WIDTH = 6;
    protected ParticleEditorScreen particleEditor;
    protected Font font;

    public PropertyTabList(int width, int height, int y, int itemHeight,ParticleEditorScreen particleEditor) {
        super(particleEditor.getMinecraft(), width, height, y, itemHeight);
        this.font = particleEditor.getMinecraft().font;
        this.particleEditor = particleEditor;
    }

    protected int labelWidth(){
        return 45;
    }
    @Override
    public int getRowLeft() {
        return getX() + PADDING;
    }

    @Override
    public int getRowWidth() {
        return getWidth() + (scrollbarVisible() ? -SCROLLBAR_WIDTH : 0) - PADDING * 2;
    }

    @Override
    protected int getScrollbarPosition() {
        return getX() + getWidth() - SCROLLBAR_WIDTH;
    }

    @Override
    protected void renderListSeparators(@NotNull GuiGraphics graphics) {
    }

    @Override
    protected void renderListBackground(GuiGraphics graphics) {
        graphics.fill(getX(), getY(), getRight(), getBottom(), Scheme.BG_COLOR_GREY);
    }

    protected LabelWidgetEntry buildEasePropertyRow(String currentEase, Consumer<String> update) {
        Dropdown<String> dropdown = new Dropdown<>(0, 0, 100, itemHeight, update, particleEditor);
        List<Dropdown.Entry<String>> modes = new ArrayList<>();
        for (String key : EaseType.EASES.keySet()) {
            modes.add(new Dropdown.Entry<>(Component.translatable(MathMeta.EASE_TRANSLATE_KEY+"."+key), key));
        }
        String defaultEase = (currentEase != null && EaseType.EASES.containsKey(currentEase)) ? currentEase : EaseType.EASES.keySet().iterator().next();
        dropdown.list(204, itemHeight, modes, defaultEase);
        StringWidget label = new StringWidget(labelWidth(), 0, Component.translatable(MathMeta.EASE_TRANSLATE_KEY), font);
        label.alignLeft();
        return new LabelWidgetEntry(label, dropdown);
    }

    protected PropertyRandomEntry buildEditBoxPropertyRow(String key, RandomType randomType, Consumer<RandomType> callback) {
        Map<String, Pair<RandomType,List<DoubleEditBox>>> randomEditBoxesMap = new LinkedHashMap<>(3);
        randomEditBoxesMap.put(RandomType.AVG_AMP_FLOAT, buildAvgAmpEditBoxes(randomType,particleEditor,font));
        randomEditBoxesMap.put(RandomType.RANGE_FLOAT, buildRangeEditBoxes(randomType,particleEditor, font));
        randomEditBoxesMap.put(RandomType.ABS_AVG_AMP_FLOAT, buildAbsAvgAmpEditBoxes(randomType, particleEditor, font));

        List<Dropdown.Entry<String>> typeEntries = new ArrayList<>();
        for (String type : randomEditBoxesMap.keySet()) {
            Dropdown.Entry<String> entry = new Dropdown.Entry<>(Component.translatable(RandomTypeMeta.getRandomFloatTranslationKey(type)), type);
            String tooltipString = type + ".tooltip";
            if (I18n.exists(tooltipString)) {
                entry.setTooltip(Tooltip.create(Component.translatable(tooltipString)));
            }
            typeEntries.add(entry);
        }

        PropertyRandomEntry[] entryRef = new PropertyRandomEntry[1];

        Dropdown<String> randomMode = new Dropdown<>(0, 0, 45, itemHeight, selected -> {
            Pair<RandomType, List<DoubleEditBox>> randomValueListPair = randomEditBoxesMap.get(selected);
            callback.accept(randomValueListPair.getLeft());
            entryRef[0].setEditBoxes(randomValueListPair.getRight());
        }, particleEditor);
        randomMode.list(64, itemHeight, typeEntries, randomType.type());

        List<DoubleEditBox> initialBoxes = randomEditBoxesMap.get(randomType.type()).getRight();
        PropertyRandomEntry entry = new PropertyRandomEntry(labelWidth(), Component.translatable(key), font, randomMode, initialBoxes);
        entryRef[0] = entry;
        return entry;
    }

    public static Pair<RandomType, List<DoubleEditBox>> buildAvgAmpEditBoxes(RandomType randomType, ParticleEditorScreen particleEditor, Font font) {
        RandomType.AvgAmpFloat type = RandomType.AVG_AMP_FLOAT.equals(randomType.type()) ? (RandomType.AvgAmpFloat) randomType : new RandomType.AvgAmpFloat();
        List<DoubleEditBox> boxes = new ArrayList<>();
        boxes.add(DoubleEditBox.create(RandomTypeMeta.AVG,type.avg, (d) -> {
            type.avg = (float) d;
            particleEditor.rebuildParticle();
        }, font));
        boxes.add(DoubleEditBox.create(RandomTypeMeta.AMP, type.amp, (d) -> {
            type.amp = (float) d;
            particleEditor.rebuildParticle();
        }, font));
        return Pair.of(type, boxes);
    }
    public static Pair<RandomType, List<DoubleEditBox>> buildRangeEditBoxes(RandomType randomType, ParticleEditorScreen particleEditor, Font font) {
        RandomType.RangeFloat type = RandomType.RANGE_FLOAT.equals(randomType.type()) ? (RandomType.RangeFloat) randomType : new RandomType.RangeFloat();
        List<DoubleEditBox> boxes = new ArrayList<>();
        boxes.add(DoubleEditBox.create(RandomTypeMeta.MIN, type.min, (d) -> {
            type.min = (float) d;
            particleEditor.rebuildParticle();
        }, font));
        boxes.add(DoubleEditBox.create(RandomTypeMeta.MAX, type.max, (d) -> {
            type.max = (float) d;
            particleEditor.rebuildParticle();
        }, font));
        return Pair.of(type, boxes);
    }
    public static Pair<RandomType, List<DoubleEditBox>> buildAbsAvgAmpEditBoxes(RandomType randomType, ParticleEditorScreen particleEditor, Font font) {
        RandomType.AbsAvgAmpFloat type = RandomType.ABS_AVG_AMP_FLOAT.equals(randomType.type()) ? (RandomType.AbsAvgAmpFloat) randomType : new RandomType.AbsAvgAmpFloat();
        List<DoubleEditBox> boxes = new ArrayList<>();
        boxes.add(DoubleEditBox.create(RandomTypeMeta.ABS_AVG, type.absAvg, (d) -> {
            type.absAvg = (float) d;
            particleEditor.rebuildParticle();
        }, font));
        boxes.add(DoubleEditBox.create(RandomTypeMeta.AMP,type.amp, (d) -> {
            type.amp = (float) d;
            particleEditor.rebuildParticle();
        }, font));
        return Pair.of(type, boxes);
    }





    public static Pair<RandomType, List<IntegerEditBox>> buildAvgAmpIntegerEditBoxes(RandomType randomType, ParticleEditorScreen particleEditor, Font font) {
        RandomType.AvgAmpInt type = RandomType.AVG_AMP_INT.equals(randomType.type()) ? (RandomType.AvgAmpInt) randomType : new RandomType.AvgAmpInt();
        List<IntegerEditBox> boxes = new ArrayList<>();
        boxes.add(IntegerEditBox.create(RandomTypeMeta.AVG,type.avg, (i) -> {
            type.avg = i;
            particleEditor.rebuildParticle();
        }, font));
        boxes.add(IntegerEditBox.create(RandomTypeMeta.AMP, type.amp, (i) -> {
            type.amp = i;
            particleEditor.rebuildParticle();
        }, font));
        return Pair.of(type, boxes);
    }
    public static Pair<RandomType,List<IntegerEditBox>> buildRangeIntegerEditBoxes(RandomType randomType, ParticleEditorScreen particleEditor, Font font) {
        RandomType.RangeInt type = RandomType.RANGE_INT.equals(randomType.type()) ? (RandomType.RangeInt) randomType : new RandomType.RangeInt();
        List<IntegerEditBox> boxes = new ArrayList<>();
        boxes.add(IntegerEditBox.create(RandomTypeMeta.MIN, type.min, (i) -> {
            type.min = i;
            particleEditor.rebuildParticle();
        }, font));
        boxes.add(IntegerEditBox.create(RandomTypeMeta.MAX, type.max, (i) -> {
            type.max = i;
            particleEditor.rebuildParticle();
        }, font));
        return Pair.of(type, boxes);
    }
    public static Pair<RandomType,List<IntegerEditBox>> buildAbsAvgAmpIntegerEditBoxes(RandomType randomType, ParticleEditorScreen particleEditor, Font font) {
        RandomType.AbsAvgAmpInt type = RandomType.ABS_AVG_AMP_INT.equals(randomType.type()) ? (RandomType.AbsAvgAmpInt) randomType : new RandomType.AbsAvgAmpInt();
        List<IntegerEditBox> boxes = new ArrayList<>();
        boxes.add(IntegerEditBox.create(RandomTypeMeta.ABS_AVG, type.absAvg, (i) -> {
            type.absAvg = i;
            particleEditor.rebuildParticle();
        }, font));
        boxes.add(IntegerEditBox.create(RandomTypeMeta.AMP,type.amp, (i) -> {
            type.amp = i;
            particleEditor.rebuildParticle();
        }, font));
        return Pair.of(type, boxes);
    }


}
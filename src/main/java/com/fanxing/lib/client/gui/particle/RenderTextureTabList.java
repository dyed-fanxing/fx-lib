package com.fanxing.lib.client.gui.particle;

import com.fanxing.lib.client.gui.component.DoubleEditBox;
import com.fanxing.lib.client.gui.component.Dropdown;
import com.fanxing.lib.client.gui.component.IntegerEditBox;
import com.fanxing.lib.client.gui.component.ScrollingStringWidget;
import com.fanxing.lib.client.gui.component.container.FormContainer;
import com.fanxing.lib.client.gui.component.entry.BaseContainerEntry;
import com.fanxing.lib.client.gui.meta.MathMeta;
import com.fanxing.lib.client.gui.meta.RandomTypeMeta;
import com.fanxing.lib.client.gui.particle.entry.LabelWidgetEntry;
import com.fanxing.lib.client.gui.particle.entry.PropertyRandomEntry;
import com.fanxing.lib.client.gui.particle.form.LabelMultiWidgetFormItem;
import com.fanxing.lib.client.gui.particle.form.LabelWidgetFormItem;
import com.fanxing.lib.client.gui.particle.form.PropertyRandomFormItem;
import com.fanxing.lib.client.gui.particle.screen.ParticleEditorScreen;
import com.fanxing.lib.client.gui.particle.screen.TexturePickerPopupScreen;
import com.fanxing.lib.client.particle.rendertypes.BlendMode;
import com.fanxing.lib.item.compoent.particle.ParticleRenderType;
import com.fanxing.lib.item.compoent.particle.PositionProperty;
import com.fanxing.lib.item.compoent.particle.UVProperty;
import com.fanxing.lib.util.math.ease.EaseType;
import com.fanxing.lib.util.math.random.RandomType;
import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;

import static com.fanxing.lib.FxLib.MOD_ID;

public class RenderTextureTabList extends FormContainer<FormContainer.FormItem> {
    private final Font font = Minecraft.getInstance().font;
    private final ParticleEditorScreen particleEditor;
    private List<FormContainer.FormItem> uvItems = new ArrayList<>();
    private final LinkedHashMap<String,Pair<UVProperty,List<FormContainer.FormItem>>> uvModeItemsMap = new LinkedHashMap<>(3);

    public static final String RENDER_TEXTURE_TRANSLATION_KEY_PREFIX = "gui."+MOD_ID+".render_texture.";


    public RenderTextureTabList(int x, int y, int width, int height, ParticleEditorScreen particleEditor) {
        super(x, y, width, height, Component.empty());
        this.particleEditor = particleEditor;
//        addDropdownRow(RENDER_TEXTURE_TRANSLATION_KEY_PREFIX+"material", new String[]{"default", "distort", "lighting"},
//                particleEditor.currLayer.renderType.shader,selected -> particleEditor.currLayer.renderType.shader = selected);
        addTextureRow();
        addDepthTestItem();
        addDepthMaskItem();
        addDropdownRow(RENDER_TEXTURE_TRANSLATION_KEY_PREFIX+"filter", ParticleRenderType.Filter.values(),
                particleEditor.currLayer.renderType.filter,selected -> particleEditor.currLayer.renderType.filter = selected);
        addDropdownRow(RENDER_TEXTURE_TRANSLATION_KEY_PREFIX+"wrap", ParticleRenderType.Wrap.values(),
                particleEditor.currLayer.renderType.wrap,selected -> particleEditor.currLayer.renderType.wrap = selected);
//        addDropdownRow(RENDER_TEXTURE_TRANSLATION_KEY_PREFIX+"blend", BlendMode.values(),
//                particleEditor.currLayer.renderType.blend,selected -> particleEditor.currLayer.renderType.blend = selected);
        addAlphaEaseItem();
        addUVPropertyItems();
    }

    protected int labelWidth() {
        return 75;
    }
    protected int normalItemHeight(){
        return 16;
    }
    
    private <T> void addDropdownRow(String labelKey, T[] options,T initial,Consumer<T> setter) {
        StringWidget label = new StringWidget(labelWidth(), 0, Component.translatable(labelKey), font);
        label.alignLeft();
        Dropdown<T> dropdown = new Dropdown<>(0, 0, 100, normalItemHeight(), setter, particleEditor);
        List<Dropdown.Entry<T>> entries = new ArrayList<>();
        for (T opt : options) {
            entries.add(new Dropdown.Entry<>(Component.translatable(labelKey + "." + opt.toString()), opt));
        }
        dropdown.list(120, normalItemHeight(), entries,initial);
        addItem(new LabelWidgetFormItem(label, dropdown, normalItemHeight()));
    }

    protected void addTextureRow() {

        TextureDisplayWidget display = new TextureDisplayWidget(0, 0, 0, 0, particleEditor.currLayer.renderType.texture);
        display.setCallback(() -> {
            // 纹理选择后更新到数据
            Minecraft.getInstance().setScreen(new TexturePickerPopupScreen(tex -> {
                particleEditor.currLayer.renderType.texture = tex;
                particleEditor.rebuildParticle();
            }, particleEditor));
        });
        StringWidget label = new StringWidget(labelWidth(), 0, Component.translatable(RENDER_TEXTURE_TRANSLATION_KEY_PREFIX+"texture"), font);
        label.alignLeft();
        addItem(new LabelWidgetFormItem(label, display, 32));
    }
    protected void addDepthMaskItem() {
        StringWidget label = new StringWidget(labelWidth(), 0, Component.translatable(RENDER_TEXTURE_TRANSLATION_KEY_PREFIX+"depth_write"), font);
        label.alignLeft();
        Checkbox checkbox = Checkbox.builder(Component.empty(), font)
                .selected(particleEditor.currLayer.renderType.depthMask)
                .onValueChange((cb, selected) -> {
                    particleEditor.currLayer.renderType.depthMask = selected;
                    particleEditor.rebuildParticle();
                }).build();
        addItem(new LabelWidgetFormItem(label,checkbox , normalItemHeight()));
    }
    protected void addDepthTestItem() {
        StringWidget label = new StringWidget(labelWidth(), 0, Component.translatable(RENDER_TEXTURE_TRANSLATION_KEY_PREFIX+"depth_test"), font);
        label.alignLeft();
        Checkbox checkbox = Checkbox.builder(Component.empty(), font)
                .selected(particleEditor.currLayer.renderType.depthTest)
                .onValueChange((cb, selected) -> {
                    particleEditor.currLayer.renderType.depthTest = selected;
                    particleEditor.rebuildParticle();
                }).build();
        addItem(new LabelWidgetFormItem(label,checkbox , normalItemHeight()));
    }
    private void addAlphaEaseItem() {
        Dropdown<String> dropdown = new Dropdown<>(0, 0, 100, normalItemHeight(), selected -> {
            particleEditor.currLayer.alphaCurve = selected;
            particleEditor.rebuildParticle();
        }, particleEditor);
        List<Dropdown.Entry<String>> entries = new ArrayList<>();
        for (String key: EaseType.EASES.keySet()) {
            entries.add(new Dropdown.Entry<>(Component.translatable(MathMeta.EASE_TRANSLATE_KEY+"."+key), key));
        }
        dropdown.list(204, normalItemHeight(),entries, particleEditor.currLayer.alphaCurve);
        StringWidget label = new StringWidget(labelWidth(), 0, Component.translatable(RENDER_TEXTURE_TRANSLATION_KEY_PREFIX+"alpha_curve"), font);
        label.alignLeft();
        addItem(new LabelWidgetFormItem(label, dropdown, normalItemHeight()));
    }




    protected void switchUVMode(String selected){
        for (FormItem item : uvItems) {
            removeItem(item);
        }
        Pair<UVProperty, List<FormItem>> uvItemsPair = uvModeItemsMap.get(selected);
        particleEditor.currLayer.renderType.uv = uvItemsPair.getLeft();
        uvItems = uvItemsPair.getRight();
        for (FormItem item : uvItems) {
            addItem(item);
        }
    }

    // ==================== UV 动画部分（数据绑定） ====================
    private void addUVPropertyItems() {
        String translationKey = RENDER_TEXTURE_TRANSLATION_KEY_PREFIX+"uv_mode";
        uvModeItemsMap.put(UVProperty.FIXED,buildFixedUVItems());
        uvModeItemsMap.put(UVProperty.SCROLL,buildScrollingUVItems());
        uvModeItemsMap.put(UVProperty.ANIMATED,buildAnimatedUVItems());
        Dropdown<String> modeDropdown = new Dropdown<>(0, 0, 100, normalItemHeight(), this::switchUVMode, particleEditor);
        List<Dropdown.Entry<String>> modes = new ArrayList<>();
        for (String key : uvModeItemsMap.keySet()) {
            modes.add(new Dropdown.Entry<>(Component.translatable(translationKey+"."+key), key));
        }
        modeDropdown.list(120, normalItemHeight(), modes, particleEditor.currLayer.renderType.uv.mode());
        StringWidget modeLabel = new StringWidget(labelWidth(), 0, Component.translatable(translationKey), font);
        modeLabel.alignLeft();
        addItem(new LabelWidgetFormItem(modeLabel, modeDropdown, normalItemHeight()));
    }

    protected Pair<UVProperty,List<FormContainer.FormItem>> buildFixedUVItems() {
        List<FormContainer.FormItem> items = new ArrayList<>();
        UVProperty.Fixed uv = UVProperty.FIXED.equals(particleEditor.currLayer.renderType.uv.mode())?(UVProperty.Fixed)particleEditor.currLayer.renderType.uv:new UVProperty.Fixed();
        items.add(buildUVItem(RENDER_TEXTURE_TRANSLATION_KEY_PREFIX+"uv_start",uv.u0, uv.v0, v -> uv.u0 = (float) v, v -> uv.v0 = (float) v));
        items.add(buildUVItem(RENDER_TEXTURE_TRANSLATION_KEY_PREFIX+"uv_end",uv.u0, uv.v0, v -> uv.u0 = (float) v, v -> uv.v0 = (float) v));
        return Pair.of(uv,items);
    }

    protected Pair<UVProperty,List<FormContainer.FormItem>> buildScrollingUVItems() {
        List<FormContainer.FormItem> items = new ArrayList<>();
        UVProperty.Scroll uv = UVProperty.SCROLL.equals(particleEditor.currLayer.renderType.uv.mode())?(UVProperty.Scroll)particleEditor.currLayer.renderType.uv:new UVProperty.Scroll();
        items.add(buildUVItem(RENDER_TEXTURE_TRANSLATION_KEY_PREFIX+"uv_start",uv.u0, uv.v0, v -> uv.u0 = (float) v, v -> uv.v0 = (float) v));
        items.add(buildUVItem(RENDER_TEXTURE_TRANSLATION_KEY_PREFIX+"uv_size",uv.uSize, uv.vSize, v -> uv.uSize = (float) v, v -> uv.vSize = (float) v));
        items.add(buildUVItem(RENDER_TEXTURE_TRANSLATION_KEY_PREFIX+"uv_speed",uv.uSpeed, uv.vSpeed, v -> uv.uSpeed = (float) v, v -> uv.vSpeed = (float) v));
        return Pair.of(uv,items);
    }

    protected Pair<UVProperty,List<FormContainer.FormItem>> buildAnimatedUVItems() {
        List<FormContainer.FormItem> items = new ArrayList<>();
        UVProperty.Animated uv = UVProperty.ANIMATED.equals(particleEditor.currLayer.renderType.uv.mode())?(UVProperty.Animated)particleEditor.currLayer.renderType.uv:new UVProperty.Animated();
        items.add(buildUVItem(RENDER_TEXTURE_TRANSLATION_KEY_PREFIX+"uv_start",uv.u0, uv.v0, v -> uv.u0 = (float) v, v -> uv.v0 = (float) v));
        items.add(buildUVItem(RENDER_TEXTURE_TRANSLATION_KEY_PREFIX+"uv_size",uv.uSize, uv.vSize, v -> uv.uSize = (float) v, v -> uv.vSize = (float) v));
        items.add(buildIntEditBoxItem(RENDER_TEXTURE_TRANSLATION_KEY_PREFIX+"uv_frame_ticks", uv.frameTicks, i -> {uv.frameTicks = i;particleEditor.rebuildParticle();}));
        items.add(buildIntEditBoxItem(RENDER_TEXTURE_TRANSLATION_KEY_PREFIX+"uv_frame_count_v", uv.frameCountV, i -> {uv.frameCountV = i;particleEditor.rebuildParticle();}));
        return Pair.of(uv,items);
    }

    protected LabelWidgetFormItem buildIntEditBoxItem(String translationKey, int initial, IntConsumer setter) {
        IntegerEditBox editBox = IntegerEditBox.create("U", initial, setter, font);
        StringWidget label = new StringWidget(labelWidth(), 0, Component.translatable(translationKey), font);
        label.alignLeft();
        return new LabelWidgetFormItem(label, editBox, normalItemHeight());
    }

    protected LabelMultiWidgetFormItem buildUVItem(String translationKey,float u0,float v0,DoubleConsumer u0Setter,DoubleConsumer v0Setter) {
        StringWidget label = new StringWidget(labelWidth(), 0, Component.translatable(translationKey), font);
        label.alignLeft();
        DoubleEditBox u0EditBox = DoubleEditBox.create("U", u0, u0Setter,font);
        DoubleEditBox v0EditBox = DoubleEditBox.create("V", v0, v0Setter,font);
        return new LabelMultiWidgetFormItem(label, List.of(u0EditBox,v0EditBox), normalItemHeight());
    }













    protected PropertyRandomFormItem<DoubleEditBox> createRandomPropertyDoubleEditBoxesItem(String key, RandomType randomType, Consumer<RandomType> callback) {
        Map<String, Pair<RandomType,List<DoubleEditBox>>> randomEditBoxesMap = new LinkedHashMap<>(3);

        randomEditBoxesMap.put(RandomType.AVG_AMP_INT, PropertyTabList.buildAvgAmpEditBoxes(randomType,particleEditor,font));
        randomEditBoxesMap.put(RandomType.RANGE_INT, PropertyTabList.buildRangeEditBoxes(randomType,particleEditor, font));
        randomEditBoxesMap.put(RandomType.ABS_AVG_AMP_INT, PropertyTabList.buildAbsAvgAmpEditBoxes(randomType, particleEditor, font));

        List<Dropdown.Entry<String>> typeEntries = new ArrayList<>();
        for (String type : randomEditBoxesMap.keySet()) {
            Dropdown.Entry<String> entry = new Dropdown.Entry<>(Component.translatable(RandomTypeMeta.getRandomFloatTranslationKey(type)), type);
            String tooltipString = type + ".tooltip";
            if (I18n.exists(tooltipString)) {
                entry.setTooltip(Tooltip.create(Component.translatable(tooltipString)));
            }
            typeEntries.add(entry);
        }
        PropertyRandomFormItem<DoubleEditBox>[] itemRef = new PropertyRandomFormItem[1];

        Dropdown<String> randomMode = new Dropdown<>(0, 0, 45,normalItemHeight(), selected -> {
            Pair<RandomType, List<DoubleEditBox>> randomValueListPair = randomEditBoxesMap.get(selected);
            callback.accept(randomValueListPair.getLeft());
            itemRef[0].setEditBoxes(randomValueListPair.getRight());
        }, particleEditor);
        randomMode.list(64,normalItemHeight(), typeEntries, randomType.type());

        List<DoubleEditBox> initialBoxes = randomEditBoxesMap.get(randomType.type()).getRight();
        PropertyRandomFormItem<DoubleEditBox> entry = new PropertyRandomFormItem<>(new StringWidget(labelWidth(), 0, Component.translatable(key), font), randomMode, initialBoxes);
        itemRef[0] = entry;
        return entry;
    }


    protected PropertyRandomFormItem<IntegerEditBox> createRandomPropertyIntegerEditBoxesItem(String key, RandomType randomType, Consumer<RandomType> callback) {
        Map<String, Pair<RandomType,List<IntegerEditBox>>> randomEditBoxesMap = new LinkedHashMap<>(3);

        randomEditBoxesMap.put(RandomType.AVG_AMP_INT, PropertyTabList.buildAvgAmpIntegerEditBoxes(randomType,particleEditor,font));
        randomEditBoxesMap.put(RandomType.RANGE_INT, PropertyTabList.buildRangeIntegerEditBoxes(randomType,particleEditor, font));
        randomEditBoxesMap.put(RandomType.ABS_AVG_AMP_INT, PropertyTabList.buildAbsAvgAmpIntegerEditBoxes(randomType, particleEditor, font));

        List<Dropdown.Entry<String>> typeEntries = new ArrayList<>();
        for (String type : randomEditBoxesMap.keySet()) {
            Dropdown.Entry<String> entry = new Dropdown.Entry<>(Component.translatable(RandomTypeMeta.getRandomFloatTranslationKey(type)), type);
            String tooltipString = type + ".tooltip";
            if (I18n.exists(tooltipString)) {
                entry.setTooltip(Tooltip.create(Component.translatable(tooltipString)));
            }
            typeEntries.add(entry);
        }
        PropertyRandomFormItem<IntegerEditBox>[] itemRef = new PropertyRandomFormItem[1];

        Dropdown<String> randomMode = new Dropdown<>(0, 0, 45,normalItemHeight(), selected -> {
            Pair<RandomType, List<IntegerEditBox>> randomValueListPair = randomEditBoxesMap.get(selected);
            callback.accept(randomValueListPair.getLeft());
            itemRef[0].setEditBoxes(randomValueListPair.getRight());
        }, particleEditor);
        randomMode.list(64,normalItemHeight(), typeEntries, randomType.type());

        List<IntegerEditBox> initialBoxes = randomEditBoxesMap.get(randomType.type()).getRight();
        PropertyRandomFormItem<IntegerEditBox> entry = new PropertyRandomFormItem<>(new StringWidget(labelWidth(), 0, Component.translatable(key), font), randomMode, initialBoxes);
        itemRef[0] = entry;
        return entry;
    }




}
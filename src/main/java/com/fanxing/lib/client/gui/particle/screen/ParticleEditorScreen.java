package com.fanxing.lib.client.gui.particle.screen;

import com.fanxing.lib.FxLib;
import com.fanxing.lib.client.gui.component.*;
import com.fanxing.lib.client.gui.component.container.TabNavigation;
import com.fanxing.lib.client.gui.layout.FlexBoxLayout;
import com.fanxing.lib.client.gui.particle.*;
import com.fanxing.lib.client.gui.screen.PopupManageScreen;
import com.fanxing.lib.client.particle.editor.base.AbstractPropertyParticle;
import com.fanxing.lib.item.compoent.ParticleEditorScreenConfig;
import com.fanxing.lib.item.compoent.particle.ParticleLayer;
import com.fanxing.lib.registry.DataComponentsFxLib;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.fanxing.lib.client.gui.Scheme.*;

public class ParticleEditorScreen extends PopupManageScreen {

    private static final Logger log = LoggerFactory.getLogger(ParticleEditorScreen.class);

    protected ItemStack stack;

    public static final float PREVIEW_WIDTH_MIN = 0.3f;
    public static final float PREVIEW_WIDTH_MAX = 0.8f;

    public static final float PREVIEW_HEIGHT_MIN = 0.3f;
    public static final float PREVIEW_HEIGHT_MAX = 0.9f;

    public static final float PROPERTY_HEIGHT_MIN = 0.3f;
    public static final float PROPERTY_HEIGHT_MAX = 0.7f;

    protected static final int SPLITTER_SIZE = 1;
    protected static final int SPLITTER_HOT_ZONE = 2;
    protected static final int TAB_NAVIGATION_HEIGHT = 20;
    protected static final int ITEM_HEIGHT = 20;
    // 布局参数
    protected int previewWidth,previewWidthMin, previewWidthMax;
    protected int previewHeight,previewHeightMin, previewHeightMax;
    protected int propertyHeight,propertyHeightMin, propertyHeightMax;

    protected ParticlePreview previewWidget;
    protected TabNavigation propertyTabNavigation;
    protected AbstractContainerWidget tab;

    protected boolean draggingVerticalSplitter, draggingLeftHorizontalSplitter, draggingRightHorizontalSplitter;

    private String currentParticleType = "minecraft:flame";

    protected AbstractPropertyParticle particle;

    public ParticleLayer currLayer;


    public ParticleEditorScreen(ItemStack stack) {
        super(Component.literal("粒子编辑器"));
        this.stack = stack;
        this.currLayer = stack.get(DataComponentsFxLib.PARTICLE_LAYER);
        log.info("构造方法获取的 layer：{}",currLayer);
    }

    @Override
    protected void init() {
        super.init();
        initLayout();
        if(previewWidget == null) previewWidget = new ParticlePreview(0, 0, previewWidth, previewHeight, currentParticleType);
        if(propertyTabNavigation == null) createPropertyPanel();
        layoutWidth();
        layoutLeftHeight();
        layoutRightHeight();



        propertyTabNavigation.init();
        buildTreePanel();
        addRenderableWidget(previewWidget);
        addRenderableWidget(propertyTabNavigation);
    }
    private void initLayout() {
        previewWidthMin = (int) (width*PREVIEW_WIDTH_MIN);
        previewWidthMax = (int) (width*PREVIEW_WIDTH_MAX);
        previewHeightMin = (int) (height*PREVIEW_HEIGHT_MIN);
        previewHeightMax = (int) (height*PREVIEW_HEIGHT_MAX);
        propertyHeightMin = (int)(height*PROPERTY_HEIGHT_MIN);
        propertyHeightMax = (int)(height*PROPERTY_HEIGHT_MAX);
        ParticleEditorScreenConfig config = stack.get(DataComponentsFxLib.PARTICLE_EDITOR_SCREEN_CONFIG);
        if(config != null) {
            previewWidth = Mth.clamp((int) (width * config.previewWidthR()),previewWidthMin,previewWidthMax);
            previewHeight = Mth.clamp((int)(height * config.previewHeightR()),previewHeightMin,previewHeightMax);
            propertyHeight = Mth.clamp((int)(height * config.propertyHeightR()),propertyHeightMin,propertyHeightMax);
        }
    }

    private void layoutWidth(){
        previewWidget.setX(0);
        previewWidget.setWidth(previewWidth);
        int rightX = previewWidth + SPLITTER_SIZE;
        int rightWidth = width - previewWidth - SPLITTER_SIZE;


        propertyTabNavigation.setWidth(rightWidth);
        propertyTabNavigation.setX(rightX);
        tab.setWidth(rightWidth);
        tab.setX(rightX);
        // 如果当前有显示的 tab，也更新它（虽然应该已经更新了）
        if (tab != null) {
            tab.setWidth(rightWidth);
            tab.setX(rightX);
        }

    }
    private void layoutLeftHeight(){
        previewWidget.setHeight(previewHeight);
    }
    private void layoutRightHeight() {
        propertyTabNavigation.setHeight(TAB_NAVIGATION_HEIGHT);
        tab.setHeight(propertyHeight-TAB_NAVIGATION_HEIGHT);
        int treeHeight = height - propertyHeight - SPLITTER_SIZE;
    }


    private void createPropertyPanel() {
        int x = previewWidth-SPLITTER_SIZE-100;
        int width = this.width - previewWidth;
        int height = propertyHeight-TAB_NAVIGATION_HEIGHT;
        // 选项卡面板
        propertyTabNavigation = new TabNavigation(x, 0, width, TAB_NAVIGATION_HEIGHT);
        addTab(Component.translatable("gui."+ FxLib.MOD_ID +".property.position"),new PositionTabList(width, height,TAB_NAVIGATION_HEIGHT, ITEM_HEIGHT, this));
        addTab(Component.translatable("gui."+ FxLib.MOD_ID +".property.rotation"),new RotationTabList(width, height,TAB_NAVIGATION_HEIGHT, ITEM_HEIGHT, this));
        addTab(Component.translatable("gui."+ FxLib.MOD_ID +".property.scale"),new ScaleTabList(width, height,TAB_NAVIGATION_HEIGHT, ITEM_HEIGHT, this));
        addTab(Component.translatable("gui."+ FxLib.MOD_ID +".property.render_texture"),new RenderTextureTabList(0,TAB_NAVIGATION_HEIGHT,width, height, this));
        addTab(Component.translatable("gui."+ FxLib.MOD_ID +".property.mesh_vertex"),new MeshVertexTabList( width, height,TAB_NAVIGATION_HEIGHT, ITEM_HEIGHT, this));
        propertyTabNavigation.init(0);                              // 初始化第一个标签页
    }

    private void buildTreePanel() {
        int rightWidth = width - previewWidth - SPLITTER_SIZE;
        int treeHeight = height - propertyHeight - SPLITTER_SIZE;
        FlexBoxLayout treeContainer = new FlexBoxLayout(rightWidth, 0);
        treeContainer.vertical().gap(4);
        for (int i = 0; i < 20; i++) {
            Button btn = Button.builder(Component.literal("粒子节点 " + i), b -> {}).size(rightWidth - 8, 20).build();
            treeContainer.addChild(btn);
        }
        treeContainer.arrangeElements();
    }



    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        // 分隔条
        boolean hoverVertical = isMouseOverVerticalSplitter(mouseX, mouseY);
        int vColor = draggingVerticalSplitter ? SPLITTER_COLOR_DRAGGING : (hoverVertical ? SPLITTER_COLOR_HOVER : SPLITTER_COLOR_NORMAL);
        graphics.fill(previewWidth, 0, previewWidth + SPLITTER_SIZE, height, vColor);

        boolean hoverLeftHor = isMouseOverLeftHorizontalSplitter(mouseX, mouseY);
        int lhColor = draggingLeftHorizontalSplitter ? SPLITTER_COLOR_DRAGGING : (hoverLeftHor ? SPLITTER_COLOR_HOVER : SPLITTER_COLOR_NORMAL);
        graphics.fill(0, previewHeight, previewWidth, previewHeight + SPLITTER_SIZE, lhColor);

        boolean hoverRightHor = isMouseOverRightHorizontalSplitter(mouseX, mouseY);
        int rhColor = draggingRightHorizontalSplitter ? SPLITTER_COLOR_DRAGGING : (hoverRightHor ? SPLITTER_COLOR_HOVER : SPLITTER_COLOR_NORMAL);
        graphics.fill(previewWidth + SPLITTER_SIZE, propertyHeight, width, propertyHeight + SPLITTER_SIZE, rhColor);
    }


    private boolean isMouseOverVerticalSplitter(double mx, double my) {
        return mx >= previewWidth && mx <= previewWidth + SPLITTER_HOT_ZONE && my >= 0 && my <= height;
    }
    private boolean isMouseOverLeftHorizontalSplitter(double mx, double my) {
        return mx >= 0 && mx <= previewWidth && my >= previewHeight && my <= previewHeight + SPLITTER_HOT_ZONE;
    }
    private boolean isMouseOverRightHorizontalSplitter(double mx, double my) {
        return mx >= previewWidth + SPLITTER_SIZE && mx <= width
                && my >= propertyHeight && my <= propertyHeight + SPLITTER_HOT_ZONE;
    }
    @Override
    public boolean mouseClicked(double x, double y, int code) {
        // 弹窗优先
        if (this.tryPopupClick(x, y, code)) {
            if (code == 0) this.setDragging(true);
            return true;
        }
        if (isMouseOverVerticalSplitter(x, y)) {
            draggingVerticalSplitter = true;
            return true;
        }
        if (isMouseOverLeftHorizontalSplitter(x, y)) {
            draggingLeftHorizontalSplitter = true;
            return true;
        }
        if (isMouseOverRightHorizontalSplitter(x, y)) {
            draggingRightHorizontalSplitter = true;
            return true;
        }
        return super.mouseClicked(x, y, code);
    }
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (draggingVerticalSplitter) {
            int newWidth = (int) Mth.clamp(mouseX, previewWidthMin, previewWidthMax);
            if (newWidth != previewWidth) {
                previewWidth = newWidth;
                layoutWidth();
            }
            return true;
        }
        if (draggingLeftHorizontalSplitter) {
            int newHeight = (int) Mth.clamp(mouseY, previewHeightMin,previewHeightMax);
            if (newHeight != previewHeight) {
                previewHeight = newHeight;
                layoutLeftHeight();
            }
            return true;
        }
        if (draggingRightHorizontalSplitter) {
            int newHeight = (int) Mth.clamp(mouseY, propertyHeightMin,propertyHeightMax);
            if (newHeight != propertyHeight) {
                propertyHeight = newHeight;
                layoutRightHeight();
            }
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggingVerticalSplitter = draggingLeftHorizontalSplitter = draggingRightHorizontalSplitter = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(Screen.hasControlDown() && keyCode == GLFW.GLFW_KEY_S){
            save();
            this.getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    @Override
    public void removed() {
        super.removed();
        save();
    }

    public void save(){
        // 保存当前图层到物品组件
        stack.remove(DataComponentsFxLib.PARTICLE_LAYER);
        stack.set(DataComponentsFxLib.PARTICLE_LAYER, currLayer);
        // 同时保存屏幕布局配置（原有代码）
        stack.set(DataComponentsFxLib.PARTICLE_EDITOR_SCREEN_CONFIG, new ParticleEditorScreenConfig((float) previewWidth / width, (float) previewHeight / height, (float) propertyHeight / height));
    }













    private void addTab(Component label, AbstractContainerWidget propertyTabList) {
        propertyTabNavigation.addTab(label, btn -> {
            if (tab != null) {
                removeWidget(tab);
                propertyTabList.setX(tab.getX());
                propertyTabList.setWidth(tab.getWidth());
                propertyTabList.setHeight(tab.getHeight());
            }
            tab = propertyTabList;
            addRenderableWidget(tab);
        });
    }

    public void rebuildParticle(){

    }
}
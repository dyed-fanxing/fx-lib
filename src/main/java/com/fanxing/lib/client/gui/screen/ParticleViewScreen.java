package com.fanxing.lib.client.gui.screen;

import com.fanxing.lib.FxLib;
import com.fanxing.lib.client.gui.Align;
import com.fanxing.lib.client.gui.component.*;
import com.fanxing.lib.client.gui.layout.FlexBoxLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ParticleViewScreen extends Screen {
    private net.minecraft.client.particle.Particle previewParticle;
    private String selectedParticleKey;
    private List<String> particleNames = new ArrayList<>();

    // 控件
    private StepSlider scaleStartSlider, scaleEndSlider, alphaStartSlider, alphaEndSlider;
    private ColorPicker colorPicker;
    private CycleButton<String> scaleCurveBtn, alphaCurveBtn;
    private IntegerEditBox lifetimeBox;
    private StepSlider speedYawSlider, speedPitchSlider, speedRollSlider;

    // 反射支持标志
    private boolean supportsLifetime = false;
    private boolean supportsScale = false;
    private boolean supportsAlpha = false;
    private boolean supportsColor = false;
    private boolean supportsRotation = false;

    public ParticleViewScreen() {
        super(Component.literal("Particle Viewer"));
        // 收集所有粒子类型
        BuiltInRegistries.PARTICLE_TYPE.forEach(type -> {
            ResourceLocation key = BuiltInRegistries.PARTICLE_TYPE.getKey(type);
            particleNames.add(key.toString());
        });
        if (!particleNames.isEmpty()) selectedParticleKey = particleNames.get(0);
    }

    @Override
    protected void init() {
        super.init();
        buildRightPanel();
        spawnCurrentParticle();
    }

    private void spawnCurrentParticle() {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        if (previewParticle != null) previewParticle.remove();

        ParticleType<?> type = BuiltInRegistries.PARTICLE_TYPE.get(ResourceLocation.tryParse(selectedParticleKey));
        if (!(type instanceof net.minecraft.core.particles.SimpleParticleType)) return;

        ParticleOptions options = (ParticleOptions) type;
        Level level = player.level();
        double x = player.getX(), y = player.getY() + 1.2, z = player.getZ() + 3;
        previewParticle = Minecraft.getInstance().particleEngine.createParticle(options, x, y, z, 0, 0, 0);
        if (previewParticle == null) return;

        Class<?> clazz = previewParticle.getClass();
        supportsLifetime = hasMethod(clazz, "setLifetime", int.class);
        supportsScale = hasMethod(clazz, "setScale", float.class);
        supportsAlpha = hasMethod(clazz, "setAlpha", float.class);
        supportsColor = hasMethod(clazz, "setColor", int.class);
        supportsRotation = hasMethod(clazz, "setRotation", float.class, float.class, float.class);

        refreshControlsEnabled();
        applyCurrentValues();
    }

    private boolean hasMethod(Class<?> clazz, String name, Class<?>... params) {
        try { clazz.getMethod(name, params); return true; } catch (NoSuchMethodException e) { return false; }
    }

    private void refreshControlsEnabled() {
        scaleStartSlider.active = supportsScale;
        scaleEndSlider.active = supportsScale;
        scaleCurveBtn.active = false; // 曲线功能需要额外实现，暂不启用
        alphaStartSlider.active = supportsAlpha;
        alphaEndSlider.active = supportsAlpha;
        alphaCurveBtn.active = false;
        colorPicker.active = supportsColor;
        speedYawSlider.active = supportsRotation;
        speedPitchSlider.active = supportsRotation;
        speedRollSlider.active = supportsRotation;
    }

    private void applyCurrentValues() {
        if (previewParticle == null) return;
        try {
            if (supportsLifetime) previewParticle.getClass().getMethod("setLifetime", int.class).invoke(previewParticle, lifetimeBox.getIntValue());
            if (supportsColor) previewParticle.getClass().getMethod("setColor", int.class).invoke(previewParticle, colorPicker.getColor());
            if (supportsAlpha) {
                float alpha = (float) alphaStartSlider.getValue(); // 使用起始值作为当前 Alpha
                previewParticle.getClass().getMethod("setAlpha", float.class).invoke(previewParticle, alpha);
            }
            if (supportsScale) {
                float scale = (float) scaleStartSlider.getValue();
                previewParticle.getClass().getMethod("setScale", float.class).invoke(previewParticle, scale);
            }
            if (supportsRotation) {
                float yaw = (float) speedYawSlider.getValue();
                float pitch = (float) speedPitchSlider.getValue();
                float roll = (float) speedRollSlider.getValue();
                previewParticle.getClass().getMethod("setRotation", float.class, float.class, float.class).invoke(previewParticle, yaw, pitch, roll);
            }
        } catch (Exception ignored) {}
    }

    private String getCurveDisplayName(String key) {
        String prefix = "util.math." + FxLib.MOD_ID + ".ease.";
        return key.startsWith(prefix) ? key.substring(prefix.length()).replace('_', ' ') : key;
    }

    private void buildRightPanel() {
        int panelWidth = Math.min(width / 3, 280);
        int rightX = width - panelWidth - 10;
        int topY = 10;
        FlexBoxLayout panel = new FlexBoxLayout(rightX, topY, panelWidth, height - 20);
        panel.vertical().gap(8).padding(5);

        // 粒子类型选择
        CycleButton<String> typeBtn = CycleButton.<String>builder(s -> Component.literal(s))
                .withValues(particleNames)
                .withInitialValue(selectedParticleKey)
                .create(0, 0, panelWidth - 10, 20, Component.empty(), (btn, val) -> {
                    selectedParticleKey = val;
                    spawnCurrentParticle();
                });
        panel.addChild(typeBtn);

        // 生命周期
        lifetimeBox = new IntegerEditBox(font, 0, 0, panelWidth - 10, 20, Component.empty(), 10, 300, 10, 60, v -> applyCurrentValues());
        panel.addChild(new LabelEditBox(font, 0, 0, panelWidth - 10, 70, panelWidth - 10 - 70 - 3, 20, 3,
                Component.literal("生命(刻)"), lifetimeBox, Align.SPREAD));

        // 缩放（不使用曲线）
        scaleStartSlider = new StepSlider(0, 0, panelWidth - 10, 20, Component.empty(), 0.1, 3.0, 0.05, 0.5, 2, v -> applyCurrentValues());
        scaleEndSlider   = new StepSlider(0, 0, panelWidth - 10, 20, Component.empty(), 0.1, 3.0, 0.05, 1.5, 2, v -> applyCurrentValues());
        panel.addChild(new LabelEditBox(font, 0, 0, panelWidth - 10, 70, panelWidth - 10 - 70 - 3, 20, 3,
                Component.literal("起始缩放"), scaleStartSlider, Align.SPREAD));
        panel.addChild(new LabelEditBox(font, 0, 0, panelWidth - 10, 70, panelWidth - 10 - 70 - 3, 20, 3,
                Component.literal("结束缩放"), scaleEndSlider, Align.SPREAD));

        // 透明度（不使用曲线）
        alphaStartSlider = new StepSlider(0, 0, panelWidth - 10, 20, Component.empty(), 0.0, 1.0, 0.05, 1.0, 2, v -> applyCurrentValues());
        alphaEndSlider   = new StepSlider(0, 0, panelWidth - 10, 20, Component.empty(), 0.0, 1.0, 0.05, 0.0, 2, v -> applyCurrentValues());
        panel.addChild(new LabelEditBox(font, 0, 0, panelWidth - 10, 70, panelWidth - 10 - 70 - 3, 20, 3,
                Component.literal("起始Alpha"), alphaStartSlider, Align.SPREAD));
        panel.addChild(new LabelEditBox(font, 0, 0, panelWidth - 10, 70, panelWidth - 10 - 70 - 3, 20, 3,
                Component.literal("结束Alpha"), alphaEndSlider, Align.SPREAD));

        // 颜色
        colorPicker = new ColorPicker(0, 0, panelWidth - 10, 160, 0xFFFFAA33, c -> applyCurrentValues());
        panel.addChild(new LabelEditBox(font, 0, 0, panelWidth - 10, 70, panelWidth - 10 - 70 - 3, 20, 3,
                Component.literal("颜色"), colorPicker, Align.SPREAD));

        // 旋转速度
        speedYawSlider = new StepSlider(0, 0, panelWidth - 10, 20, Component.empty(), -10, 10, 0.2, 0, 1, v -> applyCurrentValues());
        speedPitchSlider = new StepSlider(0, 0, panelWidth - 10, 20, Component.empty(), -10, 10, 0.2, 0, 1, v -> applyCurrentValues());
        speedRollSlider = new StepSlider(0, 0, panelWidth - 10, 20, Component.empty(), -10, 10, 0.2, 1, 1, v -> applyCurrentValues());
        panel.addChild(new LabelEditBox(font, 0, 0, panelWidth - 10, 70, panelWidth - 10 - 70 - 3, 20, 3,
                Component.literal("Yaw速度"), speedYawSlider, Align.SPREAD));
        panel.addChild(new LabelEditBox(font, 0, 0, panelWidth - 10, 70, panelWidth - 10 - 70 - 3, 20, 3,
                Component.literal("Pitch速度"), speedPitchSlider, Align.SPREAD));
        panel.addChild(new LabelEditBox(font, 0, 0, panelWidth - 10, 70, panelWidth - 10 - 70 - 3, 20, 3,
                Component.literal("Roll速度"), speedRollSlider, Align.SPREAD));

        panel.arrangeElements();
        addRenderableWidget(panel);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int leftWidth = width - (Math.min(width / 3, 280) + 20);
        graphics.fill(0, 0, leftWidth, height, 0xCC222222);
        graphics.drawCenteredString(font, Component.literal("粒子预览(请移动视角观察)"), leftWidth / 2, height / 2, 0xFFFFFF);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        if (previewParticle != null) previewParticle.remove();
        super.onClose();
    }
}
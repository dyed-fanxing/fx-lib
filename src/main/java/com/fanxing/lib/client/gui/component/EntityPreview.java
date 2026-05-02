package com.fanxing.lib.client.gui.component;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.Consumer;

/**
 * 通用实体预览组件。
 * - 支持跟随模式（鼠标实时控制方向）和自由模式（拖拽累积旋转，带惯性）。
 * - 鼠标滚轮缩放。
 * - 可继承重写 {@link #afterRender(GuiGraphics, float)} (GuiGraphics, float)} 额外渲染。
 * - 惯性基于 {@link DeltaTracker}，与游戏引擎同步。
 */
public class EntityPreview<T extends Entity> extends AbstractWidget {
    public static final float INERTIA_DAMPING_PER_SEC = 0.95f;
    public static final float INERTIA_MIN_SPEED = 0.01F;
    public static final float MAX_ZOOM = 200F;
    public static final float MIN_ZOOM = 10F;
    public static final float SENSE = 0.01f;

    protected final T entity;
    protected float zoom = 75;


    // 自由模式数据
    protected float transX = 0f, transY = 0f;   // 屏幕平移偏移量
    protected float yaw = 0.0f;
    protected float pitch = 0.0f;
    protected float velYaw = 0.0f;
    protected float velPitch = 0.0f;
    protected boolean dragging;
    // 基础修正旋转（绕Z轴180°，配合正缩放使模型正立且面向相机）
    protected static final Quaternionf FIX = new Quaternionf().rotateZ((float) Math.PI);


    protected ToggleButton modeToggle;
    protected Button resetButton;        // 重置按钮
    protected Button p45Button;     // 45度按钮
    protected Button n45Button;     // -45度按钮

    public EntityPreview(int x, int y, int width, int height, T entity) {
        super(x, y, width, height, Component.empty());
        this.entity = entity;
        int btnWidth = width / 4;
        modeToggle = new ToggleButton(btnWidth, false,
                Component.translatable("gui.fx_lib.follow"), Component.translatable("gui.fx_lib.rotation"),
                Component.translatable("gui.fx_lib.look_at").append(Component.translatable("gui.fx_lib.mouse")),
                Component.translatable("gui.fx_lib.drag").append(Component.translatable("gui.fx_lib.rotation")),
                btn -> {
                    velYaw = velPitch = 0;
                    yaw = pitch = 0;
                    if (modeToggle.getState()) {
                        p45Button.visible = true;
                        n45Button.visible = true;
                        resetButton.visible = true;
                    } else {
                        p45Button.visible = false;
                        n45Button.visible = false;
                        resetButton.visible = false;
                    }
                    setPosition(getX(), getY());
                }
        );
        int btn45Width = btnWidth / 2 - 5;
        // -45度按钮
        n45Button = Button.builder(Component.literal("-45"), btn -> yaw -= (float) Math.toRadians(45))
                .width(btn45Width).build();
        n45Button.visible = false;
        // +45度按钮
        p45Button = Button.builder(Component.literal("+45"), btn -> yaw += (float) Math.toRadians(45))
                .width(btn45Width).build();
        p45Button.visible = false;
        // 重置按钮
        resetButton = Button.builder(Component.translatable("controls.reset"), btn -> yaw = pitch = velYaw = velPitch = transX = transY = 0)
                .width(btnWidth).build();
        resetButton.visible = false;
    }


    @Override
    public void setX(int x) {
        super.setX(x);
        if (modeToggle.getState()) {
            int spacing = 5;
            int n45w = n45Button.getWidth();
            int p45w = p45Button.getWidth();
            int modeW = modeToggle.getWidth();
            int resetW = resetButton.getWidth();
            int totalWidth = n45w + p45w + modeW + resetW + spacing * 3;
            int startX = x + (getWidth() - totalWidth) / 2;
            n45Button.setX(startX);
            p45Button.setX(startX + n45w + spacing);
            modeToggle.setX(startX + n45w + spacing + p45w + spacing);
            resetButton.setX(startX + n45w + spacing + p45w + spacing + modeW + spacing);
        } else {
            // 跟随模式：只显示模式切换按钮，居中
            int startX = x + (getWidth() - modeToggle.getWidth()) / 2;
            modeToggle.setX(startX);
        }
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        int btnHeight = 20;
        int btnY = getY() + getHeight() - btnHeight - 4;
        if (modeToggle.getState()) {
            n45Button.setY(btnY);
            p45Button.setY(btnY);
            modeToggle.setY(btnY);
            resetButton.setY(btnY);
        } else {
            modeToggle.setY(btnY);
        }
    }


    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (modeToggle.getState()) {
            if (!dragging) {
                DeltaTracker deltaTracker = Minecraft.getInstance().getTimer();
                float delta = deltaTracker.getGameTimeDeltaTicks();
                if (Math.abs(velYaw) > INERTIA_MIN_SPEED) {
                    yaw += velYaw * delta;
                    velYaw *= (float) Math.pow(INERTIA_DAMPING_PER_SEC, delta);
                    if (Math.abs(velYaw) < INERTIA_MIN_SPEED) velYaw = 0;
                } else velYaw = 0;
                if (Math.abs(velPitch) > INERTIA_MIN_SPEED) {
                    pitch += velPitch * delta;
                    velPitch *= (float) Math.pow(INERTIA_DAMPING_PER_SEC, delta);
                } else velPitch = 0;
            }
            renderDragRotation(graphics, getX(), getY(), getX() + getWidth(), getY() + getHeight(),
                    (int) zoom, 0.0625F, partialTick);
        } else {
            renderFollowMouse(graphics, getX(), getY(), getX() + getWidth(), getY() + getHeight(),
                    (int) zoom, 0.0625F, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public void visitWidgets(@NotNull Consumer<AbstractWidget> consumer) {
        consumer.accept(resetButton);
        consumer.accept(modeToggle);
        consumer.accept(p45Button);
        consumer.accept(n45Button);
        super.visitWidgets(consumer);
    }

    // ==================== 跟随模式渲染 ====================
//    protected void renderFollowMouse(GuiGraphics graphics, int left, int top, int right, int bottom, int size, float yOffset, float mouseX, float mouseY, float partialTick) {
//        float centerX = (left + right) / 2.0F;
//        float centerY = (top + bottom) / 2.0F;
//        float angleX = (float) Math.atan((centerX-mouseX ));
//        float angleY = (float) Math.atan((centerY-mouseY ));
//        renderFollowAngle(graphics, centerX, centerY, size, yOffset, -angleX, -angleY,partialTick);
//    }

    protected void renderFollowMouse(GuiGraphics graphics, int left, int top, int right, int bottom, int size, float yOffset, float mouseX, float mouseY, float partialTick) {
        float centerX = (left + right) / 2.0F;
        float centerY = (top + bottom) / 2.0F;

        // 获取实体眼睛的世界高度（米）和模型总高
        float eyeHeightWorld = entity.getEyeHeight();
        float modelHeightWorld = entity.getBbHeight();
        // 眼睛在模型中的比例（0=脚底，1=头顶）
        float eyeRatio = eyeHeightWorld / modelHeightWorld;
        // 渲染时模型的像素高度（近似为 size，因为预览区域是正方形且模型适配）
        float modelScreenHeight = size;
        // 眼睛相对于组件中心的像素偏移（模型中心在屏幕中心，脚底在中心下方 modelScreenHeight/2 处）
        float eyeScreenOffset = modelScreenHeight * (eyeRatio - 0.5f);
        float eyeScreenY = centerY - eyeScreenOffset; // 因为屏幕Y轴向下，减偏移使眼睛向上

        float dx = mouseX - centerX;
        float dy = mouseY - eyeScreenY;

        // 焦距：让鼠标移动到组件边缘时产生约 45° 的旋转（可根据喜好调整系数）
        float focal = getWidth() * 0.8f;

        float yaw = (float) Math.toDegrees(Math.atan2(dx, focal));
        float pitch = (float) Math.toDegrees(Math.atan2(dy, focal)); // 负号：鼠标向上（dy<0）时 pitch 为正（抬头）

        // 限制最大角度避免翻转
        yaw = Mth.clamp(yaw, -85, 85);
        pitch = Mth.clamp(pitch, -70, 70);

        renderFollowAngle(graphics, centerX, centerY, size, yOffset, yaw, pitch, partialTick);
    }

    protected void renderFollowAngle(GuiGraphics graphics, float centerX, float centerY, int size, float yOffset, float yaw, float pitch, float partialTick) {
        float yRot = entity.getYRot();
        float xRot = entity.getXRot();
        Quaternionf finalRot = new Quaternionf()
                .rotateY(yaw * Mth.DEG_TO_RAD)
                .rotateX(pitch * Mth.DEG_TO_RAD);
        Vector3f offset = new Vector3f(0.0F, entity.getBbHeight() / 2.0F + yOffset, 0.0F);
        renderEntity(graphics, centerX, centerY, size, offset, finalRot, null, partialTick);
        entity.setYRot(yRot);
        entity.setXRot(xRot);
    }

    protected void renderDragRotation(GuiGraphics graphics, int left, int top, int right, int bottom, int size, float yOffset, float partialTick) {
        float centerX = (left + right) / 2.0F;
        float centerY = (top + bottom) / 2.0F;

        float oldYRot = entity.getYRot();
        float oldXRot = entity.getXRot();
        entity.setYRot(0);
        entity.setXRot(0);

        Quaternionf yawRot = new Quaternionf().rotateY(-yaw);
        Quaternionf pitchRot = new Quaternionf().rotateX(pitch);
        Quaternionf finalRot = new Quaternionf().mul(FIX).mul(pitchRot).mul(yawRot);

        float scale = 1.0F;
        Vector3f offset = new Vector3f(0.0F, entity.getBbHeight() / 2.0F + yOffset * scale, 0.0F);
        float scaledSize = size / scale;
        renderEntity(graphics, centerX, centerY, scaledSize, offset, finalRot, null, partialTick);

        entity.setYRot(oldYRot);
        entity.setXRot(oldXRot);
    }

    // ==================== 实例渲染（含钩子） ====================
    protected void renderEntity(GuiGraphics graphics, float centerX, float centerY, float scale, Vector3f offset, Quaternionf rotation, @Nullable Quaternionf cameraOrientation, float partialTick) {
        graphics.pose().pushPose();
        graphics.pose().translate(centerX + transX, centerY + transY, 0F);
        graphics.pose().scale(scale, scale, scale);
        graphics.pose().translate(offset.x, offset.y, offset.z);
        graphics.pose().mulPose(rotation);

        Lighting.setupForEntityInInventory();
        RenderSystem.disableBlend();
        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        if (cameraOrientation != null) {
            dispatcher.overrideCameraOrientation(cameraOrientation.conjugate(new Quaternionf()).rotateY((float) Math.PI));
        }
        dispatcher.setRenderShadow(false);
        preRender(graphics, partialTick);
        dispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, graphics.pose(), graphics.bufferSource(), 15728880);
        afterRender(graphics, partialTick);
        graphics.flush();
        dispatcher.setRenderShadow(true);
        graphics.pose().popPose();
        Lighting.setupFor3DItems();
    }

    /**
     * 钩子方法：在实体渲染完成后（矩阵状态仍然有效）调用，可用于添加额外特效。
     */
    protected void preRender(GuiGraphics graphics, float partialTick) {
    }
    /**
     * 钩子方法：在实体渲染完成后（矩阵状态仍然有效）调用，可用于添加额外特效。
     */
    protected void afterRender(GuiGraphics graphics, float partialTick) {
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        dragging = true;
        if (Minecraft.getInstance().screen != null && Screen.hasShiftDown()) {
            // 按住 Shift：平移人物
            transX += (float) dragX;
            transY += (float) dragY;
            // 平移时停止旋转惯性
            velYaw = velPitch = 0f;
        } else {
            // 原有旋转逻辑
            if (Math.abs(dragX) > Math.abs(dragY)) {
                yaw += (float) (dragX * SENSE);
                velYaw = (float) (dragX * SENSE);
                velPitch = 0;
            } else {
                pitch += (float) (dragY * SENSE);
                velPitch = (float) (dragY * SENSE);
                velYaw = 0;
            }
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (isMouseOver(mouseX, mouseY)) {
            zoom += (float) scrollY;
            zoom = Mth.clamp(zoom, MIN_ZOOM, MAX_ZOOM);
            return true;
        }
        return false;
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
        // 可留空
    }
}
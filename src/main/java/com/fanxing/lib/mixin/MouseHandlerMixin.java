package com.fanxing.lib.mixin;
import com.fanxing.lib.item.capability.LockHorizontalView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {
    @ModifyArg(
            method = "turnPlayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"),
            index = 1   // 修改第二个参数（垂直旋转）
    )
    private double disableVerticalTurn(double originalYRot) {
        Minecraft mc = Minecraft.getInstance();
        Entity cameraEntity = mc.getCameraEntity();
        if(cameraEntity instanceof LivingEntity living && living.isUsingItem() && living.getUseItem().getItem() instanceof LockHorizontalView && mc.options.keySprint.isDown()) {
            living.setXRot(0);
            return 0.0;
        }
        return originalYRot;
    }
}
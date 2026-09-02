package com.fanxing.lib.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.fanxing.lib.entity.capability.Mountable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.world.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {

    private static final Logger log = LoggerFactory.getLogger(ClientPacketListenerMixin.class);

    /**
     * 双击shift脱离可乘坐实体
     */
    @ModifyVariable(method = "handleSetEntityPassengersPacket", at =@At(value = "STORE", ordinal = 0))
    private Component modifyMountOnboardMessage(Component value, @Local(ordinal = 0) Entity vehicle) {
        if (vehicle instanceof Mountable mountable && mountable.shouldDismountOnDoubleKey()) {
            Minecraft minecraft = Minecraft.getInstance();
            value = Component.translatable("mount.onboard.double.key.dismount",minecraft.options.keyShift.getTranslatedKeyMessage());
        }
        return value;
    }


    @Inject(method = "handleParticleEvent", at = @At(value = "HEAD"))
    public void handleParticleEvent(ClientboundLevelParticlesPacket packet, CallbackInfo ci) {
    }
}
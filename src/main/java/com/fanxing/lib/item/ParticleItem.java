package com.fanxing.lib.item;

import com.fanxing.lib.client.gui.screen.ParticleViewScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * @author dyed_fanxing
 * @date 2026/5/11 18:00
 */
public class ParticleItem extends Item {
    public ParticleItem(Properties properties) {
        super(properties);
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide) {
            // 打开 GUI
            Minecraft.getInstance().setScreen(new ParticleViewScreen());
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}

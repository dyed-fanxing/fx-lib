package com.fanxing.lib.item;

import com.fanxing.lib.client.gui.particle.screen.ParticleEditorScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * @author dyed_fanxing
 * @date 2026/5/11 18:00
 */
public class ParticleEditorItem extends Item {
    public ParticleEditorItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        if (level.isClientSide) {
            Minecraft.getInstance().setScreen(new ParticleEditorScreen(player.getItemInHand(hand)));
//            Minecraft.getInstance().setScreen(new SimpleTabTestScreen());

//            Minecraft.getInstance().setScreen(new DropdownTestScreen());
//            Minecraft.getInstance().setScreen(new PopupTestScreen());
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}

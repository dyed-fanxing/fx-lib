package com.fanxing.lib.mixin;

import net.minecraft.client.gui.components.EditBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Consumer;

@Mixin(EditBox.class)
public interface EditBoxAccessor {
    @Accessor("responder")
    Consumer<String> responder();
}

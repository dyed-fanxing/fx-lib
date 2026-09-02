package com.fanxing.lib.client.gui.component;

import net.minecraft.client.gui.components.AbstractWidget;
import org.jetbrains.annotations.Nullable;

/**
 * @author dyed_fanxing
 * @date 2026/5/16 17:09
 */
public interface PopupProvider {
    @Nullable
    AbstractWidget getPopup();
    void open();
    void close();
}

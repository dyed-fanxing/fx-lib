package com.fanxing.lib.client.gui.component.entry;

import net.minecraft.client.gui.components.ContainerObjectSelectionList;

/**
 * @author dyed_fanxing
 * @date 2026/5/20 23:10
 * 所有自定义 Entry 的公共基类，用于满足 ContainerObjectSelectionList 的泛型约束。
 */
public abstract class BaseContainerEntry extends ContainerObjectSelectionList.Entry<BaseContainerEntry> {
}

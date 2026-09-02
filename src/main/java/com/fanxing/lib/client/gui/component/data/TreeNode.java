package com.fanxing.lib.client.gui.component.data;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用树节点，支持泛型数据。
 * <p>
 * 每个节点包含名称、业务数据、子节点列表以及展开/折叠状态。
 *
 * @param <T> 节点携带的业务数据类型，例如纹理的 {@code ResourceLocation}、粒子配置对象等
 * @author dyed_fanxing
 */
public class TreeNode<T> {
    private final String name;                 // 节点显示名称
    private final T data;                      // 附加的业务数据（可为 null）
    private final List<TreeNode<T>> children = new ArrayList<>();
    private boolean expanded = true;           // 是否展开子节点
    private TreeNode<T> parent;                // 父节点引用

    /**
     * 构造树节点。
     *
     * @param name 节点显示名称（用于 UI 文本）
     * @param data 业务数据，无数据时可传 {@code null}
     */
    public TreeNode(String name, T data) {
        this.name = name;
        this.data = data;
    }

    /**
     * 添加子节点，并自动建立父子双向引用。
     *
     * @param child 子节点
     */
    public void addChild(TreeNode<T> child) {
        children.add(child);
        child.parent = this;
    }

    // ----- 访问器 ------------------------------------------------------

    /** 获取节点显示名称。 */
    public String getName() { return name; }

    /** 获取节点携带的业务数据。 */
    public T getData() { return data; }

    /** 获取子节点列表。 */
    public List<TreeNode<T>> getChildren() { return children; }

    /** 获取展开/折叠状态。 */
    public boolean isExpanded() { return expanded; }

    /** 设置展开/折叠状态。 */
    public void setExpanded(boolean expanded) { this.expanded = expanded; }

    /** 获取父节点。 */
    public TreeNode<T> getParent() { return parent; }
}
package com.fanxing.lib.client.gui.component;

import com.fanxing.lib.FxLib;
import com.fanxing.lib.client.gui.Scheme;
import com.fanxing.lib.client.gui.component.data.TreeNode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Tree<T> extends ObjectSelectionList<Tree<T>.NodeEntry> {
    private static final ResourceLocation ARROW_UP = ResourceLocation.fromNamespaceAndPath(FxLib.MOD_ID, "widget/arrow_up");
    private static final ResourceLocation ARROW_DOWN = ResourceLocation.fromNamespaceAndPath(FxLib.MOD_ID, "widget/arrow_down");
    private static final ResourceLocation ARROW_UP_HIGHLIGHTED = ResourceLocation.fromNamespaceAndPath(FxLib.MOD_ID, "widget/arrow_up_highlighted");
    private static final ResourceLocation ARROW_DOWN_HIGHLIGHTED = ResourceLocation.fromNamespaceAndPath(FxLib.MOD_ID, "widget/arrow_down_highlighted");

    private static final int ARROW_SIZE = 11;
    private static final int TEXT_HEIGHT = 9;
    private static final int INDENT_WIDTH = 12;

    private final Font font;
    private final List<TreeNode<T>> rootNodes;
    private final List<NodeEntry> flatEntries = new ArrayList<>();
    private Consumer<TreeNode<T>> onNodeSelected;
    private TreeNode<T> selectedNode;

    public Tree(Minecraft mc, int width, int height, int y, int itemHeight, List<TreeNode<T>> roots, Font font) {
        super(mc, width, height, y, itemHeight);
        this.font = font;
        this.rootNodes = roots;
        rebuildFlatList();
    }

    @Override
    public int getRowLeft() {
        return getX();
    }

    @Override
    public int getRowWidth() {
        return getWidth();
    }

    @Override
    protected int getScrollbarPosition() {
        return getX() + getWidth() - 6;
    }

    private void rebuildFlatList() {
        flatEntries.clear();
        for (TreeNode<T> root : rootNodes) {
            flatten(root, 0);
        }
        replaceEntries(flatEntries);
        if (selectedNode != null) {
            for (NodeEntry entry : flatEntries) {
                if (entry.containsNode(selectedNode)) {
                    setSelected(entry);
                    break;
                }
            }
        }
    }

    private void flatten(TreeNode<T> node, int depth) {
        List<TreeNode<T>> chain = collectSingleChain(node);
        if (chain.size() > 1) {
            flatEntries.add(new MergedEntry(chain, depth));
            TreeNode<T> last = chain.getLast();
            for (TreeNode<T> child : last.getChildren()) {
                flatten(child, depth + 1);
            }
        } else {
            flatEntries.add(new NormalEntry(node, depth));
            if (node.isExpanded()) {
                for (TreeNode<T> child : node.getChildren()) {
                    flatten(child, depth + 1);
                }
            }
        }
    }

    private List<TreeNode<T>> collectSingleChain(TreeNode<T> node) {
        List<TreeNode<T>> chain = new ArrayList<>();
        TreeNode<T> current = node;
        while (current != null && current.getChildren().size() == 1) {
            chain.add(current);
            current = current.getChildren().getFirst();
        }
        if (current != null && current.getChildren().isEmpty()) {
            chain.add(current);
        }
        return chain;
    }

    public void setOnNodeSelected(Consumer<TreeNode<T>> callback) {
        this.onNodeSelected = node -> {
            this.selectedNode = node;
            if (callback != null) callback.accept(node);
        };
    }
    public void expandNode(TreeNode<T> node) {
        if (node == null) return;
        // 递归展开父节点
        if (node.getParent() != null) {
            expandNode(node.getParent());
        }
        node.setExpanded(true);
        rebuildFlatList();
    }

    public void selectNode(TreeNode<T> target) {
        if (target == null) return;
        expandNode(target); // 确保父节点展开，该节点可见
        // 选中该节点（通过查找对应的 NodeEntry）
        for (NodeEntry entry : flatEntries) {
            if (entry.containsNode(target)) {
                setSelected(entry);
                ensureVisible(entry);
                // 触发回调，更新右侧纹理网格
                if (onNodeSelected != null) onNodeSelected.accept(target);
                break;
            }
        }
    }

    public void refresh() {
        rebuildFlatList();
    }



    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 264) { // 下箭头
            int currentIdx = children().indexOf(getSelected());
            if (currentIdx < getItemCount() - 1) {
                NodeEntry next = getEntry(currentIdx + 1);
                setSelected(next);
                ensureVisible(next);
                selectedNode = next.getFirstNode();
                if (onNodeSelected != null && selectedNode != null) onNodeSelected.accept(selectedNode);
            }
            return true;
        } else if (keyCode == 265) { // 上箭头
            int currentIdx = children().indexOf(getSelected());
            if (currentIdx > 0) {
                NodeEntry prev = getEntry(currentIdx - 1);
                setSelected(prev);
                ensureVisible(prev);
                selectedNode = prev.getFirstNode();
                if (onNodeSelected != null && selectedNode != null) onNodeSelected.accept(selectedNode);
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }


    @Override
    protected void renderSelection(GuiGraphics graphics, int top, int width, int height, int outerColor, int innerColor) {
        int left = getX() + (this.width - width) / 2;
        graphics.fill(left, top, left + width, top + height, Scheme.BG_COLOR_ITEM_SELECTED);
    }



    // 内部抽象基类，提供安全的方法来获取节点信息
    public abstract class NodeEntry extends Entry<NodeEntry> {
        public abstract boolean containsNode(TreeNode<T> node);

        public abstract TreeNode<T> getFirstNode();

        @Override
        public void renderBack(@NotNull GuiGraphics graphics, int index, int top, int left,
                               int width, int height, int mouseX, int mouseY,
                               boolean hovering, float partialTick) {
            if (hovering) graphics.fill(left, top, left + width, top + height, Scheme.BG_COLOR_ITEM_HOVER);
        }
    }


    // ==================== 普通节点条目 ====================
    public class NormalEntry extends NodeEntry {
        private final TreeNode<T> node;
        private final int depth;
        private int currentTop;

        public NormalEntry(TreeNode<T> node, int depth) {
            this.node = node;
            this.depth = depth;
        }

        @Override
        public boolean containsNode(TreeNode<T> node) {
            return this.node == node;
        }

        @Override
        public TreeNode<T> getFirstNode() {
            return node;
        }

        @Override
        public void render(@NotNull GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            this.currentTop = top;
            int indent = INDENT_WIDTH * (depth + 1);
            boolean hasChildren = !node.getChildren().isEmpty();

            int textStartX = left + indent;
            int textEndX;
            if (hasChildren) {
                textEndX = left + width - ARROW_SIZE - INDENT_WIDTH;
            } else {
                textEndX = left + width - INDENT_WIDTH;
            }
            int textMaxWidth = textEndX - textStartX;
            int textY = top + (height - TEXT_HEIGHT) / 2;

            if (hasChildren) {
                int arrowX = left + width - ARROW_SIZE - INDENT_WIDTH;
                int arrowY = top + (height - ARROW_SIZE) / 2;
                ResourceLocation sprite = node.isExpanded() ? ARROW_UP : ARROW_DOWN;
                boolean overArrow = mouseX >= arrowX && mouseX <= arrowX + ARROW_SIZE && mouseY >= arrowY && mouseY <= arrowY + ARROW_SIZE;
                if (overArrow) {
                    sprite = node.isExpanded() ? ARROW_UP_HIGHLIGHTED : ARROW_DOWN_HIGHLIGHTED;
                }
                graphics.blitSprite(sprite, arrowX, arrowY, ARROW_SIZE, ARROW_SIZE);
            }

            if (textMaxWidth > 0) {
                String text = node.getName();
                int textWidth = font.width(text);
                int color = (mouseX >= left && mouseX <= left + width && mouseY >= top && mouseY <= top + height) ||
                        node == Tree.this.selectedNode ? 0xFFFF55 : 0xFFFFFF;
                if (textWidth > textMaxWidth) {
                    AbstractWidget.renderScrollingString(graphics, font, Component.literal(text), textStartX, top, textEndX, top + height, color);
                } else {
                    graphics.drawString(font, text, textStartX, textY, color);
                }
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (mouseY >= currentTop && mouseY <= currentTop + itemHeight &&
                    mouseX >= getRowLeft() && mouseX <= getRowLeft() + getRowWidth()) {
                boolean hasChildren = !node.getChildren().isEmpty();
                if (hasChildren) {
                    int arrowX = getRowLeft() + getRowWidth() - ARROW_SIZE - INDENT_WIDTH;
                    int arrowY = currentTop + (itemHeight - ARROW_SIZE) / 2;
                    if (mouseX >= arrowX && mouseX <= arrowX + ARROW_SIZE && mouseY >= arrowY && mouseY <= arrowY + ARROW_SIZE) {
                        node.setExpanded(!node.isExpanded());
                        rebuildFlatList();
                        return true;
                    }
                }
                if (onNodeSelected != null) onNodeSelected.accept(node);
                selectedNode = node;
                Tree.this.setSelected(this);
                return true;
            }
            return false;
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (keyCode == 257 || keyCode == 32) {
                boolean hasChildren = !node.getChildren().isEmpty();
                if (hasChildren) {
                    node.setExpanded(!node.isExpanded());
                    rebuildFlatList();
                } else if (onNodeSelected != null) onNodeSelected.accept(node);
                return true;
            } else if (keyCode == 263) {
                if (node.isExpanded() && !node.getChildren().isEmpty()) {
                    node.setExpanded(false);
                    rebuildFlatList();
                }
                return true;
            } else if (keyCode == 262) {
                if (!node.getChildren().isEmpty() && !node.isExpanded()) {
                    node.setExpanded(true);
                    rebuildFlatList();
                }
                return true;
            }
            return false;
        }

        @Override
        public @NotNull Component getNarration() {
            return Component.literal(node.getName());
        }
    }

    // ==================== 合并单链条目 ====================
    private record ClickArea(int startX, int endX, TreeNode<?> node) {
    }

    public class MergedEntry extends NodeEntry {
        private final List<TreeNode<T>> chain;
        private final int depth;
        private final List<ClickArea> clickAreas = new ArrayList<>();
        private int currentTop;

        public MergedEntry(List<TreeNode<T>> chain, int depth) {
            this.chain = chain;
            this.depth = depth;
        }

        @Override
        public boolean containsNode(TreeNode<T> node) {
            return chain.contains(node);
        }

        @Override
        public TreeNode<T> getFirstNode() {
            return chain.isEmpty() ? null : chain.getFirst();
        }

        @Override
        public void render(@NotNull GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            this.currentTop = top;
            int indent = INDENT_WIDTH * (depth + 1);   // 修改这里
            int currentX = left + indent;
            clickAreas.clear();
            Font f = Tree.this.font;
            if (f == null) return;

            int textY = top + (height - TEXT_HEIGHT) / 2;
            for (int i = 0; i < chain.size(); i++) {
                TreeNode<T> node = chain.get(i);
                String name = node.getName();
                if (name == null || name.isEmpty()) name = "?";
                int nameWidth = f.width(name);
                clickAreas.add(new ClickArea(currentX, currentX + nameWidth, node));
                boolean hoverText = mouseX >= currentX && mouseX <= currentX + nameWidth && mouseY >= top && mouseY <= top + height;
                boolean isSelected = node == Tree.this.selectedNode;
                int color = (hoverText || isSelected) ? 0xFFFF55 : 0xFFFFFF;
                graphics.drawString(f, name, currentX, textY, color);
                currentX += nameWidth;
                if (i < chain.size() - 1) {
                    String arrow = "➡";
                    graphics.drawString(f, arrow, currentX, textY, 0xFFFFFF);
                    currentX += f.width(arrow);
                }
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (mouseY >= currentTop && mouseY <= currentTop + itemHeight &&
                    mouseX >= getRowLeft() && mouseX <= getRowLeft() + getRowWidth()) {
                for (ClickArea area : clickAreas) {
                    if (mouseX >= area.startX && mouseX <= area.endX) {
                        if (onNodeSelected != null) onNodeSelected.accept((TreeNode<T>) area.node);
                        selectedNode = (TreeNode<T>) area.node;
                        Tree.this.setSelected(this);
                        return true;
                    }
                }
                if (!chain.isEmpty() && onNodeSelected != null) {
                    onNodeSelected.accept(chain.getFirst());
                    selectedNode = chain.getFirst();
                    Tree.this.setSelected(this);
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (chain.isEmpty()) return false;
            int currentIdx = -1;
            for (int i = 0; i < chain.size(); i++) {
                if (chain.get(i) == Tree.this.selectedNode) {
                    currentIdx = i;
                    break;
                }
            }
            if (keyCode == 263) {
                if (currentIdx == -1) currentIdx = 0;
                else if (currentIdx > 0) currentIdx--;
                else return true;
                TreeNode<T> target = chain.get(currentIdx);
                if (onNodeSelected != null) onNodeSelected.accept(target);
                selectedNode = target;
                Tree.this.setSelected(this);
                return true;
            } else if (keyCode == 262) {
                if (currentIdx == -1) currentIdx = 0;
                else if (currentIdx < chain.size() - 1) currentIdx++;
                else return true;
                TreeNode<T> target = chain.get(currentIdx);
                if (onNodeSelected != null) onNodeSelected.accept(target);
                selectedNode = target;
                Tree.this.setSelected(this);
                return true;
            } else if (keyCode == 257 || keyCode == 32) {
                if (currentIdx >= 0) {
                    if (onNodeSelected != null) onNodeSelected.accept(chain.get(currentIdx));
                    selectedNode = chain.get(currentIdx);
                } else {
                    if (onNodeSelected != null) onNodeSelected.accept(chain.getFirst());
                    selectedNode = chain.getFirst();
                }
                Tree.this.setSelected(this);
                return true;
            }
            return false;
        }

        @Override
        public @NotNull Component getNarration() {
            return Component.literal("Merged entry");
        }
    }
}
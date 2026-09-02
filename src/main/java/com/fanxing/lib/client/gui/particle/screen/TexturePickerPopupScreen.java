package com.fanxing.lib.client.gui.particle.screen;

import com.fanxing.lib.FxLib;
import com.fanxing.lib.client.gui.component.Tree;
import com.fanxing.lib.client.gui.component.data.TreeNode;
import com.fanxing.lib.client.gui.particle.TextureGrid;
import com.fanxing.lib.client.gui.screen.PopupManageScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 纹理选择弹窗，左侧树形结构，右侧纹理网格。
 */
public class TexturePickerPopupScreen extends PopupManageScreen {
    protected final Consumer<ResourceLocation> onTextureSelected;
    protected Tree<ResourceLocation> treeView;
    protected TextureGrid textureGrid;
    protected EditBox searchBox;
    protected List<ResourceLocation> currentTextures = new ArrayList<>();
    protected List<TreeNode<ResourceLocation>> treeRoots;
    protected Map<TreeNode<ResourceLocation>, List<ResourceLocation>> folderTextureMap = new HashMap<>();
    protected ParticleEditorScreen particleEditor;

    private static final List<String> SCAN_PATHS = List.of(
            "textures/particle",
            "textures/gui",
            "textures/misc"
    );

    // 在构造函数中构建所有控件（不依赖宽高）
    public TexturePickerPopupScreen(Consumer<ResourceLocation> onTextureSelected,ParticleEditorScreen particleEditor) {
        super(Component.translatable("texture_picker.title"));
        this.onTextureSelected = onTextureSelected;
        // 扫描纹理并构建树数据（不依赖屏幕尺寸）
        List<ResourceLocation> allTextures = scanAllTextures();
        treeRoots = buildTextureTree(allTextures);
        // 创建纹理网格（暂时位置为0）
        textureGrid = new TextureGrid(0, 0, 0, 0, this::onTextureChosen);
        this.particleEditor = particleEditor;
    }

    @Override
    protected void init() {
        super.init();
        if(treeView == null){
            treeView = new Tree<>(minecraft, 200, height - 40, 20, 20, treeRoots, font);
            treeView.setOnNodeSelected(this::onTreeNodeSelected);
            // 如果存在上次选中的纹理，自动定位
            if (particleEditor.currLayer.renderType.texture != null) locateAndSelectTexture(particleEditor.currLayer.renderType.texture);
            else if (!treeRoots.isEmpty()) onTreeNodeSelected(treeRoots.getFirst());
        }

        treeView.setX(10);
        treeView.setY(20);
        treeView.setHeight(height - 40);
        treeView.setWidth(200);
        addRenderableWidget(treeView);

        int gridX = 220;
        int gridWidth = width - gridX - 10;
        int gridHeight = height - 40;
        textureGrid.setX(gridX);
        textureGrid.setY(20);
        textureGrid.setWidth(gridWidth);
        textureGrid.setHeight(gridHeight);
        if(particleEditor.currLayer.renderType.texture != null) {
            textureGrid.select(particleEditor.currLayer.renderType.texture);
        }
        addRenderableWidget(textureGrid);
        if(searchBox == null){
            // 创建搜索框
            searchBox = new EditBox(font, 0, 0, 200, 18, Component.translatable("texture_picker.search"));
            searchBox.setResponder(this::filterTextures);
        }
        searchBox.setX(10);
        searchBox.setY(height - 30);
        searchBox.setWidth(200);
        addRenderableWidget(searchBox);

        // 刷新按钮
        addRenderableWidget(Button.builder(Component.literal("R"), btn -> refresh())
                .bounds(220, height - 30, 30, 20).build());

        // 关闭按钮
        addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, btn -> onClose())
                .bounds(width - 100, height - 30, 90, 20).build());
    }



    // ========== 纹理扫描与树构建 ==========
    private List<ResourceLocation> scanAllTextures() {
        ResourceManager rm = Minecraft.getInstance().getResourceManager();
        Set<ResourceLocation> result = new HashSet<>();
        for (String path : SCAN_PATHS) {
            String prefix = path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
            rm.listResources(prefix, loc -> loc.getPath().endsWith(".png"))
                    .forEach((loc, res) -> result.add(loc));
        }

        return new ArrayList<>(result);
    }

    private List<TreeNode<ResourceLocation>> buildTextureTree(List<ResourceLocation> textures) {
        folderTextureMap.clear();
        Map<String, TreeNode<ResourceLocation>> namespaceNodes = new HashMap<>();

        for (ResourceLocation loc : textures) {
            String namespace = loc.getNamespace();
            String fullPath = loc.getPath();
            int idx = fullPath.indexOf("textures/");
            if (idx < 0) continue;
            String relative = fullPath.substring(idx + "textures/".length());
            String[] parts = relative.split("/");
            if (parts.length == 0) continue;

            TreeNode<ResourceLocation> nsNode = namespaceNodes.get(namespace);
            if (nsNode == null) {
                nsNode = new TreeNode<>(namespace, null);
                namespaceNodes.put(namespace, nsNode);
                folderTextureMap.put(nsNode, new ArrayList<>());
            }

            TreeNode<ResourceLocation> current = nsNode;
            for (int i = 0; i < parts.length - 1; i++) {
                String folder = parts[i];
                TreeNode<ResourceLocation> child = findChild(current, folder);
                if (child == null) {
                    child = new TreeNode<>(folder, null);
                    current.addChild(child);
                    folderTextureMap.put(child, new ArrayList<>());
                }
                current = child;
            }
            folderTextureMap.get(current).add(loc);
        }

        // 纹理排序
        for (List<ResourceLocation> list : folderTextureMap.values()) {
            list.sort(Comparator.comparing(ResourceLocation::toString));
        }
        // 树排序
        for (TreeNode<ResourceLocation> nsNode : namespaceNodes.values()) {
            sortTree(nsNode);
        }
        // 命名空间排序
        List<TreeNode<ResourceLocation>> sorted = new ArrayList<>(namespaceNodes.values());
        sorted.sort((a, b) -> {
            String nameA = a.getName();
            String nameB = b.getName();
            if (nameA.equals("minecraft")) return -1;
            if (nameB.equals("minecraft")) return 1;
            if (nameA.equals(FxLib.MOD_ID)) return -1;
            if (nameB.equals(FxLib.MOD_ID)) return 1;
            return nameA.compareTo(nameB);
        });
        return sorted;
    }

    private void sortTree(TreeNode<ResourceLocation> node) {
        node.getChildren().sort(Comparator.comparing(TreeNode::getName));
        for (TreeNode<ResourceLocation> child : node.getChildren()) {
            sortTree(child);
        }
    }

    private TreeNode<ResourceLocation> findChild(TreeNode<ResourceLocation> parent, String name) {
        for (TreeNode<ResourceLocation> child : parent.getChildren()) {
            if (child.getName().equals(name)) return child;
        }
        return null;
    }

    // ========== 事件 ==========
    private void refresh() {
        List<ResourceLocation> all = scanAllTextures();
        treeRoots = buildTextureTree(all);
        // 重建树
        removeWidget(treeView);
        treeView = new Tree<>(minecraft, 200, height - 40, 20, 20, treeRoots, font);
        treeView.setX(10);
        treeView.setY(20);
        treeView.setOnNodeSelected(this::onTreeNodeSelected);
        addRenderableWidget(treeView);
        if (!treeRoots.isEmpty()) onTreeNodeSelected(treeRoots.getFirst());
    }

    private void onTreeNodeSelected(TreeNode<ResourceLocation> node) {
        currentTextures = getAllTexturesUnderNode(node);
        textureGrid.updateTextures(currentTextures);
        textureGrid.setScrollAmount(0);
    }

    private List<ResourceLocation> getAllTexturesUnderNode(TreeNode<ResourceLocation> node) {
        List<ResourceLocation> result = new ArrayList<>(folderTextureMap.getOrDefault(node, new ArrayList<>()));
        for (TreeNode<ResourceLocation> child : node.getChildren()) {
            result.addAll(getAllTexturesUnderNode(child));
        }
        return result;
    }

    private void filterTextures(String keyword) {
        if (keyword.isEmpty()) {
            textureGrid.updateTextures(currentTextures);
            return;
        }
        String lower = keyword.toLowerCase();
        List<ResourceLocation> filtered = currentTextures.stream()
                .filter(loc -> loc.getPath().toLowerCase().contains(lower))
                .collect(Collectors.toList());
        textureGrid.updateTextures(filtered);
    }


    private void onTextureChosen(ResourceLocation texture) {
        onTextureSelected.accept(texture);
        particleEditor.currLayer.renderType.texture = texture;
        onClose();
    }

    private void locateAndSelectTexture(ResourceLocation texture) {
        TreeNode<ResourceLocation> folderNode = findFolderNodeContainingTexture(treeRoots, texture);
        if (folderNode == null) return;
        treeView.selectNode(folderNode);
    }

    private TreeNode<ResourceLocation> findFolderNodeContainingTexture(List<TreeNode<ResourceLocation>> nodes, ResourceLocation texture) {
        for (TreeNode<ResourceLocation> node : nodes) {
            // 检查当前节点下的纹理列表
            List<ResourceLocation> texList = folderTextureMap.get(node);
            if (texList != null && texList.contains(texture)) {
                return node;
            }
            // 递归检查子节点
            TreeNode<ResourceLocation> childResult = findFolderNodeContainingTexture(node.getChildren(), texture);
            if (childResult != null) return childResult;
        }
        return null;
    }

    @Override
    public void onClose() {
        super.onClose();
        Minecraft.getInstance().setScreen(particleEditor);
    }
}
package com.fanxing.lib.client.gui;

/**
 * @author dyed_fanxing
 * @date 2026/5/13 15:39
 * GUI 全局配置（颜色、尺寸等），统一管理，便于全局修改。
 * 命名规则：<部件>_<属性>，例如 BORDER_COLOR_NORMAL、SPLITTER_SIZE。
 */
public interface Scheme {
    // ========== 边框 ==========
    /** 普通边框颜色 */
    int BORDER_COLOR_NORMAL = 0xFF888888;
    /** 高亮边框颜色 */
    int BORDER_COLOR_HOVER = 0xFFFFFFFF;
    /** 选中边框颜色 */
    int BORDER_COLOR_SELECTED = 0xFFFFFFFF;


    // ========== 背景 ==========
    /** 内容区域背景色 */
    int BG_COLOR_BLACK_TRANSPARENT = 0x70000000;      // 112透明度的黑色，如果启用混合则表示半透明黑色，如果不启用则就是黑色
    int BG_COLOR_BLACK = 0xFF000000;      //纯黑色
    /** 内容区域背景色 */
    int BG_COLOR_GREY = 0xFF222222;      // 112透明度的黑色，如果启用混合则表示半透明黑色，如果不启用则就是黑色
    /** 普通项目背景色 */
    int BG_COLOR_ITEM = 0xFF3A3A4A;
    /** 悬停项目背景色 */
    int BG_COLOR_ITEM_HOVER = 0xFF686868;
    /** 选中项目背景色 */
    int BG_COLOR_ITEM_SELECTED = 0xFF888888;

    // ========== 文本 ==========
    /** 普通文本颜色 */
    int TEXT_COLOR_NORMAL = 0xFFE0E0E0;
    /** 暗色文本颜色 */
    int TEXT_COLOR_DARK = 0xFFAAAAAA;
    /** 高亮文本颜色 */
    int TEXT_COLOR_HIGHLIGHT = 0xFFFFFFFF;
    /** 高亮文本颜色 */
    int TEXT_COLOR_HINT = 0xFF808080;


    // ========== 分隔条 ==========
    /** 分隔条默认颜色 */
    int SPLITTER_COLOR_NORMAL = 0xFF888888;
    /** 分隔条悬停颜色 */
    int SPLITTER_COLOR_HOVER = 0xFFCCCCCC;
    /** 分隔条拖拽颜色 */
    int SPLITTER_COLOR_DRAGGING = 0xFFFFFFFF;
    /** 分隔条宽度 */
    int SPLITTER_SIZE = 3;

    // ========== 滚动条 ==========
    /** 滚动条颜色 */
    int SCROLLBAR_COLOR = 0xFFAAAAAA;
    /** 滚动条宽度 */
    int SCROLLBAR_SIZE = 8;

    // ========== 通用尺寸 ==========
    /** 内边距 */
    int PADDING_SIZE = 4;
    /** 组件间距 */
    int GAP_SIZE = 6;
    /** 按钮默认高度 */
    int BUTTON_HEIGHT = 20;
    /** 输入框默认高度 */
    int INPUT_HEIGHT = 20;
}
package com.fanxing.lib.mixin.opengl;

import com.mojang.blaze3d.platform.Window;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author dyed_fanxing
 * @since 2026/7/5 20:41
 */
@Mixin(Window.class)
public abstract class WindowMixin {

    @Shadow @Final private static Logger LOGGER;

    /**
     * 修改 GLFW_CONTEXT_VERSION_MAJOR (139266) 的值
     * 原版：glfwWindowHint(139266, 3) → 改为 4
     */
    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwWindowHint(II)V", ordinal = 2),index = 1 )
    private int setMajorVersion(int original) {
        return 4; // OpenGL 4.x
    }

    /**
     * 修改 GLFW_CONTEXT_VERSION_MINOR (139267) 的值
     * 原版：glfwWindowHint(139267, 2) → 改为 3
     */
    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwWindowHint(II)V", ordinal = 3), index = 1)
    private int setMinorVersion(int original) {
        return 3; // OpenGL 4.3
    }


    // 在窗口完全初始化后打印版本
    @Inject(method = "<init>", at = @At("TAIL"))
    private void afterWindowCreated(CallbackInfo ci) {
        long windowHandle = GLFW.glfwGetCurrentContext(); // 或直接使用窗口句柄
        int major = GLFW.glfwGetWindowAttrib(windowHandle, GLFW.GLFW_CONTEXT_VERSION_MAJOR);
        int minor = GLFW.glfwGetWindowAttrib(windowHandle, GLFW.GLFW_CONTEXT_VERSION_MINOR);
        LOGGER.info("Requested OpenGL context: {}.{}", major, minor);
    }
}
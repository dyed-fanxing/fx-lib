package com.fanxing.lib.util;

public class DebugUtils {

    /**
     * 打印当前线程的调用栈，最多输出前 rows 行（忽略本方法本身）。
     *
     * @param rows 要输出的栈帧行数（大于0）
     */
    public static void printDumpStack(int rows) {
        if (rows <= 0) {
            return;
        }
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        // 跳过 getStackTrace() 和本方法 printDumpStack 这两帧
        int start = 2;
        int end = Math.min(start + rows, stackTrace.length);
        for (int i = start; i < end; i++) {
            System.err.println(stackTrace[i]);
        }
    }
}
package com.example.javaPractice.Config;

/**
 * 基于 ThreadLocal 封装的工具类，用于保存和获取当前用户的 id
 */
// 已检查，书写正确
public class BaseContext {
    private static ThreadLocal<Long> threadLocak = new ThreadLocal<>();
    public static void setCurrentId(Long id) {
        threadLocak.set(id);
    }
    public static Long getCurrentId() {
        return threadLocak.get();
    }
}

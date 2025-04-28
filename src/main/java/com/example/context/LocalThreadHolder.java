package com.example.context;

import java.util.HashMap;
import java.util.Map;

/**
 * 线程级用户信息存储工具
 * 1. 保存当前登录用户ID和角色
 * 2. 每个请求线程独立存储
 * 3. 请求结束自动清理
 */
public class LocalThreadHolder {
    // 使用ThreadLocal存储用户信息（每个线程独立）
    private static final ThreadLocal<Map<String, Integer>> USER_HOLDER = new ThreadLocal<>();

    /**
     * 保存当前用户信息（登录后调用）
     */
    public static void setUserId(Integer userId, Integer userRole) {
        Map<String, Integer> userInfo = new HashMap<>();
        userInfo.put("userId", userId);
        userInfo.put("userRole", userRole);
        USER_HOLDER.set(userInfo); // 绑定到当前线程
    }

    /**
     * 获取当前线程用户ID
     */
    public static Integer getUserId() {
        return USER_HOLDER.get().get("userId");
    }

    /**
     * 获取当前线程用户角色
     */
    public static Integer getRoleId() {
        return USER_HOLDER.get().get("userRole");
    }

    /**
     * 清理当前线程的用户数据（请求结束时调用）
     */
    public static void clear() {
        USER_HOLDER.remove(); // 防止内存泄漏
    }
}

package com.example.utils;

import java.util.UUID;

/**
 * ID生成工具类，提供文件唯一标识符生成功能
 */
public class IdFactoryUtil {

    /**
     * 生成文件唯一标识符（UUID截取形式）
     *
     * 生成逻辑：
     * 1. 创建全局唯一标识符（UUID）
     * 2. 转换为字符串形式（如：550e8400-e29b-41d4-a716-446655440000）
     * 3. 截取字符串的第2位到第8位（共7个字符，如：50e8400）
     *
     * 注意：截取后的ID唯一性概率显著降低，适用于对唯一性要求较低的场景
     *
     * @return 长度为7的字符串标识符
     */
    public static String getFileId() {
        return UUID.randomUUID().toString().substring(1, 8);
    }
}
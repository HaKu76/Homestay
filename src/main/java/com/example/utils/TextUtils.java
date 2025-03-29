package com.example.utils;

import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;

/**
 * 文本处理工具类
 */
public class TextUtils {

    /**
     * 判断指定ID是否存在于字符串中
     *
     * @param text 源字符串（可能包含逗号分隔的ID列表）
     * @param id   需要检查的ID
     * @return 是否存在该ID
     */
    public static Boolean exitId(String text, Integer id) {
        // 1. 若输入字符串或ID为null，直接返回false
        if (text == null || id == null) {
            return false;
        }
        // 2. 将ID转为字符串后检查是否存在于源字符串中
        String idStr = String.valueOf(id);
        return text.contains(idStr);
    }

    /**
     * 从字符串中移除指定ID并返回新字符串
     *
     * @param text 源字符串（逗号分隔的ID列表）
     * @param id   需要移除的ID
     * @return 过滤后的字符串，或null（当无剩余元素）
     */
    public static String split(String text, Integer id) {

        if (text == null || id == null) {
            return null;
        }
        // 1. 按逗号分割字符串为ID列表
        String idStr = String.valueOf(id);
        List<String> filtered = new ArrayList<>();
        for (String s : text.split(",")) {
            // 过滤掉与目标ID匹配的元素
            if (!s.equals(idStr)) {
                filtered.add(s);
            }
        }
        // 将剩余元素用逗号连接，若无元素则返回null
        if (filtered.isEmpty()) {
            return null;
        }
        return String.join(",", filtered);
    }

    /**
     * 将指定ID拼接到字符串末尾
     *
     * @param text 源字符串（可能为null）
     * @param id   需要拼接的ID
     * @return 新字符串，格式：原字符串+","+"ID"
     */
    public static String join(String text, Integer id) {
        // 1. 若源字符串为null，直接返回ID的字符串形式
        if (text == null) {
            return String.valueOf(id);
        }
        // 2. 否则在源字符串后追加逗号和ID
        return text + "," + id;
    }

    /**
     * 从富文本中提取纯文本并截断
     *
     * @param text       需要处理的富文本
     * @param limitCount 最大允许长度（小于等于0时返回原文本）
     * @return 截断后的文本（或原文本）
     */
    public static String extractText(String text, Integer limitCount) {
        if (text == null) {
            return null;
        }
        // 1. 使用Jsoup解析富文本，提取纯文本内容
        String plainText = Jsoup.parse(text, "utf-8").text();
        // 2. 当limitCount为0或负数时返回原文本
        if (limitCount <= 0) {
            return plainText;
        }
        if (plainText.length() <= limitCount) {
            return plainText;
        }
        // 3. 若文本长度超过指定限制，则截断并在末尾添加"..."
        return plainText.substring(0, limitCount) + "...";
    }
}

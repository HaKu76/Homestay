package com.example.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * 路径处理工具类
 */
public class PathUtils {
    /**
     * 获取类加载器根路径（跨平台兼容）
     *
     * @return 类加载器根路径字符串
     */
    public static String getClassLoadRootPath() {
        String path = ""; // 最终返回的路径
        try {
            // 1. 获取原始路径并解码（处理URL编码如%20）
            String prePath = URLDecoder.decode(
                    PathUtils.class.getClassLoader().getResource("").getPath(),
                    "utf-8"
            ).replace("/target/classes", "");

            // 2. 根据操作系统类型调整路径格式
            String osName = System.getProperty("os.name");
            if (osName.toLowerCase().startsWith("mac")) {
                // Mac系统：移除末尾斜杠（如："/Users/project/" → "/Users/project"）
                path = prePath.substring(0, prePath.length() - 1);
            } else if (osName.toLowerCase().startsWith("windows")) {
                // Windows系统：移除开头斜杠和末尾斜杠（如："/C:/project/" → "C:/project"）
                path = prePath.substring(1, prePath.length() - 1);
            } else if (osName.toLowerCase().startsWith("linux")
                    || osName.toLowerCase().startsWith("unix")) {
                // Linux/Unix系统：移除末尾斜杠
                path = prePath.substring(0, prePath.length() - 1);
            } else {
                // 默认处理（按Windows逻辑处理）
                path = prePath.substring(1, prePath.length() - 1);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return path;
    }
}
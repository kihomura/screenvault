package com.kihomura.screenvault.enums;

public enum Category {
    MOVIE,
    TV_SHOW;

    /**
     * 从字符串转换为枚举值，增强容错处理
     */
    public static Category fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return MOVIE; // 默认值
        }

        // 规范化：去除所有空白字符，转为大写
        String normalized = text.trim().toUpperCase()
                .replaceAll("\\s+", "_") // 替换任何空白字符为下划线
                .replaceAll("[^A-Z_]", ""); // 移除任何非字母和下划线的字符

        try {
            return Category.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            // 记录原始值和规范化后的值，帮助调试
            System.err.println("警告：无法解析Category值: 原始='" + text +
                    "', 规范化后='" + normalized + "'");
            return MOVIE; // 默认返回MOVIE而不是抛出异常
        }
    }
}
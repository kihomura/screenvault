package com.kihomura.screenvault.enums;

public enum Category {
    MOVIE,
    TV_SHOW;

    public static Category fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return MOVIE; //default value
        }

        String normalized = text.trim().toUpperCase()
                .replaceAll("\\s+", "_")
                .replaceAll("[^A-Z_]", "");

        try {
            return Category.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            System.err.println("Warning: Unable to parse Category value: Raw='" + text +
                    "',Normalized to ='" + normalized + "'");
            return MOVIE;
        }
    }
}
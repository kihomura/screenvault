package com.kihomura.screenvault.enums;

import java.util.HashSet;
import java.util.Set;

public enum Genre {
    ACTION("Action"),
    ADVENTURE("Adventure"),
    ANIMATION("Animation"),
    COMEDY("Comedy"),
    CRIME("Crime"),
    DOCUMENTARY("Documentary"),
    DRAMA("Drama"),
    FAMILY("Family"),
    FANTASY("Fantasy"),
    HISTORY("History"),
    HORROR("Horror"),
    MUSIC("Music"),
    MYSTERY("Mystery"),
    ROMANCE("Romance"),
    SCI_FI_FANTASY("Sci-Fi & Fantasy"),
    TV_MOVIE("TV Movie"),
    THRILLER("Thriller"),
    WAR("War"),
    WESTERN("Western"),
    ACTION_ADVENTURE("Action & Adventure"),
    KIDS("Kids"),
    NEWS("News"),
    REALITY("Reality"),
    SOAP("Soap"),
    TALK("Talk"),
    WAR_POLITICS("War & Politics"),
    SCIENCE_FICTION("Science Fiction");

    private final String name;

    Genre(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * 从字符串转换为枚举值，处理空值情况
     * @param name 类型名称
     * @return 匹配的Genre枚举值，如果输入为空或找不到匹配则返回null
     */
    public static Genre fromString(String name) {
        // 处理空值情况
        if (name == null || name.trim().isEmpty()) {
            return null;
        }

        for (Genre genre : values()) {
            if (genre.getName().equalsIgnoreCase(name.trim())) {
                return genre;
            }
        }

        // 如果找不到匹配项，可以选择返回null而不是抛出异常
        System.err.println("Warning: Unknown genre: " + name + " - returning null");
        return null;
        // 或者保持原有行为，抛出异常
        // throw new IllegalArgumentException("Unknown genre: " + name);
    }

    public static Set<Genre> parseGenres(String genreString) {
        Set<Genre> genres = new HashSet<>();

        // 处理空字符串
        if (genreString == null || genreString.trim().isEmpty()) {
            return genres;
        }

        String[] genreNames = genreString.split(",\\s*");
        for (String name : genreNames) {
            try {
                Genre genre = fromString(name);
                if (genre != null) {
                    genres.add(genre);
                }
            } catch (IllegalArgumentException e) {
                System.err.println("Warning: Unrecognized genre - " + name);
            }
        }
        return genres;
    }
}
package com.kihomura.screenvault.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kihomura.screenvault.enums.Category;
import com.kihomura.screenvault.enums.Genre;
import com.kihomura.screenvault.enums.SourceType;
import lombok.Data;

import java.time.LocalDate;

@Data
@TableName("contents")
public class Content {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("title")
    private String title;

    @TableField("other_title")
    private String otherTitle;

    @TableField("country")
    private String country;

    @TableField("language")
    private String language;

    @TableField("description")
    private String description;

    @TableField("image")
    private String image;

    @TableField("release_date")
    private LocalDate releaseDate;

    @TableField("genre")
    private Genre genre;

    @TableField("category")
    private Category category;

    @TableField("source_type")
    private SourceType sourceType;

    @TableField("creator_id")
    private Integer creatorId;
}

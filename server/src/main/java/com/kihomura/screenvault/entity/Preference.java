package com.kihomura.screenvault.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("user_preference")
public class Preference {

    @TableId(value = "user_id")
    private Integer userId;

    @TableField(exist = false)
    private User user;

    @TableField("theme")
    private String theme;

    @TableField("bg_image")
    private String bgImage;

    @TableField("layout")
    private String layout;

    @TableField("update_time")
    private Timestamp updateTime;
}

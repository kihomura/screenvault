package com.kihomura.screenvault.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tag_content")
public class TagContent {

    @TableField("tag_id")
    private Integer tagId;

    @TableField("content_id")
    private Integer contentId;
}

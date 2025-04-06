package com.kihomura.screenvault.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.HashSet;
import java.util.Set;

@Data
@TableName("tags")
public class Tag {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("tag_name")
    private String tagName;

    @TableField("creator_id")
    private Integer creatorId;

    @TableField(exist = false)
    private Set<TagContent> tagContents = new HashSet<>();
}

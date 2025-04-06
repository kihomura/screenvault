package com.kihomura.screenvault.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@TableName("lists")
public class PlayList {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("creator_id")
    private Integer creatorId;

    @TableField("list_name")
    private String listName;

    @TableField("is_default")
    private Boolean isDefault;

    @TableField("create_date")
    private Date createDate;

    @TableField(exist = false)
    private Set<ListContent> listContents = new HashSet<>();
}

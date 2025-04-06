package com.kihomura.screenvault.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.sql.Timestamp;

@Data
@TableName("list_content")
public class ListContent {

    @TableField("list_id")
    private Integer listId;

    @TableField("content_id")
    private Integer contentId;

    @TableField("add_time")
    private Timestamp addTime;
}
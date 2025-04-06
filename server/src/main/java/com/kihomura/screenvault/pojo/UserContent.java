package com.kihomura.screenvault.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kihomura.screenvault.enums.Status;
import com.kihomura.screenvault.typehandler.ReviewDetailTypeHandler;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@TableName("user_content")
public class UserContent {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("user_id")
    private Integer userId;

    @TableField("content_id")
    private Integer contentId;

    @TableField("rate")
    private BigDecimal rate;

    @TableField(value = "review", typeHandler = ReviewDetailTypeHandler.class)
    private List<ReviewDetail> reviews;

    @TableField("status")
    private Status status;

    @TableField("watch_date")
    private LocalDate watchDate;
}

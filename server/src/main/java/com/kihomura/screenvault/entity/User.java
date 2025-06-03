package com.kihomura.screenvault.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("users")
public class User {
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Integer id;

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 30,
            message = "Username must be between 3 and 30 characters")
    @Pattern.List({
            @Pattern(
                    regexp = "^(?!.*@)(?!.*\\.)(?=\\w+$)[a-zA-Z0-9_]+$|^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
                    message = "Invalid username format"
            ),
            @Pattern(regexp = "^[a-zA-Z0-9_]+$",
                    message = "Username can only contain letters, numbers, and underscores")
    })
    @TableField("username")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
    @Pattern.List({
            @Pattern(regexp = ".*[A-Z].*",
                    message = "Password must contain at least one uppercase letter"),
            @Pattern(regexp = ".*[a-z].*",
                    message = "Password must contain at least one lowercase letter"),
            @Pattern(regexp = ".*\\d.*",
                    message = "Password must contain at least one number"),
            @Pattern(regexp = ".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*",
                    message = "Password must contain at least one special character")
    })
    @TableField("password")
    private String password;

    @TableField("nickname")
    private String nickname;

    @TableField("enabled")
    private boolean enabled = true;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    // OAuth2
    @TableField("provider")
    private String provider;

    @TableField("provider_id")
    private String providerId;

    public void initBeforeInsert() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateBeforeUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
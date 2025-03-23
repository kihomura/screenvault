package com.kihomura.screenvault.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Table(name="users")
@TableName("users")
@Entity
public class User {
    @Id
    @TableId(type = IdType.AUTO)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @TableField("id")
    private Long id;

    @Column(name = "username", unique = true, nullable = false, length = 50)
    @TableField("username")
    private String username;

    @Column(name = "password", nullable = false, length = 100)
    @TableField("password")
    private String password;

    @Column(name = "nickname", length = 50)
    @TableField("nickname")
    private String nickname;

    @Column(name = "enabled")
    @TableField("enabled")
    private boolean enabled = true;

    @Column(name = "created_at")
    @TableField("created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @TableField("updated_at")
    private LocalDateTime updatedAt;

    // OAuth2
    @Column(name = "provider")
    @TableField("provider")
    private String provider;

    @Column(name = "provider_id")
    @TableField("provider_id")
    private String providerId;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
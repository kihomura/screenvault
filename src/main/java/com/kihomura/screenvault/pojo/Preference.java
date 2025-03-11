package com.kihomura.screenvault.pojo;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Table(name="user_preference")
@Entity
public class Preference {
    @Id
    @Column(name = "user_id")
    private Integer userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "theme")
    private String theme;

    @Column(name = "bg_image")
    private String bgImage;

    @Column(name = "layout", columnDefinition = "json")
    private String layout;

    @Column(name = "update_time")
    private Timestamp updateTime;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getBgImage() {
        return bgImage;
    }

    public void setBgImage(String bgImage) {
        this.bgImage = bgImage;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Preference{" +
                "userId=" + userId +
                ", user=" + user +
                ", theme='" + theme + '\'' +
                ", bgImage='" + bgImage + '\'' +
                ", layout='" + layout + '\'' +
                ", updateTime=" + updateTime +
                '}';
    }
}

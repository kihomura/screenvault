package com.kihomura.screenvault.pojo;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

import java.sql.Date;

@Table(name="lists")
@Entity
public class PlayList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "list", cascade = CascadeType.ALL)
    private Set<ListContent> listContents = new HashSet<ListContent>();

    @Column(name = "list_name", length = 225)
    private String listName;

    @Column(name = "is_default")
    private Boolean isDefault;

    @Column(name = "create_date")
    private Date createDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<ListContent> getListContents() {
        return listContents;
    }

    public void setListContents(Set<ListContent> listContents) {
        this.listContents = listContents;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Override
    public String toString() {
        return "PlayList{" +
                "id=" + id +
                ", user=" + user +
                ", listContents=" + listContents +
                ", listName='" + listName + '\'' +
                ", isDefault=" + isDefault +
                ", createDate=" + createDate +
                '}';
    }
}

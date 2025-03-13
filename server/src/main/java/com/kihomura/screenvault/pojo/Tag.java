package com.kihomura.screenvault.pojo;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Table(name="tags")
@Entity
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "tag_name", length = 50)
    private String tagName;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL)
    private Set<TagContent> tagContents = new HashSet<TagContent>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<TagContent> getTagContents() {
        return tagContents;
    }

    public void setTagContents(Set<TagContent> tagContents) {
        this.tagContents = tagContents;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", tagName='" + tagName + '\'' +
                ", user=" + user +
                ", tagContents=" + tagContents +
                '}';
    }
}

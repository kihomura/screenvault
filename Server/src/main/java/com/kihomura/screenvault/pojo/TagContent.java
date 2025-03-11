package com.kihomura.screenvault.pojo;

import com.kihomura.screenvault.pojo.key.TagContentId;
import jakarta.persistence.*;

@Table(name = "tag_content")
@Entity
public class TagContent {

    @EmbeddedId
    private TagContentId id;

    @ManyToOne
    @MapsId("tagId")
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    @ManyToOne
    @MapsId("contentId")
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    public TagContentId getId() {
        return id;
    }

    public void setId(TagContentId id) {
        this.id = id;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "TagContent{" +
                "id=" + id +
                ", tag=" + tag +
                ", content=" + content +
                '}';
    }
}
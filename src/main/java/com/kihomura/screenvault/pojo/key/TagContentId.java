package com.kihomura.screenvault.pojo.key;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TagContentId implements Serializable {
    private Integer tagId;
    private Integer contentId;

    public TagContentId() {}

    public TagContentId(Integer tagId, Integer contentId) {
        this.tagId = tagId;
        this.contentId = contentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TagContentId)) return false;
        TagContentId that = (TagContentId) o;
        return Objects.equals(tagId, that.tagId) && Objects.equals(contentId, that.contentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tagId, contentId);
    }

}
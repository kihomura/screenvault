package com.kihomura.screenvault.pojo.key;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ListContentId implements Serializable {
    private Integer listId;
    private Integer contentId;

    public ListContentId() {}

    public ListContentId(Integer listId, Integer contentId) {
        this.listId = listId;
        this.contentId = contentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListContentId that = (ListContentId) o;
        return Objects.equals(listId, that.listId) && Objects.equals(contentId, that.contentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(listId, contentId);
    }
}

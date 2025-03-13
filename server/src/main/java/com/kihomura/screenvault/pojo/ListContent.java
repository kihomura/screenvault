package com.kihomura.screenvault.pojo;

import com.kihomura.screenvault.pojo.key.ListContentId;
import jakarta.persistence.*;

import java.sql.Timestamp;

@Table(name="list_content")
@Entity
public class ListContent {

    @EmbeddedId
    private ListContentId id;

    @ManyToOne
    @MapsId("listId")
    @JoinColumn(name = "list_id", nullable = false)
    private PlayList list;

    @ManyToOne
    @MapsId("contentId")
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @Column(name = "add_time")
    private Timestamp addTime;

    public ListContentId getId() {
        return id;
    }

    public void setId(ListContentId id) {
        this.id = id;
    }

    public PlayList getList() {
        return list;
    }

    public void setList(PlayList list) {
        this.list = list;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public Timestamp getAddTime() {
        return addTime;
    }

    public void setAddTime(Timestamp addTime) {
        this.addTime = addTime;
    }

    @Override
    public String toString() {
        return "ListContent{" +
                "id=" + id +
                ", list=" + list +
                ", content=" + content +
                ", addTime=" + addTime +
                '}';
    }
}
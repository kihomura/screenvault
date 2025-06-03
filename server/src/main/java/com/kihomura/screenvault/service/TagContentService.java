package com.kihomura.screenvault.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kihomura.screenvault.entity.TagContent;

import java.util.List;

public interface TagContentService extends IService<TagContent> {
    List<Integer> getContentIdsByTagId(int tagId);
    List<Integer> getTagIdsByContentId(int contentId);
    boolean addRelation(TagContent tagContent);
    boolean deleteRelation(TagContent tagContent);
}

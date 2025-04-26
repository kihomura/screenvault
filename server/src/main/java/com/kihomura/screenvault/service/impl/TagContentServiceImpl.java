package com.kihomura.screenvault.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kihomura.screenvault.mapper.TagContentMapper;
import com.kihomura.screenvault.pojo.TagContent;
import com.kihomura.screenvault.service.TagContentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagContentServiceImpl extends ServiceImpl<TagContentMapper, TagContent> implements TagContentService {

    private final TagContentMapper tagContentMapper;

    public TagContentServiceImpl(TagContentMapper tagContentMapper) {
        this.tagContentMapper = tagContentMapper;
    }

    @Override
    public List<Integer> getContentIdsByTagId(int tagId) {
        return tagContentMapper.findContentIdsByTagId(tagId);
    }

    @Override
    public List<Integer> getTagIdsByContentId(int contentId) {
        return tagContentMapper.findTagIdsByContentId(contentId);
    }

    @Override
    public boolean addRelation(TagContent tagContent) {
        return this.save(tagContent);
    }

    @Override
    public boolean deleteRelation(TagContent tagContent) {
        return tagContentMapper.deleteByTagIdAndContentId(tagContent);
    }
}
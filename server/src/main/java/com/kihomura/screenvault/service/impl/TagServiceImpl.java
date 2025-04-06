package com.kihomura.screenvault.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kihomura.screenvault.mapper.TagMapper;
import com.kihomura.screenvault.pojo.Tag;
import com.kihomura.screenvault.service.TagService;
import com.kihomura.screenvault.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    private final TagMapper tagMapper;
    private final UserService userService;

    public TagServiceImpl(TagMapper tagMapper, UserService userService) {
        this.tagMapper = tagMapper;
        this.userService = userService;
    }

    @Override
    public Tag findById(int id) {
        Tag tag = tagMapper.selectById(id);
        if (tag == null) {
            throw new IllegalArgumentException("Tag not found");
        }
        return tag;
    }

    @Override
    public List<Tag> findAll() {
        int userId = userService.getCurrentUserId();
        return tagMapper.findByUserId(userId);
    }

    @Override
    public boolean createTag(Tag tag) {
        tag.setCreatorId(userService.getCurrentUserId());
        return this.save(tag);
    }

    @Override
    public boolean updateTag(Tag tag) {
        Tag oldTag = tagMapper.selectById(tag.getId());
        if (oldTag == null) {
            throw new IllegalArgumentException("Tag not found");
        }

        if (!oldTag.getCreatorId().equals(userService.getCurrentUserId())) {
            throw new IllegalArgumentException("Do not have permission to update tag");
        }

        tag.setCreatorId(userService.getCurrentUserId());
        return this.updateById(tag);
    }

    @Override
    public boolean deleteTag(int id) {
        Tag oldTag = tagMapper.selectById(id);
        if (oldTag == null) {
            throw new IllegalArgumentException("Tag not found");
        }

        if (!oldTag.getCreatorId().equals(userService.getCurrentUserId())) {
            throw new IllegalArgumentException("Do not have permission to delete tag");
        }

        return this.removeById(id);
    }
}

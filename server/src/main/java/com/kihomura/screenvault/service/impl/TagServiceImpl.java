package com.kihomura.screenvault.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kihomura.screenvault.mapper.TagMapper;
import com.kihomura.screenvault.entity.Tag;
import com.kihomura.screenvault.service.TagService;
import com.kihomura.screenvault.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of TagService interface.
 * Handles tag management operations including creation, retrieval,
 * update, and deletion with user ownership enforcement.
 */
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
    public boolean saveOrUpdateTag(Tag tag) {
        tag.setCreatorId(userService.getCurrentUserId());
        return this.saveOrUpdate(tag);
    }

    /**
     * Deletes a tag with ownership verification.
     * Only the creator of the tag can delete it.
     * 
     * @param id the tag ID to delete
     * @return true if deletion succeeds, false otherwise
     * @throws IllegalArgumentException if tag not found or no permission to delete
     */
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

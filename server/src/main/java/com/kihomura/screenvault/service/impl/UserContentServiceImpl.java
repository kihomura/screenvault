package com.kihomura.screenvault.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kihomura.screenvault.enums.Status;
import com.kihomura.screenvault.mapper.UserContentMapper;
import com.kihomura.screenvault.pojo.Content;
import com.kihomura.screenvault.pojo.UserContent;
import com.kihomura.screenvault.service.ContentService;
import com.kihomura.screenvault.service.UserContentService;
import com.kihomura.screenvault.service.UserService;
import org.springframework.stereotype.Service;

import java.sql.Wrapper;
import java.util.List;

@Service
public class UserContentServiceImpl extends ServiceImpl<UserContentMapper, UserContent> implements UserContentService {

    private final UserContentMapper userContentMapper;
    private final UserService userService;
    private final ContentService contentService;

    public UserContentServiceImpl(UserContentMapper userContentMapper, UserService userService, ContentService contentService) {
        this.userContentMapper = userContentMapper;
        this.userService = userService;
        this.contentService = contentService;
    }

    @Override
    public UserContent findById(int id) {
        UserContent  userContent = userContentMapper.selectById(id);
        if(userContent == null) {
            throw new IllegalArgumentException("Recording not fonud");
        }
        return userContent;
    }

    @Override
    public UserContent findByContentId(int contentId) {
        return userContentMapper.findByContentId(contentId, userService.getCurrentUserId());
    }

    @Override
    public List<UserContent> findAll() {

        int userId = userService.getCurrentUserId();
        List<UserContent> results = userContentMapper.findByUserId(userId);

        return results;
    }

    @Override
    public List<UserContent> findWishList() {
        int userId = userService.getCurrentUserId();
        List<UserContent> results = userContentMapper.findWishlistByUserId(userId);
        return results;
    }

    @Override
    public boolean create(UserContent userContent) {

        Content content = contentService.getById(userContent.getContentId());
        if (content == null) {
            throw new IllegalArgumentException("Content does not exist");
        }

        userContent.setUserId(userService.getCurrentUserId());
        userContent.setStatus(Status.WATCHED);

        return userContentMapper.insert(userContent) > 0;
    }

    @Override
    public boolean update(UserContent userContent) {

        UserContent oldUserContent = userContentMapper.selectById(userContent.getId());
        if (oldUserContent == null) {
            throw new IllegalArgumentException("Recording not fonud");
        }

        if (!oldUserContent.getUserId().equals(userService.getCurrentUserId())) {
            throw new IllegalArgumentException("Do not have permission to update this recording");
        }

        userContent.setUserId(userService.getCurrentUserId());
        userContent.setStatus(Status.WATCHED);

        return userContentMapper.updateById(userContent) > 0;
    }

    @Override
    public boolean delete(int id) {

        UserContent oldUserContent = userContentMapper.selectById(id);
        if (oldUserContent == null) {
            throw new IllegalArgumentException("Recording not fonud");
        }

        if (!oldUserContent.getUserId().equals(userService.getCurrentUserId())) {
            throw new IllegalArgumentException("Do not have permission to update this recording");
        }

        return userContentMapper.deleteById(id) > 0;
    }

    @Override
    public boolean addToWishList(UserContent userContent) {

        Content content = contentService.getById(userContent.getContentId());
        if (content == null) {
            throw new IllegalArgumentException("Content does not exist");
        }

        userContent.setUserId(userService.getCurrentUserId());
        userContent.setStatus(Status.WANT_TO_WATCH);

        return userContentMapper.addToWishList(userContent);
    }

    @Override
    public boolean removeFromWishList(int contentId) {
        return userContentMapper.removeFromWishList(contentId);
    }
}
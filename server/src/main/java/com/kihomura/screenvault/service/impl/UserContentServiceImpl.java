package com.kihomura.screenvault.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kihomura.screenvault.enums.Status;
import com.kihomura.screenvault.mapper.ListContentMapper;
import com.kihomura.screenvault.mapper.PlayListMapper;
import com.kihomura.screenvault.mapper.UserContentMapper;
import com.kihomura.screenvault.entity.ListContent;
import com.kihomura.screenvault.entity.PlayList;
import com.kihomura.screenvault.entity.UserContent;
import com.kihomura.screenvault.service.ContentService;
import com.kihomura.screenvault.service.UserContentService;
import com.kihomura.screenvault.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of UserContentService interface.
 * Handles user-content relationships including viewing records, ratings,
 * and wishlist management operations.
 */
@Service
public class UserContentServiceImpl extends ServiceImpl<UserContentMapper, UserContent> implements UserContentService {

    private final UserContentMapper userContentMapper;
    private final ListContentMapper listContentMapper;
    private final PlayListMapper playListMapper;
    private final UserService userService;
    private final ContentService contentService;

    public UserContentServiceImpl(UserContentMapper userContentMapper, ListContentMapper listContentMapper, PlayListMapper playListMapper, UserService userService, ContentService contentService) {
        this.userContentMapper = userContentMapper;
        this.listContentMapper = listContentMapper;
        this.playListMapper = playListMapper;
        this.userService = userService;
        this.contentService = contentService;
    }

    @Override
    public UserContent findById(int id) {
        UserContent  userContent = userContentMapper.selectById(id);
        if(userContent == null) {
            throw new IllegalArgumentException("Recording not found");
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

    /**
     * Saves or updates user content record with status management.
     * Handles wishlist operations when status changes between WANT_TO_WATCH and WATCHED.
     * 
     * @param userContent the user content record to save or update
     * @return true if operation succeeds, false otherwise
     */
    @Override
    public boolean saveOrUpdateByUserAndContentId(UserContent userContent) {

        System.out.println("----content---- " + userContent);
        boolean result;
        int userId = userService.getCurrentUserId();
        int contentId = userContent.getContentId();

        userContent.setUserId(userId);
        Status status = userContent.getStatus();
        UserContent existing = userContentMapper.selectByUserAndContentId(userId, contentId);

        if (existing == null) {
            result = this.save(userContent);
            if (status == Status.WANT_TO_WATCH) {
                result = result && addToWishList(contentId);
            }
        } else {
            Status oldStatus = existing.getStatus();
            userContent.setId(existing.getId());
            result = this.updateById(userContent);

            if (oldStatus != status) {
                if (status == Status.WANT_TO_WATCH) {
                    result = result && addToWishList(contentId);
                } else if (oldStatus == Status.WANT_TO_WATCH && status == Status.WATCHED) {
                    result = result && removeFromWishList(contentId);
                    System.out.println("==========final result:" + result);
                }
            }
        }

        return result;
    }

    /**
     * Adds content to user's wishlist.
     * Updates both user_content table and list_content table for wishlist playlist.
     * 
     * @param contentId the content ID to add to wishlist
     * @return true if operation succeeds, false otherwise
     */
    @Override
    public boolean addToWishList(int contentId) {

        int userId = userService.getCurrentUserId();
        PlayList wishlist = playListMapper.findWishlistByUserId(userId);

        boolean userContentUpdated = userContentMapper.addToWishList(contentId, userId);

        ListContent existing = listContentMapper.findByListIdAndContentId(wishlist.getId(), contentId);
        if (existing != null) {
            return userContentUpdated;
        }

        ListContent listContent = new ListContent();
        listContent.setContentId(contentId);
        listContent.setListId(wishlist.getId());
        return userContentUpdated && listContentMapper.insert(listContent) > 0;
    }

    /**
     * Removes content from user's wishlist.
     * Updates both user_content table and list_content table for wishlist playlist.
     * 
     * @param contentId the content ID to remove from wishlist
     * @return true if operation succeeds, false otherwise
     */
    @Override
    public boolean removeFromWishList(int contentId) {

        int userId = userService.getCurrentUserId();
        PlayList wishlist = playListMapper.findWishlistByUserId(userId);

        boolean listContentRemoved = listContentMapper.deleteByListIdAndContentId(wishlist.getId(), contentId) > 0;

        UserContent userContent = userContentMapper.selectByUserAndContentId(userId, contentId);
        if (userContent != null && userContent.getStatus() == Status.WANT_TO_WATCH) {
            boolean userContentUpdated = userContentMapper.removeFromWishList(contentId, userId) > 0;

            return  userContentUpdated && listContentRemoved;
        }

        return listContentRemoved;
    }

    @Override
    public boolean delete(int id) {

        UserContent oldUserContent = userContentMapper.selectById(id);
        if (oldUserContent == null) {
            throw new IllegalArgumentException("Recording not found");
        }

        if (!oldUserContent.getUserId().equals(userService.getCurrentUserId())) {
            throw new IllegalArgumentException("Do not have permission to update this recording");
        }

        return userContentMapper.deleteById(id) > 0;
    }

}
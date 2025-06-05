package com.kihomura.screenvault.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kihomura.screenvault.mapper.ListContentMapper;
import com.kihomura.screenvault.mapper.PlayListMapper;
import com.kihomura.screenvault.entity.PlayList;
import com.kihomura.screenvault.service.PlayListService;
import com.kihomura.screenvault.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Implementation of PlayListService interface.
 * Handles playlist management operations including creation, retrieval,
 * update, deletion, and special wishlist functionality.
 */
@Service
public class PlayListServiceImpl extends ServiceImpl<PlayListMapper, PlayList> implements PlayListService {

    private final PlayListMapper playListMapper;
    private final ListContentMapper listContentMapper;
    private final UserService userService;

    public PlayListServiceImpl(PlayListMapper playListMapper, ListContentMapper listContentMapper, @Lazy UserService userService) {
        this.playListMapper = playListMapper;
        this.listContentMapper = listContentMapper;
        this.userService = userService;
    }

    @Override
    public PlayList findById(int id) {
        PlayList playList = playListMapper.selectById(id);
        if (playList == null) {
            throw new IllegalArgumentException("list not found");
        }
        return playList;
    }

    @Override
    public List<PlayList> findAll() {
        int userId = userService.getCurrentUserId();
        List<PlayList> results = playListMapper.findByUserId(userId);

        return results;
    }

    @Override
    public PlayList findWishlist() {
        return playListMapper.findWishlistByUserId(userService.getCurrentUserId());
    }

    /**
     * Creates a default wishlist for the current user.
     * 
     * @return true if wishlist creation succeeds, false otherwise
     */
    @Override
    public boolean createWishList() {
        PlayList playList = new PlayList();
        playList.setCreatorId(userService.getCurrentUserId());
        playList.setListName("Wish List");
        playList.setIsDefault(true);
        playList.setCreateDate(Date.valueOf(LocalDate.now()));
        return this.save(playList);
    }

    /**
     * Creates a default wishlist for a specific user.
     * Used during user registration process.
     * 
     * @param userId the user ID to create wishlist for
     * @return true if wishlist creation succeeds, false otherwise
     */
    @Override
    public boolean createWishListForUser(int userId) {
        PlayList playList = new PlayList();
        playList.setCreatorId(userId);
        playList.setListName("Wish List");
        playList.setIsDefault(true);
        playList.setCreateDate(Date.valueOf(LocalDate.now()));
        return this.save(playList);
    }

    @Override
    public boolean saveOrUpdateList(PlayList playList) {
        playList.setCreatorId(userService.getCurrentUserId());
        playList.setIsDefault(false);
        playList.setCreateDate(Date.valueOf(LocalDate.now()));
        return this.saveOrUpdate(playList);
    }

    /**
     * Deletes a playlist and all its associated content relationships.
     * Prevents deletion of default playlists and enforces ownership checks.
     * 
     * @param id the playlist ID to delete
     * @return true if deletion succeeds, false otherwise
     * @throws IllegalArgumentException if playlist not found, no permission, or trying to delete default playlist
     */
    @Override
    public boolean deletePlayList(int id) {

        PlayList oldPlayList = playListMapper.selectById(id);
        if (oldPlayList == null) {
            throw new IllegalArgumentException("list not found");
        }

        if (!oldPlayList.getCreatorId().equals(userService.getCurrentUserId())) {
            throw new IllegalArgumentException("Do not have permission to delete playlist");
        }

        if (oldPlayList.getIsDefault()) {
            throw new IllegalArgumentException("Can not delete default playlist");
        }

        listContentMapper.deleteByListId(id);
        return playListMapper.deleteByListId(id) > 0;
    }
}
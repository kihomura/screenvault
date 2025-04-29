package com.kihomura.screenvault.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kihomura.screenvault.mapper.ListContentMapper;
import com.kihomura.screenvault.mapper.PlayListMapper;
import com.kihomura.screenvault.pojo.PlayList;
import com.kihomura.screenvault.service.PlayListService;
import com.kihomura.screenvault.service.UserService;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
public class PlayListServiceImpl extends ServiceImpl<PlayListMapper, PlayList> implements PlayListService {

    private final PlayListMapper playListMapper;
    private final ListContentMapper listContentMapper;
    private final UserService userService;

    public PlayListServiceImpl(PlayListMapper playListMapper, ListContentMapper listContentMapper, UserService userService) {
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

    @Override
    public boolean createWishList() {
        PlayList playList = new PlayList();
        playList.setCreatorId(userService.getCurrentUserId());
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
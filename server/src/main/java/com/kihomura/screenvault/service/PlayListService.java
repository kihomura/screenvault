package com.kihomura.screenvault.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kihomura.screenvault.entity.PlayList;

import java.util.List;

public interface PlayListService extends IService<PlayList> {
    PlayList findById(int id);
    List<PlayList> findAll();
    PlayList findWishlist();
    boolean createWishList();
    boolean createWishListForUser(int userId);
    boolean saveOrUpdateList(PlayList playList);
    boolean deletePlayList(int id);
}

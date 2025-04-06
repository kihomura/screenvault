package com.kihomura.screenvault.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kihomura.screenvault.pojo.PlayList;

import java.util.List;

public interface PlayListService extends IService<PlayList> {
    PlayList findById(int id);
    List<PlayList> findAll();
    boolean createWishList();
    boolean createPlayList(PlayList playList);
    boolean updatePlayList(PlayList playList);
    boolean deletePlayList(int id);
}

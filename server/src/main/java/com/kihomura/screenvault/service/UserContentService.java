package com.kihomura.screenvault.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kihomura.screenvault.entity.UserContent;

import java.util.List;

public interface UserContentService extends IService<UserContent> {
    UserContent findById(int id);
    UserContent findByContentId(int contentId);
    List<UserContent> findAll();
    List<UserContent> findWishList();
    boolean saveOrUpdateByUserAndContentId(UserContent userContent);
    boolean delete(int id);
    boolean addToWishList(int contentId);
    boolean removeFromWishList(int contentId);
}
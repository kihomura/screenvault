package com.kihomura.screenvault.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kihomura.screenvault.pojo.UserContent;

import java.util.List;

public interface UserContentService extends IService<UserContent> {
    UserContent findById(int id);
    UserContent findByContentId(int contentId);
    List<UserContent> findAll();
    List<UserContent> findWishList();
    boolean create(UserContent userContent);
    boolean update(UserContent userContent);
    boolean delete(int id);
    boolean addToWishList(UserContent userContent);
    boolean removeFromWishList(int contentId);
}
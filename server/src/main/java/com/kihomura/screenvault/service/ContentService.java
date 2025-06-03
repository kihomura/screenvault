package com.kihomura.screenvault.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kihomura.screenvault.entity.Content;

import java.util.List;

public interface ContentService extends IService<Content> {
    List<Content> findByTitle(String title);
    List<Content> findByTitleAndCreator(String title);
    List<Content> findCustomContent();
    boolean saveOrUpdateContent(Content content);
    boolean delete(int contentId);
}

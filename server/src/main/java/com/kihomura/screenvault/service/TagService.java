package com.kihomura.screenvault.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kihomura.screenvault.pojo.Tag;

import java.util.List;

public interface TagService extends IService<Tag> {
    Tag findById(int id);
    List<Tag> findAll();
    boolean saveOrUpdateTag(Tag tag);
    boolean deleteTag(int id);
}

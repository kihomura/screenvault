package com.kihomura.screenvault.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kihomura.screenvault.mapper.ListContentMapper;
import com.kihomura.screenvault.pojo.ListContent;
import com.kihomura.screenvault.service.ListContentService;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ListContentServiceImpl extends ServiceImpl<ListContentMapper, ListContent> implements ListContentService {

    private final ListContentMapper listContentMapper;

    public ListContentServiceImpl(ListContentMapper listContentMapper) {
        this.listContentMapper = listContentMapper;
    }

    @Override
    public List<ListContent> getContentsByListId(Integer listId) {
        return listContentMapper.findByListId(listId);
    }

    @Override
    public List<ListContent> getListsByContentId(Integer contentId) {
        return listContentMapper.findByContentId(contentId);
    }

    @Override
    public boolean addContentToList(ListContent listContent) {
        List<ListContent> existingItems = listContentMapper.findByListIdAndContentId(
                listContent.getListId(), listContent.getContentId());

        if (!existingItems.isEmpty()) {
            throw new IllegalArgumentException("Content already exists in the list");
        }

        if (listContent.getAddTime() == null) {
            listContent.setAddTime(Timestamp.valueOf(LocalDateTime.now()));
        }

        return this.save(listContent);
    }

    @Override
    public boolean addBatch(List<ListContent> listContent) {
        for (ListContent content : listContent) {
            if (content.getAddTime() == null) {
                content.setAddTime(Timestamp.valueOf(LocalDateTime.now()));
            }
        }
        return this.saveBatch(listContent);
    }

    @Override
    public boolean removeContentFromList(Integer listId, Integer contentId) {
        return listContentMapper.deleteByListIdAndContentId(listId, contentId) > 0;
    }

}
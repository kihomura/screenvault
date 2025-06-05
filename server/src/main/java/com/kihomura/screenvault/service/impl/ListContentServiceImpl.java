package com.kihomura.screenvault.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kihomura.screenvault.mapper.ListContentMapper;
import com.kihomura.screenvault.entity.ListContent;
import com.kihomura.screenvault.service.ListContentService;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of ListContentService interface.
 * Handles playlist-content relationship operations including adding,
 * removing, and querying content within playlists.
 */
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

    /**
     * Adds content to a playlist if it doesn't already exist.
     * Sets current timestamp if addTime is not provided.
     * 
     * @param listContent the list-content relationship to add
     * @return true if content is successfully added, false otherwise
     * @throws IllegalArgumentException if content already exists in the list
     */
    @Override
    public boolean addContentToList(ListContent listContent) {
        ListContent existingItem = listContentMapper.findByListIdAndContentId(
                listContent.getListId(), listContent.getContentId());

        if (existingItem != null) {
            throw new IllegalArgumentException("Content already exists in the list");
        }

        if (listContent.getAddTime() == null) {
            listContent.setAddTime(Timestamp.valueOf(LocalDateTime.now()));
        }

        return this.save(listContent);
    }

    /**
     * Batch adds multiple content items to playlists.
     * Sets current timestamp for items without addTime.
     * 
     * @param listContent list of list-content relationships to add
     * @return true if batch operation succeeds, false otherwise
     */
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
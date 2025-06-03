package com.kihomura.screenvault.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kihomura.screenvault.entity.ListContent;
import java.util.List;

public interface ListContentService extends IService<ListContent> {

    List<ListContent> getContentsByListId(Integer listId);
    List<ListContent> getListsByContentId(Integer contentId);
    boolean addContentToList(ListContent listContent);
    boolean removeContentFromList(Integer listId, Integer contentId);
    boolean addBatch(List<ListContent> listContent);
}
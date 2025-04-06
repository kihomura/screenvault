package com.kihomura.screenvault.controller;

import com.kihomura.screenvault.pojo.ListContent;
import com.kihomura.screenvault.pojo.dto.ResponseMessage;
import com.kihomura.screenvault.service.ListContentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/list-content")
public class ListContentController {

    private final ListContentService listContentService;

    public ListContentController(ListContentService listContentService) {
        this.listContentService = listContentService;
    }

    /**
     * get all contents from a list
     */
    @GetMapping("/list/{listId}")
    public ResponseMessage getContentsByListId(@PathVariable Integer listId) {
        List<ListContent> contents = listContentService.getContentsByListId(listId);
        return ResponseMessage.success(contents);
    }

    /**
     * get all lists of a content
     */
    @GetMapping("/content/{contentId}")
    public ResponseMessage getListsByContentId(@PathVariable Integer contentId) {
        List<ListContent> lists = listContentService.getListsByContentId(contentId);
        return ResponseMessage.success(lists);
    }

    /**
     * add a content to a list
     */
    @PostMapping
    public ResponseMessage addContentToList(@RequestBody ListContent listContent) {
        boolean result = listContentService.addContentToList(listContent);
        if (!result) {
            return ResponseMessage.error(400, "content already exist in the list");
        }
        return ResponseMessage.success(listContent);
    }

    /**
     * remove a content from a list
     */
    @DeleteMapping
    public ResponseMessage removeContentFromList(@RequestBody ListContent listContent) {
        boolean result = listContentService.removeContentFromList(listContent.getListId(), listContent.getContentId());
        if (!result) {
            return ResponseMessage.error(404, "list do not remove");
        }
        return ResponseMessage.success();
    }

    /**
     * add several contents to a list at once
     */
    @PostMapping("/batch")
    public ResponseMessage batchAddContentToList(@RequestBody List<ListContent> listContents) {
        boolean result = listContentService.addBatch(listContents);
        if (!result) {
            return ResponseMessage.error(400, "failed to save batch");
        }
        return ResponseMessage.success(listContents);
    }
}
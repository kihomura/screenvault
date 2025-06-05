package com.kihomura.screenvault.controller;

import com.kihomura.screenvault.entity.ListContent;
import com.kihomura.screenvault.entity.dto.ResponseMessage;
import com.kihomura.screenvault.service.ListContentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing playlist-content relationships.
 * Handles CRUD operations for the association between playlists and content items.
 * Provides endpoints for adding/removing content from playlists and retrieving playlist contents.
 */
@RestController
@RequestMapping("/list-content")
public class ListContentController {

    private final ListContentService listContentService;

    /**
     * Constructor for ListContentController.
     * 
     * @param listContentService the service for list-content operations
     */
    public ListContentController(ListContentService listContentService) {
        this.listContentService = listContentService;
    }

    /**
     * Retrieves all content items from a specific playlist.
     * 
     * GET: /list-content/list/{listId}
     * @param listId the ID of the playlist to query
     * @return ResponseMessage containing a list of content items in the playlist
     */
    @GetMapping("/list/{listId}")
    public ResponseMessage getContentsByListId(@PathVariable Integer listId) {
        List<ListContent> contents = listContentService.getContentsByListId(listId);
        return ResponseMessage.success(contents);
    }

    /**
     * Retrieves all playlists that contain a specific content item.
     * 
     * GET: /list-content/content/{contentId}
     * @param contentId the ID of the content to query
     * @return ResponseMessage containing a list of playlists containing the content
     */
    @GetMapping("/content/{contentId}")
    public ResponseMessage getListsByContentId(@PathVariable Integer contentId) {
        List<ListContent> lists = listContentService.getListsByContentId(contentId);
        return ResponseMessage.success(lists);
    }

    /**
     * Adds a content item to a playlist.
     * Creates a new relationship between a playlist and content item.
     * 
     * POST: /list-content
     * @param listContent the list-content relationship object to create
     * @return ResponseMessage indicating success or failure of the operation
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
     * Removes a content item from a playlist.
     * Deletes the relationship between a playlist and content item.
     * 
     * DELETE: /list-content
     * @param listContent the list-content relationship object containing listId and contentId
     * @return ResponseMessage indicating success or failure of the operation
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
     * Adds multiple content items to a playlist in a single operation.
     * Performs batch insertion of content items into a playlist for efficiency.
     * 
     * POST: /list-content/batch
     * @param listContents list of list-content relationships to create
     * @return ResponseMessage indicating success or failure of the batch operation
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
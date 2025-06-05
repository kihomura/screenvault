package com.kihomura.screenvault.controller;

import com.kihomura.screenvault.entity.TagContent;
import com.kihomura.screenvault.entity.dto.ResponseMessage;
import com.kihomura.screenvault.service.TagContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing tag-content relationships.
 * Handles CRUD operations for the association between tags and content items.
 */
@RestController
@RequestMapping("/tag-content")
public class TagContentController {

    @Autowired
    private final TagContentService tagContentService;

    /**
     * Constructor for TagContentController.
     * 
     * @param tagContentService the service for tag-content operations
     */
    public TagContentController(TagContentService tagContentService) {
        this.tagContentService = tagContentService;
    }

    /**
     * Retrieves all content IDs associated with a specific tag.
     * 
     * GET: /tag-content/tag/{tagId}
     * @param tagId the ID of the tag to query
     * @return ResponseMessage containing a list of content IDs
     */
    @GetMapping("/tag/{tagId}")
    public ResponseMessage getContentIdsByTag(@PathVariable int tagId) {
        List<Integer> contentIds = tagContentService.getContentIdsByTagId(tagId);
        return ResponseMessage.success(contentIds);
    }

    /**
     * Retrieves all tag IDs associated with a specific content item.
     * 
     * GET: /tag-content/content/{contentId}
     * @param contentId the ID of the content to query
     * @return ResponseMessage containing a list of tag IDs
     */
    @GetMapping("/content/{contentId}")
    public ResponseMessage getTagIdsByContent(@PathVariable int contentId) {
        List<Integer> tagIds = tagContentService.getTagIdsByContentId(contentId);
        return ResponseMessage.success(tagIds);
    }

    /**
     * Creates a new relationship between a tag and content.
     * 
     * POST: /tag-content
     * @param tagContent the tag-content relationship object to create
     * @return ResponseMessage indicating success or failure of the operation
     */
    @PostMapping
    public ResponseMessage addRelation(@RequestBody TagContent tagContent) {
        boolean result = tagContentService.addRelation(tagContent);
        if (!result) {
            return ResponseMessage.error(400, "Relation not added");
        }
        return ResponseMessage.success(tagContent);
    }

    /**
     * Removes a relationship between a tag and content.
     * 
     * DELETE: /tag-content
     * @param tagContent the tag-content relationship object to delete
     * @return ResponseMessage indicating success or failure of the operation
     */
    @DeleteMapping
    public ResponseMessage deleteRelation(@RequestBody TagContent tagContent) {
        boolean result = tagContentService.deleteRelation(tagContent);
        if (!result) {
            return ResponseMessage.error(400, "Relation not deleted");
        }
        return ResponseMessage.success();
    }
}

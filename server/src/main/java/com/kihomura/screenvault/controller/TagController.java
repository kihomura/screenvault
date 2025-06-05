package com.kihomura.screenvault.controller;

import com.kihomura.screenvault.entity.Tag;
import com.kihomura.screenvault.entity.dto.ResponseMessage;
import com.kihomura.screenvault.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing tags and tag operations.
 * Handles CRUD operations for tags that can be associated with content items.
 * Provides functionality for creating, retrieving, updating, and deleting tags.
 */
@RestController
@RequestMapping("/tag")
public class TagController {

    @Autowired
    private final TagService tagService;

    /**
     * Constructor for TagController.
     * 
     * @param tagService the service for tag operations
     */
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    /**
     * Retrieves all available tags in the system.
     * Returns a complete list of all tags that can be used for content categorization.
     * 
     * GET: /tag
     * @return ResponseMessage containing list of all tags
     */
    @GetMapping
    public ResponseMessage getAllTags() {
        return ResponseMessage.success(tagService.findAll());
    }

    /**
     * Retrieves a specific tag by its ID.
     * Returns detailed information about the requested tag.
     * 
     * GET: /tag/id/{id}
     * @param id the ID of the tag to retrieve
     * @return ResponseMessage containing the tag data or error if not found
     */
    @GetMapping("/id/{id}")
    public ResponseMessage getTagById(@PathVariable int id) {
        Tag tag = tagService.findById(id);
        if (tag == null) {
            return ResponseMessage.error(404, "Tag not found");
        }
        return ResponseMessage.success(tag);
    }

    /**
     * Creates a new tag or updates an existing one.
     * Saves tag information including name, description, and other metadata.
     * 
     * POST: /tag
     * @param tag the tag object to create or update
     * @return ResponseMessage indicating success or failure of the operation
     */
    @PostMapping
    public ResponseMessage createTag(@RequestBody Tag tag) {
        boolean result = tagService.saveOrUpdateTag(tag);
        if (!result) {
            return ResponseMessage.error(404, "Tag not created");
        }
        return ResponseMessage.success(tag);
    }

    /**
     * Deletes a tag by its ID.
     * Removes the tag and all its associations with content items.
     * 
     * DELETE: /tag/id/{id}
     * @param id the ID of the tag to delete
     * @return ResponseMessage indicating success or failure of the deletion
     */
    @DeleteMapping("/id/{id}")
    public ResponseMessage deleteTag(@PathVariable int id) {
        boolean result = tagService.deleteTag(id);
        if (!result) {
            return ResponseMessage.error(404, "Tag not deleted");
        }
        return ResponseMessage.success();
    }
}

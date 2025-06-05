package com.kihomura.screenvault.controller;

import com.kihomura.screenvault.entity.Content;
import com.kihomura.screenvault.entity.dto.ResponseMessage;
import com.kihomura.screenvault.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for managing content items and content operations.
 * Handles CRUD operations for content including movies, TV shows, and other media.
 * Provides functionality for content search, creation, retrieval, update, and deletion.
 */
@RestController
@RequestMapping("/content")
public class ContentController {

    @Autowired
    private final ContentService contentService;

    /**
     * Constructor for ContentController.
     * 
     * @param contentService the service for content operations
     */
    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    /**
     * Searches for content by title with fuzzy matching.
     * Performs title normalization by removing non-letter characters and converting to lowercase.
     * Also searches by title and creator combination for comprehensive results.
     * 
     * GET: /content/title/{title}
     * @param title the title to search for (will be normalized for fuzzy matching)
     * @return ResponseMessage containing JSON array of all matched content items
     */
    @GetMapping("/title/{title}")
    public ResponseMessage getContentByTitle(@PathVariable String title) {

        if (!StringUtils.hasText(title)) {
            return ResponseMessage.error(400, "title can not be empty");
        }

        List<Content> contents = new ArrayList<>();
        contents.addAll(contentService.findByTitle(title));
        contents.addAll(contentService.findByTitleAndCreator(title));

        if (contents.isEmpty() || contents == null) {
            return ResponseMessage.error(400, "content not found");
        }

        return ResponseMessage.success(contents);
    }

    /**
     * Retrieves a specific content item by its ID.
     * Returns detailed information about the requested content.
     * 
     * GET: /content/id/{id}
     * @param id the ID of the content to retrieve
     * @return ResponseMessage containing the content data or error if not found
     */
    @GetMapping("/id/{id}")
    public ResponseMessage getContentById(@PathVariable int id) {
        Content content = contentService.getById(id);
        if (content == null) {
            return ResponseMessage.error(400, "content not found");
        }
        return ResponseMessage.success(content);
    }

    /**
     * Retrieves all user-created custom content.
     * Returns content items that were manually added by users rather than from external sources.
     * 
     * GET: /content/custom
     * @return ResponseMessage containing list of custom content items
     */
    @GetMapping("/custom")
    public ResponseMessage getCustomContent() {
        return ResponseMessage.success(contentService.findCustomContent());
    }

    /**
     * Creates a new content item or updates an existing one.
     * Saves content information including title, description, metadata, and other details.
     * 
     * POST: /content
     * @param content the content object to create or update
     * @return ResponseMessage indicating success or failure of the operation
     */
    @PostMapping
    public ResponseMessage createContent(@RequestBody Content content) {
        boolean result = contentService.saveOrUpdateContent(content);
        if (result) {
            return ResponseMessage.success(content);
        } else {
            return ResponseMessage.error(400, "content not created");
        }
    }

    /**
     * Deletes a content item by its ID.
     * Removes the content and all its associated relationships (tags, playlists, user records).
     * 
     * DELETE: /content/id/{id}
     * @param id the ID of the content to delete
     * @return ResponseMessage indicating success or failure of the deletion
     */
    @DeleteMapping("/id/{id}")
    public ResponseMessage deleteContent(@PathVariable int id) {
        boolean result = contentService.delete(id);
        if (result) {
            return ResponseMessage.success(id);
        } else {
            return ResponseMessage.error(400, "content not deleted");
        }
    }
}
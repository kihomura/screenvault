package com.kihomura.screenvault.controller;

import com.kihomura.screenvault.entity.UserContent;
import com.kihomura.screenvault.entity.dto.ResponseMessage;
import com.kihomura.screenvault.service.UserContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing user-content relationships and user interaction records.
 * Handles operations related to user content records, including viewing history,
 * wishlist management, and content rating/tracking functionality.
 */
@RestController
@RequestMapping("/record")
public class UserContentController {

    @Autowired
    private final UserContentService userContentService;

    /**
     * Constructor for UserContentController.
     * 
     * @param userContentService the service for user-content record operations
     */
    public UserContentController(UserContentService userContentService) {
        this.userContentService = userContentService;
    }

    /**
     * Retrieves all user content records.
     * Returns complete list of user interaction records with content.
     * 
     * GET: /record
     * @return ResponseMessage containing list of all user content records
     */
    @GetMapping
    public ResponseMessage getAllRecording() {
        return ResponseMessage.success(userContentService.findAll());
    }

    /**
     * Retrieves user record for a specific content item.
     * Returns user's interaction record with the specified content.
     * 
     * GET: /record/content/{id}
     * @param id the ID of the content to query
     * @return ResponseMessage containing user's record for the specified content
     */
    @GetMapping("/content/{id}")
    public ResponseMessage getRecordByContentId(@PathVariable int id) {
        return ResponseMessage.success(userContentService.findByContentId(id));
    }

    /**
     * Retrieves the user's wishlist.
     * Returns all content items that the user has added to their wishlist.
     * 
     * GET: /record/wishlist
     * @return ResponseMessage containing list of wishlist items
     */
    @GetMapping("/wishlist")
    public ResponseMessage getWishList() {
        return ResponseMessage.success(userContentService.findWishList());
    }

    /**
     * Retrieves a specific user content record by its ID.
     * Returns detailed information about a specific user-content interaction record.
     * 
     * GET: /record/id/{id}
     * @param id the ID of the user content record
     * @return ResponseMessage containing the user content record or error if not found
     */
    @GetMapping("/id/{id}")
    public ResponseMessage getRecordingById(@PathVariable int id) {
        UserContent userContent = userContentService.findById(id);

        if (userContent == null) {
            return ResponseMessage.error(400, "recording not found");
        }
        return ResponseMessage.success(userContent);
    }

    /**
     * Creates or updates a user content record.
     * Saves user interaction data such as rating, progress, or status for content.
     * 
     * POST: /record
     * @param userContent the user content record to save or update
     * @return ResponseMessage indicating success or failure of the operation
     */
    @PostMapping
    public ResponseMessage saveOrUpdateRecord(@RequestBody UserContent userContent) {
        boolean result = userContentService.saveOrUpdateByUserAndContentId(userContent);
        if (!result) {
            return ResponseMessage.error(400, "recording not created");
        }
        return ResponseMessage.success(userContent);
    }

    /**
     * Deletes a user content record by its ID.
     * Removes the user's interaction record with specific content.
     * 
     * DELETE: /record/id/{id}
     * @param id the ID of the user content record to delete
     * @return ResponseMessage indicating success or failure of the deletion
     */
    @DeleteMapping("/id/{id}")
    public ResponseMessage deleteRecordingById(@PathVariable int id) {
        boolean result = userContentService.delete(id);
        if (!result) {
            return ResponseMessage.error(400, "recording not delete");
        }
        return ResponseMessage.success();
    }

    /**
     * Adds a content item to the user's wishlist.
     * Creates a wishlist entry for the specified content.
     * 
     * POST: /record/wishlist
     * @param userContent the user content object containing content ID to add to wishlist
     * @return ResponseMessage indicating success or failure of the operation
     */
    @PostMapping("/wishlist")
    public ResponseMessage addToWishlist(@RequestBody UserContent userContent) {
        boolean result = userContentService.addToWishList(userContent.getContentId());
        if (!result) {
            return ResponseMessage.error(400, "wishlist not added");
        }
        return ResponseMessage.success();
    }

    /**
     * Removes a content item from the user's wishlist.
     * Deletes the wishlist entry for the specified content.
     * 
     * DELETE: /record/wishlist
     * @param contentId the ID of the content to remove from wishlist
     * @return ResponseMessage indicating success or failure of the operation
     */
    @DeleteMapping("/wishlist")
    public ResponseMessage removeFromWishlist(@RequestBody int contentId) {
        boolean result = userContentService.removeFromWishList(contentId);
        if (!result) {
            return ResponseMessage.error(400, "wishlist not removed");
        }
        return ResponseMessage.success();
    }

}

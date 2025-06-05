package com.kihomura.screenvault.controller;

import com.kihomura.screenvault.entity.PlayList;
import com.kihomura.screenvault.entity.dto.ResponseMessage;
import com.kihomura.screenvault.service.PlayListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing user playlists and playlist operations.
 * Handles CRUD operations for playlists including creation, retrieval, update, and deletion.
 * Also provides special functionality for wishlist management.
 */
@RestController
@RequestMapping("/playlist")
public class PlayListController {

    @Autowired
    private final PlayListService playListService;

    /**
     * Constructor for PlayListController.
     * 
     * @param playListService the service for playlist operations
     */
    public PlayListController(PlayListService playListService) {
        this.playListService = playListService;
    }

    /**
     * Retrieves all playlists for the current user.
     * Returns a complete list of all user-created playlists.
     * 
     * GET: /playlist
     * @return ResponseMessage containing list of all user playlists
     */
    @GetMapping
    public ResponseMessage getAllPlayList() {
        return ResponseMessage.success(playListService.findAll());
    }

    /**
     * Retrieves a specific playlist by its ID.
     * Returns detailed information about the requested playlist.
     * 
     * GET: /playlist/id/{id}
     * @param id the ID of the playlist to retrieve
     * @return ResponseMessage containing the playlist data or error if not found
     */
    @GetMapping("/id/{id}")
    public ResponseMessage getPlayListById(@PathVariable int id) {
        PlayList playList = playListService.findById(id);

        if (playList == null) {
            return ResponseMessage.error(404, "List not found");
        }

        return ResponseMessage.success(playList);
    }

    /**
     * Retrieves the user's special wishlist.
     * Returns the user's designated wishlist for content they want to watch later.
     * 
     * GET: /playlist/wishlist
     * @return ResponseMessage containing the user's wishlist
     */
    @GetMapping("/wishlist")
    public ResponseMessage getWishList() {
        return ResponseMessage.success(playListService.findWishlist());
    }

    /**
     * Creates a new wishlist for the user.
     * Initializes a special wishlist playlist for the current user.
     * 
     * POST: /playlist/wishlist
     * @return ResponseMessage indicating success or failure of wishlist creation
     */
    @PostMapping("/wishlist")
    public ResponseMessage createWishList() {
        boolean result = playListService.createWishList();
        if (!result){
            return ResponseMessage.error(404, "Create wish list failed");
        }
        return ResponseMessage.success(result);
    }

    /**
     * Creates a new playlist or updates an existing one.
     * Saves playlist information including name, description, and other metadata.
     * 
     * POST: /playlist
     * @param playList the playlist object to create or update
     * @return ResponseMessage indicating success or failure of the operation
     */
    @PostMapping
    public ResponseMessage addPlayList(@RequestBody PlayList playList) {
        boolean result = playListService.saveOrUpdateList(playList);
        if(!result) {
            return ResponseMessage.error(404, "List not created");
        }
        return ResponseMessage.success(playList);
    }

    /**
     * Deletes a playlist by its ID.
     * Removes the playlist and all its associated content relationships.
     * 
     * DELETE: /playlist/id/{id}
     * @param id the ID of the playlist to delete
     * @return ResponseMessage indicating success or failure of the deletion
     */
    @DeleteMapping("/id/{id}")
    public ResponseMessage deletePlayList(@PathVariable int id) {
        boolean result = playListService.deletePlayList(id);
        if (!result) {
            return ResponseMessage.error(404, "List not deleted");
        }
        return ResponseMessage.success();
    }

}

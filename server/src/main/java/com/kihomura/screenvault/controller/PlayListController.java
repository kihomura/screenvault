package com.kihomura.screenvault.controller;

import com.kihomura.screenvault.entity.PlayList;
import com.kihomura.screenvault.entity.dto.ResponseMessage;
import com.kihomura.screenvault.service.PlayListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/playlist")
public class PlayListController {

    @Autowired
    private final PlayListService playListService;

    public PlayListController(PlayListService playListService) {
        this.playListService = playListService;
    }

    @GetMapping
    public ResponseMessage getAllPlayList() {
        return ResponseMessage.success(playListService.findAll());
    }

    @GetMapping("/id/{id}")
    public ResponseMessage getPlayListById(@PathVariable int id) {
        PlayList playList = playListService.findById(id);

        if (playList == null) {
            return ResponseMessage.error(404, "List not found");
        }

        return ResponseMessage.success(playList);
    }

    @GetMapping("/wishlist")
    public ResponseMessage getWishList() {
        return ResponseMessage.success(playListService.findWishlist());
    }

    @PostMapping("/wishlist")
    public ResponseMessage createWishList() {
        boolean result = playListService.createWishList();
        if (!result){
            return ResponseMessage.error(404, "Create wish list failed");
        }
        return ResponseMessage.success(result);
    }

    @PostMapping
    public ResponseMessage addPlayList(@RequestBody PlayList playList) {
        boolean result = playListService.saveOrUpdateList(playList);
        if(!result) {
            return ResponseMessage.error(404, "List not created");
        }
        return ResponseMessage.success(playList);
    }

    @DeleteMapping("/id/{id}")
    public ResponseMessage deletePlayList(@PathVariable int id) {
        boolean result = playListService.deletePlayList(id);
        if (!result) {
            return ResponseMessage.error(404, "List not deleted");
        }
        return ResponseMessage.success();
    }

}

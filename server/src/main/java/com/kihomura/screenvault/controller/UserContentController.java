package com.kihomura.screenvault.controller;

import com.kihomura.screenvault.pojo.UserContent;
import com.kihomura.screenvault.pojo.dto.ResponseMessage;
import com.kihomura.screenvault.service.UserContentService;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/record")
public class UserContentController {

    @Autowired
    private final UserContentService userContentService;

    public UserContentController(UserContentService userContentService) {
        this.userContentService = userContentService;
    }

    /**
     * GET: /record
     */
    @GetMapping
    public ResponseMessage getAllRecording() {
        return ResponseMessage.success(userContentService.findAll());
    }

    /**
     * GET: /record/content/{id}
     */
    @GetMapping("/content/{id}")
    public ResponseMessage getRecordByContentId(@PathVariable int id) {
        return ResponseMessage.success(userContentService.findByContentId(id));
    }

    /**
     * GET: /record/wishlist
     */
    @GetMapping("/wishlist")
    public ResponseMessage getWishList() {
        return ResponseMessage.success(userContentService.findWishList());
    }

    /**
     * GET: /record/id/{id}
     */
    @GetMapping("/id/{id}")
    public ResponseMessage getRecordingById(@PathVariable int id) {
        UserContent userContent = userContentService.findById(id);

        if (userContent == null) {
            return ResponseMessage.error(400, "recording not found");
        }
        return ResponseMessage.success(userContent);
    }

    @PostMapping
    public ResponseMessage addRecording(@RequestBody UserContent userContent) {
        boolean result = userContentService.create(userContent);
        if (!result) {
            return ResponseMessage.error(400, "recording not created");
        }
        return ResponseMessage.success(userContent);
    }

    @PatchMapping
    public ResponseMessage updateRecording(@RequestBody UserContent userContent) {
        boolean result = userContentService.update(userContent);
        if (!result) {
            return ResponseMessage.error(400, "recording not updated");
        }
        return ResponseMessage.success(userContent);
    }

    /**
     * DELETE: /record/id/{id}
     */
    @DeleteMapping("/id/{id}")
    public ResponseMessage deleteRecordingById(@PathVariable int id) {
        boolean result = userContentService.delete(id);
        if (!result) {
            return ResponseMessage.error(400, "recording not delete");
        }
        return ResponseMessage.success();
    }

    @PostMapping("/wishlist")
    public ResponseMessage addWishlist(@RequestBody UserContent userContent) {
        boolean result = userContentService.addToWishList(userContent);
        if (!result) {
            return ResponseMessage.error(400, "wishlist not added");
        }
        return ResponseMessage.success();
    }

    @DeleteMapping("/wishlist")
    public ResponseMessage removeFromWishlist(@RequestBody int contentId) {
        boolean result = userContentService.removeFromWishList(contentId);
        if (!result) {
            return ResponseMessage.error(400, "wishlist not removed");
        }
        return ResponseMessage.success();
    }

}

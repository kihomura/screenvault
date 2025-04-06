package com.kihomura.screenvault.controller;

import com.kihomura.screenvault.pojo.TagContent;
import com.kihomura.screenvault.pojo.dto.ResponseMessage;
import com.kihomura.screenvault.service.TagContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tag-content")
public class TagContentController {

    @Autowired
    private final TagContentService tagContentService;

    public TagContentController(TagContentService tagContentService) {
        this.tagContentService = tagContentService;
    }

    @GetMapping("/tag/{tagId}")
    public ResponseMessage getContentIdsByTag(@PathVariable int tagId) {
        List<Integer> contentIds = tagContentService.getContentIdsByTagId(tagId);
        return ResponseMessage.success(contentIds);
    }

    @GetMapping("/content/{contentId}")
    public ResponseMessage getTagIdsByContent(@PathVariable int contentId) {
        List<Integer> tagIds = tagContentService.getTagIdsByContentId(contentId);
        return ResponseMessage.success(tagIds);
    }

    @PostMapping
    public ResponseMessage addRelation(@RequestBody TagContent tagContent) {
        boolean result = tagContentService.addRelation(tagContent);
        if (!result) {
            return ResponseMessage.error(400, "Relation not added");
        }
        return ResponseMessage.success(tagContent);
    }

    @DeleteMapping
    public ResponseMessage deleteRelation(@RequestBody TagContent tagContent) {
        boolean result = tagContentService.deleteRelation(tagContent);
        if (!result) {
            return ResponseMessage.error(400, "Relation not deleted");
        }
        return ResponseMessage.success();
    }
}

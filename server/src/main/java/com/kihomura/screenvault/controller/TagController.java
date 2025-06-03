package com.kihomura.screenvault.controller;

import com.kihomura.screenvault.entity.Tag;
import com.kihomura.screenvault.entity.dto.ResponseMessage;
import com.kihomura.screenvault.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tag")
public class TagController {

    @Autowired
    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public ResponseMessage getAllTags() {
        return ResponseMessage.success(tagService.findAll());
    }

    @GetMapping("/id/{id}")
    public ResponseMessage getTagById(@PathVariable int id) {
        Tag tag = tagService.findById(id);
        if (tag == null) {
            return ResponseMessage.error(404, "Tag not found");
        }
        return ResponseMessage.success(tag);
    }

    @PostMapping
    public ResponseMessage createTag(@RequestBody Tag tag) {
        boolean result = tagService.saveOrUpdateTag(tag);
        if (!result) {
            return ResponseMessage.error(404, "Tag not created");
        }
        return ResponseMessage.success(tag);
    }

    @DeleteMapping("/id/{id}")
    public ResponseMessage deleteTag(@PathVariable int id) {
        boolean result = tagService.deleteTag(id);
        if (!result) {
            return ResponseMessage.error(404, "Tag not deleted");
        }
        return ResponseMessage.success();
    }
}

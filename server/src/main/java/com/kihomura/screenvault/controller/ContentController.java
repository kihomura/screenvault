package com.kihomura.screenvault.controller;

import com.kihomura.screenvault.pojo.Content;
import com.kihomura.screenvault.pojo.dto.ResponseMessage;
import com.kihomura.screenvault.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/content")
public class ContentController {

    @Autowired
    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    /**
     * GET: /content/{title}
     * @param title will be normalized by removing non-letter characters
     *              and converting to lowercase for fuzzy matching.
     * @return data will be a JSON array containing all matched contents.
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
     * GET: /content/{id}
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
     * POST: /content
     * @param content
     * @return
     */
    @PostMapping
    public ResponseMessage createContent(@RequestBody Content content) {
        boolean result = contentService.create(content);
        if (result) {
            return ResponseMessage.success(content);
        } else {
            return ResponseMessage.error(400, "content not created");
        }
    }

    @PutMapping
    public ResponseMessage updateContent(@RequestBody Content content) {
        boolean result = contentService.update(content);
        if (result) {
            return ResponseMessage.success(content);
        } else {
            return ResponseMessage.error(400, "content not updated");
        }
    }

    /**
     * DELETE: /content/{id}
     * @param id
     * @return
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
package com.kihomura.screenvault.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kihomura.screenvault.enums.Category;
import com.kihomura.screenvault.enums.Genre;
import com.kihomura.screenvault.enums.SourceType;
import com.kihomura.screenvault.mapper.ContentMapper;
import com.kihomura.screenvault.service.ContentService;
import com.kihomura.screenvault.entity.Content;
import com.kihomura.screenvault.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of ContentService interface.
 * Handles content management operations including search, creation,
 * update, and deletion with title normalization and enum validation.
 */
@Service
public class ContentServiceImpl extends ServiceImpl<ContentMapper, Content> implements ContentService {

    private final ContentMapper contentMapper;
    private final UserService userService;

    public ContentServiceImpl(ContentMapper contentMapper, UserService userService) {
        this.contentMapper = contentMapper;
        this.userService = userService;
    }

    @Override
    public List<Content> findByTitle(String title) {
        String normalizedTitle = normalize(title);
        return contentMapper.findByTitle(normalizedTitle);
    }

    @Override
    public List<Content> findByTitleAndCreator(String title) {
        String normalizedTitle = normalize(title);
        return contentMapper.findByTitleAndCreator(normalizedTitle, userService.getCurrentUserId());
    }

    @Override
    public List<Content> findCustomContent() {
        return contentMapper.findCustomContentByUserId(userService.getCurrentUserId());
    }

    /**
     * Saves or updates content with validation and user ownership assignment.
     * Validates required fields and enum values before saving.
     * 
     * @param content the content to save or update
     * @return true if operation succeeds, false otherwise
     * @throws IllegalArgumentException if validation fails
     */
    @Override
    public boolean saveOrUpdateContent(Content content) {
        if (content.getTitle() == null || content.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }

        if (content.getGenre() != null && !isValidEnumValue(content.getGenre().toString(), Genre.class)){
            throw new IllegalArgumentException("Genre doesn't exist");
        }

        if (content.getCategory() != null && !isValidEnumValue(content.getCategory().toString(), Category.class)){
            throw new IllegalArgumentException("Category doesn't exist");
        }

        content.setSourceType(SourceType.CUSTOM_DATA);
        content.setCreatorId(userService.getCurrentUserId());

        return this.saveOrUpdate(content);
    }

    @Override
    public boolean delete(int contentId) {
        return contentMapper.deleteByIdAndCreator(contentId, userService.getCurrentUserId());
    }

    /**
     * Normalizes input string by removing all non-letter characters and converting to lowercase.
     * Used for fuzzy title matching in search operations.
     * 
     * @param input the string to normalize
     * @return normalized string containing only lowercase letters
     */
    private String normalize(String input) {
        if (input == null) return "";
        return input.replaceAll("[^a-zA-Z]", "").toLowerCase();
    }

    /**
     * Validates if a string value is a valid enum constant.
     * 
     * @param value the string value to validate
     * @param enumClass the enum class to validate against
     * @return true if value is a valid enum constant, false otherwise
     */
    private boolean isValidEnumValue(String value, Class<? extends Enum<?>> enumClass) {
        try {
            Enum.valueOf(enumClass.asSubclass(Enum.class), value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}

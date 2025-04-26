package com.kihomura.screenvault.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kihomura.screenvault.enums.Category;
import com.kihomura.screenvault.enums.Genre;
import com.kihomura.screenvault.enums.SourceType;
import com.kihomura.screenvault.mapper.ContentMapper;
import com.kihomura.screenvault.service.ContentService;
import com.kihomura.screenvault.pojo.Content;
import com.kihomura.screenvault.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

        return this.saveOrUpdateContent(content);
    }

    @Override
    public boolean delete(int contentId) {
        return contentMapper.deleteByIdAndCreator(contentId, userService.getCurrentUserId());
    }

    /**
     * 将输入字符串标准化：去除所有非字母字符，并转为小写
     */
    private String normalize(String input) {
        if (input == null) return "";
        return input.replaceAll("[^a-zA-Z]", "").toLowerCase();
    }

    private boolean isValidEnumValue(String value, Class<? extends Enum<?>> enumClass) {
        try {
            Enum.valueOf(enumClass.asSubclass(Enum.class), value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}

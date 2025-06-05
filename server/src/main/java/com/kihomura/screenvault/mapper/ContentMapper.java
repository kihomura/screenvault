package com.kihomura.screenvault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kihomura.screenvault.entity.Content;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ContentMapper extends BaseMapper<Content> {

    /**
     * Search content by title using fuzzy matching with regex normalization.
     * Removes non-letter characters and converts to lowercase for matching.
     */
    @Select("SELECT * FROM contents " +
            "WHERE REGEXP_REPLACE(LOWER(title), '[^a-z]', '') LIKE CONCAT('%', #{title}, '%') " +
            "OR REGEXP_REPLACE(LOWER(other_title), '[^a-z]', '') LIKE CONCAT('%', #{title}, '%')" +
            "AND category = 'OFFICIAL_DATA'")
    List<Content> findByTitle(String title);

    /**
     * Search custom content by title for specific user.
     */
    @Select("SELECT * FROM contents " +
            "WHERE (REGEXP_REPLACE(LOWER(title), '[^a-z]', '') LIKE CONCAT('%', #{title}, '%') " +
            "OR REGEXP_REPLACE(LOWER(other_title), '[^a-z]', '') LIKE CONCAT('%', #{title}, '%')) " +
            "AND category = 'CUSTOM_DATA'" +
            "AND (creator_id = #{currentUserId})")
    List<Content> findByTitleAndCreator(@Param("title") String title, @Param("currentUserId") Integer currentUserId);

    @Select("SELECT * FROM contents WHERE source_type = 'CUSTOM_DATA' AND creator_id = #{userId}")
    List<Content> findCustomContentByUserId(int userId);

    @Update("UPDATE contents SET title = #{title}, other_title = #{otherTitle}, " +
            "country = #{country}, language = #{language}, description = #{description}, " +
            "image = #{image}, release_date = #{releaseDate}, genre = #{genre}, " +
            "category = #{category}, source_type = #{sourceType} " +
            "WHERE id = #{id} AND creator_id = #{creatorId}")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    boolean updateContent(Content content);

    @Delete("DELETE FROM contents WHERE id = #{contentId} AND creator_id = #{creatorId}")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    boolean deleteByIdAndCreator(@Param("contentId") int contentId, @Param("creatorId") Integer creatorId);

}
package com.kihomura.screenvault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kihomura.screenvault.pojo.Content;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ContentMapper extends BaseMapper<Content> {

    /**
     * 通过对title和other_title字段进行正则替换，将非字母字符去除后做模糊匹配
     * REGEXP_REPLACE - MySQL8+ 的正则匹配函数
     */
    @Select("SELECT * FROM contents " +
            "WHERE REGEXP_REPLACE(LOWER(title), '[^a-z]', '') LIKE CONCAT('%', #{title}, '%') " +
            "OR REGEXP_REPLACE(LOWER(other_title), '[^a-z]', '') LIKE CONCAT('%', #{title}, '%')" +
            "AND category = 'OFFICIAL_DATA'")
    List<Content> findByTitle(String title);

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
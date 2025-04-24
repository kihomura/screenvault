package com.kihomura.screenvault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kihomura.screenvault.pojo.ListContent;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ListContentMapper extends BaseMapper<ListContent> {

    @Select("SELECT * FROM list_content WHERE list_id = #{listId}")
    List<ListContent> findByListId(@Param("listId") Integer listId);

    @Select("SELECT * FROM list_content WHERE content_id = #{contentId}")
    List<ListContent> findByContentId(@Param("contentId") Integer contentId);

    @Select("SELECT * FROM list_content WHERE list_id = #{listId} AND content_id = #{contentId}")
    ListContent findByListIdAndContentId(@Param("listId") Integer listId, @Param("contentId") Integer contentId);

    @Delete("DELETE FROM list_content WHERE list_id = #{listId} AND content_id = #{contentId}")
    int deleteByListIdAndContentId(@Param("listId") Integer listId, @Param("contentId") Integer contentId);

    @Delete("DELETE FROM list_content WHERE list_id = #{listId}")
    int deleteByListId(@Param("listId") Integer listId);
}
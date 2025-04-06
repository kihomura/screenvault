package com.kihomura.screenvault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kihomura.screenvault.pojo.TagContent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;

import java.util.List;

@Mapper
public interface TagContentMapper extends BaseMapper<TagContent> {

    @Select("SELECT content_id FROM tag_content WHERE tag_id = #{tagId}")
    List<Integer> findContentIdsByTagId(int tagId);

    @Select("SELECT tag_id FROM tag_content WHERE content_id = #{contentId}")
    List<Integer> findTagIdsByContentId(int contentId);

    @Delete("DELETE FROM tag_content WHERE tag_id = #{tagId} AND content_id = #{contentId}")
    boolean deleteByTagIdAndContentId(TagContent tagContent);

}

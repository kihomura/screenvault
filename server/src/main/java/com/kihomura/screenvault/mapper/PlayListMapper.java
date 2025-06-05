package com.kihomura.screenvault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kihomura.screenvault.entity.PlayList;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PlayListMapper extends BaseMapper<PlayList> {

    @Select("SELECT * FROM lists WHERE creator_id = #{userId} AND is_default = false")
    List<PlayList> findByUserId(int userId);

    @Select("SELECT * FROM lists WHERE list_name = 'Wish List' AND is_default = true AND creator_id = #{userId}")
    PlayList findWishlistByUserId(int userId);

    /**
     * Delete non-default playlist by ID.
     */
    @Delete("DELETE FROM lists WHERE id = #{listId} AND is_default = false")
    int deleteByListId(int listId);
}

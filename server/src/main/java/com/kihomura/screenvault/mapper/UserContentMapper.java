package com.kihomura.screenvault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kihomura.screenvault.entity.UserContent;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserContentMapper extends BaseMapper<UserContent> {
    @Select("SELECT * FROM user_content WHERE user_id = #{userId} AND status = 'WATCHED'")
    List<UserContent> findByUserId(int userId);

    @Select("SELECT * FROM user_content WHERE user_id = #{userId} AND status = 'WANT_TO_WATCH'")
    List<UserContent> findWishlistByUserId(int userId);

    @Select("SELECT * FROM user_content WHERE content_id = #{contentId} AND user_id = #{userId} AND status = 'WATCHED'")
    UserContent findByContentId(int contentId, int userId);

    @Select("SELECT * FROM user_content WHERE content_id = #{contentId} AND user_id = #{userId}")
    UserContent selectByUserAndContentId(int userId, int contentId);

    @Insert("INSERT INTO user_content (user_id, content_id, status) VALUES (#{userId}, #{contentId}, 'WANT_TO_WATCH')")
    boolean addToWishList(int contentId, int userId);

    @Delete("DELETE FROM user_content WHERE content_id = #{contentId} AND user_id = #{userId} AND status = 'WANT_TO_WATCH'")
    int removeFromWishList(int contentId, int userId);
}

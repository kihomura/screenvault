package com.kihomura.screenvault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kihomura.screenvault.pojo.UserContent;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserContentMapper extends BaseMapper<UserContent> {
    @Select("SELECT * FROM user_content WHERE user_id = #{userId}")
    List<UserContent> findByUserId(int userId);

    @Insert("INSERT INTO user_content (user_id, content_id, status) VALUES (#{userId}, #{contentId}, #{status})")
    boolean addToWishList(UserContent userContent);
}

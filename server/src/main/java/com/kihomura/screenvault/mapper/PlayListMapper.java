package com.kihomura.screenvault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kihomura.screenvault.pojo.PlayList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PlayListMapper extends BaseMapper<PlayList> {

    @Select("SELECT * FROM lists WHERE creator_id = #{userId}")
    List<PlayList> findByUserId(int userId);
}

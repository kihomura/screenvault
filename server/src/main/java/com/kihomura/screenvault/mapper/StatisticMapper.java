package com.kihomura.screenvault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kihomura.screenvault.pojo.Statistic.StatisticData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StatisticMapper extends BaseMapper<Object> {

    @Select("SELECT c.country AS name, COUNT(*) AS count " +
            "FROM user_content uc " +
            "JOIN contents c ON uc.content_id = c.id " +
            "WHERE uc.user_id = #{userId} AND uc.status = 'WATCHED' " +
            "AND (#{year} IS NULL OR EXTRACT(YEAR FROM uc.watch_date) = #{year}) " +
            "GROUP BY c.country")
    List<StatisticData> getCountryStatistics(@Param("userId") Integer userId, @Param("year") Integer year);

    @Select("SELECT c.genre AS name, COUNT(*) AS count " +
            "FROM user_content uc " +
            "JOIN contents c ON uc.content_id = c.id " +
            "WHERE uc.user_id = #{userId} AND uc.status = 'WATCHED' " +
            "AND (#{year} IS NULL OR EXTRACT(YEAR FROM uc.watch_date) = #{year}) " +
            "GROUP BY c.genre")
    List<StatisticData> getGenreStatistics(@Param("userId") Integer userId, @Param("year") Integer year);

    @Select("SELECT c.category AS name, COUNT(*) AS count " +
            "FROM user_content uc " +
            "JOIN contents c ON uc.content_id = c.id " +
            "WHERE uc.user_id = #{userId} AND uc.status = 'WATCHED' " +
            "AND (#{year} IS NULL OR EXTRACT(YEAR FROM uc.watch_date) = #{year}) " +
            "GROUP BY c.category")
    List<StatisticData> getCategoryStatistics(@Param("userId") Integer userId, @Param("year") Integer year);

    @Select("SELECT " +
            "CASE " +
            "  WHEN uc.rate BETWEEN 0 AND 0.5 THEN '0-0.5' " +
            "  WHEN uc.rate BETWEEN 1 AND 1.5 THEN '1-1.5' " +
            "  WHEN uc.rate BETWEEN 2 AND 2.5 THEN '2-2.5' " +
            "  WHEN uc.rate BETWEEN 3 AND 3.5 THEN '3-3.5' " +
            "  WHEN uc.rate BETWEEN 4 AND 4.5 THEN '4-4.5' " +
            "  WHEN uc.rate BETWEEN 5 AND 5.5 THEN '5-5.5' " +
            "  WHEN uc.rate BETWEEN 6 AND 6.5 THEN '6-6.5' " +
            "  WHEN uc.rate BETWEEN 7 AND 7.5 THEN '7-7.5' " +
            "  WHEN uc.rate BETWEEN 8 AND 8.5 THEN '8-8.5' " +
            "  WHEN uc.rate BETWEEN 9 AND 9.5 THEN '9-9.5' " +
            "  WHEN uc.rate = 10 THEN '10' " +
            "  ELSE 'No Rating' " +
            "END AS name, " +
            "COUNT(*) AS count " +
            "FROM user_content uc " +
            "WHERE uc.user_id = #{userId} AND uc.status = 'WATCHED' " +
            "AND (#{year} IS NULL OR EXTRACT(YEAR FROM uc.watch_date) = #{year}) " +
            "GROUP BY name " +
            "ORDER BY " +
            "CASE " +
            "  WHEN name = 'No Rating' THEN 11 " +
            "  WHEN name = '0-0.5' THEN 0 " +
            "  WHEN name = '1-1.5' THEN 1 " +
            "  WHEN name = '2-2.5' THEN 2 " +
            "  WHEN name = '3-3.5' THEN 3 " +
            "  WHEN name = '4-4.5' THEN 4 " +
            "  WHEN name = '5-5.5' THEN 5 " +
            "  WHEN name = '6-6.5' THEN 6 " +
            "  WHEN name = '7-7.5' THEN 7 " +
            "  WHEN name = '8-8.5' THEN 8 " +
            "  WHEN name = '9-9.5' THEN 9 " +
            "  WHEN name = '10' THEN 10 " +
            "END")
    List<StatisticData> getRatingStatistics(@Param("userId") Integer userId, @Param("year") Integer year);

    @Select("SELECT COUNT(*) " +
            "FROM user_content uc " +
            "WHERE uc.user_id = #{userId} AND uc.status = 'WATCHED' " +
            "AND (#{year} IS NULL OR EXTRACT(YEAR FROM uc.watch_date) = #{year})")
    Integer getTotalWatchedCount(@Param("userId") Integer userId, @Param("year") Integer year);
}
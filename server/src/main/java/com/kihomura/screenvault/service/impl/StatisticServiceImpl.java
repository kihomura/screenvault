package com.kihomura.screenvault.service.impl;

import com.kihomura.screenvault.entity.Statistic;
import com.kihomura.screenvault.entity.Statistic.StatisticData;
import com.kihomura.screenvault.mapper.StatisticMapper;
import com.kihomura.screenvault.service.StatisticService;
import com.kihomura.screenvault.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of StatisticService interface.
 * Provides statistical analysis of user's watched content including
 * country, genre, category, and rating distributions with percentage calculations.
 */
@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {

    private final StatisticMapper statisticMapper;
    private final UserService userService;

    @Override
    public Statistic getAllStatistics(Integer year) {
        return Statistic.builder()
                .countryData(getCountryStatisticsWithPercentage(year))
                .genreData(getGenreStatisticsWithPercentage(year))
                .categoryData(getCategoryStatisticsWithPercentage(year))
                .ratingData(getRatingStatisticsWithPercentage(year))
                .build();
    }

    @Override
    public Statistic getCountryStatistics(Integer year) {
        return Statistic.builder()
                .countryData(getCountryStatisticsWithPercentage(year))
                .build();
    }

    @Override
    public Statistic getGenreStatistics(Integer year) {
        return Statistic.builder()
                .genreData(getGenreStatisticsWithPercentage(year))
                .build();
    }

    @Override
    public Statistic getCategoryStatistics(Integer year) {
        return Statistic.builder()
                .categoryData(getCategoryStatisticsWithPercentage(year))
                .build();
    }

    @Override
    public Statistic getRatingStatistics(Integer year) {
        return Statistic.builder()
                .ratingData(getRatingStatisticsWithPercentage(year))
                .build();
    }

    private List<StatisticData> getCountryStatisticsWithPercentage(Integer year) {
        Integer userId = userService.getCurrentUserId();
        List<StatisticData> countryStats = statisticMapper.getCountryStatistics(userId, year);
        return calculatePercentages(countryStats, userId, year);
    }

    private List<StatisticData> getGenreStatisticsWithPercentage(Integer year) {
        Integer userId = userService.getCurrentUserId();
        List<StatisticData> genreStats = statisticMapper.getGenreStatistics(userId, year);
        return calculatePercentages(genreStats, userId, year);
    }

    private List<StatisticData> getCategoryStatisticsWithPercentage(Integer year) {
        Integer userId = userService.getCurrentUserId();
        List<StatisticData> categoryStats = statisticMapper.getCategoryStatistics(userId, year);
        return calculatePercentages(categoryStats, userId, year);
    }

    private List<StatisticData> getRatingStatisticsWithPercentage(Integer year) {
        Integer userId = userService.getCurrentUserId();
        List<StatisticData> ratingStats = statisticMapper.getRatingStatistics(userId, year);
        return calculatePercentages(ratingStats, userId, year);
    }

    /**
     * Calculates percentage values for statistical data based on total watched count.
     * 
     * @param stats the raw statistical data
     * @param userId the user ID for total count calculation
     * @param year optional year filter for total count
     * @return list of statistical data with calculated percentages
     */
    private List<StatisticData> calculatePercentages(List<StatisticData> stats, Integer userId, Integer year) {
        Integer total = statisticMapper.getTotalWatchedCount(userId, year);
        if (total <= 0) {
            return stats;
        }

        return stats.stream().map(item -> {
            BigDecimal percentage = BigDecimal.valueOf(item.getCount())
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);
            item.setPercentage(percentage);
            return item;
        }).collect(Collectors.toList());
    }
}
package com.kihomura.screenvault.service;

import com.kihomura.screenvault.entity.Statistic;

public interface StatisticService {

    /**
     * Get all statistics for the current user
     * @param year Optional filter for year
     * @return Statistic containing all statistics
     */
    Statistic getAllStatistics(Integer year);

    /**
     * Get country statistics for the current user
     * @param year Optional filter for year
     * @return Statistic containing country statistics
     */
    Statistic getCountryStatistics(Integer year);

    /**
     * Get genre statistics for the current user
     * @param year Optional filter for year
     * @return Statistic containing genre statistics
     */
    Statistic getGenreStatistics(Integer year);

    /**
     * Get category statistics for the current user
     * @param year Optional filter for year
     * @return Statistic containing category statistics
     */
    Statistic getCategoryStatistics(Integer year);

    /**
     * Get rating statistics for the current user
     * @param year Optional filter for year
     * @return Statistic containing rating statistics
     */
    Statistic getRatingStatistics(Integer year);
}
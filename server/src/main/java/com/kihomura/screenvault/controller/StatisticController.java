package com.kihomura.screenvault.controller;

import com.kihomura.screenvault.entity.Statistic;
import com.kihomura.screenvault.service.StatisticService;
import com.kihomura.screenvault.entity.dto.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for retrieving various statistical data about content.
 * Provides endpoints for accessing analytics and statistics related to content,
 * including country, genre, category, and rating distributions.
 */
@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticController {

    private final StatisticService statisticService;

    /**
     * Retrieves comprehensive statistics for all content data.
     * Returns aggregated statistics including all available metrics.
     * 
     * GET: /statistics
     * @param year Optional filter by year to get statistics for specific year
     * @return ResponseMessage containing Statistic object with all data
     */
    @GetMapping
    public ResponseMessage<Statistic> getAllStatistics(
            @RequestParam(required = false) Integer year) {
        return ResponseMessage.success(statisticService.getAllStatistics(year));
    }

    /**
     * Retrieves statistics grouped by country of origin.
     * Returns content distribution data organized by country.
     * 
     * GET: /statistics/country
     * @param year Optional filter by year to get country statistics for specific year
     * @return ResponseMessage containing Statistic object with country data
     */
    @GetMapping("/country")
    public ResponseMessage<Statistic> getCountryStatistics(
            @RequestParam(required = false) Integer year) {
        return ResponseMessage.success(statisticService.getCountryStatistics(year));
    }

    /**
     * Retrieves statistics grouped by genre classification.
     * Returns content distribution data organized by genre categories.
     * 
     * GET: /statistics/genre
     * @param year Optional filter by year to get genre statistics for specific year
     * @return ResponseMessage containing Statistic object with genre data
     */
    @GetMapping("/genre")
    public ResponseMessage<Statistic> getGenreStatistics(
            @RequestParam(required = false) Integer year) {
        return ResponseMessage.success(statisticService.getGenreStatistics(year));
    }

    /**
     * Retrieves statistics grouped by content category.
     * Returns content distribution data organized by category types.
     * 
     * GET: /statistics/category
     * @param year Optional filter by year to get category statistics for specific year
     * @return ResponseMessage containing Statistic object with category data
     */
    @GetMapping("/category")
    public ResponseMessage<Statistic> getCategoryStatistics(
            @RequestParam(required = false) Integer year) {
        return ResponseMessage.success(statisticService.getCategoryStatistics(year));
    }

    /**
     * Retrieves statistics grouped by rating scores.
     * Returns content distribution data organized by rating ranges.
     * 
     * GET: /statistics/rating
     * @param year Optional filter by year to get rating statistics for specific year
     * @return ResponseMessage containing Statistic object with rating data
     */
    @GetMapping("/rating")
    public ResponseMessage<Statistic> getRatingStatistics(
            @RequestParam(required = false) Integer year) {
        return ResponseMessage.success(statisticService.getRatingStatistics(year));
    }
}
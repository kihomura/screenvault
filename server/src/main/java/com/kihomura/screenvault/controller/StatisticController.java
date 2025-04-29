package com.kihomura.screenvault.controller;

import com.kihomura.screenvault.pojo.Statistic;
import com.kihomura.screenvault.service.StatisticService;
import com.kihomura.screenvault.pojo.dto.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticController {

    private final StatisticService statisticService;

    /**
     * Get all statistics
     * @param year Optional filter by year
     * @return Statistic with all data
     */
    @GetMapping
    public ResponseMessage<Statistic> getAllStatistics(
            @RequestParam(required = false) Integer year) {
        return ResponseMessage.success(statisticService.getAllStatistics(year));
    }

    /**
     * Get country statistics
     * @param year Optional filter by year
     * @return Statistic with country data
     */
    @GetMapping("/country")
    public ResponseMessage<Statistic> getCountryStatistics(
            @RequestParam(required = false) Integer year) {
        return ResponseMessage.success(statisticService.getCountryStatistics(year));
    }

    /**
     * Get genre statistics
     * @param year Optional filter by year
     * @return Statistic with genre data
     */
    @GetMapping("/genre")
    public ResponseMessage<Statistic> getGenreStatistics(
            @RequestParam(required = false) Integer year) {
        return ResponseMessage.success(statisticService.getGenreStatistics(year));
    }

    /**
     * Get category statistics
     * @param year Optional filter by year
     * @return Statistic with category data
     */
    @GetMapping("/category")
    public ResponseMessage<Statistic> getCategoryStatistics(
            @RequestParam(required = false) Integer year) {
        return ResponseMessage.success(statisticService.getCategoryStatistics(year));
    }

    /**
     * Get rating statistics
     * @param year Optional filter by year
     * @return Statistic with rating data
     */
    @GetMapping("/rating")
    public ResponseMessage<Statistic> getRatingStatistics(
            @RequestParam(required = false) Integer year) {
        return ResponseMessage.success(statisticService.getRatingStatistics(year));
    }
}
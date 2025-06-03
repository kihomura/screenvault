package com.kihomura.screenvault.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Statistic {

    private List<StatisticData> countryData;
    private List<StatisticData> genreData;
    private List<StatisticData> categoryData;
    private List<StatisticData> ratingData;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatisticData {
        private String name;
        private Integer count;
        private BigDecimal percentage;
    }
}
package com.example.logindemo.controller;

import com.example.logindemo.service.StatisticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/behavior-type")
    public Map<String, Object> getBehaviorTypeStats() {
        return statisticsService.getBehaviorTypeStats();
    }

    @GetMapping("/top-items")
    public Map<String, Object> getTopItemsStats() {
        return statisticsService.getTopItemsStats();
    }

    @GetMapping("/hourly")
    public Map<String, Object> getHourlyStats() {
        return statisticsService.getHourlyStats();
    }

    @GetMapping("/overview")
    public Map<String, Object> getOverviewStats() {
        return statisticsService.getOverviewStats();
    }

    @GetMapping("/recent")
    public Map<String, Object> getRecentRecords() {
        return statisticsService.getRecentRecords();
    }
}
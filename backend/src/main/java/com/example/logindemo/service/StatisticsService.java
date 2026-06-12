package com.example.logindemo.service;

import com.example.logindemo.entity.UserBehavior;
import com.example.logindemo.repository.UserBehaviorRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class StatisticsService {

    private final UserBehaviorRepository userBehaviorRepository;

    public StatisticsService(UserBehaviorRepository userBehaviorRepository) {
        this.userBehaviorRepository = userBehaviorRepository;
    }

    public Map<String, Object> getBehaviorTypeStats() {
        List<Object[]> results = userBehaviorRepository.countByBehaviorType();
        Map<String, Object> stats = new HashMap<>();
        List<Map<String, Object>> data = new ArrayList<>();
        
        for (Object[] row : results) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", row[0]);
            item.put("value", row[1]);
            data.add(item);
        }
        
        stats.put("data", data);
        return stats;
    }

    public Map<String, Object> getTopItemsStats() {
        List<Object[]> results = userBehaviorRepository.countByItemIdTop10();
        Map<String, Object> stats = new HashMap<>();
        List<String> itemIds = new ArrayList<>();
        List<Long> counts = new ArrayList<>();
        
        int limit = Math.min(10, results.size());
        for (int i = 0; i < limit; i++) {
            Object[] row = results.get(i);
            itemIds.add("商品" + row[0]);
            counts.add(((Number) row[1]).longValue());
        }
        
        stats.put("itemIds", itemIds);
        stats.put("counts", counts);
        return stats;
    }

    public Map<String, Object> getOverviewStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userBehaviorRepository.countDistinctUsers());
        stats.put("totalItems", userBehaviorRepository.countDistinctItems());
        stats.put("totalRecords", userBehaviorRepository.countTotalRecords());
        stats.put("totalPurchases", userBehaviorRepository.countPurchases());
        return stats;
    }

    public Map<String, Object> getRecentRecords() {
        Map<String, Object> stats = new HashMap<>();
        List<UserBehavior> recent = userBehaviorRepository.findTop5ByTimestamp();
        stats.put("data", recent);
        return stats;
    }

    public Map<String, Object> getHourlyStats() {
        List<Long> timestamps = userBehaviorRepository.findAllTimestamps();
        int[] hourlyCounts = new int[24];
        
        for (Long ts : timestamps) {
            LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(ts), 
                ZoneId.systemDefault()
            );
            int hour = dateTime.getHour();
            hourlyCounts[hour]++;
        }
        
        Map<String, Object> stats = new HashMap<>();
        List<String> hours = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        
        for (int i = 0; i < 24; i++) {
            hours.add(i + "时");
            counts.add(hourlyCounts[i]);
        }
        
        stats.put("hours", hours);
        stats.put("counts", counts);
        return stats;
    }
}
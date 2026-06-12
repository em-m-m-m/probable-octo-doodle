package com.example.logindemo.service;

import com.example.logindemo.entity.UserBehavior;
import com.example.logindemo.repository.UserBehaviorRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvImportService {

    private final UserBehaviorRepository userBehaviorRepository;

    public CsvImportService(UserBehaviorRepository userBehaviorRepository) {
        this.userBehaviorRepository = userBehaviorRepository;
    }

    public int importFromCsv(MultipartFile file) {
        List<UserBehavior> records = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                     .builder()
                     .setHeader("user_id", "item_id", "behavior_type", "timestamp", "category", "location")
                     .setSkipHeaderRecord(true)
                     .setIgnoreHeaderCase(true)
                     .setTrim(true)
                     .build())) {
            
            for (CSVRecord csvRecord : csvParser) {
                UserBehavior behavior = new UserBehavior();
                behavior.setUserId(Integer.parseInt(csvRecord.get("user_id")));
                behavior.setItemId(Integer.parseInt(csvRecord.get("item_id")));
                behavior.setBehaviorType(csvRecord.get("behavior_type"));
                behavior.setTimestamp(Long.parseLong(csvRecord.get("timestamp")));
                behavior.setCategory(csvRecord.get("category"));
                behavior.setLocation(csvRecord.get("location"));
                records.add(behavior);
            }
            
            userBehaviorRepository.saveAll(records);
            return records.size();
            
        } catch (Exception e) {
            throw new RuntimeException("CSV导入失败: " + e.getMessage(), e);
        }
    }
}

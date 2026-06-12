package com.example.logindemo.controller;

import com.example.logindemo.service.CsvImportService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CsvImportController {

    private final CsvImportService csvImportService;

    public CsvImportController(CsvImportService csvImportService) {
        this.csvImportService = csvImportService;
    }

    @PostMapping("/import/csv")
    public Map<String, Object> importCsv(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        if (file.isEmpty()) {
            response.put("success", false);
            response.put("message", "请选择要上传的CSV文件");
            return response;
        }
        
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".csv")) {
            response.put("success", false);
            response.put("message", "只支持CSV格式文件");
            return response;
        }
        
        try {
            int count = csvImportService.importFromCsv(file);
            response.put("success", true);
            response.put("message", "导入成功");
            response.put("count", count);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "导入失败: " + e.getMessage());
        }
        
        return response;
    }
}

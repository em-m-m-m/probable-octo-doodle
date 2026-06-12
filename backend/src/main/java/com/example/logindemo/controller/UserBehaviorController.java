package com.example.logindemo.controller;

import com.example.logindemo.entity.UserBehavior;
import com.example.logindemo.repository.UserBehaviorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/behavior")
public class UserBehaviorController {

    private final UserBehaviorRepository userBehaviorRepository;

    public UserBehaviorController(UserBehaviorRepository userBehaviorRepository) {
        this.userBehaviorRepository = userBehaviorRepository;
    }

    @GetMapping("/list")
    public Map<String, Object> getList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer itemId,
            @RequestParam(required = false) String behaviorType,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String location) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<UserBehavior> behaviorPage;

        behaviorPage = userBehaviorRepository.findByFilters(userId, behaviorType, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", behaviorPage.getContent());
        response.put("totalElements", behaviorPage.getTotalElements());
        response.put("totalPages", behaviorPage.getTotalPages());
        response.put("currentPage", page);
        response.put("pageSize", size);

        return response;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        Optional<UserBehavior> behavior = userBehaviorRepository.findById(id);
        if (behavior.isPresent()) {
            response.put("success", true);
            response.put("data", behavior.get());
        } else {
            response.put("success", false);
            response.put("message", "记录不存在");
        }
        return response;
    }

    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody UserBehavior behavior) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserBehavior saved = userBehaviorRepository.save(behavior);
            response.put("success", true);
            response.put("message", "新增成功");
            response.put("data", saved);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "新增失败: " + e.getMessage());
        }
        return response;
    }

    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable Long id, @RequestBody UserBehavior behavior) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<UserBehavior> existing = userBehaviorRepository.findById(id);
            if (existing.isPresent()) {
                UserBehavior entity = existing.get();
                entity.setUserId(behavior.getUserId());
                entity.setItemId(behavior.getItemId());
                entity.setBehaviorType(behavior.getBehaviorType());
                entity.setTimestamp(behavior.getTimestamp());
                entity.setCategory(behavior.getCategory());
                entity.setLocation(behavior.getLocation());
                UserBehavior saved = userBehaviorRepository.save(entity);
                response.put("success", true);
                response.put("message", "更新成功");
                response.put("data", saved);
            } else {
                response.put("success", false);
                response.put("message", "记录不存在");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新失败: " + e.getMessage());
        }
        return response;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (userBehaviorRepository.existsById(id)) {
                userBehaviorRepository.deleteById(id);
                response.put("success", true);
                response.put("message", "删除成功");
            } else {
                response.put("success", false);
                response.put("message", "记录不存在");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "删除失败: " + e.getMessage());
        }
        return response;
    }
}
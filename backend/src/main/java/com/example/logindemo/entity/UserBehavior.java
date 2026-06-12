package com.example.logindemo.entity;

import javax.persistence.*;

@Entity
@Table(name = "user", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_item_id", columnList = "item_id"),
    @Index(name = "idx_behavior_type", columnList = "behavior_type"),
    @Index(name = "idx_timestamp", columnList = "timestamp"),
    @Index(name = "idx_category", columnList = "category"),
    @Index(name = "idx_location", columnList = "location")
})
public class UserBehavior {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "item_id", nullable = false)
    private Integer itemId;

    @Column(name = "behavior_type", length = 10, nullable = false)
    private String behaviorType;

    @Column(name = "timestamp", nullable = false)
    private Long timestamp;

    @Column(name = "category", length = 20, nullable = false)
    private String category;

    @Column(name = "location", length = 50, nullable = false)
    private String location;

    public UserBehavior() {
    }

    public UserBehavior(Integer userId, Integer itemId, String behaviorType, Long timestamp, String category, String location) {
        this.userId = userId;
        this.itemId = itemId;
        this.behaviorType = behaviorType;
        this.timestamp = timestamp;
        this.category = category;
        this.location = location;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getBehaviorType() {
        return behaviorType;
    }

    public void setBehaviorType(String behaviorType) {
        this.behaviorType = behaviorType;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

package com.example.logindemo.repository;

import com.example.logindemo.entity.UserBehavior;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBehaviorRepository extends JpaRepository<UserBehavior, Long> {

    @Query("SELECT ub.behaviorType, COUNT(ub) FROM UserBehavior ub GROUP BY ub.behaviorType")
    List<Object[]> countByBehaviorType();

    @Query("SELECT ub.itemId, COUNT(ub) as cnt FROM UserBehavior ub GROUP BY ub.itemId ORDER BY cnt DESC")
    List<Object[]> countByItemIdTop10();

    @Query("SELECT ub.timestamp FROM UserBehavior ub")
    List<Long> findAllTimestamps();

    @Query("SELECT COUNT(DISTINCT ub.userId) FROM UserBehavior ub")
    Long countDistinctUsers();

    @Query("SELECT COUNT(DISTINCT ub.itemId) FROM UserBehavior ub")
    Long countDistinctItems();

    @Query("SELECT COUNT(ub) FROM UserBehavior ub")
    Long countTotalRecords();

    @Query("SELECT COUNT(ub) FROM UserBehavior ub WHERE ub.behaviorType = '购买'")
    Long countPurchases();

    @Query("SELECT ub FROM UserBehavior ub WHERE " +
           "(:userId IS NULL OR ub.userId = :userId) AND " +
           "(:behaviorType IS NULL OR ub.behaviorType = :behaviorType)")
    Page<UserBehavior> findByFilters(
            @Param("userId") Integer userId,
            @Param("behaviorType") String behaviorType,
            Pageable pageable);

    @Query("SELECT ub FROM UserBehavior ub ORDER BY ub.timestamp DESC")
    List<UserBehavior> findTop5ByTimestamp();
}

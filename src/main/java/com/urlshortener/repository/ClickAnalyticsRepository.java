package com.urlshortener.repository;

import com.urlshortener.model.ClickAnalytics;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClickAnalyticsRepository extends MongoRepository<ClickAnalytics, String>, ClickAnalyticsRepositoryCustom {

    List<ClickAnalytics> findByShortCodeOrderByClickedAtDesc(String shortCode, Pageable pageable);
    long countByShortCode(String shortCode);
    void deleteByShortCode(String shortCode);
}

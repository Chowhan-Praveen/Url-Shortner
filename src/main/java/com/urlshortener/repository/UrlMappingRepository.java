package com.urlshortener.repository;

import com.urlshortener.model.UrlMapping;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlMappingRepository extends MongoRepository<UrlMapping, String> {

    Optional<UrlMapping> findByShortCode(String shortCode);
    Optional<UrlMapping> findByCustomAlias(String customAlias);
    boolean existsByShortCode(String shortCode);
    boolean existsByCustomAlias(String customAlias);
    List<UrlMapping> findAllByOrderByCreatedAtDesc();

    @Query("{ 'shortCode': ?0 }")
    @Update("{ '$inc': { 'totalClicks': 1 } }")
    void incrementClickCount(String shortCode);
}

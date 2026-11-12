package com.urlshortener.repository;

import com.urlshortener.model.ClickAnalytics;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClickAnalyticsRepository extends JpaRepository<ClickAnalytics, Long> {

    List<ClickAnalytics> findByShortCodeOrderByClickedAtDesc(String shortCode, Pageable pageable);

    long countByShortCode(String shortCode);

    @Query("SELECT c.browser AS name, COUNT(c) AS count FROM ClickAnalytics c WHERE c.shortCode = :shortCode GROUP BY c.browser")
    List<Object[]> countClicksByBrowser(@Param("shortCode") String shortCode);

    @Query("SELECT c.operatingSystem AS name, COUNT(c) AS count FROM ClickAnalytics c WHERE c.shortCode = :shortCode GROUP BY c.operatingSystem")
    List<Object[]> countClicksByOperatingSystem(@Param("shortCode") String shortCode);

    @Query("SELECT c.deviceType AS name, COUNT(c) AS count FROM ClickAnalytics c WHERE c.shortCode = :shortCode GROUP BY c.deviceType")
    List<Object[]> countClicksByDeviceType(@Param("shortCode") String shortCode);

    @Query("SELECT COALESCE(c.referer, 'Direct') AS name, COUNT(c) AS count FROM ClickAnalytics c WHERE c.shortCode = :shortCode GROUP BY c.referer ORDER BY count DESC")
    List<Object[]> countClicksByReferer(@Param("shortCode") String shortCode, Pageable pageable);

    void deleteByShortCode(String shortCode);
}

package com.urlshortener.dto;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class AnalyticsResponse {

    private String shortCode;
    private String shortUrl;
    private String originalUrl;
    private String customAlias;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private long totalClicks;
    private boolean active;
    private boolean expired;

    private Map<String, Long> clicksByBrowser;
    private Map<String, Long> clicksByOperatingSystem;
    private Map<String, Long> clicksByDeviceType;
    private Map<String, Long> topReferrers;
    private Map<String, Long> clicksOverTime;
    private List<ClickLogDto> recentClicks;

    public AnalyticsResponse() {
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getCustomAlias() {
        return customAlias;
    }

    public void setCustomAlias(String customAlias) {
        this.customAlias = customAlias;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public long getTotalClicks() {
        return totalClicks;
    }

    public void setTotalClicks(long totalClicks) {
        this.totalClicks = totalClicks;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public Map<String, Long> getClicksByBrowser() {
        return clicksByBrowser;
    }

    public void setClicksByBrowser(Map<String, Long> clicksByBrowser) {
        this.clicksByBrowser = clicksByBrowser;
    }

    public Map<String, Long> getClicksByOperatingSystem() {
        return clicksByOperatingSystem;
    }

    public void setClicksByOperatingSystem(Map<String, Long> clicksByOperatingSystem) {
        this.clicksByOperatingSystem = clicksByOperatingSystem;
    }

    public Map<String, Long> getClicksByDeviceType() {
        return clicksByDeviceType;
    }

    public void setClicksByDeviceType(Map<String, Long> clicksByDeviceType) {
        this.clicksByDeviceType = clicksByDeviceType;
    }

    public Map<String, Long> getTopReferrers() {
        return topReferrers;
    }

    public void setTopReferrers(Map<String, Long> topReferrers) {
        this.topReferrers = topReferrers;
    }

    public Map<String, Long> getClicksOverTime() {
        return clicksOverTime;
    }

    public void setClicksOverTime(Map<String, Long> clicksOverTime) {
        this.clicksOverTime = clicksOverTime;
    }

    public List<ClickLogDto> getRecentClicks() {
        return recentClicks;
    }

    public void setRecentClicks(List<ClickLogDto> recentClicks) {
        this.recentClicks = recentClicks;
    }
}

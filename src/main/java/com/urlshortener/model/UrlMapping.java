package com.urlshortener.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

/** Document representing a shortened URL mapping. */
@Document(collection = "url_mappings")
public class UrlMapping {

    @Id
    private String id;

    private String originalUrl;

    @Indexed(unique = true)
    private String shortCode;

    @Indexed(unique = true, partialFilter = "{ 'customAlias': { '$type': 'string' } }")
    private String customAlias;

    @Indexed
    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;
    private long totalClicks = 0;
    private boolean isActive = true;

    public UrlMapping() {
        this.createdAt = LocalDateTime.now();
    }

    public UrlMapping(String originalUrl, String shortCode, String customAlias, LocalDateTime expiresAt) {
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
        this.customAlias = customAlias;
        this.expiresAt = expiresAt;
        this.createdAt = LocalDateTime.now();
        this.totalClicks = 0;
        this.isActive = true;
    }

    public boolean isExpired() {
        return this.expiresAt != null && LocalDateTime.now().isAfter(this.expiresAt);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getOriginalUrl() { return originalUrl; }
    public void setOriginalUrl(String originalUrl) { this.originalUrl = originalUrl; }
    public String getShortCode() { return shortCode; }
    public void setShortCode(String shortCode) { this.shortCode = shortCode; }
    public String getCustomAlias() { return customAlias; }
    public void setCustomAlias(String customAlias) { this.customAlias = customAlias; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public long getTotalClicks() { return totalClicks; }
    public void setTotalClicks(long totalClicks) { this.totalClicks = totalClicks; }
    public void incrementClicks() { this.totalClicks++; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}


package com.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ShortenRequest {

    @NotBlank(message = "Original URL cannot be empty")
    @Size(max = 2048, message = "URL cannot exceed 2048 characters")
    private String originalUrl;

    @Size(min = 3, max = 20, message = "Custom alias must be between 3 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_-]*$", message = "Custom alias can only contain letters, numbers, hyphens, and underscores")
    private String customAlias;

    private Integer expirationDays;

    public ShortenRequest() {
    }

    public ShortenRequest(String originalUrl, String customAlias, Integer expirationDays) {
        this.originalUrl = originalUrl;
        this.customAlias = customAlias;
        this.expirationDays = expirationDays;
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

    public Integer getExpirationDays() {
        return expirationDays;
    }

    public void setExpirationDays(Integer expirationDays) {
        this.expirationDays = expirationDays;
    }
}

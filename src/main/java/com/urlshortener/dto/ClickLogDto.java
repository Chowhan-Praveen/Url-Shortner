package com.urlshortener.dto;

import java.time.LocalDateTime;

public class ClickLogDto {

    private LocalDateTime clickedAt;
    private String ipAddress;
    private String browser;
    private String operatingSystem;
    private String deviceType;
    private String referer;

    public ClickLogDto() {
    }

    public ClickLogDto(LocalDateTime clickedAt, String ipAddress, String browser,
                       String operatingSystem, String deviceType, String referer) {
        this.clickedAt = clickedAt;
        this.ipAddress = ipAddress;
        this.browser = browser;
        this.operatingSystem = operatingSystem;
        this.deviceType = deviceType;
        this.referer = referer;
    }

    public LocalDateTime getClickedAt() {
        return clickedAt;
    }

    public void setClickedAt(LocalDateTime clickedAt) {
        this.clickedAt = clickedAt;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }
}

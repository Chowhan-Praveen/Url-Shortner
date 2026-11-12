package com.urlshortener.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

/** Document representing an individual click tracking event. */
@Document(collection = "click_analytics")
public class ClickAnalytics {

    @Id
    private String id;

    @Indexed
    private String shortCode;

    @Indexed
    private LocalDateTime clickedAt;

    private String ipAddress;
    private String userAgent;
    private String referer;
    private String browser;
    private String operatingSystem;
    private String deviceType;

    public ClickAnalytics() {
        this.clickedAt = LocalDateTime.now();
    }

    public ClickAnalytics(String shortCode, String ipAddress, String userAgent, String referer,
                          String browser, String operatingSystem, String deviceType) {
        this.shortCode = shortCode;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.referer = referer;
        this.browser = browser;
        this.operatingSystem = operatingSystem;
        this.deviceType = deviceType;
        this.clickedAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getShortCode() { return shortCode; }
    public void setShortCode(String shortCode) { this.shortCode = shortCode; }
    public LocalDateTime getClickedAt() { return clickedAt; }
    public void setClickedAt(LocalDateTime clickedAt) { this.clickedAt = clickedAt; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    public String getReferer() { return referer; }
    public void setReferer(String referer) { this.referer = referer; }
    public String getBrowser() { return browser; }
    public void setBrowser(String browser) { this.browser = browser; }
    public String getOperatingSystem() { return operatingSystem; }
    public void setOperatingSystem(String operatingSystem) { this.operatingSystem = operatingSystem; }
    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }
}

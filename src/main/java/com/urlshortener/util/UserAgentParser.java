package com.urlshortener.util;

import java.util.Locale;

/**
 * Lightweight, zero-dependency User-Agent parser to extract Browser,
 * Operating System, and Device Type for click analytics.
 */
public final class UserAgentParser {

    private UserAgentParser() {
        // Utility class
    }

    public record ClientInfo(String browser, String operatingSystem, String deviceType) {}

    public static ClientInfo parse(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) {
            return new ClientInfo("Unknown", "Unknown", "Unknown");
        }

        String ua = userAgent.toLowerCase(Locale.ROOT);

        // Detect Bots / Crawlers
        if (ua.contains("bot") || ua.contains("spider") || ua.contains("crawl") || ua.contains("curl") || ua.contains("postman")) {
            return new ClientInfo("Bot/Crawler", "Other", "Bot");
        }

        // Detect Device Type
        String deviceType = "Desktop";
        if (ua.contains("ipad") || ua.contains("tablet") || ua.contains("kindle")) {
            deviceType = "Tablet";
        } else if (ua.contains("mobile") || ua.contains("android") || ua.contains("iphone") || ua.contains("ipod")) {
            deviceType = "Mobile";
        }

        // Detect Operating System
        String os = "Unknown";
        if (ua.contains("windows nt 10.0") || ua.contains("windows nt 11.0") || ua.contains("windows 10") || ua.contains("windows 11")) {
            os = "Windows 10/11";
        } else if (ua.contains("windows")) {
            os = "Windows";
        } else if (ua.contains("android")) {
            os = "Android";
        } else if (ua.contains("iphone") || ua.contains("ipad") || ua.contains("ipod")) {
            os = "iOS";
        } else if (ua.contains("mac os x") || ua.contains("macintosh")) {
            os = "macOS";
        } else if (ua.contains("linux")) {
            os = "Linux";
        } else if (ua.contains("cros")) {
            os = "Chrome OS";
        }

        // Detect Browser (Order matters because Chrome UA includes Safari, Edge includes Chrome, etc.)
        String browser = "Other";
        if (ua.contains("edg/") || ua.contains("edge/")) {
            browser = "Microsoft Edge";
        } else if (ua.contains("opr/") || ua.contains("opera/")) {
            browser = "Opera";
        } else if (ua.contains("chrome/") && !ua.contains("chromium/")) {
            browser = "Google Chrome";
        } else if (ua.contains("firefox/")) {
            browser = "Mozilla Firefox";
        } else if (ua.contains("safari/") && !ua.contains("chrome/")) {
            browser = "Apple Safari";
        } else if (ua.contains("trident/") || ua.contains("msie ")) {
            browser = "Internet Explorer";
        }

        return new ClientInfo(browser, os, deviceType);
    }
}

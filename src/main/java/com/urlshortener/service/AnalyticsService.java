package com.urlshortener.service;

import com.urlshortener.dto.AnalyticsResponse;
import com.urlshortener.dto.ClickLogDto;
import com.urlshortener.model.ClickAnalytics;
import com.urlshortener.model.UrlMapping;
import com.urlshortener.repository.ClickAnalyticsRepository;
import com.urlshortener.repository.UrlMappingRepository;
import com.urlshortener.util.UserAgentParser;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final ClickAnalyticsRepository clickAnalyticsRepository;
    private final UrlMappingRepository urlMappingRepository;
    private final UrlService urlService;

    public AnalyticsService(
        ClickAnalyticsRepository clickAnalyticsRepository,
        UrlMappingRepository urlMappingRepository,
        UrlService urlService
    ) {
        this.clickAnalyticsRepository = clickAnalyticsRepository;
        this.urlMappingRepository = urlMappingRepository;
        this.urlService = urlService;
    }

    /**
     * Records an individual click asynchronously so the user's redirection is never slowed down.
     */
    @Async
    public void recordClick(String shortCode, String ipAddress, String userAgent, String referer) {
        UserAgentParser.ClientInfo clientInfo = UserAgentParser.parse(userAgent);

        // Sanitize Referer (null or empty -> Direct)
        String cleanReferer = (referer != null && !referer.isBlank()) ? referer.trim() : "Direct";

        ClickAnalytics click = new ClickAnalytics(
            shortCode,
            anonymizeIp(ipAddress),
            userAgent,
            cleanReferer,
            clientInfo.browser(),
            clientInfo.operatingSystem(),
            clientInfo.deviceType()
        );

        clickAnalyticsRepository.save(click);
        urlMappingRepository.incrementClickCount(shortCode);
    }

    /**
     * Fetches complete analytics data for a specific shortened URL.
     */
    public AnalyticsResponse getAnalytics(String shortCode) {
        UrlMapping mapping = urlService.getUrlMapping(shortCode);

        AnalyticsResponse response = new AnalyticsResponse();
        response.setShortCode(mapping.getShortCode());
        response.setShortUrl(urlService.buildShortUrl(mapping.getShortCode()));
        response.setOriginalUrl(mapping.getOriginalUrl());
        response.setCustomAlias(mapping.getCustomAlias());
        response.setCreatedAt(mapping.getCreatedAt());
        response.setExpiresAt(mapping.getExpiresAt());
        response.setTotalClicks(mapping.getTotalClicks());
        response.setActive(mapping.isActive());
        response.setExpired(mapping.isExpired());

        // Aggregate statistics
        response.setClicksByBrowser(toMap(clickAnalyticsRepository.countClicksByBrowser(shortCode)));
        response.setClicksByOperatingSystem(toMap(clickAnalyticsRepository.countClicksByOperatingSystem(shortCode)));
        response.setClicksByDeviceType(toMap(clickAnalyticsRepository.countClicksByDeviceType(shortCode)));
        response.setTopReferrers(toMap(clickAnalyticsRepository.countClicksByReferer(shortCode, PageRequest.of(0, 10))));

        // Fetch recent 50 clicks
        List<ClickAnalytics> recentList = clickAnalyticsRepository.findByShortCodeOrderByClickedAtDesc(
            shortCode,
            PageRequest.of(0, 50)
        );

        List<ClickLogDto> recentLogs = recentList.stream()
            .map(c -> new ClickLogDto(
                c.getClickedAt(),
                c.getIpAddress(),
                c.getBrowser(),
                c.getOperatingSystem(),
                c.getDeviceType(),
                c.getReferer()
            ))
            .collect(Collectors.toList());
        response.setRecentClicks(recentLogs);

        // Calculate timeline breakdown (group by date YYYY-MM-DD)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Map<String, Long> timeline = recentList.stream()
            .collect(Collectors.groupingBy(
                c -> c.getClickedAt().format(formatter),
                TreeMap::new,
                Collectors.counting()
            ));
        response.setClicksOverTime(timeline);

        return response;
    }

    private Map<String, Long> toMap(List<Object[]> queryResults) {
        Map<String, Long> map = new LinkedHashMap<>();
        if (queryResults != null) {
            for (Object[] row : queryResults) {
                String key = row[0] != null ? row[0].toString() : "Unknown";
                Long count = ((Number) row[1]).longValue();
                map.put(key, count);
            }
        }
        return map;
    }

    /**
     * Anonymizes IP address for GDPR compliance / privacy (e.g. 192.168.1.45 -> 192.168.1.xxx)
     */
    private String anonymizeIp(String ip) {
        if (ip == null || ip.isBlank()) {
            return "Unknown";
        }
        int lastDot = ip.lastIndexOf('.');
        if (lastDot != -1) {
            return ip.substring(0, lastDot) + ".xxx";
        }
        int lastColon = ip.lastIndexOf(':');
        if (lastColon != -1) {
            return ip.substring(0, lastColon) + ":xxxx";
        }
        return ip;
    }
}



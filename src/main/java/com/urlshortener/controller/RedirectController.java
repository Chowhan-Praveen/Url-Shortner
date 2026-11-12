package com.urlshortener.controller;

import com.urlshortener.service.AnalyticsService;
import com.urlshortener.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.net.URI;

@Controller
public class RedirectController {

    private final UrlService urlService;
    private final AnalyticsService analyticsService;

    public RedirectController(UrlService urlService, AnalyticsService analyticsService) {
        this.urlService = urlService;
        this.analyticsService = analyticsService;
    }

    /**
     * Public redirection endpoint: GET /{shortCode}
     * Resolves the original URL, asynchronously logs analytics metadata,
     * and sends an HTTP 302 (Found) redirect header to the user's browser.
     */
    @GetMapping("/{shortCode:[a-zA-Z0-9_-]+}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortCode, HttpServletRequest request) {
        // Resolve original URL (validates existence and non-expiration)
        String originalUrl = urlService.resolveOriginalUrl(shortCode);

        // Extract client headers for analytics
        String clientIp = extractClientIp(request);
        String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
        String referer = request.getHeader(HttpHeaders.REFERER);

        // Record click asynchronously (non-blocking)
        analyticsService.recordClick(shortCode, clientIp, userAgent, referer);

        // Return HTTP 302 Temporary Redirect
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(originalUrl));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    /**
     * Extracts client IP address respecting reverse proxies / load balancers.
     */
    private String extractClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}

package com.urlshortener.controller;

import com.urlshortener.dto.AnalyticsResponse;
import com.urlshortener.dto.ShortenRequest;
import com.urlshortener.dto.ShortenResponse;
import com.urlshortener.dto.UrlSummaryResponse;
import com.urlshortener.service.AnalyticsService;
import com.urlshortener.service.UrlService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/urls")
@CrossOrigin(origins = "*") // Allows API access from any web frontend
public class UrlRestController {

    private final UrlService urlService;
    private final AnalyticsService analyticsService;

    public UrlRestController(UrlService urlService, AnalyticsService analyticsService) {
        this.urlService = urlService;
        this.analyticsService = analyticsService;
    }

    /**
     * Shortens a long URL.
     * POST /api/v1/urls/shorten
     */
    @PostMapping("/shorten")
    public ResponseEntity<ShortenResponse> shortenUrl(@Valid @RequestBody ShortenRequest request) {
        ShortenResponse response = urlService.shortenUrl(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Lists all shortened URLs.
     * GET /api/v1/urls
     */
    @GetMapping
    public ResponseEntity<List<UrlSummaryResponse>> getAllUrls() {
        return ResponseEntity.ok(urlService.getAllUrls());
    }

    /**
     * Fetches detailed analytics for a short code.
     * GET /api/v1/urls/{shortCode}/analytics
     */
    @GetMapping("/{shortCode}/analytics")
    public ResponseEntity<AnalyticsResponse> getAnalytics(@PathVariable String shortCode) {
        AnalyticsResponse response = analyticsService.getAnalytics(shortCode);
        return ResponseEntity.ok(response);
    }

    /**
     * Deactivates a URL mapping.
     * PATCH /api/v1/urls/{shortCode}/deactivate
     */
    @PatchMapping("/{shortCode}/deactivate")
    public ResponseEntity<Void> deactivateUrl(@PathVariable String shortCode) {
        urlService.deactivateUrl(shortCode);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes a URL mapping.
     * DELETE /api/v1/urls/{shortCode}
     */
    @DeleteMapping("/{shortCode}")
    public ResponseEntity<Void> deleteUrl(@PathVariable String shortCode) {
        urlService.deleteUrl(shortCode);
        return ResponseEntity.noContent().build();
    }
}

package com.urlshortener.service;

import com.urlshortener.dto.ShortenRequest;
import com.urlshortener.dto.ShortenResponse;
import com.urlshortener.dto.UrlSummaryResponse;
import com.urlshortener.exception.AliasAlreadyExistsException;
import com.urlshortener.exception.InvalidUrlException;
import com.urlshortener.exception.UrlExpiredException;
import com.urlshortener.exception.UrlNotFoundException;
import com.urlshortener.model.UrlMapping;
import com.urlshortener.repository.UrlMappingRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UrlService {

    private final UrlMappingRepository urlMappingRepository;
    private final ShortCodeGenerator shortCodeGenerator;
    private final String baseUrl;

    public UrlService(
        UrlMappingRepository urlMappingRepository,
        ShortCodeGenerator shortCodeGenerator,
        @Value("${app.base-url:http://localhost:8080}") String baseUrl
    ) {
        this.urlMappingRepository = urlMappingRepository;
        this.shortCodeGenerator = shortCodeGenerator;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    /**
     * Creates a shortened URL from the request.
     */
    public ShortenResponse shortenUrl(ShortenRequest request) {
        validateUrl(request.getOriginalUrl());

        String shortCode;
        String customAlias = request.getCustomAlias();

        if (customAlias != null && !customAlias.trim().isEmpty()) {
            customAlias = customAlias.trim();
            if (shortCodeGenerator.isReserved(customAlias)) {
                throw new InvalidUrlException("Custom alias '" + customAlias + "' is a reserved keyword");
            }
            if (urlMappingRepository.existsByShortCode(customAlias) || urlMappingRepository.existsByCustomAlias(customAlias)) {
                throw new AliasAlreadyExistsException("Custom alias '" + customAlias + "' is already in use");
            }
            shortCode = customAlias;
        } else {
            shortCode = shortCodeGenerator.generateUniqueShortCode();
        }

        LocalDateTime expiresAt = null;
        if (request.getExpirationDays() != null && request.getExpirationDays() > 0) {
            expiresAt = LocalDateTime.now().plusDays(request.getExpirationDays());
        }

        UrlMapping mapping = new UrlMapping(
            request.getOriginalUrl().trim(),
            shortCode,
            customAlias,
            expiresAt
        );

        UrlMapping saved = urlMappingRepository.save(mapping);

        return new ShortenResponse(
            saved.getShortCode(),
            buildShortUrl(saved.getShortCode()),
            saved.getOriginalUrl(),
            saved.getCustomAlias(),
            saved.getCreatedAt(),
            saved.getExpiresAt(),
            saved.getTotalClicks()
        );
    }

    /**
     * Resolves a short code to its original destination URL for redirection.
     */
    public String resolveOriginalUrl(String shortCode) {
        UrlMapping mapping = urlMappingRepository.findByShortCode(shortCode)
            .orElseThrow(() -> new UrlNotFoundException("Short URL '" + shortCode + "' does not exist"));

        if (!mapping.isActive()) {
            throw new UrlNotFoundException("Short URL '" + shortCode + "' is inactive");
        }

        if (mapping.isExpired()) {
            throw new UrlExpiredException("Short URL '" + shortCode + "' has expired on " + mapping.getExpiresAt());
        }

        return mapping.getOriginalUrl();
    }

    /**
     * Retrieves all URLs ordered by creation timestamp descending.
     */
    public List<UrlSummaryResponse> getAllUrls() {
        return urlMappingRepository.findAllByOrderByCreatedAtDesc()
            .stream()
            .map(this::toSummaryResponse)
            .collect(Collectors.toList());
    }

    /**
     * Deactivates a URL mapping.
     */
    public void deactivateUrl(String shortCode) {
        UrlMapping mapping = urlMappingRepository.findByShortCode(shortCode)
            .orElseThrow(() -> new UrlNotFoundException("Short URL '" + shortCode + "' does not exist"));
        mapping.setActive(false);
        urlMappingRepository.save(mapping);
    }

    /**
     * Permanently deletes a URL mapping.
     */
    public void deleteUrl(String shortCode) {
        UrlMapping mapping = urlMappingRepository.findByShortCode(shortCode)
            .orElseThrow(() -> new UrlNotFoundException("Short URL '" + shortCode + "' does not exist"));
        urlMappingRepository.delete(mapping);
    }

    public UrlMapping getUrlMapping(String shortCode) {
        return urlMappingRepository.findByShortCode(shortCode)
            .orElseThrow(() -> new UrlNotFoundException("Short URL '" + shortCode + "' does not exist"));
    }

    public String buildShortUrl(String shortCode) {
        return baseUrl + "/" + shortCode;
    }

    private UrlSummaryResponse toSummaryResponse(UrlMapping mapping) {
        return new UrlSummaryResponse(
            mapping.getId(),
            mapping.getShortCode(),
            buildShortUrl(mapping.getShortCode()),
            mapping.getOriginalUrl(),
            mapping.getCustomAlias(),
            mapping.getCreatedAt(),
            mapping.getExpiresAt(),
            mapping.getTotalClicks(),
            mapping.isActive(),
            mapping.isExpired()
        );
    }

    private void validateUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new InvalidUrlException("URL cannot be empty");
        }

        try {
            URI uri = new URI(url.trim());
            String scheme = uri.getScheme();
            if (scheme == null || (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https"))) {
                throw new InvalidUrlException("URL must start with http:// or https://");
            }
            if (uri.getHost() == null || uri.getHost().trim().isEmpty()) {
                throw new InvalidUrlException("URL must have a valid host name");
            }
        } catch (URISyntaxException e) {
            throw new InvalidUrlException("Invalid URL syntax: " + e.getMessage());
        }
    }
}



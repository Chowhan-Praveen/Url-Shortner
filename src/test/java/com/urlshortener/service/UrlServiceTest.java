package com.urlshortener.service;

import com.urlshortener.dto.ShortenRequest;
import com.urlshortener.dto.ShortenResponse;
import com.urlshortener.exception.AliasAlreadyExistsException;
import com.urlshortener.exception.InvalidUrlException;
import com.urlshortener.exception.UrlExpiredException;
import com.urlshortener.exception.UrlNotFoundException;
import com.urlshortener.model.UrlMapping;
import com.urlshortener.repository.UrlMappingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlMappingRepository urlMappingRepository;

    @Mock
    private ShortCodeGenerator shortCodeGenerator;

    private UrlService urlService;

    @BeforeEach
    void setUp() {
        urlService = new UrlService(urlMappingRepository, shortCodeGenerator, "http://localhost:8080");
    }

    @Test
    @DisplayName("Should successfully shorten a valid URL with generated code")
    void shouldShortenValidUrlWithGeneratedCode() {
        ShortenRequest request = new ShortenRequest("https://spring.io/projects/spring-boot", null, 30);

        when(shortCodeGenerator.generateUniqueShortCode()).thenReturn("abc123");
        when(urlMappingRepository.save(any(UrlMapping.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ShortenResponse response = urlService.shortenUrl(request);

        assertNotNull(response);
        assertEquals("abc123", response.getShortCode());
        assertEquals("http://localhost:8080/abc123", response.getShortUrl());
        assertEquals("https://spring.io/projects/spring-boot", response.getOriginalUrl());
        assertNotNull(response.getExpiresAt());
        verify(urlMappingRepository, times(1)).save(any(UrlMapping.class));
    }

    @Test
    @DisplayName("Should successfully shorten a valid URL with custom alias")
    void shouldShortenValidUrlWithCustomAlias() {
        ShortenRequest request = new ShortenRequest("https://google.com", "my-search", null);

        when(shortCodeGenerator.isReserved("my-search")).thenReturn(false);
        when(urlMappingRepository.existsByShortCode("my-search")).thenReturn(false);
        when(urlMappingRepository.existsByCustomAlias("my-search")).thenReturn(false);
        when(urlMappingRepository.save(any(UrlMapping.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ShortenResponse response = urlService.shortenUrl(request);

        assertNotNull(response);
        assertEquals("my-search", response.getShortCode());
        assertEquals("http://localhost:8080/my-search", response.getShortUrl());
        assertEquals("my-search", response.getCustomAlias());
    }

    @Test
    @DisplayName("Should throw AliasAlreadyExistsException when custom alias is taken")
    void shouldThrowExceptionWhenAliasAlreadyExists() {
        ShortenRequest request = new ShortenRequest("https://google.com", "existing-alias", null);

        when(shortCodeGenerator.isReserved("existing-alias")).thenReturn(false);
        when(urlMappingRepository.existsByShortCode("existing-alias")).thenReturn(true);

        assertThrows(AliasAlreadyExistsException.class, () -> urlService.shortenUrl(request));
        verify(urlMappingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw InvalidUrlException for malformed URL")
    void shouldThrowExceptionForInvalidUrl() {
        ShortenRequest request = new ShortenRequest("not-a-valid-url", null, null);

        assertThrows(InvalidUrlException.class, () -> urlService.shortenUrl(request));
        verify(urlMappingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should resolve active URL successfully")
    void shouldResolveActiveUrl() {
        UrlMapping mapping = new UrlMapping("https://github.com", "git001", null, null);
        when(urlMappingRepository.findByShortCode("git001")).thenReturn(Optional.of(mapping));

        String resolved = urlService.resolveOriginalUrl("git001");

        assertEquals("https://github.com", resolved);
    }

    @Test
    @DisplayName("Should throw UrlNotFoundException when code does not exist")
    void shouldThrowExceptionWhenCodeNotFound() {
        when(urlMappingRepository.findByShortCode("unknown")).thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> urlService.resolveOriginalUrl("unknown"));
    }

    @Test
    @DisplayName("Should throw UrlExpiredException when URL is past its expiration date")
    void shouldThrowExceptionWhenUrlExpired() {
        UrlMapping mapping = new UrlMapping("https://expired.com", "exp123", null, LocalDateTime.now().minusDays(1));
        when(urlMappingRepository.findByShortCode("exp123")).thenReturn(Optional.of(mapping));

        assertThrows(UrlExpiredException.class, () -> urlService.resolveOriginalUrl("exp123"));
    }
}

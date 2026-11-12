package com.urlshortener.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urlshortener.dto.ShortenRequest;
import com.urlshortener.dto.ShortenResponse;
import com.urlshortener.exception.GlobalExceptionHandler;
import com.urlshortener.service.AnalyticsService;
import com.urlshortener.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UrlRestController.class)
@Import(GlobalExceptionHandler.class)
class UrlRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UrlService urlService;

    @MockBean
    private AnalyticsService analyticsService;

    private ShortenResponse sampleResponse;

    @BeforeEach
    void setUp() {
        sampleResponse = new ShortenResponse(
            "xyz789",
            "http://localhost:8080/xyz789",
            "https://spring.io",
            null,
            LocalDateTime.now(),
            null,
            0
        );
    }

    @Test
    @DisplayName("POST /api/v1/urls/shorten returns 201 Created on valid input")
    void shouldCreateShortUrl() throws Exception {
        ShortenRequest request = new ShortenRequest("https://spring.io", null, null);

        when(urlService.shortenUrl(any(ShortenRequest.class))).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/v1/urls/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.shortCode").value("xyz789"))
            .andExpect(jsonPath("$.shortUrl").value("http://localhost:8080/xyz789"))
            .andExpect(jsonPath("$.originalUrl").value("https://spring.io"));
    }

    @Test
    @DisplayName("POST /api/v1/urls/shorten returns 400 Bad Request when originalUrl is blank")
    void shouldRejectBlankUrl() throws Exception {
        ShortenRequest request = new ShortenRequest("", null, null);

        mockMvc.perform(post("/api/v1/urls/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.validationErrors.originalUrl").exists());
    }
}

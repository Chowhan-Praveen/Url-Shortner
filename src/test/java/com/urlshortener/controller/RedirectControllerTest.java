package com.urlshortener.controller;

import com.urlshortener.exception.GlobalExceptionHandler;
import com.urlshortener.service.AnalyticsService;
import com.urlshortener.service.UrlService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RedirectController.class)
@Import(GlobalExceptionHandler.class)
class RedirectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlService urlService;

    @MockBean
    private AnalyticsService analyticsService;

    @Test
    @DisplayName("GET /{shortCode} returns 302 Found and Location header")
    void shouldRedirectToOriginalUrl() throws Exception {
        when(urlService.resolveOriginalUrl("docs123")).thenReturn("https://docs.spring.io");

        mockMvc.perform(get("/docs123")
                .header("User-Agent", "Mozilla/5.0")
                .header("Referer", "https://news.ycombinator.com"))
            .andExpect(status().isFound())
            .andExpect(header().string("Location", "https://docs.spring.io"));

        verify(analyticsService, times(1)).recordClick(
            eq("docs123"),
            anyString(),
            eq("Mozilla/5.0"),
            eq("https://news.ycombinator.com")
        );
    }
}

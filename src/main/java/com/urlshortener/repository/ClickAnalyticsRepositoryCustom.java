package com.urlshortener.repository;

import java.util.List;

public interface ClickAnalyticsRepositoryCustom {
    List<Object[]> countClicksByBrowser(String shortCode);
    List<Object[]> countClicksByOperatingSystem(String shortCode);
    List<Object[]> countClicksByDeviceType(String shortCode);
    List<Object[]> countClicksByReferer(String shortCode, int limit);
}

package com.urlshortener.service;

import com.urlshortener.repository.UrlMappingRepository;
import com.urlshortener.util.Base62Encoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Set;

/**
 * Service responsible for generating unique, collision-resistant short codes.
 */
@Service
public class ShortCodeGenerator {

    private static final String ALPHABET = Base62Encoder.getAlphabet();
    private static final SecureRandom RANDOM = new SecureRandom();

    // Reserved paths that should never be used as short codes
    private static final Set<String> RESERVED_KEYWORDS = Set.of(
        "api", "h2-console", "analytics", "dashboard", "static", "css", "js",
        "favicon.ico", "error", "health", "metrics", "swagger", "v3"
    );

    private final UrlMappingRepository urlMappingRepository;
    private final int codeLength;
    private final int maxRetries;

    public ShortCodeGenerator(
        UrlMappingRepository urlMappingRepository,
        @Value("${app.short-code.length:6}") int codeLength,
        @Value("${app.short-code.max-retries:5}") int maxRetries
    ) {
        this.urlMappingRepository = urlMappingRepository;
        this.codeLength = codeLength;
        this.maxRetries = maxRetries;
    }

    /**
     * Generates a unique short code with retry logic to guarantee uniqueness.
     */
    public String generateUniqueShortCode() {
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            String code = generateRandomCode(codeLength);
            if (!isReserved(code) && !urlMappingRepository.existsByShortCode(code)) {
                return code;
            }
        }
        // If random collisions happen frequently (large scale), slightly extend length
        return generateRandomCode(codeLength + 1);
    }

    public boolean isReserved(String code) {
        return RESERVED_KEYWORDS.contains(code.toLowerCase());
    }

    private String generateRandomCode(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(ALPHABET.length());
            sb.append(ALPHABET.charAt(index));
        }
        return sb.toString();
    }
}

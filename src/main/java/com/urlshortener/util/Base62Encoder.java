package com.urlshortener.util;

/**
 * Utility for Base62 encoding and decoding.
 * Base62 uses 62 alphanumeric characters (0-9, a-z, A-Z), making it URL-safe
 * and compact for short code generation.
 */
public final class Base62Encoder {

    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = ALPHABET.length(); // 62

    private Base62Encoder() {
        // Utility class
    }

    /**
     * Encodes a positive integer/long into a Base62 string.
     * For example, ID 100000 becomes "q0U".
     */
    public static String encode(long number) {
        if (number <= 0) {
            return String.valueOf(ALPHABET.charAt(0));
        }

        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % BASE);
            sb.append(ALPHABET.charAt(remainder));
            number /= BASE;
        }

        return sb.reverse().toString();
    }

    /**
     * Decodes a Base62 string back to its long numeric representation.
     */
    public static long decode(String str) {
        long result = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            int value = ALPHABET.indexOf(c);
            if (value == -1) {
                throw new IllegalArgumentException("Invalid Base62 character: " + c);
            }
            result = result * BASE + value;
        }
        return result;
    }

    public static String getAlphabet() {
        return ALPHABET;
    }
}

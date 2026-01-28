package com.rihan.linkshortenerservice.core;

public final class Base62 {
    private static final char[] ALPHABET =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final int BASE = 62;

    private Base62() {}

    public static String encode(long num) {
        if (num < 0) throw new IllegalArgumentException("id must be >= 0");
        if (num == 0) return "0";

        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            int rem = (int) (num % BASE);
            sb.append(ALPHABET[rem]);
            num /= BASE;
        }
        return sb.reverse().toString();
    }
}

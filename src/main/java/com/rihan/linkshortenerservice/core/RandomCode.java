package com.rihan.linkshortenerservice.core;

import java.security.SecureRandom;

public final class RandomCode {
    private static final String CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RND = new SecureRandom();

    private RandomCode() {}

    public static String generate(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(CHARS.charAt(RND.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
}

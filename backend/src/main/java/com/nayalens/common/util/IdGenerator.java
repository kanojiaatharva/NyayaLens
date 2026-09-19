package com.nayalens.common.util;

import java.security.SecureRandom;

public final class IdGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final char[] ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();

    private IdGenerator() {}

    public static String generate(String prefix, int length) {
        StringBuilder sb = new StringBuilder(prefix != null ? prefix + "_" : "");
        for (int i = 0; i < length; i++) {
            sb.append(ALPHABET[RANDOM.nextInt(ALPHABET.length)]);
        }
        return sb.toString();
    }

    public static String documentId() {
        return generate("doc", 12);
    }

    public static String claimId() {
        return generate("clm", 8);
    }

    public static String evidenceId() {
        return generate("ev", 8);
    }

    public static String requestId() {
        return generate("req", 10);
    }
}

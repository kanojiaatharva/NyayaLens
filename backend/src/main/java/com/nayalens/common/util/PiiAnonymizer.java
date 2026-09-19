package com.nayalens.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PiiAnonymizer {

    // Regex for 12-digit Indian Aadhaar number (optionally separated by space or hyphen)
    private static final Pattern AADHAAR_PATTERN = Pattern.compile("\\b[2-9]\\d{3}[-\\s]?\\d{4}[-\\s]?\\d{4}\\b");

    // Regex for Indian Permanent Account Number (PAN): 5 letters, 4 digits, 1 letter
    private static final Pattern PAN_PATTERN = Pattern.compile("\\b[A-Z]{5}[0-9]{4}[A-Z]\\b");

    // Regex for 10-digit Indian mobile numbers (with optional +91 or 0 prefix)
    private static final Pattern PHONE_PATTERN = Pattern.compile("(?:\\+91[-\\s]?|0)?[6-9]\\d{9}\\b");

    // Standard RFC-compliant email pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b");

    // Bank account numbers (9-18 digits preceded by common indicators)
    private static final Pattern BANK_ACC_PATTERN = Pattern.compile("(?i)(?:a/c|account(?:\\s+no)?[:.]?\\s*)([0-9]{9,18})");

    public record AnonymizationResult(
        String sanitizedText,
        Map<String, Integer> detectedCounts,
        boolean piiFound
    ) {}

    public static AnonymizationResult anonymize(String text) {
        if (text == null || text.isBlank()) {
            return new AnonymizationResult(text, Map.of(), false);
        }

        Map<String, Integer> counts = new HashMap<>();
        String processed = text;

        // 1. Aadhaar Anonymization
        Matcher aadhaarMatcher = AADHAAR_PATTERN.matcher(processed);
        int aadhaarCount = 0;
        StringBuilder sb = new StringBuilder();
        while (aadhaarMatcher.find()) {
            aadhaarCount++;
            aadhaarMatcher.appendReplacement(sb, "[AADHAAR_" + String.format("%03d", aadhaarCount) + "]");
        }
        aadhaarMatcher.appendTail(sb);
        processed = sb.toString();
        if (aadhaarCount > 0) counts.put("AADHAAR", aadhaarCount);

        // 2. PAN Anonymization
        Matcher panMatcher = PAN_PATTERN.matcher(processed);
        int panCount = 0;
        sb = new StringBuilder();
        while (panMatcher.find()) {
            panCount++;
            panMatcher.appendReplacement(sb, "[PAN_" + String.format("%03d", panCount) + "]");
        }
        panMatcher.appendTail(sb);
        processed = sb.toString();
        if (panCount > 0) counts.put("PAN", panCount);

        // 3. Email Anonymization
        Matcher emailMatcher = EMAIL_PATTERN.matcher(processed);
        int emailCount = 0;
        sb = new StringBuilder();
        while (emailMatcher.find()) {
            emailCount++;
            emailMatcher.appendReplacement(sb, "[EMAIL_" + String.format("%03d", emailCount) + "]");
        }
        emailMatcher.appendTail(sb);
        processed = sb.toString();
        if (emailCount > 0) counts.put("EMAIL", emailCount);

        // 4. Phone Number Anonymization
        Matcher phoneMatcher = PHONE_PATTERN.matcher(processed);
        int phoneCount = 0;
        sb = new StringBuilder();
        while (phoneMatcher.find()) {
            phoneCount++;
            phoneMatcher.appendReplacement(sb, "[PHONE_" + String.format("%03d", phoneCount) + "]");
        }
        phoneMatcher.appendTail(sb);
        processed = sb.toString();
        if (phoneCount > 0) counts.put("PHONE", phoneCount);

        // 5. Bank Account Anonymization
        Matcher bankMatcher = BANK_ACC_PATTERN.matcher(processed);
        int bankCount = 0;
        sb = new StringBuilder();
        while (bankMatcher.find()) {
            bankCount++;
            bankMatcher.appendReplacement(sb, "Account [ACCOUNT_" + String.format("%03d", bankCount) + "]");
        }
        bankMatcher.appendTail(sb);
        processed = sb.toString();
        if (bankCount > 0) counts.put("BANK_ACCOUNT", bankCount);

        boolean found = !counts.isEmpty();
        return new AnonymizationResult(processed, counts, found);
    }
}

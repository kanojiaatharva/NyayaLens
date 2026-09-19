package com.nayalens;

import com.nayalens.common.util.PiiAnonymizer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PiiAnonymizerTest {

    @Test
    @DisplayName("Should detect and redact Indian Aadhaar numbers")
    void testAadhaarRedaction() {
        String input = "The citizen's Aadhaar is 4521 8892 1044 and secondary is 9876-5432-1098.";
        var result = PiiAnonymizer.anonymize(input);

        assertTrue(result.piiFound());
        assertEquals(2, result.detectedCounts().get("AADHAAR"));
        assertFalse(result.sanitizedText().contains("4521 8892 1044"));
        assertFalse(result.sanitizedText().contains("9876-5432-1098"));
        assertTrue(result.sanitizedText().contains("[AADHAAR_001]"));
        assertTrue(result.sanitizedText().contains("[AADHAAR_002]"));
    }

    @Test
    @DisplayName("Should detect and redact Indian PAN identifiers")
    void testPanRedaction() {
        String input = "Employee PAN card number is ABCPV1234F for taxation.";
        var result = PiiAnonymizer.anonymize(input);

        assertTrue(result.piiFound());
        assertEquals(1, result.detectedCounts().get("PAN"));
        assertFalse(result.sanitizedText().contains("ABCPV1234F"));
        assertTrue(result.sanitizedText().contains("[PAN_001]"));
    }

    @Test
    @DisplayName("Should detect and redact email and phone numbers")
    void testEmailAndPhoneRedaction() {
        String input = "Contact rohit.verma@example.com or reach out via +91 9876543210 immediately.";
        var result = PiiAnonymizer.anonymize(input);

        assertTrue(result.piiFound());
        assertEquals(1, result.detectedCounts().get("EMAIL"));
        assertEquals(1, result.detectedCounts().get("PHONE"));
        assertFalse(result.sanitizedText().contains("rohit.verma@example.com"));
        assertFalse(result.sanitizedText().contains("9876543210"));
    }

    @Test
    @DisplayName("Should leave clean text untouched")
    void testCleanText() {
        String clean = "Standard agreement terms between Company A and Company B.";
        var result = PiiAnonymizer.anonymize(clean);

        assertFalse(result.piiFound());
        assertEquals(clean, result.sanitizedText());
    }
}

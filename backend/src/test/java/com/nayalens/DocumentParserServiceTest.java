package com.nayalens;

import com.nayalens.common.exception.InvalidDocumentException;
import com.nayalens.document.service.DocumentParserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class DocumentParserServiceTest {

    private final DocumentParserService parser = new DocumentParserService(50);

    @Test
    @DisplayName("Should reject empty document payload")
    void testEmptyDocumentRejection() {
        assertThrows(InvalidDocumentException.class, () ->
                parser.parseDocument("empty.txt", new byte[0], false));
    }

    @Test
    @DisplayName("Should reject executable files with MZ header")
    void testExecutableBinaryRejection() {
        byte[] mzExecutable = new byte[]{'M', 'Z', 0x00, 0x01, 0x02};
        assertThrows(InvalidDocumentException.class, () ->
                parser.parseDocument("malicious.exe", mzExecutable, false));
    }

    @Test
    @DisplayName("Should sanitize path traversal attempts in filenames")
    void testPathTraversalSanitization() {
        String content = "Clause 1. Standard Agreement Text.";
        var doc = parser.parseDocument("../../etc/passwd/contract.txt", content.getBytes(StandardCharsets.UTF_8), false);

        assertNotNull(doc);
        assertFalse(doc.filename().contains("../"));
        assertFalse(doc.filename().contains("/"));
        assertTrue(doc.filename().contains("contract.txt"));
    }
}

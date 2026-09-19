package com.nayalens.document.service;

import com.nayalens.common.exception.InvalidDocumentException;
import com.nayalens.common.util.IdGenerator;
import com.nayalens.common.util.PiiAnonymizer;
import com.nayalens.document.model.DocumentPage;
import com.nayalens.document.model.DocumentSection;
import com.nayalens.document.model.LegalDocument;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DocumentParserService {

    private static final Logger log = LoggerFactory.getLogger(DocumentParserService.class);

    private final int maxPages;

    // Regex to detect clause and section headers (e.g., "Clause 8.1", "Section 3", "Article IV", "8. Notice Period")
    private static final Pattern CLAUSE_PATTERN = Pattern.compile(
            "(?im)^(?:clause|section|article)\\s+([0-9A-Z]+(?:\\.[0-9A-Z]+)*)\\s*[:.-]?\\s*(.*)$|" +
            "^([0-9]+\\.[0-9]+(?:\\.[0-9]+)?)\\s+([A-Z][^\\n]{2,60})$"
    );

    public DocumentParserService(@Value("${nyayalens.document.max-pages:50}") int maxPages) {
        this.maxPages = maxPages;
    }

    public LegalDocument parseDocument(String originalFilename, byte[] bytes, boolean redactPii) {
        if (bytes == null || bytes.length == 0) {
            throw new InvalidDocumentException("Document cannot be empty.");
        }

        // Validate safe filename
        String safeName = sanitizeFilename(originalFilename);

        // Magic byte verification
        validateFileSafety(bytes, safeName);

        List<DocumentPage> pages = new ArrayList<>();
        String rawFullText;

        if (isPdf(bytes, safeName)) {
            pages = extractPdfPages(bytes);
        } else {
            // Text or Markdown
            String text = new String(bytes, StandardCharsets.UTF_8);
            pages = splitTextIntoPages(text);
        }

        if (pages.isEmpty() || pages.stream().allMatch(p -> p.text().isBlank())) {
            throw new InvalidDocumentException("Document contains no readable text content.");
        }

        StringBuilder fullTextBuilder = new StringBuilder();
        for (DocumentPage p : pages) {
            fullTextBuilder.append("--- PAGE ").append(p.pageNumber()).append(" ---\n");
            fullTextBuilder.append(p.text()).append("\n\n");
        }
        rawFullText = fullTextBuilder.toString();

        // PII redaction if requested
        String sanitizedText = rawFullText;
        var piiSummary = new java.util.HashMap<String, Integer>();
        boolean piiFound = false;

        if (redactPii) {
            var result = PiiAnonymizer.anonymize(rawFullText);
            sanitizedText = result.sanitizedText();
            piiSummary.putAll(result.detectedCounts());
            piiFound = result.piiFound();
        }

        // Extract sections & clauses
        List<DocumentSection> sections = extractSections(pages);

        String docId = IdGenerator.documentId();
        String fileType = isPdf(bytes, safeName) ? "PDF" : "TXT";

        log.info("Successfully parsed document [id={}, name={}, pages={}, sections={}, piiRedacted={}]",
                docId, safeName, pages.size(), sections.size(), redactPii);

        return new LegalDocument(
                docId,
                safeName,
                fileType,
                bytes.length,
                Instant.now(),
                pages.size(),
                rawFullText,
                sanitizedText,
                pages,
                sections,
                piiSummary,
                piiFound
        );
    }

    private String sanitizeFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            return "document_" + System.currentTimeMillis() + ".txt";
        }
        // Remove directory traversal characters
        String clean = filename.replace("\\", "/");
        int lastSlash = clean.lastIndexOf('/');
        if (lastSlash >= 0) {
            clean = clean.substring(lastSlash + 1);
        }
        return clean.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private void validateFileSafety(byte[] bytes, String filename) {
        // Reject Windows executable (MZ header)
        if (bytes.length >= 2 && bytes[0] == 'M' && bytes[1] == 'Z') {
            throw new InvalidDocumentException("Executable files (MZ) are strictly forbidden.");
        }
        // Reject ELF binaries
        if (bytes.length >= 4 && bytes[0] == 0x7F && bytes[1] == 'E' && bytes[2] == 'L' && bytes[3] == 'F') {
            throw new InvalidDocumentException("Binary executable files (ELF) are strictly forbidden.");
        }
        // Reject script files disguised as text
        String sample = new String(bytes, 0, Math.min(bytes.length, 256), StandardCharsets.ISO_8859_1).trim();
        if (sample.startsWith("#!/bin/") || sample.startsWith("#!/usr/bin/")) {
            throw new InvalidDocumentException("Script files are not supported.");
        }
    }

    private boolean isPdf(byte[] bytes, String filename) {
        if (bytes.length >= 4 && bytes[0] == '%' && bytes[1] == 'P' && bytes[2] == 'D' && bytes[3] == 'F') {
            return true;
        }
        return filename.toLowerCase().endsWith(".pdf");
    }

    private List<DocumentPage> extractPdfPages(byte[] bytes) {
        List<DocumentPage> pages = new ArrayList<>();
        try (PDDocument document = Loader.loadPDF(bytes)) {
            int totalPages = document.getNumberOfPages();
            if (totalPages > maxPages) {
                throw new InvalidDocumentException(
                        "Document exceeds maximum page limit of " + maxPages + " pages (has " + totalPages + ")."
                );
            }

            PDFTextStripper stripper = new PDFTextStripper();
            for (int i = 1; i <= totalPages; i++) {
                stripper.setStartPage(i);
                stripper.setEndPage(i);
                String pageText = stripper.getText(document).trim();
                pages.add(new DocumentPage(i, pageText));
            }
        } catch (IOException e) {
            log.error("Failed to parse PDF: {}", e.getMessage());
            throw new InvalidDocumentException("The uploaded PDF could not be processed. It may be corrupt or encrypted.");
        }
        return pages;
    }

    private List<DocumentPage> splitTextIntoPages(String text) {
        List<DocumentPage> pages = new ArrayList<>();
        String[] lines = text.split("\r?\n");
        StringBuilder currentPage = new StringBuilder();
        int pageNum = 1;
        int lineCount = 0;

        for (String line : lines) {
            currentPage.append(line).append("\n");
            lineCount++;
            // Approximate ~50 lines or explicit form feeds per page
            if (lineCount >= 50 || line.contains("\f")) {
                pages.add(new DocumentPage(pageNum++, currentPage.toString().trim()));
                currentPage.setLength(0);
                lineCount = 0;
            }
        }
        if (!currentPage.isEmpty()) {
            pages.add(new DocumentPage(pageNum, currentPage.toString().trim()));
        }
        return pages;
    }

    private List<DocumentSection> extractSections(List<DocumentPage> pages) {
        List<DocumentSection> sections = new ArrayList<>();
        int sectionIndex = 1;

        for (DocumentPage page : pages) {
            String[] lines = page.text().split("\r?\n");
            String currentClause = null;
            String currentTitle = null;
            StringBuilder sectionContent = new StringBuilder();

            for (String line : lines) {
                Matcher matcher = CLAUSE_PATTERN.matcher(line.trim());
                if (matcher.find()) {
                    // Flush previous section
                    if (currentClause != null || !sectionContent.isEmpty()) {
                        sections.add(new DocumentSection(
                                "sec_" + (sectionIndex++),
                                currentClause != null ? currentClause : "Sec " + sectionIndex,
                                currentTitle != null ? currentTitle : "Clause",
                                page.pageNumber(),
                                sectionContent.toString().trim()
                        ));
                        sectionContent.setLength(0);
                    }

                    if (matcher.group(1) != null) {
                        currentClause = matcher.group(1);
                        currentTitle = matcher.group(2) != null && !matcher.group(2).isBlank() ? matcher.group(2).trim() : "Clause " + currentClause;
                    } else if (matcher.group(3) != null) {
                        currentClause = matcher.group(3);
                        currentTitle = matcher.group(4) != null ? matcher.group(4).trim() : "Section " + currentClause;
                    }
                }
                sectionContent.append(line).append("\n");
            }

            if (!sectionContent.isEmpty()) {
                sections.add(new DocumentSection(
                        "sec_" + (sectionIndex++),
                        currentClause != null ? currentClause : "P" + page.pageNumber() + "-S" + sectionIndex,
                        currentTitle != null ? currentTitle : "Page " + page.pageNumber() + " Provisions",
                        page.pageNumber(),
                        sectionContent.toString().trim()
                ));
            }
        }
        return sections;
    }
}

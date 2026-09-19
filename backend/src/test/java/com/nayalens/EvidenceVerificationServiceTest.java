package com.nayalens;

import com.nayalens.document.model.DocumentPage;
import com.nayalens.document.model.DocumentSection;
import com.nayalens.document.model.LegalDocument;
import com.nayalens.evidence.model.Claim;
import com.nayalens.evidence.model.Severity;
import com.nayalens.evidence.model.VerificationStatus;
import com.nayalens.evidence.service.EvidenceVerificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EvidenceVerificationServiceTest {

    private EvidenceVerificationService verificationService;
    private LegalDocument mockDocument;

    @BeforeEach
    void setUp() {
        verificationService = new EvidenceVerificationService();
        List<DocumentPage> pages = List.of(
                new DocumentPage(1, "Clause 1. Position. The employee is appointed as Lead Engineer."),
                new DocumentPage(2, "Clause 8.2 Notice Period. Either party may terminate this Agreement by providing sixty (60) days prior written notice, or gross salary in lieu.")
        );
        mockDocument = new LegalDocument(
                "doc_test123",
                "sample.txt",
                "TXT",
                1024L,
                Instant.now(),
                2,
                "Full Text",
                "Full Text",
                pages,
                List.of(),
                Map.of(),
                false
        );
    }

    @Test
    @DisplayName("Should assign VERIFIED status when excerpt exactly exists in document text")
    void testExactExcerptVerification() {
        Claim claim = verificationService.verifyClaim(
                mockDocument,
                "Termination requires 60 days notice",
                "TERMINATION_NOTICE",
                Severity.HIGH,
                "8.2",
                2,
                "Either party may terminate this Agreement by providing sixty (60) days prior written notice",
                "Standard notice provision",
                "What is the notice period?",
                false
        );

        assertEquals(VerificationStatus.VERIFIED, claim.status());
        assertTrue(claim.confidence() >= 0.85);
        assertFalse(claim.evidenceList().isEmpty());
        assertEquals(2, claim.evidenceList().get(0).pageNumber());
    }

    @Test
    @DisplayName("Should downgrade hallucinated or fabricated citation to NEEDS_REVIEW")
    void testFabricatedExcerptDowngrade() {
        Claim claim = verificationService.verifyClaim(
                mockDocument,
                "Fabricated penalty of 1 crore rupees",
                "PENALTY",
                Severity.CRITICAL,
                "99.9",
                1,
                "Employee shall forfeit one crore rupees if resignation occurs during festival season",
                "Fabricated clause",
                "Is this penalty enforceable?",
                false
        );

        assertNotEquals(VerificationStatus.VERIFIED, claim.status());
        assertEquals(VerificationStatus.NEEDS_REVIEW, claim.status());
        assertTrue(claim.explanation().contains("could not be directly confirmed"));
    }

    @Test
    @DisplayName("Should assign INFERRED when explicitly marked as an inference")
    void testInferenceVerification() {
        Claim claim = verificationService.verifyClaim(
                mockDocument,
                "Implied duty of good faith during employment tenure",
                "DUTY_OF_FAITH",
                Severity.LOW,
                "1",
                1,
                "",
                "Logical inference from appointed role",
                "Is good faith implied?",
                true
        );

        assertEquals(VerificationStatus.INFERRED, claim.status());
        assertTrue(claim.evidenceList().isEmpty());
    }

    @Test
    @DisplayName("Should detect and auto-correct misattributed page citation")
    void testMisattributedPageAutoCorrection() {
        // Cited as Page 1, but text exists on Page 2
        Claim claim = verificationService.verifyClaim(
                mockDocument,
                "Termination requires 60 days notice",
                "TERMINATION_NOTICE",
                Severity.HIGH,
                "8.2",
                1,
                "Either party may terminate this Agreement by providing sixty (60) days prior written notice",
                "Notice provision",
                "Notice period?",
                false
        );

        assertEquals(VerificationStatus.VERIFIED, claim.status());
        assertFalse(claim.evidenceList().isEmpty());
        assertEquals(2, claim.evidenceList().get(0).pageNumber(), "Expected verifier to auto-correct to Page 2");
    }

    @Test
    @DisplayName("Should test text similarity fast-paths and empty boundaries")
    void testCalculateTextSimilarityFastPaths() {
        assertEquals(0.0, verificationService.calculateTextSimilarity(null, "text"));
        assertEquals(0.0, verificationService.calculateTextSimilarity("text", ""));
        assertEquals(1.0, verificationService.calculateTextSimilarity("exact phrase", "This is an exact phrase in full context"));
        assertEquals(1.0, verificationService.calculateTextSimilarity("EXACT PHRASE", "this is an exact phrase in full context"));
    }
}

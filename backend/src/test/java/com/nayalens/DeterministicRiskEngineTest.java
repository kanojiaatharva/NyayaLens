package com.nayalens;

import com.nayalens.analysis.rules.DeterministicRiskEngine;
import com.nayalens.document.service.DocumentParserService;
import com.nayalens.document.service.SyntheticDemoProvider;
import com.nayalens.evidence.model.Claim;
import com.nayalens.evidence.model.Severity;
import com.nayalens.evidence.service.EvidenceVerificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeterministicRiskEngineTest {

    private DeterministicRiskEngine riskEngine;
    private DocumentParserService parserService;
    private SyntheticDemoProvider demoProvider;

    @BeforeEach
    void setUp() {
        EvidenceVerificationService verificationService = new EvidenceVerificationService();
        riskEngine = new DeterministicRiskEngine(verificationService);
        parserService = new DocumentParserService(50);
        demoProvider = new SyntheticDemoProvider();
    }

    @Test
    @DisplayName("Should detect non-compete restraint in Employment Agreement")
    void testNonCompeteDetection() {
        byte[] bytes = demoProvider.getDemoFileBytes("employment-agreement");
        var doc = parserService.parseDocument("employment.txt", bytes, false);

        List<Claim> risks = riskEngine.scanRisks(doc);

        assertFalse(risks.isEmpty());
        boolean hasNonCompete = risks.stream()
                .anyMatch(r -> r.category().contains("NON_COMPETE") || r.explanation().contains("Section 27"));
        assertTrue(hasNonCompete, "Expected non-compete risk with Section 27 reference");
    }

    @Test
    @DisplayName("Should detect short notice period in Termination Notice")
    void testShortNoticeDetection() {
        byte[] bytes = demoProvider.getDemoFileBytes("termination-notice");
        var doc = parserService.parseDocument("notice.txt", bytes, false);

        List<Claim> risks = riskEngine.scanRisks(doc);

        assertFalse(risks.isEmpty());
        boolean hasShortNotice = risks.stream()
                .anyMatch(r -> r.category().contains("NOTICE_PERIOD") || r.severity() == Severity.HIGH);
        assertTrue(hasShortNotice, "Expected short notice period risk");
    }
}

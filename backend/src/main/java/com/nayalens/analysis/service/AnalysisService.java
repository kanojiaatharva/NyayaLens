package com.nayalens.analysis.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nayalens.ai.GeminiClient;
import com.nayalens.ai.PromptRegistry;
import com.nayalens.analysis.model.AnalysisResult;
import com.nayalens.analysis.rules.DeterministicRiskEngine;
import com.nayalens.common.exception.DocumentNotFoundException;
import com.nayalens.document.model.LegalDocument;
import com.nayalens.document.repository.DocumentRepository;
import com.nayalens.evidence.model.Claim;
import com.nayalens.evidence.model.Severity;
import com.nayalens.evidence.service.EvidenceVerificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AnalysisService {

    private static final Logger log = LoggerFactory.getLogger(AnalysisService.class);

    private final DocumentRepository documentRepository;
    private final DeterministicRiskEngine riskEngine;
    private final EvidenceVerificationService verificationService;
    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper;

    // Cache analysis results to avoid redundant LLM calls
    private final Map<String, AnalysisResult> analysisCache = new ConcurrentHashMap<>();

    public AnalysisService(
            DocumentRepository documentRepository,
            DeterministicRiskEngine riskEngine,
            EvidenceVerificationService verificationService,
            GeminiClient geminiClient,
            ObjectMapper objectMapper
    ) {
        this.documentRepository = documentRepository;
        this.riskEngine = riskEngine;
        this.verificationService = verificationService;
        this.geminiClient = geminiClient;
        this.objectMapper = objectMapper;
    }

    public AnalysisResult analyzeDocument(String documentId) {
        if (analysisCache.containsKey(documentId)) {
            log.info("Returning cached analysis result for document [{}]", documentId);
            return analysisCache.get(documentId);
        }

        LegalDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(documentId));

        // 1. Run deterministic risk rule engine
        List<Claim> deterministicRisks = riskEngine.scanRisks(document);

        // 2. Fetch AI structured understanding
        String prompt = PromptRegistry.buildUnderstandPrompt(document.sanitizedText());
        String aiJson = geminiClient.generateStructuredJson(prompt, GeminiClient.ModelTier.FLASH);
        String modelUsed = "gemini-3.8-flash";

        if (aiJson == null) {
            aiJson = geminiClient.getFallbackProvider().getFallbackUnderstand(document);
            modelUsed = "deterministic-rule-engine";
        }

        AnalysisResult result = parseAndVerifyAnalysis(document, aiJson, deterministicRisks, modelUsed);
        analysisCache.put(documentId, result);
        return result;
    }

    public Optional<AnalysisResult> getCachedAnalysis(String documentId) {
        return Optional.ofNullable(analysisCache.get(documentId));
    }

    private AnalysisResult parseAndVerifyAnalysis(
            LegalDocument document,
            String rawJson,
            List<Claim> deterministicRisks,
            String modelUsed
    ) {
        try {
            JsonNode root = objectMapper.readTree(rawJson);

            String documentType = root.path("documentType").asText("Legal Document");
            String plainSummary = root.path("plainSummary").asText("No summary provided.");
            String effectiveDate = root.path("effectiveDate").asText("Not Specified");
            String governingLaw = root.path("governingLaw").asText("Not Specified");
            String jurisdiction = root.path("jurisdiction").asText("Not Specified");

            // Parties
            List<AnalysisResult.PartyInfo> parties = new ArrayList<>();
            JsonNode partiesNode = root.path("parties");
            if (partiesNode.isArray()) {
                for (JsonNode p : partiesNode) {
                    parties.add(new AnalysisResult.PartyInfo(p.path("name").asText(), p.path("role").asText()));
                }
            }

            // Obligations
            List<AnalysisResult.ObligationItem> obligations = new ArrayList<>();
            JsonNode obsNode = root.path("obligations");
            if (obsNode.isArray()) {
                for (JsonNode o : obsNode) {
                    obligations.add(new AnalysisResult.ObligationItem(
                            o.path("party").asText(),
                            o.path("description").asText(),
                            o.path("clause").asText("General"),
                            o.path("page").asInt(1),
                            o.path("excerpt").asText()
                    ));
                }
            }

            // Deadlines
            List<AnalysisResult.DeadlineItem> deadlines = new ArrayList<>();
            JsonNode deadNode = root.path("deadlinesAndDates");
            if (deadNode.isArray()) {
                for (JsonNode d : deadNode) {
                    deadlines.add(new AnalysisResult.DeadlineItem(
                            d.path("description").asText(),
                            d.path("dateOrDuration").asText(),
                            d.path("clause").asText(),
                            d.path("page").asInt(1),
                            d.path("excerpt").asText()
                    ));
                }
            }

            // Financial
            List<AnalysisResult.FinancialTermItem> financial = new ArrayList<>();
            JsonNode finNode = root.path("financialTerms");
            if (finNode.isArray()) {
                for (JsonNode f : finNode) {
                    financial.add(new AnalysisResult.FinancialTermItem(
                            f.path("type").asText(),
                            f.path("amount").asText(),
                            f.path("clause").asText(),
                            f.path("page").asInt(1),
                            f.path("excerpt").asText()
                    ));
                }
            }

            // Termination
            List<AnalysisResult.TerminationItem> terminations = new ArrayList<>();
            JsonNode termNode = root.path("terminationConditions");
            if (termNode.isArray()) {
                for (JsonNode t : termNode) {
                    terminations.add(new AnalysisResult.TerminationItem(
                            t.path("description").asText(),
                            t.path("noticePeriod").asText("Not specified"),
                            t.path("clause").asText(),
                            t.path("page").asInt(1),
                            t.path("excerpt").asText()
                    ));
                }
            }

            // Renewal
            List<AnalysisResult.RenewalItem> renewals = new ArrayList<>();
            JsonNode renNode = root.path("renewalConditions");
            if (renNode.isArray()) {
                for (JsonNode r : renNode) {
                    renewals.add(new AnalysisResult.RenewalItem(
                            r.path("description").asText(),
                            r.path("clause").asText(),
                            r.path("page").asInt(1),
                            r.path("excerpt").asText()
                    ));
                }
            }

            // Dispute Resolution
            JsonNode dispNode = root.path("disputeResolution");
            AnalysisResult.DisputeResolutionItem dispute = new AnalysisResult.DisputeResolutionItem(
                    dispNode.path("method").asText("Negotiation or Litigation"),
                    dispNode.path("venue").asText(jurisdiction),
                    dispNode.path("clause").asText("General"),
                    dispNode.path("page").asInt(1),
                    dispNode.path("excerpt").asText()
            );

            // Unusual Clauses
            List<AnalysisResult.UnusualClauseItem> unusual = new ArrayList<>();
            JsonNode unNode = root.path("unusualClauses");
            if (unNode.isArray()) {
                for (JsonNode u : unNode) {
                    unusual.add(new AnalysisResult.UnusualClauseItem(
                            u.path("title").asText(),
                            u.path("explanation").asText(),
                            u.path("clause").asText(),
                            u.path("page").asInt(1),
                            u.path("excerpt").asText()
                    ));
                }
            }

            // Missing / Ambiguous
            List<String> missing = new ArrayList<>();
            JsonNode misNode = root.path("missingOrAmbiguousInformation");
            if (misNode.isArray()) {
                for (JsonNode m : misNode) {
                    missing.add(m.asText());
                }
            }

            // AI Risk claims verified against document
            List<Claim> allRisks = new ArrayList<>(deterministicRisks);
            Set<String> existingCategories = new HashSet<>();
            for (Claim c : deterministicRisks) {
                existingCategories.add(c.category().toUpperCase());
            }

            // Scan AI risks if available
            String riskPrompt = PromptRegistry.buildRiskPrompt(document.sanitizedText());
            String riskJson = geminiClient.generateStructuredJson(riskPrompt, GeminiClient.ModelTier.FLASH);
            if (riskJson == null) {
                riskJson = geminiClient.getFallbackProvider().getFallbackRisks(document);
            }

            try {
                JsonNode riskRoot = objectMapper.readTree(riskJson);
                JsonNode risksArray = riskRoot.path("risks");
                if (risksArray.isArray()) {
                    for (JsonNode r : risksArray) {
                        String cat = r.path("category").asText("LEGAL_RISK").toUpperCase();
                        if (!existingCategories.contains(cat)) {
                            Severity sev = Severity.valueOf(r.path("severity").asText("MEDIUM").toUpperCase());
                            Claim verifiedClaim = verificationService.verifyClaim(
                                    document,
                                    r.path("claimText").asText(),
                                    cat,
                                    sev,
                                    r.path("clause").asText(),
                                    r.path("page").asInt(1),
                                    r.path("excerpt").asText(),
                                    r.path("explanation").asText(),
                                    r.path("lawyerQuestion").asText("What are the practical implications of this clause?"),
                                    false
                            );
                            allRisks.add(verifiedClaim);
                            existingCategories.add(cat);
                        }
                    }
                }
            } catch (Exception re) {
                log.warn("Failed to parse secondary AI risks JSON: {}", re.getMessage());
            }

            return new AnalysisResult(
                    document.id(),
                    documentType,
                    plainSummary,
                    parties,
                    effectiveDate,
                    governingLaw,
                    jurisdiction,
                    obligations,
                    deadlines,
                    financial,
                    terminations,
                    renewals,
                    dispute,
                    unusual,
                    missing,
                    allRisks,
                    Instant.now(),
                    modelUsed
            );
        } catch (Exception e) {
            log.error("Failed to parse analysis JSON: {}", e.getMessage(), e);
            // Construct safe baseline result from deterministic findings
            return new AnalysisResult(
                    document.id(),
                    "Legal Document",
                    "Document analysis parsed with " + document.pages().size() + " pages and " + document.sections().size() + " sections.",
                    List.of(),
                    "Not Specified",
                    "Not Specified",
                    "Not Specified",
                    List.of(),
                    List.of(),
                    List.of(),
                    List.of(),
                    List.of(),
                    new AnalysisResult.DisputeResolutionItem("Standard Legal Process", "Competent Court", "N/A", 1, ""),
                    List.of(),
                    List.of(),
                    deterministicRisks,
                    Instant.now(),
                    "rule-based-fallback"
            );
        }
    }
}

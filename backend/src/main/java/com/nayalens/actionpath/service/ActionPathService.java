package com.nayalens.actionpath.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nayalens.actionpath.model.ActionPathResult;
import com.nayalens.ai.GeminiClient;
import com.nayalens.ai.PromptRegistry;
import com.nayalens.analysis.model.AnalysisResult;
import com.nayalens.analysis.service.AnalysisService;
import com.nayalens.common.exception.DocumentNotFoundException;
import com.nayalens.common.util.IdGenerator;
import com.nayalens.document.model.LegalDocument;
import com.nayalens.document.repository.DocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class ActionPathService {

    private static final Logger log = LoggerFactory.getLogger(ActionPathService.class);

    private final DocumentRepository documentRepository;
    private final AnalysisService analysisService;
    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper;

    public ActionPathService(
            DocumentRepository documentRepository,
            AnalysisService analysisService,
            GeminiClient geminiClient,
            ObjectMapper objectMapper
    ) {
        this.documentRepository = documentRepository;
        this.analysisService = analysisService;
        this.geminiClient = geminiClient;
        this.objectMapper = objectMapper;
    }

    public ActionPathResult generateActionPath(String documentId) {
        LegalDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(documentId));

        String summaryContext = analysisService.getCachedAnalysis(documentId)
                .map(AnalysisResult::plainSummary)
                .orElse("Standard contract analysis");

        log.info("Generating ActionPath for document [{}]", document.filename());

        String prompt = PromptRegistry.buildActionPathPrompt(document.sanitizedText(), summaryContext);
        String aiJson = geminiClient.generateStructuredJson(prompt, GeminiClient.ModelTier.FLASH);
        String modelUsed = "gemini-3.8-flash";

        if (aiJson == null) {
            aiJson = geminiClient.getFallbackProvider().getFallbackActionPath(document, summaryContext);
            modelUsed = "deterministic-actionpath-fallback";
        }

        return parseActionPath(document.id(), aiJson, modelUsed);
    }

    private ActionPathResult parseActionPath(String documentId, String rawJson, String modelUsed) {
        try {
            JsonNode root = objectMapper.readTree(rawJson);
            String overview = root.path("situationOverview").asText("Follow these structured steps to evaluate your contractual rights and prepare for professional legal counsel.");

            List<ActionPathResult.ActionStep> steps = new ArrayList<>();
            JsonNode checklistNode = root.path("checklist");
            if (checklistNode.isArray()) {
                int defaultIdx = 1;
                for (JsonNode s : checklistNode) {
                    steps.add(new ActionPathResult.ActionStep(
                            s.path("stepNumber").asInt(defaultIdx++),
                            s.path("title").asText("Action Step"),
                            s.path("action").asText(),
                            s.path("category").asText("LEGAL_PREPARATION"),
                            s.path("priority").asText("MEDIUM")
                    ));
                }
            }

            List<ActionPathResult.LawyerQuestion> questions = new ArrayList<>();
            JsonNode qNode = root.path("questionsToAskLawyer");
            if (qNode.isArray()) {
                for (JsonNode q : qNode) {
                    questions.add(new ActionPathResult.LawyerQuestion(
                            q.path("question").asText(),
                            q.path("context").asText(),
                            q.path("relevantClause").asText("General")
                    ));
                }
            }

            List<ActionPathResult.OfficialResourceItem> resources = new ArrayList<>();
            JsonNode resNode = root.path("recommendedOfficialResources");
            if (resNode.isArray()) {
                for (JsonNode r : resNode) {
                    resources.add(new ActionPathResult.OfficialResourceItem(
                            r.path("name").asText(),
                            r.path("description").asText(),
                            r.path("officialUrl").asText()
                    ));
                }
            }

            return new ActionPathResult(
                    IdGenerator.generate("act", 8),
                    documentId,
                    overview,
                    steps,
                    questions,
                    resources,
                    Instant.now(),
                    modelUsed
            );
        } catch (Exception e) {
            log.error("Failed to parse ActionPath JSON: {}", e.getMessage(), e);
            return new ActionPathResult(
                    IdGenerator.generate("act", 8),
                    documentId,
                    "Procedural roadmap for contract review.",
                    List.of(
                            new ActionPathResult.ActionStep(1, "Preserve Documentation", "Download signed agreements and relevant correspondence.", "DOCUMENT_GATHERING", "HIGH"),
                            new ActionPathResult.ActionStep(2, "Review Clauses", "Verify notice and termination provisions.", "CONTRACT_VERIFICATION", "HIGH"),
                            new ActionPathResult.ActionStep(3, "Consult Counsel", "Discuss legal options with an advocate.", "LEGAL_PREPARATION", "MEDIUM")
                    ),
                    List.of(
                            new ActionPathResult.LawyerQuestion("Does the termination procedure follow the contract?", "Notice discrepancies", "Clause 8")
                    ),
                    List.of(
                            new ActionPathResult.OfficialResourceItem("National Legal Services Authority", "Statutory free legal aid authority in India.", "https://nalsa.gov.in")
                    ),
                    Instant.now(),
                    "fallback-engine"
            );
        }
    }
}

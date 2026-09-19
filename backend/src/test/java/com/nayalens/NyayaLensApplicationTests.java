package com.nayalens;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class NyayaLensApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Health endpoint should return UP status without leaking sensitive details")
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is("UP")))
                .andExpect(jsonPath("$.data.service", containsString("NyayaLens")))
                .andExpect(header().exists("X-Request-Id"))
                .andExpect(header().string("X-Frame-Options", "DENY"))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"));
    }

    @Test
    @DisplayName("Should load employment demo document and analyze without errors")
    void testLoadDemoAndAnalyze() throws Exception {
        // 1. Load demo document
        String responseContent = mockMvc.perform(post("/api/v1/documents/demo/employment-agreement"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.id", notNullValue()))
                .andExpect(jsonPath("$.data.filename", containsString("Employment")))
                .andReturn().getResponse().getContentAsString();

        String docId = com.jayway.jsonpath.JsonPath.read(responseContent, "$.data.id");

        // 2. Analyze loaded document
        mockMvc.perform(post("/api/v1/documents/" + docId + "/analyze"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.documentId", is(docId)))
                .andExpect(jsonPath("$.data.plainSummary", notNullValue()))
                .andExpect(jsonPath("$.data.risks", not(empty())));

        // 3. Ask a question about the document
        String questionJson = """
                {
                  "documentId": "%s",
                  "question": "What is the contractual notice period required for termination?"
                }
                """.formatted(docId);

        mockMvc.perform(post("/api/v1/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(questionJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.directAnswer", notNullValue()))
                .andExpect(jsonPath("$.data.status", isOneOf("VERIFIED", "INFERRED")));

        // 4. Generate ActionPath
        String actionPathJson = """
                {
                  "documentId": "%s"
                }
                """.formatted(docId);

        mockMvc.perform(post("/api/v1/actionpath")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(actionPathJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.checklist", not(empty())))
                .andExpect(jsonPath("$.data.questionsToAskLawyer", not(empty())));
    }

    @Test
    @DisplayName("Should list official Indian legal resources")
    void testLegalResources() throws Exception {
        mockMvc.perform(get("/api/v1/legal-resources"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(4))));
    }
}

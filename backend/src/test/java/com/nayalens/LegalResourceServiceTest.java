package com.nayalens;

import com.nayalens.legalresources.model.LegalResource;
import com.nayalens.legalresources.service.LegalResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class LegalResourceServiceTest {

    private LegalResourceService legalResourceService;

    @BeforeEach
    void setUp() {
        legalResourceService = new LegalResourceService();
    }

    @Test
    @DisplayName("Should return complete list of sovereign Indian legal resources")
    void testGetAllResources() {
        List<LegalResource> resources = legalResourceService.getAllResources();
        assertNotNull(resources);
        assertTrue(resources.size() >= 5);

        for (LegalResource r : resources) {
            assertNotNull(r.id());
            assertNotNull(r.name());
            assertNotNull(r.category());
            assertNotNull(r.officialUrl());
            assertTrue(r.officialUrl().startsWith("https://"));
        }
    }

    @Test
    @DisplayName("Should retrieve specific resource by ID correctly")
    void testGetResourceById() {
        Optional<LegalResource> nalsa = legalResourceService.getResourceById("res_nalsa");
        assertTrue(nalsa.isPresent());
        assertEquals("res_nalsa", nalsa.get().id());
        assertTrue(nalsa.get().name().contains("NALSA"));

        Optional<LegalResource> indiacode = legalResourceService.getResourceById("res_indiacode");
        assertTrue(indiacode.isPresent());
        assertTrue(indiacode.get().officialUrl().contains("indiacode.nic.in"));
    }

    @Test
    @DisplayName("Should return empty optional for non-existent resource ID")
    void testGetNonExistentResource() {
        Optional<LegalResource> missing = legalResourceService.getResourceById("invalid_id_xyz");
        assertTrue(missing.isEmpty());
    }
}

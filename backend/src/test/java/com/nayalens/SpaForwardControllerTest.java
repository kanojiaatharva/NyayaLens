package com.nayalens;

import com.nayalens.config.SpaForwardController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpaForwardControllerTest {

    @Test
    @DisplayName("Should forward SPA client-side routes to index.html")
    void testForwardSpaRoutes() {
        SpaForwardController controller = new SpaForwardController();
        String result = controller.forwardSpaRoutes();
        assertEquals("forward:/index.html", result);
    }
}

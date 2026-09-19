package com.nayalens.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaForwardController {

    @GetMapping(value = {
            "/upload",
            "/analysis",
            "/compare",
            "/ask",
            "/actionpath",
            "/sources",
            "/privacy-security",
            "/how-it-works"
    })
    public String forwardSpaRoutes() {
        return "forward:/index.html";
    }
}

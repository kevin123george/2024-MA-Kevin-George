package com.cattleDB.FrontEndService.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PositionControllerweb {
    @GetMapping("/positions-map")
    public String showPositionsMap() {
        // Return the Thymeleaf view (positions.html)
        return "smart-spec-execution";
    }
}

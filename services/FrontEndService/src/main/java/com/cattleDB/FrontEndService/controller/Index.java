package com.cattleDB.FrontEndService.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/")  // Optional base URL for this controller
public class Index {


    @GetMapping("/dash-board")
    public String dashBoard(Model model) {
        return "dash-board";  // View name, ensure "exp.html" or equivalent template exists
    }


    @GetMapping
    public String index(Model model, Principal principal) {

        return "index";  // View name, ensure "exp.html" or equivalent template exists
    }

    @GetMapping("/analysis")
    public String analysisConfig(Model model, Principal principal) {

        return "analysis-config";  // View name, ensure "exp.html" or equivalent template exists
    }


    @GetMapping("/smart-spec-execution")
    public String smartSpecExecutions(Model model) {
        return "smart-spec-execution";  // View name, ensure "exp.html" or equivalent template exists
    }


    @GetMapping("/smart-spec-settings")
    public String smartSpecSettings(Model model) {
        return "smart-spec-settings";  // View name, ensure "exp.html" or equivalent template exists
    }

    @GetMapping("/smart-spec-simulation")
    public String smartSpecSimulation(Model model) {
//        return "smart-spec-simulation";  // View name, ensure "exp.html" or equivalent template exists
        return "dash-board-simulationtest";  // View name, ensure "exp.html" or equivalent template exists
    }


    @GetMapping("/position-stats")
    public String getPositionStats(Model model) {
        return "positions";  // View name, ensure "exp.html" or equivalent template exists
    }

    @GetMapping("/messages")
    public String getMessages(Model model, Authentication authentication) {
        System.out.println(authentication.getAuthorities().toString());

        return "messages";  // View name, ensure "exp.html" or equivalent template exists
    }



    @GetMapping("/alerts")
    public String getAlerts(Model model) {
        return "alerts";  // View name, ensure "exp.html" or equivalent template exists
    }


    @GetMapping("/sanitized-positions")
    public String getPositionDashBoard(Model model) {
        return "positions";  // View name, ensure "exp.html" or equivalent template exists
    }
    @GetMapping("/simulated-positions")
    public String getSimulatedPositionDashBoard(Model model) {
        return "simulated-positions";  // View name, ensure "exp.html" or equivalent template exists
    }

}

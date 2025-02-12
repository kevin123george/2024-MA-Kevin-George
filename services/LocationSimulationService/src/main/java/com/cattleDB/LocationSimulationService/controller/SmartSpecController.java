package com.cattleDB.LocationSimulationService.controller;


import com.cattleDB.LocationSimulationService.Service.SimulationService;
import com.cattleDB.LocationSimulationService.dto.TestCaseResult;
import com.cattleDB.LocationSimulationService.fegin.SmartSPECClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@RequestMapping("/smartspec")
public class SmartSpecController {
    private final SmartSPECClient smartSPECClient;


    @Autowired
    public SmartSpecController(SmartSPECClient smartSPECClient) {
        this.smartSPECClient = smartSPECClient;
    }

    @GetMapping("/persist-result")
    public String persistResult() {
        smartSPECClient.persistResult();
        return "Result persisted successfully.";
    }

    @GetMapping("/start-generation")
    public String startGeneration() {
        smartSPECClient.startGenerate();
        return "Generation started successfully.";
    }

    @GetMapping("/start-learning")
    public String startLearning() {
        var stuff  = smartSPECClient.startLearning();
        return "Learning started successfully.";
    }


}

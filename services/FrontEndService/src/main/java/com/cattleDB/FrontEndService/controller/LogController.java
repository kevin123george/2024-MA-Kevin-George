package com.cattleDB.FrontEndService.controller;

import com.cattleDB.FrontEndService.service.LogStreamerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;


@RestController
public class LogController {


    @Autowired
    private LogStreamerService logStreamerService;

    // Serve the Thymeleaf template for the log streaming page
    @GetMapping("/test")
    public String logStreamPage() throws FileNotFoundException {
        return "logs";
    }

}

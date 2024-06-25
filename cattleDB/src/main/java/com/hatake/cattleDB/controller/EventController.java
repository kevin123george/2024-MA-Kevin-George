package com.hatake.cattleDB.controller;

import com.hatake.cattleDB.dtos.EventResponse;
import com.hatake.cattleDB.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping("/fetch")
    public List<EventResponse> fetchEvents() {
        return eventService.fetchEvents();
    }
}
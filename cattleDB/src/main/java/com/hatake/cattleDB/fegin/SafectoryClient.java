package com.hatake.cattleDB.fegin;

import com.hatake.cattleDB.dtos.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "safectoryClient", url = "https://devtrack.safectory.com")
public interface SafectoryClient {
    @PostMapping("/api/reports/events")
    List<EventResponse> getEvents(
            @RequestHeader("Authorization") String authorization,
            @RequestBody EventRequest request
    );

    @PostMapping("/api/reports/route")
    List<RouteResponse> getRoutes(
            @RequestHeader("Authorization") String authorization,
            @RequestBody RouteRequest request
    );

    @PostMapping("/api/reports/summary")
    List<SummaryResponse> getSummary(
            @RequestHeader("Authorization") String authorization,
            @RequestBody SummaryRequest request
    );
}
package com.cattleDb.DataInjectionService.fegin;

import com.cattleDb.DataInjectionService.dtos.PositionResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "safectoryClient", url = "https://devtrack.safectory.com")
public interface SafectoryClient {

    @GetMapping("/api/positions")
    List<PositionResponse> getPositions(
            @RequestHeader("Authorization") String authorization
    );
}

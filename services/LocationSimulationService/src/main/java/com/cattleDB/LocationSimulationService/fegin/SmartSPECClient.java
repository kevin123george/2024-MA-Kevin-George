package com.cattleDB.LocationSimulationService.fegin;

import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "smartSpecClient",  url = "http://host.docker.internal:8080/api")
public interface SmartSPECClient {

    @GetMapping("/generation/persist")
    void persistResult(
    );


    @GetMapping("/generation/start")
    void startGenerate(
    );


    @GetMapping("/learning/start")
    Response startLearning(
    );
}

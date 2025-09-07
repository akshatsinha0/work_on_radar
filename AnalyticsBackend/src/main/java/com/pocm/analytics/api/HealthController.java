package com.pocm.analytics.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;


@RestController
public class HealthController {
    @GetMapping(path="/health",produces=MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Object> health(){
        return Map.of("status","healthy","timestamp", Instant.now().toString());
    }

    @GetMapping(path="/ready",produces=MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Object> ready(){
        return Map.of(
                "status","ready",
                "dependencies", Map.of("database","connected")
        );
    }
}


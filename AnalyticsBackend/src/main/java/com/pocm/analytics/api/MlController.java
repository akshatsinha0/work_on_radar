package com.pocm.analytics.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// simple stub for ML predictions
@RestController
@RequestMapping("/analytics/ml")
public class MlController {
    @PostMapping(path="/predict",consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Object> predict(@RequestBody Map<String,Object> body){
        // return dummy prediction
        return Map.of(
            "prediction",0.23,
            "confidence",0.87,
            "explanation", Map.of("top_features", List.of(Map.of("feature","days_since_last_order","importance",0.42))),
            "model_version","v2.3.1"
        );
    }
}


package com.pocm.analytics.api;

import com.pocm.analytics.domain.BatchOperation;
import com.pocm.analytics.repo.BatchOperationRepository;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Map;

// simple batch ingestion and status endpoints
@RestController
@RequestMapping("/analytics")
public class BatchController {
    private final BatchOperationRepository batchRepo;

    public BatchController(BatchOperationRepository batchRepo){this.batchRepo=batchRepo;}

    @PostMapping(path="/ingest/batch",consumes= MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Map<String,Object>> ingestBatch(@RequestBody BatchRequest req){
        BatchOperation bo=new BatchOperation();
        bo.setBatchId(req.batch_id);
        bo.setStatus("accepted");
        bo.setTotalEvents(req.events==null?0:req.events.size());
        bo.setCreatedAt(Instant.now());
        bo.setUpdatedAt(Instant.now());
        bo.setTrackingUrl("/analytics/batch/"+req.batch_id+"/status");
        return batchRepo.save(bo).map(saved-> Map.of(
                "batch_id", saved.getBatchId(),
                "status", saved.getStatus(),
                "total_events", saved.getTotalEvents(),
                "estimated_completion", Instant.now().plusSeconds(30).toString(),
                "tracking_url", saved.getTrackingUrl()
        ));
    }

    @GetMapping(path="/batch/{batch_id}/status",produces=MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String,Object>> status(@PathVariable("batch_id") String batchId){
        return batchRepo.findByBatchId(batchId).map(bo-> Map.of(
                "batch_id", bo.getBatchId(),
                "status", bo.getStatus(),
                "progress", Map.of(
                        "processed", 0,
                        "total", bo.getTotalEvents()==null?0:bo.getTotalEvents(),
                        "success_count", 0,
                        "error_count", 0,
                        "duplicate_count", 0
                ),
                "errors", List.of()
        ));
    }

    // simple request body models
    public static class BatchRequest{
        @NotBlank public String batch_id;
        public List<IngestEvent> events;
        public Map<String,Object> options;
    }
    public static class IngestEvent{
        public String type; // keep generic
        public Object data;
        public String occurred_at;
        public String producer;
    }
}


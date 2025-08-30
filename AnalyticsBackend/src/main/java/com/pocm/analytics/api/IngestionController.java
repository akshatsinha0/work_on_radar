package com.pocm.analytics.api;

import com.pocm.analytics.domain.EventEntity;
import com.pocm.analytics.domain.EventType;
import com.pocm.analytics.repo.EventRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.Instant;

// simple controller to accept single event
@RestController
@RequestMapping(path="/analytics")
@Validated
public class IngestionController {
    private final EventRepository eventRepository;
    private final com.pocm.analytics.service.EventService eventService;

    public IngestionController(EventRepository eventRepository, com.pocm.analytics.service.EventService eventService){
        this.eventRepository=eventRepository;
        this.eventService=eventService;
    }

    @PostMapping(path="/ingest",consumes= MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Mono<IngestResponse> ingest(@RequestHeader(value="Idempotency-Key",required=false) String idempotencyKey,
                                       @RequestBody @NotNull IngestEnvelope envelope){
        // simple idempotency check
        if(idempotencyKey!=null && !idempotencyKey.isBlank()){
            return eventRepository.findFirstByIdempotencyKey(idempotencyKey)
                .map(e->new IngestResponse(e.getId().toString(),"ingested",true,Instant.now().toString()))
                .switchIfEmpty(saveNew(envelope,idempotencyKey));
        }
        return saveNew(envelope,null);
    }

    private Mono<IngestResponse> saveNew(IngestEnvelope env,String idem){
        EventEntity e=new EventEntity();
        e.setType(env.type);
        e.setOccurredAt(env.occurred_at);
        e.setReceivedAt(Instant.now());
        e.setProducer(env.producer);
        e.setSourceService(env.source_service);
        e.setUserId(env.user_id);
        e.setOrderId(env.order_id);
        e.setSessionId(env.session_id);
        e.setTraceId(env.trace_id);
        e.setIdempotencyKey(idem);
        e.setDataJson(env.data==null?"null":env.data.toString());
        return eventService.process(e)
                .map(saved->new IngestResponse(saved.getId().toString(),"ingested",false,Instant.now().toString()));
    }

    // request model matching spec (simplified to keep code short)
    public static class IngestEnvelope{
        public EventType type;
        public com.fasterxml.jackson.databind.JsonNode data; // keep as raw JSON object
        public Instant occurred_at;
        public String producer;
        public String source_service;
        public String user_id;
        public String order_id;
        public String session_id;
        public String trace_id;
    }

    public static class IngestResponse{
        public String event_id;
        public String status;
        public boolean deduped;
        public String received_at;
        public IngestResponse(String id,String status,boolean deduped,String receivedAt){
            this.event_id=id;this.status=status;this.deduped=deduped;this.received_at=receivedAt;
        }
    }
}


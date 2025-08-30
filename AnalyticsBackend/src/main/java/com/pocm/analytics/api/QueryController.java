package com.pocm.analytics.api;

import com.pocm.analytics.domain.EventEntity;
import com.pocm.analytics.domain.EventType;
import com.pocm.analytics.repo.EventRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Optional;

// simple query controller for listing and get-by-id
@RestController
@RequestMapping("/analytics")
public class QueryController {
    private final EventRepository eventRepository;

    public QueryController(EventRepository eventRepository){
        this.eventRepository=eventRepository;
    }

    @GetMapping(path="/events",produces= MediaType.APPLICATION_JSON_VALUE)
    public Flux<EventEntity> list(@RequestParam Optional<EventType> type,
                                  @RequestParam(required=false) String user_id,
                                  @RequestParam(required=false) @DateTimeFormat(iso= DateTimeFormat.ISO.DATE_TIME) Instant from,
                                  @RequestParam(required=false) @DateTimeFormat(iso= DateTimeFormat.ISO.DATE_TIME) Instant to,
                                  @RequestParam(defaultValue="100") long limit){
        Instant f=from!=null?from:Instant.now().minusSeconds(86400);
        Instant t=to!=null?to:Instant.now();
        if(type.isPresent()){
            return eventRepository.findByTypeAndTimeRange(type.get(),f,t,Math.min(Math.max(limit,1),1000));
        }
        if(user_id!=null && !user_id.isBlank()){
            return eventRepository.findRecentByUser(user_id,Math.min(Math.max(limit,1),1000));
        }
        return eventRepository.findByTypeAndTimeRange(EventType.cart,f,t,Math.min(Math.max(limit,1),100));
    }

    @GetMapping(path="/events/{id}",produces=MediaType.APPLICATION_JSON_VALUE)
    public Mono<EventEntity> get(@PathVariable long id){
        return eventRepository.findById(id);
    }
}


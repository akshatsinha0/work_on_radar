package com.pocm.analytics.service;

import com.pocm.analytics.domain.EventEntity;
import com.pocm.analytics.repo.EventRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;

// simple service to route/process events
@Service
public class EventService {
    private final EventRepository eventRepository;
    public EventService(EventRepository eventRepository){this.eventRepository=eventRepository;}

    public Mono<EventEntity> process(EventEntity e){
        // just set receivedAt; add routing/biz rules later
        e.setReceivedAt(Instant.now());
        return eventRepository.save(e);
    }
}


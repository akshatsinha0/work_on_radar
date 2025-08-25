package com.pocm.analytics.repo;

import com.pocm.analytics.domain.EventEntity;
import com.pocm.analytics.domain.EventType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

// reactive repository for events
public interface EventRepository extends ReactiveCrudRepository<EventEntity,Long> {
    @Query("SELECT * FROM events WHERE type=:type AND occurred_at BETWEEN :from AND :to ORDER BY occurred_at DESC LIMIT :limit")
    Flux<EventEntity> findByTypeAndTimeRange(EventType type, Instant from, Instant to, long limit);

    @Query("SELECT * FROM events WHERE user_id=:userId ORDER BY occurred_at DESC LIMIT :limit")
    Flux<EventEntity> findRecentByUser(String userId, long limit);

    Mono<EventEntity> findFirstByIdempotencyKey(String idempotencyKey);
}


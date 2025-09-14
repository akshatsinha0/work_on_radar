package com.pocm.analytics.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import io.r2dbc.postgresql.codec.Json;

import java.time.Instant;

//entity to store events, keep data as JSONB via r2dbc Json
@Data
@Table("events")
public class EventEntity {
    @Id
    private Long id;
    private EventType type;
    private Instant occurredAt;
    private Instant receivedAt;
    private String producer;
    private String sourceService;
    private String userId;
    private String orderId;
    private String sessionId;
    private String traceId;
    private String idempotencyKey;
    private Json dataJson;
}


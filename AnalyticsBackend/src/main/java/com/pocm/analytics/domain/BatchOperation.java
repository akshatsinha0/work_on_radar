package com.pocm.analytics.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

// simple entity to audit batch operations
@Data
@Table("batch_operations")
public class BatchOperation {
    @Id
    private Long id;
    private String batchId;
    private String status; // accepted, processing, completed, failed
    private Integer totalEvents;
    private Instant createdAt;
    private Instant updatedAt;
    private String trackingUrl;
}


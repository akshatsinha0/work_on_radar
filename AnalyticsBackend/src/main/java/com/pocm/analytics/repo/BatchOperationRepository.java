package com.pocm.analytics.repo;

import com.pocm.analytics.domain.BatchOperation;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

// repo for batch operations
public interface BatchOperationRepository extends ReactiveCrudRepository<BatchOperation,Long> {
    Mono<BatchOperation> findByBatchId(String batchId);
}


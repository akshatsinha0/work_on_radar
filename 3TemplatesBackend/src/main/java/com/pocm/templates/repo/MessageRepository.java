package com.pocm.templates.repo;

import com.pocm.templates.domain.Message;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

import reactor.core.publisher.Mono;

public interface MessageRepository extends ReactiveCrudRepository<Message, UUID> {
  Mono<Message> findByProviderMsgId(String providerMsgId);
}

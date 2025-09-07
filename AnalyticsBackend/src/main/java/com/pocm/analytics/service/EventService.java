package com.pocm.analytics.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pocm.analytics.domain.EventEntity;
import com.pocm.analytics.repo.EventRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;


@Service
public class EventService {
    private final EventRepository eventRepository;
    private final KafkaTemplate<String,String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topicPrefix;

    public EventService(EventRepository eventRepository,
                        KafkaTemplate<String,String> kafkaTemplate,
                        ObjectMapper objectMapper,
                        @Value("${kafka.topic-prefix:pocm.analytics}") String topicPrefix){
        this.eventRepository=eventRepository;
        this.kafkaTemplate=kafkaTemplate;
        this.objectMapper=objectMapper;
        this.topicPrefix=topicPrefix;
    }

    public Mono<EventEntity> process(EventEntity e){
        e.setReceivedAt(Instant.now());
        return eventRepository.save(e).map(saved->{

            try{
                String topic=topicPrefix+"."+saved.getType().name()+".v1";
                Map<String,Object> payload=new HashMap<>();
                payload.put("id",saved.getId());
                payload.put("type",saved.getType());
                payload.put("occurred_at",saved.getOccurredAt());
                payload.put("received_at",saved.getReceivedAt());
                payload.put("producer",saved.getProducer());
                payload.put("source_service",saved.getSourceService());
                payload.put("user_id",saved.getUserId());
                payload.put("order_id",saved.getOrderId());
                payload.put("session_id",saved.getSessionId());
                payload.put("trace_id",saved.getTraceId());
                payload.put("data",saved.getDataJson());
                String json=objectMapper.writeValueAsString(payload);
                kafkaTemplate.send(topic,json);
            }catch(JsonProcessingException ex){
            }
            return saved;
        });
    }
}


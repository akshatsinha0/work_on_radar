package com.pocm.analytics.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
public class EventStreamService {
    private final KafkaTemplate<String,String> kafkaTemplate;
    public EventStreamService(KafkaTemplate<String,String> kafkaTemplate){this.kafkaTemplate=kafkaTemplate;}

    public void publish(String topic,String value){
        kafkaTemplate.send(topic,value);
    }

    @KafkaListener(topics = "analytics-events", groupId = "analytics-svc")
    public void consume(String message){

    }
}


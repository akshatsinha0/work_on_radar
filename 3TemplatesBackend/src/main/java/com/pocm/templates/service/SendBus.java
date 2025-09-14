package com.pocm.templates.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SendBus {
  private final KafkaTemplate<String, Object> kafkaTemplate;
  public SendBus(KafkaTemplate<String, Object> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }
  public void enqueueSend(String topic, Map<String,Object> payload){
    kafkaTemplate.send(topic, payload);
  }
}

package com.pocm.templates.web;

import com.pocm.templates.service.SendBus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(path="/send", produces = MediaType.APPLICATION_JSON_VALUE)
public class SendController {
  private final SendBus bus;
  private final String topic;
  public SendController(SendBus bus, @Value("${app.kafka.topics.send-request:template.send.request}") String topic){
    this.bus=bus; this.topic=topic;
  }

  @PostMapping(path="/{templateId}")
  public Map<String,Object> send(@PathVariable UUID templateId, @RequestBody Map<String,Object> body){
    body.put("templateId", templateId.toString());
    bus.enqueueSend(topic, body);
    return Map.of("status","queued","id", UUID.randomUUID().toString());
  }
}

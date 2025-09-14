package com.pocm.templates.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AnalyticsController {
  private final String topic;
  public AnalyticsController(@Value("${app.kafka.topics.send-result:template.send.result}") String topic){ this.topic=topic; }

  @GetMapping(path="/templates/analytics", produces = MediaType.APPLICATION_JSON_VALUE)
  public Map<String,Object> analytics(){
    return Map.of(
      "message","Use Analytics service to query detailed KPIs",
      "events_topic", topic
    );
  }
}

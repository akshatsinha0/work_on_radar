package com.pocm.templates.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicsConfig {
  @Bean
  public NewTopic sendRequestTopic(@Value("${app.kafka.topics.send-request:template.send.request}") String name){
    return new NewTopic(name,1,(short)1);
  }
  @Bean
  public NewTopic sendResultTopic(@Value("${app.kafka.topics.send-result:template.send.result}") String name){
    return new NewTopic(name,1,(short)1);
  }
}

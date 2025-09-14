package com.pocm.templates.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pocm.templates.domain.Message;
import com.pocm.templates.repo.MessageRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
public class SendWorker {
  private final MessageRepository messages;
  private final ProviderRegistry providers;
  private final ObjectMapper mapper = new ObjectMapper();
  private final String resultTopic;
  private final SendBus bus;

  public SendWorker(MessageRepository messages, ProviderRegistry providers,
                    SendBus bus,
                    @Value("${app.kafka.topics.send-result:template.send.result}") String resultTopic){
    this.messages=messages; this.providers=providers; this.bus=bus; this.resultTopic=resultTopic;
  }

  @KafkaListener(topics = "${app.kafka.topics.send-request:template.send.request}", groupId = "templates-svc")
  public void onSendRequest(ConsumerRecord<String, Map<String,Object>> record, Acknowledgment ack){
    try {
      Map<String,Object> payload = record.value();
      String channel = String.valueOf(payload.getOrDefault("channel","email"));
      String tenantId = String.valueOf(payload.getOrDefault("tenant_id","default"));
      ProviderAdapter adapter = providers.byChannel(channel);
      String providerMsgId = adapter.send(payload);

      Message m = new Message();
      m.setId(UUID.randomUUID());
      m.setNewEntity(true);
      m.setTemplateId(UUID.fromString(String.valueOf(payload.get("templateId"))));
      m.setVersion(Integer.parseInt(String.valueOf(payload.getOrDefault("version","1"))));
      m.setChannel(channel);
      m.setRecipient(mapper.writeValueAsString(payload.getOrDefault("recipient", Map.of())));
      m.setVariables(mapper.writeValueAsString(payload.getOrDefault("variables", Map.of())));
      m.setProvider(adapter.channel());
      m.setProviderMsgId(providerMsgId);
      m.setStatus("sent");
      m.setSentAt(Instant.now());
      m.setTenantId(tenantId);
      m.setCreatedAt(Instant.now());
      messages.save(m).then(Mono.fromRunnable(() -> bus.enqueueSend(resultTopic, Map.of(
        "message_id", providerMsgId,
        "status", "sent"
      )))).subscribe();

      ack.acknowledge();
    } catch (Exception e){
      //retry//
      throw new RuntimeException(e);
    }
  }
}

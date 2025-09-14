package com.pocm.templates.web;
import com.pocm.templates.repo.MessageRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
//
import java.util.Map;
@RestController
@RequestMapping(path="/webhooks", produces = MediaType.APPLICATION_JSON_VALUE)
public class WebhookController {
  private final MessageRepository messages;
  public WebhookController(MessageRepository messages){ this.messages=messages; }

  @PostMapping("/{provider}")
  public Mono<Map<String,Object>> handle(@PathVariable String provider, @RequestBody Map<String,Object> payload){
    String providerMsgId = String.valueOf(payload.getOrDefault("message_id",""));
    String status = String.valueOf(payload.getOrDefault("status","delivered"));
    return messages.findByProviderMsgId(providerMsgId)
      .flatMap(msg -> { msg.setStatus(status); return messages.save(msg); })
      .map(saved -> Map.<String,Object>of("ok",Boolean.TRUE))
      .defaultIfEmpty(Map.<String,Object>of("ok",Boolean.FALSE));
  }
}

package com.pocm.templates.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class ApiKeyFilter implements WebFilter {
  private final Set<String> validKeys;

  public ApiKeyFilter(@Value("${app.security.api-keys:}") String keysCsv){
    if(keysCsv==null||keysCsv.isBlank()) validKeys=new HashSet<>();
    else validKeys=new HashSet<>(Arrays.asList(keysCsv.split(",")));
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain){
    ServerHttpRequest req=exchange.getRequest();
    String path=req.getPath().value();
    // allow health/actuator and webhooks without key in dev; tighten later
    if(path.startsWith("/health")||path.startsWith("/ready")||path.startsWith("/actuator")||path.startsWith("/webhooks")){
      return chain.filter(exchange);
    }
    if(validKeys.isEmpty()) return chain.filter(exchange);
    String key=req.getHeaders().getFirst("X-API-Key");
    if(key!=null && validKeys.contains(key)) return chain.filter(exchange);
    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
    return exchange.getResponse().setComplete();
  }
}

package com.pocm.analytics.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

// simple API key filter using X-API-Key; allows health/ready without key
@Component
public class ApiKeyFilter implements WebFilter {
    private final List<String> apiKeys;
    public ApiKeyFilter(@Value("${security.api-keys:}") List<String> keys){this.apiKeys=keys;}

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest req=exchange.getRequest();
        String path=req.getPath().value();
        if(path.startsWith("/health")||path.startsWith("/ready")||path.startsWith("/actuator")){
            return chain.filter(exchange);
        }
        if(apiKeys==null||apiKeys.isEmpty())return chain.filter(exchange);
        String key=req.getHeaders().getFirst("X-API-Key");
        if(key!=null && apiKeys.contains(key))return chain.filter(exchange);
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add(HttpHeaders.WWW_AUTHENTICATE,"ApiKey");
        return exchange.getResponse().setComplete();
    }
}


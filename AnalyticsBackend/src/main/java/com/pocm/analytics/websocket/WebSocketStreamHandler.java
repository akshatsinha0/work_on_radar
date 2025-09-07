package com.pocm.analytics.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;


@Component
public class WebSocketStreamHandler implements WebSocketHandler {
    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Flux<WebSocketMessage> out=Flux.interval(Duration.ofSeconds(1))
                .map(i->session.textMessage("{\"message\":\"tick "+i+"\"}"));
        return session.send(out);
    }
}


package com.pocm.analytics.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.Map;


@Configuration
public class WebSocketConfig {
    @Bean
    public HandlerMapping webSocketMapping(WebSocketStreamHandler handler){
        return new SimpleUrlHandlerMapping(Map.of("/ws/stream", handler),10);
    }
    @Bean
    public WebSocketHandlerAdapter handlerAdapter(){
        return new WebSocketHandlerAdapter();
    }
}


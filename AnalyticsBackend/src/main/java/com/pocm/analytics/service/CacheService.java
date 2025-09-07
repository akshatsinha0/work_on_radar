package com.pocm.analytics.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;


@Service
public class CacheService {
    private final ReactiveStringRedisTemplate redis;
    private final Duration ttl;
    public CacheService(ReactiveStringRedisTemplate redis,@Value("${cache.ttl-seconds:60}") long ttlSeconds){
        this.redis=redis;this.ttl=Duration.ofSeconds(ttlSeconds);
    }

    public Mono<String> get(String key){
        return redis.opsForValue().get(key);
    }
    public Mono<Boolean> put(String key,String value){
        return redis.opsForValue().set(key,value,ttl);
    }
}


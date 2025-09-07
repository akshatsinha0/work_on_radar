package com.pocm.analytics.service;

import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;


@Service
public class RateLimiterService {
    private final ReactiveStringRedisTemplate redis;
    public RateLimiterService(ReactiveStringRedisTemplate redis){this.redis=redis;}

    public Mono<Boolean> allow(String key,int max,Duration window){
        String redisKey="rl:"+key;
        return redis.opsForValue().increment(redisKey)
            .flatMap(count->{
                if(count==1L){
                    return redis.expire(redisKey,window).thenReturn(true);
                }
                return Mono.just(count<=max);
            });
    }
}


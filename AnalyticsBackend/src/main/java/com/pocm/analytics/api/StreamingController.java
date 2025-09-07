package com.pocm.analytics.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Map;

@RestController
public class StreamingController {
    @GetMapping(path="/analytics/stream",produces=MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestParam(required=false) String filter,
                               @RequestParam(name="buffer_size",required=false,defaultValue="10") int bufferSize){
        return Flux.interval(Duration.ofSeconds(1))
                .map(i->"event: tick\ndata: {\"message\":\"tick "+i+"\",\"filter\":\""+(filter==null?"":filter)+"\"}\n\n")
                .onBackpressureBuffer(bufferSize);
    }
}


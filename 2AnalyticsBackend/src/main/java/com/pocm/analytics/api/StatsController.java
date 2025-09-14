package com.pocm.analytics.api;

import com.pocm.analytics.clickhouse.ClickHouseService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;


@RestController
@RequestMapping("/analytics")
public class StatsController {
    private final ClickHouseService ch;
    public StatsController(ClickHouseService ch){this.ch=ch;}

    @GetMapping(path="/stats",produces=MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String,Object>> stats(@RequestParam(required=false) @DateTimeFormat(iso= DateTimeFormat.ISO.DATE_TIME) Instant from,
                                          @RequestParam(required=false) @DateTimeFormat(iso= DateTimeFormat.ISO.DATE_TIME) Instant to){
        Instant f=from!=null?from:Instant.now().minusSeconds(86400);
        Instant t=to!=null?to:Instant.now();
        if(!ch.isEnabled()){
            return Mono.just(Map.of(
                "clickhouse_configured",false,
                "message","ClickHouse not configured; set clickhouse.url in application.yml",
                "stats", Map.of()
            ));
        }
        return ch.countByType(f,t).map(map-> Map.of(
            "clickhouse_configured",true,
            "from",f.toString(),
            "to",t.toString(),
            "counts_by_type",map
        ));
    }
}

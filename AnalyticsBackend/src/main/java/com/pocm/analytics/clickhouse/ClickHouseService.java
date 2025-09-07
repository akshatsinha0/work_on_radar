package com.pocm.analytics.clickhouse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;


@Service
public class ClickHouseService {
    private final String url;
    private final String user;
    private final String pass;

    public ClickHouseService(@Value("${clickhouse.url:}") String url,
                             @Value("${clickhouse.username:}") String user,
                             @Value("${clickhouse.password:}") String pass){
        this.url=url;this.user=user;this.pass=pass;
    }

    public boolean isEnabled(){
        return url!=null && !url.isBlank();
    }

    public Mono<Map<String,Long>> countByType(Instant from, Instant to){
        if(!isEnabled())return Mono.error(new IllegalStateException("ClickHouse not configured"));
        return Mono.fromCallable(()->{
            Map<String,Long> out=new HashMap<>();
            try(Connection conn=(user==null||user.isBlank())?DriverManager.getConnection(url):DriverManager.getConnection(url,user,pass)){

                String sql="SELECT type, count() AS cnt FROM events_raw WHERE occurred_at BETWEEN parseDateTimeBestEffort(?) AND parseDateTimeBestEffort(?) GROUP BY type";
                try(PreparedStatement ps=conn.prepareStatement(sql)){
                    ps.setString(1,from.toString());
                    ps.setString(2,to.toString());
                    try(ResultSet rs=ps.executeQuery()){
                        while(rs.next()){
                            out.put(rs.getString("type"), rs.getLong("cnt"));
                        }
                    }
                }
            }
            return out;
        }).subscribeOn(Schedulers.boundedElastic());
    }
}

package com.pocm.templates.web;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.Instant;
import java.util.Map;

@RestController
public class InfoController {
  @GetMapping(path="/info", produces= MediaType.APPLICATION_JSON_VALUE)
  public Map<String,Object> info(){
    return Map.of("service","templates-backend","version","0.1.0","time", Instant.now().toString());
  }
}

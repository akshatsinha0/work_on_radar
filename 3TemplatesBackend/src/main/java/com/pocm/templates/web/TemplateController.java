package com.pocm.templates.web;

import com.pocm.templates.domain.Template;
import com.pocm.templates.domain.TemplateVersion;
import com.pocm.templates.service.RenderService;
import com.pocm.templates.service.TemplateService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(path="/templates", produces = MediaType.APPLICATION_JSON_VALUE)
public class TemplateController {
  private final TemplateService templateService;
  private final RenderService renderService;

  public TemplateController(TemplateService templateService, RenderService renderService){
    this.templateService=templateService;
    this.renderService=renderService;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public Mono<Template> create(@RequestBody Template t){
    return templateService.create(t);
  }

  @GetMapping
  public Flux<Template> list(){ return templateService.list(); }

  @GetMapping("/{id}")
  public Mono<Template> get(@PathVariable UUID id){ return templateService.get(id); }

  @DeleteMapping("/{id}")
  public Mono<Void> delete(@PathVariable UUID id){ return templateService.delete(id); }

  @PostMapping(path="/{id}/versions", consumes = MediaType.APPLICATION_JSON_VALUE)
  public Mono<TemplateVersion> addVersion(@PathVariable UUID id, @RequestBody TemplateVersion v){
    v.setTemplateId(id);
    return templateService.addVersion(v);
  }

  @PostMapping(path="/{id}/render", consumes = MediaType.APPLICATION_JSON_VALUE)
  public Mono<Map<String,Object>> render(@PathVariable UUID id, @RequestBody Map<String,Object> body){
    /*
        choose engine=liquid/handlebars via payload
    */
    String engine = String.valueOf(body.getOrDefault("engine","liquid"));
    String raw = String.valueOf(body.getOrDefault("content",""));
    Map<String,Object> variables = (Map<String,Object>) body.getOrDefault("variables", Map.of());
    try{
      String output = engine.equals("handlebars") ? renderService.renderHandlebars(raw, variables) : renderService.renderLiquid(raw, variables);
      return Mono.just(Map.of("rendered", output));
    } catch (IOException e){
      return Mono.error(e);
    }
  }
}

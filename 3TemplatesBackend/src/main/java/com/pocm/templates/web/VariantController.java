package com.pocm.templates.web;

import com.pocm.templates.domain.TemplateVariant;
import com.pocm.templates.service.VariantService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping(path="/templates/{id}/variants", produces = MediaType.APPLICATION_JSON_VALUE)
public class VariantController {
  private final VariantService service;
  public VariantController(VariantService service){ this.service=service; }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public Mono<TemplateVariant> create(@PathVariable UUID id, @RequestBody TemplateVariant body){
    return service.create(id, body);
  }
}

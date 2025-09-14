package com.pocm.templates.service;

import com.pocm.templates.domain.TemplateVariant;
import com.pocm.templates.repo.TemplateVariantRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class VariantService {
  private final TemplateVariantRepository repo;
  public VariantService(TemplateVariantRepository repo){ this.repo=repo; }

  public Mono<TemplateVariant> create(UUID templateId, TemplateVariant v){
    v.setId(UUID.randomUUID());
    v.setTemplateId(templateId);
    return repo.save(v);
  }
}

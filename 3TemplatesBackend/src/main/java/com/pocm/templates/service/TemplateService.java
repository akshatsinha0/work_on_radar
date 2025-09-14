package com.pocm.templates.service;

import com.pocm.templates.domain.Template;
import com.pocm.templates.domain.TemplateVersion;
import com.pocm.templates.repo.TemplateRepository;
import com.pocm.templates.repo.TemplateVersionRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class TemplateService {
  private final TemplateRepository templateRepo;
  private final TemplateVersionRepository versionRepo;

  public TemplateService(TemplateRepository templateRepo, TemplateVersionRepository versionRepo) {
    this.templateRepo = templateRepo;
    this.versionRepo = versionRepo;
  }

  public Mono<Template> create(Template t){
    t.setId(UUID.randomUUID());
    return templateRepo.save(t);
  }

  public Mono<Template> get(UUID id){ return templateRepo.findById(id); }
  public Flux<Template> list(){ return templateRepo.findAll(); }
  public Mono<Void> delete(UUID id){ return templateRepo.deleteById(id); }

  public Mono<TemplateVersion> addVersion(TemplateVersion v){
    v.setId(UUID.randomUUID());
    return versionRepo.save(v);
  }
}

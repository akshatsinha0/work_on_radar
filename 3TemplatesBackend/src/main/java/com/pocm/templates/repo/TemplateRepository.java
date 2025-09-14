package com.pocm.templates.repo;

import com.pocm.templates.domain.Template;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface TemplateRepository extends ReactiveCrudRepository<Template, UUID> {
}

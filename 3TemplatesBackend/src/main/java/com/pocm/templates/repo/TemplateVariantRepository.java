package com.pocm.templates.repo;

import com.pocm.templates.domain.TemplateVariant;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface TemplateVariantRepository extends ReactiveCrudRepository<TemplateVariant, UUID> {
}

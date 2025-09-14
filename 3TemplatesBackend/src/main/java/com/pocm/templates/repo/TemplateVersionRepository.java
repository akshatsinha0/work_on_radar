package com.pocm.templates.repo;

import com.pocm.templates.domain.TemplateVersion;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface TemplateVersionRepository extends ReactiveCrudRepository<TemplateVersion, UUID> {
}

package com.pocm.templates.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("template_versions")
public class TemplateVersion implements Persistable<UUID> {
  @Id
  private UUID id;
  @Column("template_id")
  private UUID templateId;
  private int version;
  private String subject; // for emails
  @Column("content_raw")
  private String contentRaw;
  @Column("content_compiled")
  private String contentCompiled; // for MJML compiled HTML
  private String engine; // liquid, handlebars, raw
  @Column("variables_schema")
  private String variablesSchema; // JSON string
  @Column("is_active")
  private boolean isActive;
  @Column("created_by")
  private String createdBy;
  @Column("created_at")
  private Instant createdAt;

  @Transient
  private boolean newEntity = true;

  @Override
  public UUID getId(){ return id; }
  public void setId(UUID id){ this.id=id; }
  public UUID getTemplateId(){ return templateId; }
  public void setTemplateId(UUID templateId){ this.templateId=templateId; }
  public int getVersion(){ return version; }
  public void setVersion(int version){ this.version=version; }
  public String getSubject(){ return subject; }
  public void setSubject(String subject){ this.subject=subject; }
  public String getContentRaw(){ return contentRaw; }
  public void setContentRaw(String contentRaw){ this.contentRaw=contentRaw; }
  public String getContentCompiled(){ return contentCompiled; }
  public void setContentCompiled(String contentCompiled){ this.contentCompiled=contentCompiled; }
  public String getEngine(){ return engine; }
  public void setEngine(String engine){ this.engine=engine; }
  public String getVariablesSchema(){ return variablesSchema; }
  public void setVariablesSchema(String variablesSchema){ this.variablesSchema=variablesSchema; }
  public boolean isActive(){ return isActive; }
  public void setActive(boolean active){ isActive = active; }
  public String getCreatedBy(){ return createdBy; }
  public void setCreatedBy(String createdBy){ this.createdBy=createdBy; }
  public Instant getCreatedAt(){ return createdAt; }
  public void setCreatedAt(Instant createdAt){ this.createdAt=createdAt; }

  @Override
  public boolean isNew() { return newEntity; }
  public void setNewEntity(boolean newEntity){ this.newEntity=newEntity; }
}

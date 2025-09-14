package com.pocm.templates.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("template_variants")
public class TemplateVariant implements Persistable<UUID> {
  @Id
  private UUID id;
  @Column("template_id")
  private UUID templateId;
  private String key;
  private int weight;
  private String diff;
  @Column("created_at")
  private Instant createdAt;

  @Transient
  private boolean newEntity = true;

  @Override
  public UUID getId(){ return id; }
  public void setId(UUID id){ this.id=id; }
  public UUID getTemplateId(){ return templateId; }
  public void setTemplateId(UUID templateId){ this.templateId=templateId; }
  public String getKey(){ return key; }
  public void setKey(String key){ this.key=key; }
  public int getWeight(){ return weight; }
  public void setWeight(int weight){ this.weight=weight; }
  public String getDiff(){ return diff; }
  public void setDiff(String diff){ this.diff=diff; }
  public Instant getCreatedAt(){ return createdAt; }
  public void setCreatedAt(Instant createdAt){ this.createdAt=createdAt; }

  @Override
  public boolean isNew() { return newEntity; }
  public void setNewEntity(boolean newEntity){ this.newEntity=newEntity; }
}

package com.pocm.templates.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("templates")
public class Template {
  @Id
  private UUID id;
  private String name;
  private String type; // email, sms, push, whatsapp, inapp
  private String category; // transactional, logistics, marketing
  private String language;
  private String status; // draft, active, archived
  @Column("tenant_id")
  private String tenantId;
  @Column("created_at")
  private Instant createdAt;
  @Column("updated_at")
  private Instant updatedAt;

  // getters/setters
  public UUID getId(){ return id; }
  public void setId(UUID id){ this.id=id; }
  public String getName(){ return name; }
  public void setName(String name){ this.name=name; }
  public String getType(){ return type; }
  public void setType(String type){ this.type=type; }
  public String getCategory(){ return category; }
  public void setCategory(String category){ this.category=category; }
  public String getLanguage(){ return language; }
  public void setLanguage(String language){ this.language=language; }
  public String getStatus(){ return status; }
  public void setStatus(String status){ this.status=status; }
  public String getTenantId(){ return tenantId; }
  public void setTenantId(String tenantId){ this.tenantId=tenantId; }
  public Instant getCreatedAt(){ return createdAt; }
  public void setCreatedAt(Instant createdAt){ this.createdAt=createdAt; }
  public Instant getUpdatedAt(){ return updatedAt; }
  public void setUpdatedAt(Instant updatedAt){ this.updatedAt=updatedAt; }
}

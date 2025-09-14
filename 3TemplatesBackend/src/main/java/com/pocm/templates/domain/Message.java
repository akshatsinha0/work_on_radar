package com.pocm.templates.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("messages")
public class Message {
  @Id
  private UUID id;
  @Column("template_id")
  private UUID templateId;
  private int version;
  private String channel; // email, sms, push, whatsapp, inapp
  private String recipient; // json string
  private String variables; // json string
  private String provider;
  @Column("provider_msg_id")
  private String providerMsgId;
  private String status;
  @Column("error_code")
  private String errorCode;
  @Column("sent_at")
  private Instant sentAt;
  @Column("delivered_at")
  private Instant deliveredAt;
  @Column("opened_at")
  private Instant openedAt;
  @Column("clicked_at")
  private Instant clickedAt;
  @Column("tenant_id")
  private String tenantId;
  @Column("created_at")
  private Instant createdAt;

  // getters/setters
  public UUID getId(){ return id; }
  public void setId(UUID id){ this.id=id; }
  public UUID getTemplateId(){ return templateId; }
  public void setTemplateId(UUID templateId){ this.templateId=templateId; }
  public int getVersion(){ return version; }
  public void setVersion(int version){ this.version=version; }
  public String getChannel(){ return channel; }
  public void setChannel(String channel){ this.channel=channel; }
  public String getRecipient(){ return recipient; }
  public void setRecipient(String recipient){ this.recipient=recipient; }
  public String getVariables(){ return variables; }
  public void setVariables(String variables){ this.variables=variables; }
  public String getProvider(){ return provider; }
  public void setProvider(String provider){ this.provider=provider; }
  public String getProviderMsgId(){ return providerMsgId; }
  public void setProviderMsgId(String providerMsgId){ this.providerMsgId=providerMsgId; }
  public String getStatus(){ return status; }
  public void setStatus(String status){ this.status=status; }
  public String getErrorCode(){ return errorCode; }
  public void setErrorCode(String errorCode){ this.errorCode=errorCode; }
  public Instant getSentAt(){ return sentAt; }
  public void setSentAt(Instant sentAt){ this.sentAt=sentAt; }
  public Instant getDeliveredAt(){ return deliveredAt; }
  public void setDeliveredAt(Instant deliveredAt){ this.deliveredAt=deliveredAt; }
  public Instant getOpenedAt(){ return openedAt; }
  public void setOpenedAt(Instant openedAt){ this.openedAt=openedAt; }
  public Instant getClickedAt(){ return clickedAt; }
  public void setClickedAt(Instant clickedAt){ this.clickedAt=clickedAt; }
  public String getTenantId(){ return tenantId; }
  public void setTenantId(String tenantId){ this.tenantId=tenantId; }
  public Instant getCreatedAt(){ return createdAt; }
  public void setCreatedAt(Instant createdAt){ this.createdAt=createdAt; }
}

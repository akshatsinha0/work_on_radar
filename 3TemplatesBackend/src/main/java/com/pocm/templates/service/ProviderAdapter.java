package com.pocm.templates.service;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Provider adapter SPI. Implement Email/SMS/Push by this interface.
 */
public interface ProviderAdapter {
  String channel(); // email, sms, push, whatsapp, inapp
  /**
   * Send rendered content with recipient/variables metadata.
   * Return provider message id.
   */
  String send(Map<String,Object> payload) throws Exception;
}

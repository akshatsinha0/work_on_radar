package com.pocm.templates.service;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ProviderRegistry {
  private final List<ProviderAdapter> adapters;
  public ProviderRegistry(List<ProviderAdapter> adapters){ this.adapters = adapters; }

  public ProviderAdapter byChannel(String channel){
    return adapters.stream().filter(a -> a.channel().equalsIgnoreCase(channel)).findFirst()
      .orElse(new NoopProvider(channel));
  }

  static class NoopProvider implements ProviderAdapter {
    private final String ch; NoopProvider(String ch){ this.ch=ch; }
    @Override public String channel(){ return ch; }
    @Override public String send(Map<String, Object> payload) { return "noop-"+System.currentTimeMillis(); }
  }
}

package com.pocm.templates.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http){
    return http
      .csrf(csrf->csrf.disable())
      .authorizeExchange(ex->ex
        .pathMatchers("/health","/ready","/actuator/**","/info","/webhooks/**").permitAll()
        .anyExchange().permitAll() //unguardedfor better run
      )
      .httpBasic(Customizer.withDefaults())
      .build();
  }
}

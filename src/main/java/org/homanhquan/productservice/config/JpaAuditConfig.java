package org.homanhquan.productservice.config;

import org.homanhquan.productservice.security.audit.SpringSecurityAuditorAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.UUID;

@Configuration
@EnableJpaAuditing
public class JpaAuditConfig {

    @Bean
    public AuditorAware<UUID> auditorProvider() {
        return new SpringSecurityAuditorAware();
    }
}


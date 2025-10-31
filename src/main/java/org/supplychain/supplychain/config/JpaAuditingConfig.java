package org.supplychain.supplychain.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            // Retourner l'utilisateur connecté depuis le SecurityContext
            // Pour le moment, retourner un utilisateur par défaut
            return Optional.of("SYSTEM");
        };
    }
}

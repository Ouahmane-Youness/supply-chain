package org.supplychain.mysupply.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/raw-materials/**", "/api/suppliers/**", "/api/supply-orders/**")
                        .hasAnyRole("ADMIN", "GESTIONNAIRE_APPROVISIONNEMENT", "RESPONSABLE_ACHATS", "SUPERVISEUR_LOGISTIQUE")

                        .requestMatchers("/api/products/**", "/api/production-orders/**")
                        .hasAnyRole("ADMIN", "CHEF_PRODUCTION", "PLANIFICATEUR", "SUPERVISEUR_PRODUCTION")

                        .requestMatchers("/api/customers/**", "/api/customer-orders/**", "/api/deliveries/**")
                        .hasAnyRole("ADMIN", "GESTIONNAIRE_COMMERCIAL", "RESPONSABLE_LOGISTIQUE", "SUPERVISEUR_LIVRAISONS")

                        .requestMatchers("/api/users/**")
                        .hasRole("ADMIN")

                        .requestMatchers("/api/test/**")
                        .permitAll()

                        .anyRequest().authenticated()
                )

                .httpBasic(basic -> {})

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("password123"))
                .roles("ADMIN")
                .build();

        UserDetails gestionnaireAppro = User.builder()
                .username("gestionnaire")
                .password(passwordEncoder().encode("password123"))
                .roles("GESTIONNAIRE_APPROVISIONNEMENT")
                .build();

        UserDetails responsableAchats = User.builder()
                .username("acheteur")
                .password(passwordEncoder().encode("password123"))
                .roles("RESPONSABLE_ACHATS")
                .build();

        UserDetails superviseurLogistique = User.builder()
                .username("logistique_sup")
                .password(passwordEncoder().encode("password123"))
                .roles("SUPERVISEUR_LOGISTIQUE")
                .build();

        UserDetails chefProduction = User.builder()
                .username("chef_prod")
                .password(passwordEncoder().encode("password123"))
                .roles("CHEF_PRODUCTION")
                .build();

        UserDetails planificateur = User.builder()
                .username("planif")
                .password(passwordEncoder().encode("password123"))
                .roles("PLANIFICATEUR")
                .build();

        UserDetails superviseurProduction = User.builder()
                .username("prod_sup")
                .password(passwordEncoder().encode("password123"))
                .roles("SUPERVISEUR_PRODUCTION")
                .build();

        UserDetails gestionnaireCommercial = User.builder()
                .username("commercial")
                .password(passwordEncoder().encode("password123"))
                .roles("GESTIONNAIRE_COMMERCIAL")
                .build();

        UserDetails responsableLogistique = User.builder()
                .username("resp_logistique")
                .password(passwordEncoder().encode("password123"))
                .roles("RESPONSABLE_LOGISTIQUE")
                .build();

        UserDetails superviseurLivraisons = User.builder()
                .username("livraison_sup")
                .password(passwordEncoder().encode("password123"))
                .roles("SUPERVISEUR_LIVRAISONS")
                .build();

        return new InMemoryUserDetailsManager(
                admin, gestionnaireAppro, responsableAchats, superviseurLogistique,
                chefProduction, planificateur, superviseurProduction,
                gestionnaireCommercial, responsableLogistique, superviseurLivraisons
        );
    }
}
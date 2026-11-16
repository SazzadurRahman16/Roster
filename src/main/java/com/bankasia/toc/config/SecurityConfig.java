package com.bankasia.toc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(a -> a
                        // Public, read-only pages: anyone can view the roster without signing in
                        .requestMatchers(HttpMethod.GET, "/", "/roster", "/roster/export", "/employees",
                                "/am", "/am/roster", "/am/roster/export", "/am/team", "/am/branches", "/am/departments").permitAll()
                        .requestMatchers("/css/**", "/login", "/actuator/health").permitAll()
                        .anyRequest().authenticated())
            .formLogin(f -> f.loginPage("/login").defaultSuccessUrl("/roster", true).permitAll())
            .logout(l -> l.logoutSuccessUrl("/roster"));
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /** Only the admin signs in; everyone else views the roster anonymously. */
    @Bean
    UserDetailsService users(PasswordEncoder pe,
                             @Value("${app.security.admin-user}") String au,
                             @Value("${app.security.admin-password}") String ap) {
        return new InMemoryUserDetailsManager(
                User.withUsername(au).password(pe.encode(ap)).roles("ADMIN").build());
    }
}

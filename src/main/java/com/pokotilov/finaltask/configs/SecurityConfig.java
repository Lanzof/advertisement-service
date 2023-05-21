package com.pokotilov.finaltask.configs;

import com.pokotilov.finaltask.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests()
            .requestMatchers("/api/auth/**").permitAll()
            .requestMatchers(HttpMethod.GET,"/api/advert/**").permitAll()
            .requestMatchers("/swagger-ui.html", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}

//Выключает все фильтры безопасности
//http
//    .anonymous(AbstractHttpConfigurer::disable)         // AnonymousAuthenticationFilter
//    .csrf(AbstractHttpConfigurer::disable)              // CsrfFilter
//    .sessionManagement(AbstractHttpConfigurer::disable) // DisableEncodeUrlFilter, SessionManagementFilter
//    .exceptionHandling(AbstractHttpConfigurer::disable) // ExceptionTranslationFilter
//    .headers(AbstractHttpConfigurer::disable)           // HeaderWriterFilter
//    .logout(AbstractHttpConfigurer::disable)            // LogoutFilter
//    .requestCache(AbstractHttpConfigurer::disable)      // RequestCacheAwareFilter
//    .servletApi(AbstractHttpConfigurer::disable)        // SecurityContextHolderAwareRequestFilter
//    .securityContext(AbstractHttpConfigurer::disable);   // SecurityContextPersistenceFilter
//    .build();
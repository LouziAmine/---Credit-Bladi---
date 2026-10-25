package com.credit.bladi.config.security;

import jakarta.servlet.DispatcherType;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Value("${security.cors.allowed-origin}")
    private String allowedOrigin;

    @Value("${swagger.public:false}")
    private boolean swaggerPublic;

    @Bean
    @Order(1)
    public SecurityFilterChain fakeSmsGatewaySecurityFilterChain(HttpSecurity http) throws Exception {
        return http
            .securityMatcher("/api/v1/fake-sms-gateway/**")
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.deny())
                .contentSecurityPolicy(csp -> csp.policyDirectives(
                    "default-src 'self'; "
                        + "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com https://cdn.jsdelivr.net; "
                        + "font-src 'self' https://fonts.gstatic.com https://cdn.jsdelivr.net; "
                        + "frame-ancestors 'none'"))
            )
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthFilter jwtAuthFilter,
                                                   RateLimitFilter rateLimitFilter,
                                                   SecurityExceptionHandler securityExceptionHandler) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .headers(SecurityConfig::securityHeaders)
            .authorizeHttpRequests(this::authorizeRoutes)
            .exceptionHandling(ex -> ex
                .accessDeniedHandler(securityExceptionHandler)
                .authenticationEntryPoint(securityExceptionHandler)
            )
            .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    private static void securityHeaders(HeadersConfigurer<HttpSecurity> headers) {
        headers
            .frameOptions(frameOptions -> frameOptions.deny())
            .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'; frame-ancestors 'none'"))
            .referrerPolicy(referrerPolicy -> referrerPolicy.policy(
                ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER))
            .addHeaderWriter(new StaticHeadersWriter("Permissions-Policy",
                "geolocation=(), camera=(), microphone=(), payment=()"));
    }

    private void authorizeRoutes(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth
            .requestMatchers(HttpMethod.POST, "/api/v1/auth/register", "/api/v1/auth/login").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/v1/auth/refresh").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/v1/auth/logout").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/auth/me").authenticated()
            .requestMatchers(HttpMethod.POST, "/api/v1/mortgage-credit").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/v1/investment-credit").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/invitations/*").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/v1/invitations/*/accept").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/activity-sectors").permitAll()
            .requestMatchers("/actuator/health", "/actuator/info").permitAll()
            .requestMatchers("/actuator/**").hasRole("MANAGER");
        if (swaggerPublic) {
            auth.requestMatchers("/swagger/credit-bladi/**", "/swagger-ui/**", "/webjars/**").permitAll();
        } else {
            auth.requestMatchers("/swagger/credit-bladi/**", "/swagger-ui/**", "/webjars/**").hasRole("MANAGER");
        }
        auth
            .requestMatchers("/h2-console/**").hasRole("MANAGER")
            .requestMatchers("/api/v1/bam-parameters/**").hasRole("MANAGER")
            .requestMatchers("/api/v1/credit-rates/**").hasRole("MANAGER")
            .requestMatchers("/api/v1/mourabaha-parameters/**").hasRole("MANAGER")
            .requestMatchers("/api/v1/investment-credit-parameters/**").hasRole("MANAGER")
            .requestMatchers("/api/v1/demands/**").hasRole("MANAGER")
            .requestMatchers("/api/v1/users/**").hasRole("MANAGER")
            .requestMatchers("/api/v1/grades/**").hasRole("MANAGER")
            .requestMatchers("/api/v1/teams/**").hasRole("MANAGER")
            .requestMatchers("/api/v1/audit-log/**").hasRole("MANAGER")
            .requestMatchers("/api/v1/enterprise-user-links/**").hasRole("MANAGER")
            .requestMatchers("/api/v1/me/**").hasRole("CLIENT")
            .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
            .anyRequest().authenticated();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(allowedOrigin));
        config.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
                                                       PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }
}

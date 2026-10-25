package com.credit.bladi.config.web;

import com.credit.bladi.config.security.JwtAuthFilter;
import com.credit.bladi.config.security.RateLimitFilter;
import java.time.Clock;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class WebConfig {
    @Bean
    public RequestContextInterceptor requestContextInterceptor(RequestContext requestContext) {
        return new RequestContextInterceptor(requestContext);
    }

    @Bean
    public RequestLoggerInterceptor requestLoggerInterceptor() {
        return new RequestLoggerInterceptor();
    }

    @Bean
    public FilterRegistrationBean<CachedBodyRequestFilter> cachedBodyRequestFilter(
        @Value("${security.request.max-body-size-bytes:1048576}") int maxBodySizeBytes,
        ObjectMapper objectMapper,
        MessageSource messageSource,
        LocaleResolver localeResolver,
        Clock clock
    ) {
        FilterRegistrationBean<CachedBodyRequestFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new CachedBodyRequestFilter(maxBodySizeBytes, objectMapper, messageSource,
            localeResolver, clock));
        registration.addUrlPatterns("/api/*");
        registration.setOrder(1);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtAuthFilterRegistration(JwtAuthFilter filter) {
        FilterRegistrationBean<JwtAuthFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<RateLimitFilter> rateLimitFilterRegistration(RateLimitFilter filter) {
        FilterRegistrationBean<RateLimitFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public WebMvcConfigurer webMvcConfigurer(RequestContextInterceptor requestContextInterceptor,
                                             RequestLoggerInterceptor requestLoggerInterceptor) {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(@NonNull InterceptorRegistry registry) {
                registry.addInterceptor(requestContextInterceptor).order(1);
                registry.addInterceptor(requestLoggerInterceptor).order(2);
            }
        };
    }
}

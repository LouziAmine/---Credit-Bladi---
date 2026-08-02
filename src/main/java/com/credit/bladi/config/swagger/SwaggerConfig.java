package com.credit.bladi.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.method.HandlerMethod;

@Configuration
public class SwaggerConfig {

    public static final String SECURITY_SCHEME_NAME = "Bearer_Authentication";

    private final String title;

    private final String description;

    private final String version;

    private final String basePackage;

    private final String contactName;

    private final String contactEmail;

    private final String serverUrl;

    public SwaggerConfig(

            @Value("${credit.bladi.swagger.config.title}") String title,
            @Value("${credit.bladi.swagger.config.description}") String description,
            @Value("${code.version}") String version,
            @Value("${credit.bladi.swagger.config.package:com.credit.bladi}") String basePackage,
            @Value("${credit.bladi.swagger.config.contact.name:developer}") String contactName,
            @Value("${credit.bladi.swagger.config.contact.email:developer@credit-bladi.ma}") String contactEmail,
            @Value("${credit.bladi.swagger.server.url:#{null}}") String serverUrl) {
        this.title = title;
        this.description = description;
        this.version = version;
        this.basePackage = basePackage;
        this.contactName = contactName;
        this.contactEmail = contactEmail;
        this.serverUrl = serverUrl;
    }

    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group("api")
                .displayName(title)
                .packagesToScan(basePackage)
                .addOperationCustomizer(customizer())
                .build();
    }

    @Bean
    public OpenAPI creditBladiOpenAPI() {
        Contact contact = new Contact()
                .name(contactName)
                .email(contactEmail);
        OpenAPI openAPI = new OpenAPI();
        if (Objects.nonNull(serverUrl)) {
            openAPI = openAPI.servers(List.of(new Server().url(serverUrl)));
        }
        return openAPI
                .info(new Info()
                        .title(title)
                        .description(description)
                        .version(version)
                        .contact(contact))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    private static final Set<String> RATE_LIMITED_METHODS = Set.of(
        "com.credit.bladi.controller.auth.AuthController#register",
        "com.credit.bladi.controller.auth.AuthController#login",
        "com.credit.bladi.controller.auth.AuthController#refresh",
        "com.credit.bladi.controller.auth.AuthController#logout",
        "com.credit.bladi.controller.simulation.SimulationController#simulate"
    );

    private OperationCustomizer customizer() {
        return (operation, handlerMethod) -> {
            if (Objects.nonNull(operation.getParameters())) {
                operation.getParameters().removeIf(p -> "Authorization".equals(p.getName()));
            }
            addRateLimitAndPayloadResponses(operation, handlerMethod);
            return operation;
        };
    }

    private void addRateLimitAndPayloadResponses(Operation operation, HandlerMethod handlerMethod) {
        Class<?> beanType = handlerMethod.getBeanType();
        if (beanType == null) {
            return;
        }
        addResponseIfAbsent(operation, "406", "None of the available representations matches the Accept header.");

        String key = beanType.getName() + "#" + handlerMethod.getMethod().getName();
        if (RATE_LIMITED_METHODS.contains(key)) {
            addResponseIfAbsent(operation, "429", "Too many requests. Please try again in 1 minute.");
        }
        boolean hasBody = handlerMethod.getMethodAnnotation(PostMapping.class) != null
            || handlerMethod.getMethodAnnotation(PatchMapping.class) != null;
        if (hasBody) {
            addResponseIfAbsent(operation, "413", "Request body exceeds the maximum allowed size.");
        }
    }

    private void addResponseIfAbsent(Operation operation, String code, String description) {
        ApiResponses responses = operation.getResponses();
        if (responses != null && !responses.containsKey(code)) {
            responses.addApiResponse(code, new ApiResponse().description(description));
        }
    }
}
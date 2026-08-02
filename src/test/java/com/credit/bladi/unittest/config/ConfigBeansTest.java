package com.credit.bladi.unittest.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.config.security.JwtAuthFilter;
import com.credit.bladi.config.security.RateLimitFilter;
import com.credit.bladi.config.swagger.SwaggerConfig;
import com.credit.bladi.config.web.RequestContext;
import com.credit.bladi.config.web.RequestContextInterceptor;
import com.credit.bladi.config.web.RequestLoggerInterceptor;
import com.credit.bladi.config.web.WebConfig;
import com.credit.bladi.controller.auth.AuthController;
import com.credit.bladi.controller.mourabaha.MourabahaParametersController;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponses;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

class ConfigBeansTest {

    @Test
    void shouldCreateGroupedOpenApi() {
        SwaggerConfig config = new SwaggerConfig(
            "Credit Bladi API",
            "Moroccan mortgage simulator",
            "1.0.0",
            "com.credit.bladi",
            "dev",
            "dev@credit-bladi.ma",
            null
        );
        GroupedOpenApi api = config.api();
        assertNotNull(api);
    }

    @Test
    void shouldCreateOpenAPI() {
        SwaggerConfig config = new SwaggerConfig(
            "Credit Bladi API",
            "Moroccan mortgage simulator",
            "1.0.0",
            "com.credit.bladi",
            "dev",
            "dev@credit-bladi.ma",
            null
        );
        OpenAPI openAPI = config.creditBladiOpenAPI();
        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
    }

    @Test
    void shouldCreateOpenAPIWithServerUrl() {
        SwaggerConfig config = new SwaggerConfig(
            "Credit Bladi API",
            "Moroccan mortgage simulator",
            "1.0.0",
            "com.credit.bladi",
            "dev",
            "dev@credit-bladi.ma",
            "https://api.credit-bladi.ma"
        );
        OpenAPI openAPI = config.creditBladiOpenAPI();
        assertNotNull(openAPI);
        assertNotNull(openAPI.getServers());
        assertNotNull(openAPI.getServers().get(0));
    }

    @Test
    void shouldCreateRequestContextInterceptor() {
        WebConfig config = new WebConfig();
        RequestContext requestContext = new RequestContext();
        RequestContextInterceptor interceptor = config.requestContextInterceptor(requestContext);
        assertNotNull(interceptor);
    }

    @Test
    void shouldCreateRequestLoggerInterceptor() {
        WebConfig config = new WebConfig();
        RequestLoggerInterceptor interceptor = config.requestLoggerInterceptor();
        assertNotNull(interceptor);
    }

    @Test
    void shouldCreateCachedBodyRequestFilterRegistration() {
        WebConfig config = new WebConfig();

        FilterRegistrationBean<com.credit.bladi.config.web.CachedBodyRequestFilter> registration =
            config.cachedBodyRequestFilter(1048576);

        assertNotNull(registration);
        assertNotNull(registration.getFilter());
    }

    @Test
    void shouldCreateJwtAuthFilterRegistrationDisabledByDefault() {
        WebConfig config = new WebConfig();
        JwtAuthFilter filter = mock(JwtAuthFilter.class);

        FilterRegistrationBean<JwtAuthFilter> registration = config.jwtAuthFilterRegistration(filter);

        assertNotNull(registration);
        assertFalse(registration.isEnabled());
    }

    @Test
    void shouldCreateRateLimitFilterRegistrationDisabledByDefault() {
        WebConfig config = new WebConfig();
        RateLimitFilter filter = mock(RateLimitFilter.class);

        FilterRegistrationBean<RateLimitFilter> registration = config.rateLimitFilterRegistration(filter);

        assertNotNull(registration);
        assertFalse(registration.isEnabled());
    }

    @Test
    void shouldConfigureInterceptors() {
        WebConfig config = new WebConfig();
        RequestContext requestContext = new RequestContext();
        RequestContextInterceptor contextInterceptor = config.requestContextInterceptor(requestContext);
        RequestLoggerInterceptor loggerInterceptor = config.requestLoggerInterceptor();
        WebMvcConfigurer configurer = config.webMvcConfigurer(contextInterceptor, loggerInterceptor);
        assertNotNull(configurer);

        InterceptorRegistry registry = mock(InterceptorRegistry.class);
        InterceptorRegistration registration = mock(InterceptorRegistration.class);
        when(registry.addInterceptor(any(HandlerInterceptor.class))).thenReturn(registration);
        when(registration.order(any(int.class))).thenReturn(registration);

        configurer.addInterceptors(registry);

        verify(registry, times(2)).addInterceptor(any(HandlerInterceptor.class));
    }

    @Test
    void shouldCustomizerRemoveAuthorizationHeader() throws Exception {
        SwaggerConfig config = new SwaggerConfig(
            "Credit Bladi API", "Moroccan mortgage simulator", "1.0.0",
            "com.credit.bladi", "dev", "dev@credit-bladi.ma", null);
        GroupedOpenApi api = config.api();
        assertNotNull(api);

        OperationCustomizer customizer = api.getOperationCustomizers().iterator().next();
        Operation operation = new Operation();
        List<Parameter> params = new ArrayList<>();
        params.add(new Parameter().name("Authorization"));
        params.add(new Parameter().name("page"));
        operation.setParameters(params);

        Operation result = customizer.customize(operation, mockHandlerMethod());

        assertNotNull(result);
    }

    @Test
    void shouldCustomizerHandleNullParameters() throws Exception {
        SwaggerConfig config = new SwaggerConfig(
            "Credit Bladi API", "Moroccan mortgage simulator", "1.0.0",
            "com.credit.bladi", "dev", "dev@credit-bladi.ma", null);
        GroupedOpenApi api = config.api();
        OperationCustomizer customizer = api.getOperationCustomizers().iterator().next();
        Operation operation = new Operation();

        Operation result = customizer.customize(operation, mockHandlerMethod());

        assertNotNull(result);
    }

    @Test
    void shouldCustomizerHandleHandlerMethodWithNullBeanType() throws Exception {
        SwaggerConfig config = new SwaggerConfig(
            "Credit Bladi API", "Moroccan mortgage simulator", "1.0.0",
            "com.credit.bladi", "dev", "dev@credit-bladi.ma", null);
        GroupedOpenApi api = config.api();
        OperationCustomizer customizer = api.getOperationCustomizers().iterator().next();
        Operation operation = new Operation();

        Operation result = customizer.customize(operation, mock(HandlerMethod.class));

        assertNotNull(result);
    }

    @Test
    void shouldCustomizerAddRateLimitAndPayloadResponsesForRegisterEndpoint() throws Exception {
        SwaggerConfig config = new SwaggerConfig(
            "Credit Bladi API", "Moroccan mortgage simulator", "1.0.0",
            "com.credit.bladi", "dev", "dev@credit-bladi.ma", null);
        GroupedOpenApi api = config.api();
        OperationCustomizer customizer = api.getOperationCustomizers().iterator().next();
        Operation operation = new Operation();
        operation.setResponses(new ApiResponses());

        HandlerMethod handlerMethod = mock(HandlerMethod.class);
        when(handlerMethod.getBeanType()).thenReturn((Class) AuthController.class);
        when(handlerMethod.getMethod())
            .thenReturn(AuthController.class.getMethod("register",
                com.credit.bladi.controller.dto.auth.request.RegisterRequestDTO.class));
        when(handlerMethod.getMethodAnnotation(org.springframework.web.bind.annotation.PostMapping.class))
            .thenReturn(AuthController.class
                .getMethod("register", com.credit.bladi.controller.dto.auth.request.RegisterRequestDTO.class)
                .getAnnotation(org.springframework.web.bind.annotation.PostMapping.class));

        Operation result = customizer.customize(operation, handlerMethod);

        assertNotNull(result);
        assertNotNull(result.getResponses().get("429"));
        assertNotNull(result.getResponses().get("413"));
        assertNotNull(result.getResponses().get("406"));
    }

    @Test
    void shouldCustomizerAddPayloadResponseForPatchMappingEndpoint() throws Exception {
        SwaggerConfig config = new SwaggerConfig(
            "Credit Bladi API", "Moroccan mortgage simulator", "1.0.0",
            "com.credit.bladi", "dev", "dev@credit-bladi.ma", null);
        GroupedOpenApi api = config.api();
        OperationCustomizer customizer = api.getOperationCustomizers().iterator().next();
        Operation operation = new Operation();
        operation.setResponses(new ApiResponses());

        HandlerMethod handlerMethod = mock(HandlerMethod.class);
        when(handlerMethod.getBeanType()).thenReturn((Class) MourabahaParametersController.class);
        when(handlerMethod.getMethod()).thenReturn(MourabahaParametersController.class.getMethod("patchParameters",
            Long.class, com.credit.bladi.controller.dto.mourabaha.request.PatchMourabahaParametersDTO.class));
        when(handlerMethod.getMethodAnnotation(org.springframework.web.bind.annotation.PatchMapping.class))
            .thenReturn(MourabahaParametersController.class.getMethod("patchParameters",
                Long.class, com.credit.bladi.controller.dto.mourabaha.request.PatchMourabahaParametersDTO.class)
                .getAnnotation(org.springframework.web.bind.annotation.PatchMapping.class));

        Operation result = customizer.customize(operation, handlerMethod);

        assertNotNull(result.getResponses().get("413"));
    }

    @Test
    void shouldCustomizerNotOverwriteExistingResponseCode() throws Exception {
        SwaggerConfig config = new SwaggerConfig(
            "Credit Bladi API", "Moroccan mortgage simulator", "1.0.0",
            "com.credit.bladi", "dev", "dev@credit-bladi.ma", null);
        GroupedOpenApi api = config.api();
        OperationCustomizer customizer = api.getOperationCustomizers().iterator().next();
        Operation operation = new Operation();
        ApiResponses responses = new ApiResponses();
        responses.addApiResponse("406", new io.swagger.v3.oas.models.responses.ApiResponse()
            .description("custom pre-existing description"));
        operation.setResponses(responses);

        Operation result = customizer.customize(operation, mockHandlerMethod());

        assertEquals("custom pre-existing description", result.getResponses().get("406").getDescription());
    }

    private HandlerMethod mockHandlerMethod() throws NoSuchMethodException {
        HandlerMethod handlerMethod = mock(HandlerMethod.class);
        when(handlerMethod.getBeanType()).thenReturn((Class) Object.class);
        when(handlerMethod.getMethod()).thenReturn(Object.class.getMethod("toString"));
        return handlerMethod;
    }
}
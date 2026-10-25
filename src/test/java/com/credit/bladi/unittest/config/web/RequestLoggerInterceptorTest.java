package com.credit.bladi.unittest.config.web;

import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_GET;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_POST;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.credit.bladi.config.web.RequestLoggerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestLoggerInterceptorTest {
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private RequestLoggerInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new RequestLoggerInterceptor();
    }

    @Test
    void shouldReturnTrueForNonActuatorPath() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/v1/mortgage-credit");
        when(request.getMethod()).thenReturn(CODE_POST);
        when(request.getQueryString()).thenReturn(null);

        boolean result = interceptor.preHandle(request, response, new Object());

        assertTrue(result);
    }

    @Test
    void shouldReturnTrueForActuatorPath() throws Exception {
        when(request.getRequestURI()).thenReturn("/actuator/health");

        boolean result = interceptor.preHandle(request, response, new Object());

        assertTrue(result);
    }

    @Test
    void shouldReturnTrueWhenUriIsNull() throws Exception {
        when(request.getRequestURI()).thenReturn(null);

        boolean result = interceptor.preHandle(request, response, new Object());

        assertTrue(result);
    }

    @Test
    void shouldReturnTrueForPathWithQueryString() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/v1/bam");
        when(request.getMethod()).thenReturn(CODE_GET);
        when(request.getQueryString()).thenReturn("page=1&size=10");

        boolean result = interceptor.preHandle(request, response, new Object());

        assertTrue(result);
    }

    @Test
    void shouldLogOutgoingResponseWithDurationWhenStartTimeRecorded() {
        when(request.getRequestURI()).thenReturn("/api/v1/mortgage-credit");
        when(request.getMethod()).thenReturn(CODE_POST);
        when(request.getAttribute("requestStartNanos")).thenReturn(System.nanoTime() - 1_000_000);
        when(response.getStatus()).thenReturn(200);

        interceptor.afterCompletion(request, response, new Object(), null);
    }

    @Test
    void shouldLogOutgoingResponseWithUnknownDurationWhenStartTimeMissing() {
        when(request.getRequestURI()).thenReturn("/api/v1/mortgage-credit");
        when(request.getMethod()).thenReturn(CODE_POST);
        when(request.getAttribute("requestStartNanos")).thenReturn(null);
        when(response.getStatus()).thenReturn(200);

        interceptor.afterCompletion(request, response, new Object(), null);
    }

    @Test
    void shouldSkipLoggingOutgoingResponseForActuatorPath() {
        when(request.getRequestURI()).thenReturn("/actuator/health");

        interceptor.afterCompletion(request, response, new Object(), null);
    }

    @Test
    void shouldLogOutgoingResponseWhenUriIsNull() {
        when(request.getRequestURI()).thenReturn(null);
        when(request.getMethod()).thenReturn(CODE_GET);
        when(request.getAttribute("requestStartNanos")).thenReturn(null);
        when(response.getStatus()).thenReturn(200);

        interceptor.afterCompletion(request, response, new Object(), null);
    }
}

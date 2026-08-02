package com.credit.bladi.unittest.config.security;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.config.security.SecurityExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

@ExtendWith(MockitoExtension.class)
class SecurityExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private SecurityExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new SecurityExceptionHandler();
    }

    @Test
    void shouldWrite403WhenHandleAccessDenied() throws Exception {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        handler.handle(request, response, new AccessDeniedException("denied"));

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(response).setContentType("application/json");
        String output = stringWriter.toString();
        assertTrue(output.contains("CB-403"));
    }

    @Test
    void shouldWrite401WhenCommenceAuthenticationException() throws Exception {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        handler.commence(request, response, new AuthenticationException("unauthorized") {});

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");
        String output = stringWriter.toString();
        assertTrue(output.contains("CB-401"));
    }

    @Test
    void shouldEchoValidClientRequestId() throws Exception {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
        when(request.getHeader("x-request-id")).thenReturn("client-abc-123");

        handler.handle(request, response, new AccessDeniedException("denied"));

        assertTrue(stringWriter.toString().contains("\"requestId\":\"client-abc-123\""));
    }

    @Test
    void shouldIgnoreInvalidClientRequestId() throws Exception {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
        when(request.getHeader("x-request-id")).thenReturn("forged\nlog\nline");

        handler.handle(request, response, new AccessDeniedException("denied"));

        assertTrue(!stringWriter.toString().contains("forged"));
    }
}
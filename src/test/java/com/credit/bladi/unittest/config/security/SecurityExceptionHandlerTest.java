package com.credit.bladi.unittest.config.security;

import static com.credit.bladi.unittest.mock.data.MockFiles.MIME_APPLICATION_JSON;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_CB_401;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_CB_403;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_UTF_8;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.config.security.SecurityExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Clock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import tools.jackson.databind.json.JsonMapper;

@ExtendWith(MockitoExtension.class)
class SecurityExceptionHandlerTest {
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private SecurityExceptionHandler handler;

    @BeforeEach
    void setUp() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("i18n/common/error", "i18n/common/security");
        messageSource.setDefaultEncoding(LABEL_UTF_8);
        handler = new SecurityExceptionHandler(messageSource, new AcceptHeaderLocaleResolver(),
            JsonMapper.builder().build(), Clock.systemUTC());
    }

    @Test
    void shouldWrite403WhenHandleAccessDenied() throws Exception {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        handler.handle(request, response, new AccessDeniedException("denied"));

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(response).setContentType(MIME_APPLICATION_JSON);
        String output = stringWriter.toString();
        assertTrue(output.contains(LABEL_CB_403));
    }

    @Test
    void shouldWrite401WhenCommenceAuthenticationException() throws Exception {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        handler.commence(request, response, new AuthenticationException("unauthorized") {});

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType(MIME_APPLICATION_JSON);
        String output = stringWriter.toString();
        assertTrue(output.contains(LABEL_CB_401));
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

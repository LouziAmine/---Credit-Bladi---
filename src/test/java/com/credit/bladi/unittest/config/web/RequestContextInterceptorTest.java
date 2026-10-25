package com.credit.bladi.unittest.config.web;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.config.web.RequestContext;
import com.credit.bladi.config.web.RequestContextInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestContextInterceptorTest {
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestContext requestContext;

    private RequestContextInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new RequestContextInterceptor(requestContext);
    }

    @Test
    void shouldSetRequestIdFromHeaderWhenPresent() throws Exception {
        when(request.getHeader("x-request-id")).thenReturn("header-req-id");

        boolean result = interceptor.preHandle(request, response, new Object());

        verify(requestContext).setRequestId("header-req-id");
        assertTrue(result);
    }

    @Test
    void shouldSkipSettingRequestIdWhenHeaderAbsent() throws Exception {
        when(request.getHeader("x-request-id")).thenReturn(null);

        boolean result = interceptor.preHandle(request, response, new Object());

        assertTrue(result);
    }

    @Test
    void shouldSkipSettingRequestIdWhenHeaderIsBlank() throws Exception {
        when(request.getHeader("x-request-id")).thenReturn("   ");

        boolean result = interceptor.preHandle(request, response, new Object());

        assertTrue(result);
    }

    @Test
    void shouldReturnTrueFromPreHandle() throws Exception {
        when(request.getHeader("x-request-id")).thenReturn(null);

        boolean result = interceptor.preHandle(request, response, new Object());

        assertTrue(result);
    }

    @Test
    void shouldClearMdcOnAfterCompletion() throws Exception {
        interceptor.afterCompletion(request, response, new Object(), null);
    }
}

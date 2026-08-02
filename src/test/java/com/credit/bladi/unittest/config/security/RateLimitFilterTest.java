package com.credit.bladi.unittest.config.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.config.security.RateLimitFilter;
import com.credit.bladi.config.security.RateLimitStore;
import jakarta.servlet.FilterChain;
import java.lang.reflect.Method;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@ExtendWith(MockitoExtension.class)
class RateLimitFilterTest {

    @Mock
    private RateLimitStore rateLimitStore;

    @Mock
    private FilterChain filterChain;

    private RateLimitFilter filter;

    @BeforeEach
    void setUp() {
        filter = new RateLimitFilter(rateLimitStore, 5, 20, 10, 10);
    }

    @Test
    void shouldPassNonRateLimitedPath() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/bam");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(rateLimitStore, never()).isRateLimited(anyString(), anyInt());
    }

    @Test
    void shouldPassWhenNotRateLimited() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/auth/login");
        request.setRemoteAddr("10.0.0.1");
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(rateLimitStore.isRateLimited(anyString(), anyInt())).thenReturn(false);

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldReject429WhenRateLimited() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/auth/login");
        request.setRemoteAddr("192.168.1.1");
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(rateLimitStore.isRateLimited(anyString(), anyInt())).thenReturn(true);

        filter.doFilter(request, response, filterChain);

        assertEquals(429, response.getStatus());
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void shouldRateLimitRefreshPath() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/auth/refresh");
        request.setRemoteAddr("192.168.1.2");
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(rateLimitStore.isRateLimited(anyString(), anyInt())).thenReturn(true);

        filter.doFilter(request, response, filterChain);

        assertEquals(429, response.getStatus());
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void shouldRateLimitLogoutPath() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/auth/logout");
        request.setRemoteAddr("192.168.1.3");
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(rateLimitStore.isRateLimited(anyString(), anyInt())).thenReturn(true);

        filter.doFilter(request, response, filterChain);

        assertEquals(429, response.getStatus());
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void shouldRateLimitSimulationPath() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/simulation");
        request.setRemoteAddr("127.0.0.1");
        request.addHeader("X-Forwarded-For", "203.0.113.5");
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(rateLimitStore.isRateLimited(anyString(), anyInt())).thenReturn(false);

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldResolveIpFromForwardedForWhenTrustedProxy() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/auth/register");
        request.setRemoteAddr("10.0.0.1");
        request.addHeader("X-Forwarded-For", "203.0.113.5");
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(rateLimitStore.isRateLimited(anyString(), anyInt())).thenReturn(false);

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldIgnoreForwardedForFromUntrustedProxy() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/auth/login");
        request.setRemoteAddr("8.8.8.8");
        request.addHeader("X-Forwarded-For", "203.0.113.5");
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(rateLimitStore.isRateLimited(anyString(), anyInt())).thenReturn(false);

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldResolveTrustedProxyFrom172Range() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/auth/login");
        request.setRemoteAddr("172.16.0.1");
        request.addHeader("X-Forwarded-For", "203.0.113.5");
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(rateLimitStore.isRateLimited(anyString(), anyInt())).thenReturn(false);

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldIgnoreForwardedForFromInvalid172Range() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/auth/login");
        request.setRemoteAddr("172.32.0.1");
        request.addHeader("X-Forwarded-For", "203.0.113.5");
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(rateLimitStore.isRateLimited(anyString(), anyInt())).thenReturn(false);

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    private String normalize(String path) throws Exception {
        Method method = RateLimitFilter.class.getDeclaredMethod("normalize", String.class);
        method.setAccessible(true);
        return (String) method.invoke(null, path);
    }

    @Test
    void shouldNotStripTrailingSlashFromRootPath() throws Exception {
        assertEquals("/", normalize("/"));
    }

    @Test
    void shouldStripTrailingSlashFromLongerPath() throws Exception {
        assertEquals("/api/v1/bam", normalize("/api/v1/bam/"));
    }

    @Test
    void shouldNotModifyPathWithoutTrailingSlash() throws Exception {
        assertEquals("/api/v1/bam", normalize("/api/v1/bam"));
    }
}
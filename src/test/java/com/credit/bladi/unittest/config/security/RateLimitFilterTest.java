package com.credit.bladi.unittest.config.security;

import static com.credit.bladi.unittest.mock.data.MockContacts.IP_10_0_0_1;
import static com.credit.bladi.unittest.mock.data.MockContacts.IP_127_0_0_1;
import static com.credit.bladi.unittest.mock.data.MockContacts.IP_172_16_0_1;
import static com.credit.bladi.unittest.mock.data.MockContacts.IP_172_32_0_1;
import static com.credit.bladi.unittest.mock.data.MockContacts.IP_192_168_1_1;
import static com.credit.bladi.unittest.mock.data.MockContacts.IP_192_168_1_2;
import static com.credit.bladi.unittest.mock.data.MockContacts.IP_192_168_1_3;
import static com.credit.bladi.unittest.mock.data.MockContacts.IP_203_0_113_5;
import static com.credit.bladi.unittest.mock.data.MockContacts.IP_8_8_8_8;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_X_FORWARDED_FOR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.config.security.RateLimitFilter;
import com.credit.bladi.config.security.RateLimitStore;
import jakarta.servlet.FilterChain;
import java.lang.reflect.Method;
import java.time.Clock;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.LocaleResolver;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@ExtendWith(MockitoExtension.class)
class RateLimitFilterTest {
    @Mock
    private RateLimitStore rateLimitStore;

    @Mock
    private MessageSource messageSource;

    @Mock
    private LocaleResolver localeResolver;

    @Mock
    private FilterChain filterChain;

    private RateLimitFilter filter;

    @BeforeEach
    void setUp() {
        lenient().when(localeResolver.resolveLocale(any())).thenReturn(Locale.ENGLISH);
        lenient().when(messageSource.getMessage(anyString(), any(), any(Locale.class)))
            .thenReturn("Too many requests. Please try again in 1 minute.");
        ObjectMapper objectMapper = JsonMapper.builder().build();
        filter = new RateLimitFilter(rateLimitStore, messageSource, localeResolver, objectMapper, 5, 20, 10, 10,
            Clock.systemUTC());
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
        request.setRemoteAddr(IP_10_0_0_1);
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(rateLimitStore.isRateLimited(anyString(), anyInt())).thenReturn(false);

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldReject429WhenRateLimited() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/auth/login");
        request.setRemoteAddr(IP_192_168_1_1);
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
        request.setRemoteAddr(IP_192_168_1_2);
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
        request.setRemoteAddr(IP_192_168_1_3);
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(rateLimitStore.isRateLimited(anyString(), anyInt())).thenReturn(true);

        filter.doFilter(request, response, filterChain);

        assertEquals(429, response.getStatus());
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void shouldRateLimitSimulationPath() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/mortgage-credit");
        request.setRemoteAddr(IP_127_0_0_1);
        request.addHeader(LABEL_X_FORWARDED_FOR, IP_203_0_113_5);
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(rateLimitStore.isRateLimited(anyString(), anyInt())).thenReturn(false);

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldResolveIpFromForwardedForWhenTrustedProxy() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/auth/register");
        request.setRemoteAddr(IP_10_0_0_1);
        request.addHeader(LABEL_X_FORWARDED_FOR, IP_203_0_113_5);
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(rateLimitStore.isRateLimited(anyString(), anyInt())).thenReturn(false);

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldIgnoreForwardedForFromUntrustedProxy() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/auth/login");
        request.setRemoteAddr(IP_8_8_8_8);
        request.addHeader(LABEL_X_FORWARDED_FOR, IP_203_0_113_5);
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(rateLimitStore.isRateLimited(anyString(), anyInt())).thenReturn(false);

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldResolveTrustedProxyFrom172Range() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/auth/login");
        request.setRemoteAddr(IP_172_16_0_1);
        request.addHeader(LABEL_X_FORWARDED_FOR, IP_203_0_113_5);
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(rateLimitStore.isRateLimited(anyString(), anyInt())).thenReturn(false);

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldIgnoreForwardedForFromInvalid172Range() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/auth/login");
        request.setRemoteAddr(IP_172_32_0_1);
        request.addHeader(LABEL_X_FORWARDED_FOR, IP_203_0_113_5);
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

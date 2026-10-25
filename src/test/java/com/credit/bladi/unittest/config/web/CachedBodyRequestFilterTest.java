package com.credit.bladi.unittest.config.web;

import static com.credit.bladi.unittest.mock.data.MockFiles.MIME_APPLICATION_JSON;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_UTF_8;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import com.credit.bladi.config.web.CachedBodyHttpServletRequest;
import com.credit.bladi.config.web.CachedBodyRequestFilter;
import jakarta.servlet.FilterChain;
import java.time.Clock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import tools.jackson.databind.json.JsonMapper;

@ExtendWith(MockitoExtension.class)
class CachedBodyRequestFilterTest {
    @Mock
    private FilterChain filterChain;

    private CachedBodyRequestFilter filter;

    @BeforeEach
    void setUp() {
        filter = newFilter(1_048_576);
    }

    @Test
    void shouldWrapNormalRequestInCachedBody() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContent("test body".getBytes());
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(any(CachedBodyHttpServletRequest.class), any());
    }

    @Test
    void shouldPassAlreadyCachedRequestAsIs() throws Exception {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setContent("already cached".getBytes());
        CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(mockRequest, 1_048_576);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(cachedRequest, response, filterChain);

        verify(filterChain).doFilter(cachedRequest, response);
    }

    @Test
    void shouldReturn413WhenBodyExceedsMaxSize() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContent("this body is too large".getBytes());
        MockHttpServletResponse response = new MockHttpServletResponse();
        CachedBodyRequestFilter tinyLimitFilter = newFilter(4);

        tinyLimitFilter.doFilter(request, response, filterChain);

        org.junit.jupiter.api.Assertions.assertEquals(413, response.getStatus());
        org.junit.jupiter.api.Assertions.assertTrue(
            response.getContentType().startsWith(MIME_APPLICATION_JSON));
        org.junit.jupiter.api.Assertions.assertTrue(response.getContentAsString().contains("CB-413"));
    }

    private static CachedBodyRequestFilter newFilter(int maxBodySizeBytes) {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("i18n/common/error");
        messageSource.setDefaultEncoding(LABEL_UTF_8);
        messageSource.setUseCodeAsDefaultMessage(true);
        return new CachedBodyRequestFilter(maxBodySizeBytes, JsonMapper.builder().build(), messageSource,
            new AcceptHeaderLocaleResolver(), Clock.systemUTC());
    }
}

package com.credit.bladi.unittest.config.web;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.config.web.CachedBodyHttpServletRequest;
import com.credit.bladi.config.web.RequestBodyTooLargeException;
import jakarta.servlet.ServletInputStream;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

class CachedBodyHttpServletRequestTest {
    @Test
    void shouldCacheRequestBody() throws Exception {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setContent("hello world".getBytes(StandardCharsets.UTF_8));

        CachedBodyHttpServletRequest cached = new CachedBodyHttpServletRequest(mockRequest, 1_048_576);
        ServletInputStream stream = cached.getInputStream();

        assertNotNull(stream);
        byte[] bytes = stream.readAllBytes();
        assertArrayEquals("hello world".getBytes(StandardCharsets.UTF_8), bytes);
    }

    @Test
    void shouldAllowReadingBodyMultipleTimes() throws Exception {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setContent("body content".getBytes(StandardCharsets.UTF_8));

        CachedBodyHttpServletRequest cached = new CachedBodyHttpServletRequest(mockRequest, 1_048_576);

        byte[] first = cached.getInputStream().readAllBytes();
        byte[] second = cached.getInputStream().readAllBytes();

        assertArrayEquals(first, second);
    }

    @Test
    void shouldReturnReaderWithUtf8Encoding() throws Exception {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setContent("test".getBytes(StandardCharsets.UTF_8));

        CachedBodyHttpServletRequest cached = new CachedBodyHttpServletRequest(mockRequest, 1_048_576);
        BufferedReader reader = cached.getReader();

        assertNotNull(reader);
        String line = reader.readLine();
        assertNotNull(line);
    }

    @Test
    void shouldStreamBeReadyByDefault() throws Exception {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setContent(new byte[0]);

        CachedBodyHttpServletRequest cached = new CachedBodyHttpServletRequest(mockRequest, 1_048_576);
        ServletInputStream stream = cached.getInputStream();

        assertTrue(stream.isReady());
    }

    @Test
    void shouldStreamBeFinishedWhenEmpty() throws Exception {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setContent(new byte[0]);

        CachedBodyHttpServletRequest cached = new CachedBodyHttpServletRequest(mockRequest, 1_048_576);
        ServletInputStream stream = cached.getInputStream();

        assertTrue(stream.isFinished());
    }

    @Test
    void shouldStreamNotBeFinishedWhenHasContent() throws Exception {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setContent("data".getBytes(StandardCharsets.UTF_8));

        CachedBodyHttpServletRequest cached = new CachedBodyHttpServletRequest(mockRequest, 1_048_576);
        ServletInputStream stream = cached.getInputStream();

        assertFalse(stream.isFinished());
    }

    @Test
    void shouldSetReadListenerWithoutError() throws Exception {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setContent(new byte[0]);

        CachedBodyHttpServletRequest cached = new CachedBodyHttpServletRequest(mockRequest, 1_048_576);
        ServletInputStream stream = cached.getInputStream();

        stream.setReadListener(null);
    }

    @Test
    void shouldReturnReaderWithExplicitEncoding() throws Exception {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setCharacterEncoding("ISO-8859-1");
        mockRequest.setContent("hello".getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));

        CachedBodyHttpServletRequest cached = new CachedBodyHttpServletRequest(mockRequest, 1_048_576);
        BufferedReader reader = cached.getReader();

        assertNotNull(reader);
    }

    @Test
    void shouldRejectBodyExceedingMaxSize() {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setContent("this body is too large".getBytes(StandardCharsets.UTF_8));

        assertThrows(RequestBodyTooLargeException.class,
            () -> new CachedBodyHttpServletRequest(mockRequest, 4));
    }
}

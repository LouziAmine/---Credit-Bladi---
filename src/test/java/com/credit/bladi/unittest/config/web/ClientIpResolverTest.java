package com.credit.bladi.unittest.config.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.credit.bladi.config.web.ClientIpResolver;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;

class ClientIpResolverTest {

    @Test
    void shouldReturnRemoteAddrWhenForwardedForHeaderAbsent() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("8.8.8.8");
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);

        assertEquals("8.8.8.8", ClientIpResolver.resolve(request));
    }

    @Test
    void shouldReturnRemoteAddrWhenNotTrustedProxy() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("8.8.8.8");
        when(request.getHeader("X-Forwarded-For")).thenReturn("1.2.3.4");

        assertEquals("8.8.8.8", ClientIpResolver.resolve(request));
    }

    @Test
    void shouldReturnClientIpFromForwardedForWhenRemoteAddrIsTrustedProxy() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("10.0.0.1");
        when(request.getHeader("X-Forwarded-For")).thenReturn("203.0.113.5, 10.0.0.1");

        assertEquals("203.0.113.5", ClientIpResolver.resolve(request));
    }

    @Test
    void shouldFallBackToRemoteAddrWhenAllForwardedForEntriesAreTrusted() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("10.0.0.1");
        when(request.getHeader("X-Forwarded-For")).thenReturn("10.0.0.2, 10.0.0.1");

        assertEquals("10.0.0.1", ClientIpResolver.resolve(request));
    }

    @Test
    void shouldReturnRemoteAddrWhenForwardedForHeaderIsBlank() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("8.8.8.8");
        when(request.getHeader("X-Forwarded-For")).thenReturn("   ");

        assertEquals("8.8.8.8", ClientIpResolver.resolve(request));
    }

    @Test
    void shouldSkipEmptySegmentsInForwardedForChain() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("10.0.0.1");
        when(request.getHeader("X-Forwarded-For")).thenReturn("203.0.113.5,,10.0.0.1");

        assertEquals("203.0.113.5", ClientIpResolver.resolve(request));
    }

    @Test
    void shouldNotTrustMalformed172Address() {
        assertFalse(ClientIpResolver.isTrustedProxy("172.abc.0.1"));
    }

    @Test
    void shouldNotTrust172AddressWithoutSecondOctet() {
        assertFalse(ClientIpResolver.isTrustedProxy("172."));
    }

    @Test
    void shouldNotTrust172AddressBelowPrivateRange() {
        assertFalse(ClientIpResolver.isTrustedProxy("172.15.0.1"));
    }

    @Test
    void shouldNotTrust172AddressAbovePrivateRange() {
        assertFalse(ClientIpResolver.isTrustedProxy("172.32.0.1"));
    }

    @Test
    void shouldTrustIpv4PrivateRanges() {
        assertTrue(ClientIpResolver.isTrustedProxy("10.0.0.1"));
        assertTrue(ClientIpResolver.isTrustedProxy("192.168.1.1"));
        assertTrue(ClientIpResolver.isTrustedProxy("172.16.0.1"));
        assertTrue(ClientIpResolver.isTrustedProxy("127.0.0.1"));
    }

    @Test
    void shouldNotTrustPublicIpv4() {
        assertFalse(ClientIpResolver.isTrustedProxy("8.8.8.8"));
    }

    @Test
    void shouldTrustIpv6Loopback() {
        assertTrue(ClientIpResolver.isTrustedProxy("::1"));
    }

    @Test
    void shouldTrustIpv6UniqueLocalAddresses() {
        assertTrue(ClientIpResolver.isTrustedProxy("fd00::1"));
        assertTrue(ClientIpResolver.isTrustedProxy("fc00::1"));
        assertTrue(ClientIpResolver.isTrustedProxy("FD12:3456:789a::1"));
    }

    @Test
    void shouldNotTrustPublicIpv6() {
        assertFalse(ClientIpResolver.isTrustedProxy("2001:4860:4860::8888"));
    }
}

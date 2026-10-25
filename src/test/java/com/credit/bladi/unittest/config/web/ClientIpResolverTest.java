package com.credit.bladi.unittest.config.web;

import static com.credit.bladi.unittest.mock.data.MockContacts.IP_10_0_0_1;
import static com.credit.bladi.unittest.mock.data.MockContacts.IP_127_0_0_1;
import static com.credit.bladi.unittest.mock.data.MockContacts.IP_172_15_0_1;
import static com.credit.bladi.unittest.mock.data.MockContacts.IP_172_16_0_1;
import static com.credit.bladi.unittest.mock.data.MockContacts.IP_172_32_0_1;
import static com.credit.bladi.unittest.mock.data.MockContacts.IP_192_168_1_1;
import static com.credit.bladi.unittest.mock.data.MockContacts.IP_1_2_3_4;
import static com.credit.bladi.unittest.mock.data.MockContacts.IP_203_0_113_5;
import static com.credit.bladi.unittest.mock.data.MockContacts.IP_8_8_8_8;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_X_FORWARDED_FOR;
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
        when(request.getRemoteAddr()).thenReturn(IP_8_8_8_8);
        when(request.getHeader(LABEL_X_FORWARDED_FOR)).thenReturn(null);

        assertEquals(IP_8_8_8_8, ClientIpResolver.resolve(request));
    }

    @Test
    void shouldReturnRemoteAddrWhenNotTrustedProxy() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn(IP_8_8_8_8);
        when(request.getHeader(LABEL_X_FORWARDED_FOR)).thenReturn(IP_1_2_3_4);

        assertEquals(IP_8_8_8_8, ClientIpResolver.resolve(request));
    }

    @Test
    void shouldReturnClientIpFromForwardedForWhenRemoteAddrIsTrustedProxy() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn(IP_10_0_0_1);
        when(request.getHeader(LABEL_X_FORWARDED_FOR)).thenReturn("203.0.113.5, 10.0.0.1");

        assertEquals(IP_203_0_113_5, ClientIpResolver.resolve(request));
    }

    @Test
    void shouldFallBackToRemoteAddrWhenAllForwardedForEntriesAreTrusted() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn(IP_10_0_0_1);
        when(request.getHeader(LABEL_X_FORWARDED_FOR)).thenReturn("10.0.0.2, 10.0.0.1");

        assertEquals(IP_10_0_0_1, ClientIpResolver.resolve(request));
    }

    @Test
    void shouldReturnRemoteAddrWhenForwardedForHeaderIsBlank() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn(IP_8_8_8_8);
        when(request.getHeader(LABEL_X_FORWARDED_FOR)).thenReturn("   ");

        assertEquals(IP_8_8_8_8, ClientIpResolver.resolve(request));
    }

    @Test
    void shouldSkipEmptySegmentsInForwardedForChain() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn(IP_10_0_0_1);
        when(request.getHeader(LABEL_X_FORWARDED_FOR)).thenReturn("203.0.113.5,,10.0.0.1");

        assertEquals(IP_203_0_113_5, ClientIpResolver.resolve(request));
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
        assertFalse(ClientIpResolver.isTrustedProxy(IP_172_15_0_1));
    }

    @Test
    void shouldNotTrust172AddressAbovePrivateRange() {
        assertFalse(ClientIpResolver.isTrustedProxy(IP_172_32_0_1));
    }

    @Test
    void shouldTrustIpv4PrivateRanges() {
        assertTrue(ClientIpResolver.isTrustedProxy(IP_10_0_0_1));
        assertTrue(ClientIpResolver.isTrustedProxy(IP_192_168_1_1));
        assertTrue(ClientIpResolver.isTrustedProxy(IP_172_16_0_1));
        assertTrue(ClientIpResolver.isTrustedProxy(IP_127_0_0_1));
    }

    @Test
    void shouldNotTrustPublicIpv4() {
        assertFalse(ClientIpResolver.isTrustedProxy(IP_8_8_8_8));
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

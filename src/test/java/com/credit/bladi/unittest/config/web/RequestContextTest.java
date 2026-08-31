package com.credit.bladi.unittest.config.web;

import static com.credit.bladi.unittest.mock.data.MockContacts.IP_10_0_0_1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.credit.bladi.config.web.RequestContext;
import org.junit.jupiter.api.Test;

class RequestContextTest {
    @Test
    void shouldHaveNonNullDefaultRequestId() {
        RequestContext context = new RequestContext();
        assertNotNull(context.getRequestId());
    }

    @Test
    void shouldUpdateRequestId() {
        RequestContext context = new RequestContext();
        context.setRequestId("custom-id-123");
        assertEquals("custom-id-123", context.getRequestId());
    }

    @Test
    void shouldUpdateClientIp() {
        RequestContext context = new RequestContext();
        context.setClientIp(IP_10_0_0_1);
        assertEquals(IP_10_0_0_1, context.getClientIp());
    }
}

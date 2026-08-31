package com.credit.bladi.unittest.service.notification.sms;

import static com.credit.bladi.unittest.mock.data.MockContacts.PHONE_0612345678;
import static com.credit.bladi.unittest.mock.data.MockContacts.PHONE_0698765432;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_BEARER_KEY;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_CREDITBLADI;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.credit.bladi.infrastructure.sms.FakeSmsEntry;
import com.credit.bladi.infrastructure.sms.FakeSmsInbox;
import java.util.List;
import org.junit.jupiter.api.Test;

class FakeSmsInboxTest {
    @Test
    void shouldReturnMostRecentEntryFirst() {
        FakeSmsInbox inbox = new FakeSmsInbox(java.time.Clock.systemUTC());

        inbox.add(PHONE_0612345678, LABEL_CREDITBLADI, "first", LABEL_BEARER_KEY);
        inbox.add(PHONE_0698765432, LABEL_CREDITBLADI, "second", LABEL_BEARER_KEY);

        List<FakeSmsEntry> entries = inbox.list();

        assertEquals(2, entries.size());
        assertEquals("second", entries.get(0).message());
        assertEquals("first", entries.get(1).message());
    }

    @Test
    void shouldCapAtOneHundredEntries() {
        FakeSmsInbox inbox = new FakeSmsInbox(java.time.Clock.systemUTC());

        for (int i = 0; i < 120; i++) {
            inbox.add(PHONE_0612345678, LABEL_CREDITBLADI, "message-" + i, LABEL_BEARER_KEY);
        }

        List<FakeSmsEntry> entries = inbox.list();

        assertEquals(100, entries.size());
        assertEquals("message-119", entries.get(0).message());
    }
}

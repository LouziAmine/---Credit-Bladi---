package com.credit.bladi.unittest.controller.devtools;

import static com.credit.bladi.unittest.mock.data.MockContacts.PHONE_0612345678;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_BEARER_KEY;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_BEARER_SECRET_KEY;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_CREDITBLADI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.controller.devtools.FakeSmsGatewayController;
import com.credit.bladi.infrastructure.sms.FakeSmsInbox;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class FakeSmsGatewayControllerTest {
    @Test
    void shouldStoreReceivedMessageInTheInbox() {
        FakeSmsInbox inbox = new FakeSmsInbox(java.time.Clock.systemUTC());
        FakeSmsGatewayController controller = new FakeSmsGatewayController(inbox, java.time.Clock.systemUTC());

        ResponseEntity<Void> response = controller.receive(
            new FakeSmsGatewayController.SendRequest(PHONE_0612345678, LABEL_CREDITBLADI, "Votre dossier a avancé."),
            LABEL_BEARER_SECRET_KEY);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, inbox.list().size());
        assertEquals(PHONE_0612345678, inbox.list().get(0).to());
        assertEquals(LABEL_BEARER_SECRET_KEY, inbox.list().get(0).authorizationHeader());
    }

    @Test
    void shouldRenderReceivedMessagesEscapedInTheInboxPage() {
        FakeSmsInbox inbox = new FakeSmsInbox(java.time.Clock.systemUTC());
        FakeSmsGatewayController controller = new FakeSmsGatewayController(inbox, java.time.Clock.systemUTC());
        controller.receive(new FakeSmsGatewayController.SendRequest(PHONE_0612345678, LABEL_CREDITBLADI,
            "<script>alert(1)</script>"), LABEL_BEARER_KEY);

        String html = controller.inbox();

        assertTrue(html.contains("&lt;script&gt;alert(1)&lt;/script&gt;"));
        assertTrue(html.contains(PHONE_0612345678));
    }

    @Test
    void shouldRenderEmptyInboxWithoutError() {
        FakeSmsGatewayController controller = new FakeSmsGatewayController(
            new FakeSmsInbox(java.time.Clock.systemUTC()), java.time.Clock.systemUTC());

        String html = controller.inbox();

        assertTrue(html.contains("Fake SMS Gateway"));
    }
}

package com.credit.bladi.unittest.service.servicing;

import static com.credit.bladi.unittest.mock.data.MockUsers.USER_MANAGER1;
import static org.mockito.Mockito.verify;

import com.credit.bladi.model.workflow.event.DemandCompletedEvent;
import com.credit.bladi.service.servicing.CreditFileEventListener;
import com.credit.bladi.service.servicing.ICreditFileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreditFileEventListenerTest {
    @Mock
    private ICreditFileService creditFileService;

    private CreditFileEventListener listener;

    @BeforeEach
    void setUp() {
        listener = new CreditFileEventListener(creditFileService);
    }

    @Test
    void onDemandCompleted_opensCreditFileForTheCompletedDemand() {
        listener.onDemandCompleted(new DemandCompletedEvent(1L, USER_MANAGER1));

        verify(creditFileService).openCreditFile(1L, USER_MANAGER1);
    }
}

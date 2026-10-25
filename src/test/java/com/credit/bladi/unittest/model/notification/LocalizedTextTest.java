package com.credit.bladi.unittest.model.notification;

import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_DEM_1;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.model.notification.LocalizedText;
import org.junit.jupiter.api.Test;

class LocalizedTextTest {
    @Test
    void shouldBeEqualWhenKeyAndArgumentsHaveTheSameContent() {
        LocalizedText first = LocalizedText.of("paymentReceived.body", LABEL_DEM_1, 3);
        LocalizedText second = LocalizedText.of("paymentReceived.body", LABEL_DEM_1, 3);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void shouldDifferWhenArgumentsDiffer() {
        assertNotEquals(LocalizedText.of("k", LABEL_DEM_1), LocalizedText.of("k", "DEM-2"));
    }

    @Test
    void shouldPrintArgumentsReadablyForLogs() {
        assertTrue(LocalizedText.of("k", LABEL_DEM_1, 3).toString().contains("[DEM-1, 3]"));
    }

    @Test
    void shouldNotBeAffectedByLaterChangesToTheCallersArray() {
        Object[] args = {LABEL_DEM_1};
        LocalizedText text = LocalizedText.of("k", args);
        args[0] = "tampered";

        assertArrayEquals(new Object[] {LABEL_DEM_1}, text.args());
    }

    @Test
    void shouldTreatNullArgumentsAsNone() {
        assertEquals(0, new LocalizedText("k", (Object[]) null).args().length);
    }
}

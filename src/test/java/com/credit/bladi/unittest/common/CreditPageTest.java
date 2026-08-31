package com.credit.bladi.unittest.common;

import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_10;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_OTHER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.common.CreditPage;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class CreditPageTest {
    @Test
    void shouldGetItemsAsEmptyWhenNotSet() {
        CreditPage<String> page = new CreditPage<>();
        List<String> items = page.getItems();
        assertTrue(items.isEmpty());
    }

    @Test
    void shouldGetItemsWhenSet() {
        CreditPage<String> page = new CreditPage<>();
        page.setItems(List.of("a", "b", "c"));

        List<String> items = page.getItems();

        assertEquals(3, items.size());
    }

    @Test
    void shouldBuildEmptyPage() {
        CreditPage<String> page = CreditPage.empty(1, 20);

        assertNotNull(page);
        assertTrue(page.getItems().isEmpty());
    }

    @Test
    void shouldSetAllFields() {
        CreditPage<String> page = new CreditPage<>();
        page.setItems(List.of("x", "y"));
        page.setCurrentPage(2);
        page.setLastPage(10);
        page.setSize(5);
        page.setTotal(50);

        assertEquals(2, page.getItems().size());
        assertEquals(2, page.getCurrentPage());
        assertEquals(10, page.getLastPage());
        assertEquals(5, page.getSize());
        assertEquals(50, page.getTotal());
    }

    @Test
    void shouldTestEqualsAndHashCode() {
        CreditPage<String> p1 = new CreditPage<>();
        p1.setCurrentPage(1);
        p1.setSize(10);
        p1.setItems(List.of("a"));

        CreditPage<String> p2 = new CreditPage<>();
        p2.setCurrentPage(1);
        p2.setSize(10);
        p2.setItems(List.of("a"));

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenSameTypeButDifferentFields() {
        CreditPage<String> p1 = new CreditPage<>();
        p1.setCurrentPage(1);

        CreditPage<String> p2 = new CreditPage<>();
        p2.setCurrentPage(2);

        assertFalse(p1.equals(p2));
    }

    @Test
    void shouldNotBeEqualToDifferentType() {
        CreditPage<String> page = new CreditPage<>();
        assertFalse(page.equals(USER_OTHER));
    }

    @Test
    void shouldMatchToString() {
        CreditPage<String> page = new CreditPage<>();
        page.setCurrentPage(1);
        page.setSize(10);
        page.setItems(Collections.emptyList());

        String str = page.toString();

        assertTrue(str.contains("1"));
        assertTrue(str.contains(TEXT_10));
    }
}

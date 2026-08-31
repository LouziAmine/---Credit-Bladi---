package com.credit.bladi.unittest.model.vo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.model.vo.DurationRange;
import org.junit.jupiter.api.Test;

class DurationRangeTest {
    @Test
    void shouldCreateWithValidRange() {
        DurationRange range = DurationRange.of(12, 300);
        assertEquals(12, range.minMonths());
        assertEquals(300, range.maxMonths());
    }

    @Test
    void shouldRejectNegativeMinMonths() {
        assertThrows(IllegalArgumentException.class, () -> DurationRange.of(-1, 300));
    }

    @Test
    void shouldRejectMaxLessThanMin() {
        assertThrows(IllegalArgumentException.class, () -> DurationRange.of(300, 12));
    }

    @Test
    void shouldAcceptEqualMinAndMax() {
        DurationRange range = DurationRange.of(12, 12);
        assertEquals(12, range.minMonths());
        assertEquals(12, range.maxMonths());
    }

    @Test
    void shouldContainMonthsWithinRange() {
        DurationRange range = DurationRange.of(12, 300);
        assertTrue(range.contains(12));
        assertTrue(range.contains(300));
        assertTrue(range.contains(150));
    }

    @Test
    void shouldNotContainMonthsOutsideRange() {
        DurationRange range = DurationRange.of(12, 300);
        assertFalse(range.contains(11));
        assertFalse(range.contains(301));
    }

    @Test
    void shouldToStringWithMonthsSuffix() {
        DurationRange range = DurationRange.of(12, 300);
        assertEquals("[12, 300] months", range.toString());
    }
}

package com.credit.bladi.unittest.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.model.vo.DurationRange;
import org.junit.jupiter.api.Test;

class DurationRangeTest {

    @Test
    void shouldCreateDurationRangeWithOf() {
        DurationRange range = DurationRange.of(12, 300);
        assertEquals(12, range.minMonths());
        assertEquals(300, range.maxMonths());
    }

    @Test
    void shouldContainValueWithinRange() {
        DurationRange range = DurationRange.of(12, 300);
        assertTrue(range.contains(120));
        assertTrue(range.contains(12));
        assertTrue(range.contains(300));
    }

    @Test
    void shouldNotContainValueOutsideRange() {
        DurationRange range = DurationRange.of(12, 300);
        assertFalse(range.contains(11));
        assertFalse(range.contains(301));
    }

    @Test
    void shouldAllowEqualMinAndMax() {
        DurationRange range = DurationRange.of(120, 120);
        assertEquals(120, range.minMonths());
        assertEquals(120, range.maxMonths());
        assertTrue(range.contains(120));
    }

    @Test
    void shouldThrowWhenMinIsNegative() {
        assertThrows(IllegalArgumentException.class,
            () -> DurationRange.of(-1, 300));
    }

    @Test
    void shouldThrowWhenMaxIsLessThanMin() {
        assertThrows(IllegalArgumentException.class,
            () -> DurationRange.of(120, 60));
    }

    @Test
    void shouldHaveToStringWithBrackets() {
        DurationRange range = DurationRange.of(12, 300);
        String str = range.toString();
        assertTrue(str.contains("12"));
        assertTrue(str.contains("300"));
        assertTrue(str.contains("months"));
    }

    @Test
    void shouldBeEqualWhenSameValues() {
        DurationRange r1 = DurationRange.of(12, 300);
        DurationRange r2 = DurationRange.of(12, 300);
        assertEquals(r1, r2);
    }
}
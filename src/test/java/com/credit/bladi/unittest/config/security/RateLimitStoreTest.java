package com.credit.bladi.unittest.config.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.config.security.RateLimitStore;
import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RateLimitStoreTest {

    private RateLimitStore store;

    @BeforeEach
    void setUp() {
        store = new RateLimitStore();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Deque<Long>> timestamps() throws Exception {
        Field field = RateLimitStore.class.getDeclaredField("timestamps");
        field.setAccessible(true);
        return (Map<String, Deque<Long>>) field.get(store);
    }

    @Test
    void shouldNotRateLimitBelowThreshold() {
        boolean limited = store.isRateLimited("key1", 5);
        assertFalse(limited);
    }

    @Test
    void shouldRateLimitWhenThresholdExceeded() {
        for (int i = 0; i < 3; i++) {
            store.isRateLimited("key2", 3);
        }
        boolean limited = store.isRateLimited("key2", 3);
        assertTrue(limited);
    }

    @Test
    void shouldTrackDifferentKeysIndependently() {
        for (int i = 0; i < 3; i++) {
            store.isRateLimited("keyA", 3);
        }
        boolean limitedA = store.isRateLimited("keyA", 3);
        boolean limitedB = store.isRateLimited("keyB", 3);

        assertTrue(limitedA);
        assertFalse(limitedB);
    }

    @Test
    void shouldEvictExpiredEntriesWithoutError() {
        store.isRateLimited("evict1", 10);
        store.isRateLimited("evict2", 10);

        store.evictExpiredEntries();
    }

    @Test
    void shouldAllowNewRequestAfterWindowExpires() {
        assertFalse(store.isRateLimited("fresh", 1));
        boolean second = store.isRateLimited("fresh", 1);
        assertTrue(second);
    }

    @Test
    void shouldPurgeStaleTimestampsBeforeCountingOnIsRateLimited() throws Exception {
        long staleTimestamp = System.currentTimeMillis() - 120_000L;
        Deque<Long> deque = new ArrayDeque<>();
        deque.addLast(staleTimestamp);
        timestamps().put("stale-key", deque);

        boolean limited = store.isRateLimited("stale-key", 1);

        assertFalse(limited);
    }

    @Test
    void shouldEvictExpiredEntryEntirelyWhenAllTimestampsStale() throws Exception {
        long staleTimestamp = System.currentTimeMillis() - 120_000L;
        Deque<Long> deque = new ArrayDeque<>();
        deque.addLast(staleTimestamp);
        timestamps().put("all-stale", deque);

        store.evictExpiredEntries();

        assertFalse(timestamps().containsKey("all-stale"));
    }

    @Test
    void shouldKeepEntryWithMixOfStaleAndFreshTimestamps() throws Exception {
        long staleTimestamp = System.currentTimeMillis() - 120_000L;
        long freshTimestamp = System.currentTimeMillis();
        Deque<Long> deque = new ArrayDeque<>();
        deque.addLast(staleTimestamp);
        deque.addLast(freshTimestamp);
        timestamps().put("mixed", deque);

        store.evictExpiredEntries();

        assertTrue(timestamps().containsKey("mixed"));
    }

    private static final class RaceSimulatingMap extends ConcurrentHashMap<String, Deque<Long>> {
        @Override
        public Deque<Long> compute(String key,
                BiFunction<? super String, ? super Deque<Long>, ? extends Deque<Long>> remappingFunction) {
            return super.compute(key, (k, ignoredCurrent) -> remappingFunction.apply(k, null));
        }
    }

    @Test
    void shouldHandleKeyConcurrentlyRemovedDuringEviction() throws Exception {
        RaceSimulatingMap raceMap = new RaceSimulatingMap();
        raceMap.put("raced-away", new ArrayDeque<>(java.util.List.of(System.currentTimeMillis())));
        Field field = RateLimitStore.class.getDeclaredField("timestamps");
        field.setAccessible(true);
        field.set(store, raceMap);

        store.evictExpiredEntries();

        assertFalse(raceMap.containsKey("raced-away"));
    }
}
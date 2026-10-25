package com.credit.bladi.infrastructure.sms;

import java.time.Clock;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class FakeSmsInbox {
    private static final int MAX_ENTRIES = 100;

    private final Deque<FakeSmsEntry> entries = new ArrayDeque<>();

    private final Clock clock;

    public FakeSmsInbox(Clock clock) {
        this.clock = clock;
    }

    public synchronized void add(String to, String from, String message, String authorizationHeader) {
        entries.addFirst(new FakeSmsEntry(to, from, message, authorizationHeader, clock.instant()));
        while (entries.size() > MAX_ENTRIES) {
            entries.removeLast();
        }
    }

    public synchronized List<FakeSmsEntry> list() {
        return List.copyOf(entries);
    }
}

package com.credit.bladi.infrastructure.sms;

import java.time.Instant;

public record FakeSmsEntry(String to, String from, String message, String authorizationHeader, Instant receivedAt) {
}

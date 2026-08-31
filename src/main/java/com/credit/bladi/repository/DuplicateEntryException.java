package com.credit.bladi.repository;

public class DuplicateEntryException extends RuntimeException {
    public DuplicateEntryException(Throwable cause) {
        super("Unique constraint violated", cause);
    }
}

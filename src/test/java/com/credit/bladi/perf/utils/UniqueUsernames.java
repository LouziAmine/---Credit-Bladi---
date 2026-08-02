package com.credit.bladi.perf.utils;

public final class UniqueUsernames {

    private static final long RUN_ID = System.currentTimeMillis();

    private UniqueUsernames() {
    }

    public static String suffix(long userId) {
        return RUN_ID + "." + userId;
    }
}
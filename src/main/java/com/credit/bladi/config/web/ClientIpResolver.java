package com.credit.bladi.config.web;

import jakarta.servlet.http.HttpServletRequest;

public final class ClientIpResolver {
    private ClientIpResolver() {}

    public static String resolve(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor == null || forwardedFor.isBlank() || !isTrustedProxy(remoteAddr)) {
            return remoteAddr;
        }
        String[] chain = forwardedFor.split(",");
        for (int i = chain.length - 1; i >= 0; i--) {
            String candidate = chain[i].trim();
            if (!candidate.isEmpty() && !isTrustedProxy(candidate)) {
                return candidate;
            }
        }
        return remoteAddr;
    }

    public static boolean isTrustedProxy(String ip) {
        if (ip.startsWith("10.") || ip.startsWith("192.168.")
                || ip.equals("127.0.0.1") || ip.equals("::1")) {
            return true;
        }
        if (ip.startsWith("172.")) {
            String[] parts = ip.split("\\.");
            if (parts.length >= 2) {
                try {
                    int second = Integer.parseInt(parts[1]);
                    return second >= 16 && second <= 31;
                } catch (NumberFormatException ignored) {
                    return false;
                }
            }
        }
        String lower = ip.toLowerCase(java.util.Locale.ROOT);
        return lower.startsWith("fc") || lower.startsWith("fd");
    }
}

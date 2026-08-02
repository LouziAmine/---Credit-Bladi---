package com.credit.bladi.integration.tools;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface IntegrationToken {

    String DEFAULT_TOKEN = "Bearer CANNOT_GET_TOKEN";
    int TIMEOUT = 15_000;
    String COOKIE_NAME = "cb_access_token";
    Logger LOGGER = LogManager.getLogger(IntegrationToken.class);

    static String getManagerToken(String baseUrl, String username, String password) {
        try {
            String body = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
            byte[] postData = body.getBytes(StandardCharsets.UTF_8);
            URL url = URI.create(baseUrl + "/api/v1/auth/login").toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setConnectTimeout(TIMEOUT);
            conn.setReadTimeout(TIMEOUT);
            try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                wr.write(postData);
            }
            conn.getResponseCode();

            Map<String, List<String>> headers = conn.getHeaderFields();
            List<String> cookies = headers.getOrDefault("Set-Cookie", List.of());
            for (String cookie : cookies) {
                if (cookie.contains(COOKIE_NAME + "=")) {
                    int start = cookie.indexOf(COOKIE_NAME + "=") + COOKIE_NAME.length() + 1;
                    int end = cookie.indexOf(";", start);
                    String token = (end == -1) ? cookie.substring(start) : cookie.substring(start, end);
                    return "Bearer " + token;
                }
            }
            LOGGER.error("Cookie '{}' not found in login response Set-Cookie headers", COOKIE_NAME);
            return DEFAULT_TOKEN;
        } catch (IOException e) {
            LOGGER.error("Cannot get manager token (check admin credentials in IntegrationTest): ", e);
            return DEFAULT_TOKEN;
        }
    }
}
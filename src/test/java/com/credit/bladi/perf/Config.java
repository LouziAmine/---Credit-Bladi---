package com.credit.bladi.perf;

public interface Config {

    String BASE_URL = System.getProperty("baseUrl", "http://localhost:8080");
    String ADMIN_USERNAME = System.getProperty("adminUsername", "admin");
    String ADMIN_PASSWORD = System.getProperty("adminPassword", "admin123");
}
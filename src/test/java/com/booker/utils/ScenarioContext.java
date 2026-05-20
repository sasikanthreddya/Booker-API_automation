package com.booker.utils;

import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * Shared state container injected via PicoContainer into step definitions.
 * Keeps scenarios isolated — each scenario gets a fresh instance.
 */
public class ScenarioContext {

    private final Map<String, Object> context = new HashMap<>();
    private Response lastResponse;
    private String authToken;
    private int lastBookingId;

    public void set(String key, Object value) {
        context.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) context.get(key);
    }

    public Response getLastResponse() { return lastResponse; }
    public void setLastResponse(Response response) { this.lastResponse = response; }

    public String getAuthToken() { return authToken; }
    public void setAuthToken(String token) { this.authToken = token; }

    public int getLastBookingId() { return lastBookingId; }
    public void setLastBookingId(int bookingId) { this.lastBookingId = bookingId; }
}

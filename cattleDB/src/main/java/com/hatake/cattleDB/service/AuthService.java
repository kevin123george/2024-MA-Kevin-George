package com.hatake.cattleDB.service;

import jakarta.servlet.http.HttpSession;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
@Service
public class AuthService {

    private final OkHttpClient client = new OkHttpClient();

    public void authenticate(HttpSession session) throws IOException {
        String email = "***************************e"; // Replace with actual username
        String password = "***************************!"; // Replace with actual password

        RequestBody body = new FormBody.Builder()
                .add("email", email)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url("https://devtrack.safectory.com/api/session")
                .post(body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Cookie", "JSESSIONID=node0ku1l2xpjcjqb167sczx0ys3ul40978.node0")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                System.out.println(responseBody);
                // Assume the session ID is part of the response, or set it appropriately
                session.setAttribute("AUTH_SESSION", responseBody);
            } else {
                throw new IOException("Unexpected code " + response);
            }
        }
    }


    public String makeAuthenticatedRequest(HttpSession session) throws IOException {
        String sessionId = (String) session.getAttribute("AUTH_SESSION");

        if (sessionId == null) {
            throw new IllegalStateException("No session ID found. Please authenticate first.");
        }

        Request request = new Request.Builder()
                .url("https://devtrack.safectory.com/api/beacons?responseType=json&delimiter=%2C") // Replace with actual URL
                .get()
                .addHeader("Cookie", "JSESSIONID=" + sessionId)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            } else {
                throw new IOException("Unexpected code " + response);
            }
        }
    }
}
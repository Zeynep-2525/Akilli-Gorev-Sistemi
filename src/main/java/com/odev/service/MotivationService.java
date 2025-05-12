package com.odev.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

@Service
public class MotivationService {

    private final RestTemplate restTemplate = new RestTemplate();

    public String getMotivationalQuote() {
        String apiUrl = "https://zenquotes.io/api/random";
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
            return response.getBody();
        } catch (Exception e) {
            return "Keep going! You're doing great!";
        }
    }
}

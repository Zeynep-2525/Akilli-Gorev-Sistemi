package com.odev.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MotivationService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getMotivationalQuote() {
        String apiUrl = "https://zenquotes.io/api/random";
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
            String json = response.getBody();

            JsonNode root = objectMapper.readTree(json);
            if (root.isArray() && root.size() > 0) {
                JsonNode quoteNode = root.get(0);
                String quote = quoteNode.get("q").asText();
                String author = quoteNode.get("a").asText();
                return "\"" + quote + "\" — " + author;
            }

            return "Devam et! Harika gidiyorsun!";
        } catch (Exception e) {
            return "Devam et! Harika gidiyorsun!";
        }
    }
}


package com.odev.taskmanager.service.impl;

import com.odev.taskmanager.service.MotivationService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONArray;
import org.json.JSONObject;

@Service
public class MotivationServiceImpl implements MotivationService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String getDailyQuote() {
        String url = "https://zenquotes.io/api/random";
        String response = restTemplate.getForObject(url, String.class);

        if (response != null) {
            JSONArray jsonArray = new JSONArray(response);
            JSONObject quoteObj = jsonArray.getJSONObject(0);
            return quoteObj.getString("q") + " — " + quoteObj.getString("a");
        }

        return "Stay motivated!";
    }
    
    @Override
    public String getMotivationalQuote() {
        return getDailyQuote(); // veya farklı bir söz dönebilirsin
    }
}

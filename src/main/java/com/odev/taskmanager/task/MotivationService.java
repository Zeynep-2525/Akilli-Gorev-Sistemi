package com.odev.taskmanager.task;

import org.springframework.stereotype.Service;

@Service
public class MotivationService {

    public String getDailyMotivation() {
        // Şimdilik sabit metin döndürüyoruz
        return "Her yeni gün, yeni bir başlangıçtır!";
    }
}

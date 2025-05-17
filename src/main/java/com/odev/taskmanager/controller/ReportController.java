package com.odev.taskmanager.controller;

import java.io.IOException;  
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.odev.service.PdfReportGenerator;
import com.odev.taskmanager.model.Task;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final PdfReportGenerator pdfReportGenerator;

    public ReportController(PdfReportGenerator pdfReportGenerator) {
        this.pdfReportGenerator = pdfReportGenerator;
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generateReport(@RequestBody List<Task> tasks) {
        try {
            pdfReportGenerator.generatePdf(tasks);
            return ResponseEntity.ok("PDF başarıyla oluşturuldu.");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("PDF oluşturulurken hata oluştu: " + e.getMessage());
        }
    }
}

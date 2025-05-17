package com.odev.taskmanager.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
public class PdfController {

    @GetMapping("/pdf")
    public ResponseEntity<InputStreamResource> getPdf() throws IOException {
        File pdfFile = new File("reports/task_report.pdf");
        if (!pdfFile.exists()) {
            return ResponseEntity.notFound().build();
        }

        FileInputStream fis = new FileInputStream(pdfFile);
        InputStreamResource resource = new InputStreamResource(fis);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=task_report.pdf")  
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdfFile.length())
                .body(resource);
    }
}

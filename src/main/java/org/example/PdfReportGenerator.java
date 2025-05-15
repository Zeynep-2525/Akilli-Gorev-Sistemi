package org.example;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PdfReportGenerator {

    private final PdfFont boldFont;
    private final PdfFont normalFont;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm - dd.MM.yyyy");





    private static final Color[] PRIORITY_COLORS = {
            ColorConstants.PINK,      // HIGH
            ColorConstants.MAGENTA,   // MEDIUM
            ColorConstants.BLUE      // LOW
    };

    private int getPriorityIndex(PriorityLevel priority) {
        return switch (priority) {
            case High -> 0;
            case Medium -> 1;
            case Low -> 2;
        };
    }



    public PdfReportGenerator() throws IOException {
        this.boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        this.normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
    }

    public void generatePdf(List<Task> tasks) throws IOException {

        File reportsDir = new File("reports");
        if (!reportsDir.exists()) {
            boolean created = reportsDir.mkdirs();
            if (!created) {
                throw new IOException("reports klasörü oluşturulamadı: " + reportsDir.getAbsolutePath());
            }
        }


        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new FileOutputStream("reports/task_report.pdf")));
             Document document = new Document(pdfDoc, PageSize.A4)) {

            document.setMargins(40, 40, 40, 40);


            addCoverPage(document);


            document.add(new AreaBreak());
            addTasksTable(document, tasks);


            document.add(new AreaBreak());
            addStatistics(document, tasks);
        }
    }

    private void addCoverPage(Document document) {
        // Ana Başlık
        document.add(new Paragraph("Task Management Report")
                .setFont(boldFont)
                .setFontSize(30)
                .setFontColor(ColorConstants.BLACK)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(250));


        // Tarih Bilgisi
        document.add(new Paragraph("Generated: " + LocalDate.now().format(DATE_FORMATTER))
                .setFont(normalFont)
                .setFontSize(12)
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginTop(430));
    }

    private void addTasksTable(Document document, List<Task> tasks) {
        // Başlık
        document.add(new Paragraph("Task Overview")
                .setFont(boldFont)
                .setFontSize(22)
                .setFontColor(ColorConstants.BLACK)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

        Table table = new Table(UnitValue.createPercentArray(new float[]{15, 45, 15, 15,10}));
        table.setWidth(UnitValue.createPercentValue(100));
        table.setFixedLayout();


        table.addHeaderCell(createColoredHeaderCell("Task"));
        table.addHeaderCell(createColoredHeaderCell("Description"));
        table.addHeaderCell(createColoredHeaderCell("Due Date"));
        table.addHeaderCell(createColoredHeaderCell("Status"));
        table.addHeaderCell(createColoredHeaderCell("Priority"));



        tasks.forEach(task -> {
            LocalDateTime dateTime = null;
            String dateStr = task.getDate();

            if (dateStr != null && !dateStr.trim().isEmpty()) {
                try {
                    // Önce tam tarih-saat formatını dene (HH:mm - dd.MM.yyyy)
                    try {
                        dateTime = LocalDateTime.parse(dateStr, DATE_TIME_FORMATTER);
                    } catch (DateTimeParseException e) {
                        // Eğer saat bilgisi yoksa, sadece tarih olarak parse et
                        LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER);
                        dateTime = date.atStartOfDay(); // Tarihi 00:00 saatine ayarla
                    }
                } catch (DateTimeParseException e) {
                    System.err.println("Geçersiz tarih formatı: " + dateStr);
                }
            }

            table.addCell(createTaskCell(task.getName(), getPriorityIndex(task.getPriority())));
            table.addCell(createDescriptionCell(task.getDetails()));
            table.addCell(createDateTimeCell(dateTime));
            table.addCell(createStatusCell(task.getStatus(), true));
            table.addCell(createPriorityCell(task.getPriority()));
        });


        document.add(table);
    }

    private void addStatistics(Document document, List<Task> tasks) {
        // Ana Başlık
        document.add(new Paragraph("Task Analytics")
                .setFont(boldFont)
                .setFontSize(22)
                .setFontColor(ColorConstants.BLACK)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(30));

        // 1. Dağılım Grafiği
        addDistributionChart(document, tasks);

        // 2. Durum Detayları
        addStatusDetails(document, tasks);
    }

    private void addDistributionChart(Document document, List<Task> tasks) {
        document.add(new Paragraph("Task Distribution")
                .setFont(boldFont)
                .setFontSize(18)
                .setFontColor(ColorConstants.BLACK)
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginBottom(15));

        Table chart = new Table(UnitValue.createPercentArray(new float[]{20, 60, 20}));
        chart.setWidth(UnitValue.createPercentValue(90));

        long total = tasks.size();
        String[] statuses = {"Completed", "Pending", "Delayed"};

        for (String status : statuses) {
            long count = countByStatus(tasks, status.toLowerCase());
            double percent = calculatePercentage(count, total);

            chart.addCell(new Cell()
                    .add(new Paragraph(status)
                            .setFont(boldFont)
                            .setFontColor(ColorConstants.BLACK)) // Statü renginde yazı
                    .setPadding(5));

            chart.addCell(new Cell()
                    .add(new Paragraph(String.format("%.1f%%", percent))
                            .setFont(normalFont)
                            .setFontColor(ColorConstants.BLACK))
                    .setBackgroundColor(ColorConstants.WHITE)
                    .setTextAlignment(TextAlignment.CENTER));

            chart.addCell(new Cell()
                    .add(new Paragraph(count + " tasks")
                            .setFont(normalFont)
                            .setFontColor(ColorConstants.BLACK))
                    .setTextAlignment(TextAlignment.RIGHT));
        }

        document.add(chart);
    }

    private void addStatusDetails(Document document, List<Task> tasks) {
        document.add(new Paragraph("Status Breakdown")
                .setFont(boldFont)
                .setFontSize(18)
                .setFontColor(ColorConstants.BLACK)
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginTop(30)
                .setMarginBottom(15));

        String[] statusOrder = {"Completed", "Pendıng", "Delayed"};

        for (int i = 0; i < statusOrder.length; i++) {
            final int statusIndex = i;
            List<Task> filteredTasks = filterByStatus(tasks, statusOrder[i]);
            if (filteredTasks.isEmpty()) continue;

            document.add(new Paragraph(statusOrder[i].toUpperCase())
                    .setFont(boldFont)
                    .setFontSize(16)
                    .setFontColor(ColorConstants.BLACK)
                    .setPadding(8)
                    .setMarginTop(20));

            Table detailTable = new Table(UnitValue.createPercentArray(new float[]{15, 45, 15, 15, 10}));
            detailTable.setWidth(UnitValue.createPercentValue(100));

            detailTable.addHeaderCell(createColoredHeaderCell("Task"));
            detailTable.addHeaderCell(createColoredHeaderCell("Description"));
            detailTable.addHeaderCell(createColoredHeaderCell("Due Date"));
            detailTable.addHeaderCell(createColoredHeaderCell("Status"));
            detailTable.addHeaderCell(createColoredHeaderCell("Priority"));


            filteredTasks.forEach(task -> {

                LocalDateTime dateTime = null;
                String dateStr = task.getDate();

                if (dateStr != null && !dateStr.trim().isEmpty()) {
                    try {
                        try {
                            dateTime = LocalDateTime.parse(dateStr, DATE_TIME_FORMATTER);
                        } catch (DateTimeParseException e) {
                            LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER);
                            dateTime = date.atStartOfDay();
                        }
                    } catch (DateTimeParseException e) {
                        System.err.println("Invalid date format: '" + dateStr + "'. Expected format: " +
                                DATE_FORMATTER + " or " + DATE_TIME_FORMATTER);

                    }
                }

                detailTable.addCell(createTaskCell(task.getName(), statusIndex)); // Siyah
                detailTable.addCell(createDescriptionCell(task.getDetails()));
                detailTable.addCell(createDateTimeCell(dateTime));
                detailTable.addCell(createStatusCell(task.getStatus(),false));
                detailTable.addCell(createPriorityCell(task.getPriority()));
            });

            document.add(detailTable);
        }
    }

    // Yardımcı Metodlar
    private Cell createStatusCell(String status, boolean colored) {
        Color statusColor = ColorConstants.BLACK;
        if (colored) {
            statusColor = "delayed".equalsIgnoreCase(status)
                    ? ColorConstants.RED
                    : ColorConstants.BLACK;
        }


        return new Cell()
                .add(new Paragraph(status)
                        .setFont(normalFont)
                        .setFontSize(10)
                        .setFontColor(statusColor))
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(5);
    }




    private Cell createColoredHeaderCell(String text) {
        return new Cell()
                .add(new Paragraph(text)
                        .setFont(boldFont)
                        .setFontSize(12)
                        .setFontColor(ColorConstants.BLACK))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
    }

    private Cell createTaskCell(String text, int priorityIndex) {
        Color textColor = (priorityIndex >= 0 && priorityIndex < PRIORITY_COLORS.length)
                ? PRIORITY_COLORS[priorityIndex]
                : ColorConstants.BLACK;

        return new Cell()
                .add(new Paragraph(text)
                        .setFont(boldFont)
                        .setFontSize(10)
                        .setFontColor(textColor))
                .setBackgroundColor(ColorConstants.WHITE)
                .setPadding(5);
    }







    private Cell createDescriptionCell(String text) {
        String displayText = text.length() > 150 ?
                text.substring(0, 150) + "..." : text;

        return new Cell()
                .add(new Paragraph(displayText)
                        .setFont(normalFont)
                        .setFontSize(9)
                        .setFixedLeading(11)
                        .setFontColor(ColorConstants.BLACK))
                .setPadding(6)
                .setTextAlignment(TextAlignment.LEFT);
    }



    private Cell createPriorityCell(PriorityLevel priority ) {
        return new Cell()
                .add(new Paragraph(priority.toString())
                        .setFont(normalFont)
                        .setFontSize(10)
                        .setFontColor(ColorConstants.BLACK))
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(5);
    }


    private Cell createDateTimeCell(LocalDateTime dateTime) {
        String text = (dateTime != null) ? dateTime.format(DATE_TIME_FORMATTER) : "-";

        return new Cell()
                .add(new Paragraph(text)
                        .setFont(normalFont)
                        .setFontSize(10)
                        .setFontColor(dateTime != null ? ColorConstants.BLACK : ColorConstants.GRAY))
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(5);
    }


    private long countByStatus(List<Task> tasks, String status) {
        return tasks.stream()
                .filter(t -> t.getStatus().equalsIgnoreCase(status))
                .count();
    }

    private List<Task> filterByStatus(List<Task> tasks, String status) {
        return tasks.stream()
                .filter(t -> t.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }

    private double calculatePercentage(long count, long total) {
        return total == 0 ? 0 : (count * 100.0 / total);
    }
}










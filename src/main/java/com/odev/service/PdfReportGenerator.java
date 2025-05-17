package com.odev.service;


import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.DeviceCmyk;
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
import com.odev.taskmanager.model.TaskPriority;
import com.odev.taskmanager.model.Task;

import org.springframework.stereotype.Service;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PdfReportGenerator {

    private final PdfFont boldFont;
    private final PdfFont normalFont;
    static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm - dd.MM.yyyy");





    private static final Color[] PRIORITY_COLORS = {
            
            new DeviceCmyk(0.00f, 0.60f, 1.00f, 0.00f),

            
            new DeviceCmyk(0.80f, 0, 0.40f, 0.10f),

            
            new DeviceCmyk(1.00f, 0.20f, 0.00f, 0.10f)
    };

    private int getPriorityIndex(TaskPriority priority) {
        return switch (priority) {
            case HIGH -> 0;
            case MEDIUM -> 1;
            case LOW -> 2;
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



        Table table = new Table(UnitValue.createPercentArray(new float[]{5,15, 50, 15, 15}));
        table.setWidth(UnitValue.createPercentValue(100));
        table.setFixedLayout();

        table.addHeaderCell(createColoredHeaderCell("    "));
        table.addHeaderCell(createColoredHeaderCell("Task"));
        table.addHeaderCell(createColoredHeaderCell("Description"));
        table.addHeaderCell(createColoredHeaderCell("Due Date"));
        table.addHeaderCell(createColoredHeaderCell("Priority"));



        tasks.forEach(task -> {
        	LocalDateTime dueDateTime = task.getDueDate(); 
        	boolean isDelayed = false;

        	if (dueDateTime != null) {
        	    
        	    if (!task.isCompleted() && dueDateTime.isBefore(LocalDateTime.now())) {
        	        isDelayed = true;
        	    }
        	}

            table.addCell(createStatusCell(task.isCompleted(), isDelayed));
            table.addCell(createTaskCell(task.getTitle(), getPriorityIndex(task.getPriority())));
            table.addCell(createDescriptionCell(task.getDescription()));
            table.addCell(createDateTimeCell(dueDateTime));
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

        
        addDistributionChart(document, tasks);

        
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
                            .setFontColor(ColorConstants.BLACK)) 
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

        String[] statusOrder = {"Completed", "Pending", "Delayed"};

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

            Table detailTable = new Table(UnitValue.createPercentArray(new float[]{20, 50, 15, 15}));
            detailTable.setWidth(UnitValue.createPercentValue(100));

            detailTable.addHeaderCell(createColoredHeaderCell("Task"));
            detailTable.addHeaderCell(createColoredHeaderCell("Description"));
            detailTable.addHeaderCell(createColoredHeaderCell("Due Date"));
            detailTable.addHeaderCell(createColoredHeaderCell("Priority"));


            filteredTasks.forEach(task -> {

            	LocalDateTime dueDateTime = task.getDueDate();

                detailTable.addCell(createTaskCell(task.getTitle(), statusIndex)); 
                detailTable.addCell(createDescriptionCell(task.getDescription()));
                detailTable.addCell(createDateTimeCell(dueDateTime));
                detailTable.addCell(createPriorityCell(task.getPriority()));
            });

            document.add(detailTable);
        }
    }

    // Yardımcı Metodlar
     Cell createStatusCell(boolean isCompleted, boolean isDelayed) {
        String statusText;
        Color statusColor;


        if (isDelayed) {
            statusText = "!";
            statusColor = ColorConstants.RED;
        } else {
            statusText = " ";
            statusColor = ColorConstants.BLACK;
        }


        return new Cell()
                .add(new Paragraph(statusText)
                        .setFont(normalFont)
                        .setFontSize(30)
                        .setFontColor(statusColor))
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(5);
    }

     boolean isTaskDelayed(Task task) {
    	    if (task.isCompleted() || task.getDueDate() == null) {
    	        return false;
    	    }
    	    return task.getDueDate().isBefore(LocalDateTime.now());
    	}








     Cell createColoredHeaderCell(String text) {
        return new Cell()
                .add(new Paragraph(text)
                        .setFont(boldFont)
                        .setFontSize(12)
                        .setFontColor(ColorConstants.BLACK))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
    }

     Cell createTaskCell(String text, int priorityIndex) {
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







     Cell createDescriptionCell(String text) {
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



     Cell createPriorityCell(TaskPriority priority ) {
        return new Cell()
                .add(new Paragraph(priority.toString())
                        .setFont(normalFont)
                        .setFontSize(10)
                        .setFontColor(ColorConstants.BLACK))
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(5);
    }


     Cell createDateTimeCell(LocalDateTime dateTime) {
        String text = (dateTime != null) ? dateTime.format(DATE_TIME_FORMATTER) : "-";

        return new Cell()
                .add(new Paragraph(text)
                        .setFont(normalFont)
                        .setFontSize(10)
                        .setFontColor(dateTime != null ? ColorConstants.BLACK : ColorConstants.GRAY))
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(5);
    }


     long countByStatus(List<Task> tasks, String status) {
        return tasks.stream()
                .filter(t -> {
                    boolean isCompleted = t.isCompleted();
                    boolean isDelayed = !isCompleted && isTaskDelayed(t);

                    return switch (status.toLowerCase()) {
                        case "completed" -> isCompleted;
                        case "delayed" -> isDelayed;
                        default -> !isCompleted && !isDelayed; 
                    };
                })
                .count();
    }

     List<Task> filterByStatus(List<Task> tasks, String status) {
        return tasks.stream()
                .filter(t -> {
                    boolean isCompleted = t.isCompleted();
                    boolean isDelayed = !isCompleted && isTaskDelayed(t);

                    return switch (status.toLowerCase()) {
                        case "completed" -> isCompleted;
                        case "delayed" -> isDelayed;
                        default -> !isCompleted && !isDelayed; 
                    };
                })
                .collect(Collectors.toList());
    }

     double calculatePercentage(long count, long total) {
        return total == 0 ? 0 : (count * 100.0 / total);
    }
}


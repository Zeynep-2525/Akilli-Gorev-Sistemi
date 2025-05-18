
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

    // Fontlar hazır, Bold ve Normal - PDF'de kullanacağız
    private final PdfFont boldFont;
    private final PdfFont normalFont;
    static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm - dd.MM.yyyy");

    // Öncelik renkleri, göze hoş görünmesi için ayarlandı
    private static final Color[] PRIORITY_COLORS = {
            new DeviceCmyk(0.00f, 0.60f, 1.00f, 0.00f),  // yüksek öncelik için mavi tonları
            new DeviceCmyk(0.80f, 0, 0.40f, 0.10f),      // orta öncelik için turuncu gibi
            new DeviceCmyk(1.00f, 0.20f, 0.00f, 0.10f)   // düşük öncelik için kırmızımsı
    };

    // Öncelik seviyesine göre renk dizisindeki yeri alıyoruz
    private int getPriorityIndex(TaskPriority priority) {
        return switch (priority) {
            case HIGH -> 0;
            case MEDIUM -> 1;
            case LOW -> 2;
        };
    }

    // Constructor - fontları başlatıyoruz
    public PdfReportGenerator() throws IOException {
        this.boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        this.normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
    }

    // PDF oluşturma işlemi buradan başlıyor
    public void generatePdf(List<Task> tasks) throws IOException {
        // Klasör yoksa oluşturuyoruz, olmazsa hata fırlatır
        File reportsDir = new File("reports");
        if (!reportsDir.exists()) {
            boolean created = reportsDir.mkdirs();
            if (!created) {
                throw new IOException("reports klasörü oluşturulamadı");
            }
        }

        // PDF dosyasını açıyoruz, sayfa boyutu A4
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new FileOutputStream("reports/task_report.pdf")));
             Document document = new Document(pdfDoc, PageSize.A4)) {

            document.setMargins(40, 40, 40, 40);

            // İlk sayfa kapak sayfası
            addCoverPage(document);

            // Sonraki sayfada görev tablosu var
            document.add(new AreaBreak());
            addTasksTable(document, tasks);

            // Son olarak da istatistikler
            document.add(new AreaBreak());
            addStatistics(document, tasks);
        }
    }

    // Kapak sayfasını hazırlıyoruz, fazla bir şey yok
    private void addCoverPage(Document document) {
        document.add(new Paragraph("Task Management Report")
                .setFont(boldFont)
                .setFontSize(30)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(250));

        document.add(new Paragraph("Generated: " + LocalDate.now().format(DATE_FORMATTER))
                .setFont(normalFont)
                .setFontSize(12)
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginTop(430));
    }

    // Görev listesini tablo şeklinde gösteriyoruz
    private void addTasksTable(Document document, List<Task> tasks) {
        document.add(new Paragraph("Task Overview")
                .setFont(boldFont)
                .setFontSize(22)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

        Table table = new Table(UnitValue.createPercentArray(new float[]{5, 15, 50, 15, 15}));
        table.setWidth(UnitValue.createPercentValue(100));

        // Tablo başlıkları ekleniyor
        table.addHeaderCell(createColoredHeaderCell("    "));
        table.addHeaderCell(createColoredHeaderCell("Task"));
        table.addHeaderCell(createColoredHeaderCell("Description"));
        table.addHeaderCell(createColoredHeaderCell("Due Date"));
        table.addHeaderCell(createColoredHeaderCell("Priority"));

        // Görevleri tabloya satır satır ekliyoruz
        for (Task task : tasks) {
            LocalDateTime dueDateTime = task.getDueDate();
            boolean isDelayed = false;

            // Gecikmiş görevleri kırmızı yapabilmek için kontrol
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
        }

        document.add(table);
    }

    // İstatistik kısmını oluşturuyoruz
    private void addStatistics(Document document, List<Task> tasks) {
        document.add(new Paragraph("Task Analytics")
                .setFont(boldFont)
                .setFontSize(22)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(30));

        addDistributionChart(document, tasks);
        addStatusDetails(document, tasks);
    }

    // Görev durumlarını tablo şeklinde gösteriyoruz
    private void addDistributionChart(Document document, List<Task> tasks) {
        document.add(new Paragraph("Task Distribution")
                .setFont(boldFont)
                .setFontSize(18)
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
                            .setFont(boldFont)))
                    .setPadding(5);

            chart.addCell(new Cell()
                    .add(new Paragraph(String.format("%.1f%%", percent))
                            .setFont(normalFont))
                    .setTextAlignment(TextAlignment.CENTER));

            chart.addCell(new Cell()
                    .add(new Paragraph(count + " tasks")
                            .setFont(normalFont))
                    .setTextAlignment(TextAlignment.RIGHT));
        }

        document.add(chart);
    }

    // Duruma göre görevlerin detaylarını yazıyoruz
    private void addStatusDetails(Document document, List<Task> tasks) {
        document.add(new Paragraph("Status Breakdown")
                .setFont(boldFont)
                .setFontSize(18)
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginTop(30)
                .setMarginBottom(15));

        String[] statusOrder = {"Completed", "Pending", "Delayed"};

        for (String status : statusOrder) {
            List<Task> filteredTasks = filterByStatus(tasks, status);
            if (filteredTasks.isEmpty()) continue;

            document.add(new Paragraph(status.toUpperCase())
                    .setFont(boldFont)
                    .setFontSize(16)
                    .setMarginTop(20));

            Table detailTable = new Table(UnitValue.createPercentArray(new float[]{20, 50, 15, 15}));
            detailTable.setWidth(UnitValue.createPercentValue(100));

            detailTable.addHeaderCell(createColoredHeaderCell("Task"));
            detailTable.addHeaderCell(createColoredHeaderCell("Description"));
            detailTable.addHeaderCell(createColoredHeaderCell("Due Date"));
            detailTable.addHeaderCell(createColoredHeaderCell("Priority"));

            for (Task task : filteredTasks) {
                LocalDateTime dueDateTime = task.getDueDate();

                detailTable.addCell(createTaskCell(task.getTitle(), getPriorityIndex(task.getPriority())));
                detailTable.addCell(createDescriptionCell(task.getDescription()));
                detailTable.addCell(createDateTimeCell(dueDateTime));
                detailTable.addCell(createPriorityCell(task.getPriority()));
            }

            document.add(detailTable);
        }
    }

    // Status hücresi oluşturuyoruz, gecikme varsa kırmızı ünlem koyuyoruz
    Cell createStatusCell(boolean isCompleted, boolean isDelayed) {
        String statusText = isDelayed ? "!" : " ";
        Color statusColor = isDelayed ? ColorConstants.RED : ColorConstants.BLACK;

        return new Cell()
                .add(new Paragraph(statusText)
                        .setFont(normalFont)
                        .setFontSize(30)
                        .setFontColor(statusColor))
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(5);
    }

    // Gecikme kontrolü
    boolean isTaskDelayed(Task task) {
        if (task.isCompleted() || task.getDueDate() == null) {
            return false;
        }
        return task.getDueDate().isBefore(LocalDateTime.now());
    }

    // Tablo başlık hücresi, arka plan koyduk biraz belirgin olsun diye
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

    // Görev başlığı hücresi, öncelik rengini uyguluyoruz
    Cell createTaskCell(String text, int priorityIndex) {
        Color textColor = (priorityIndex >= 0 && priorityIndex < PRIORITY_COLORS.length)
                ? PRIORITY_COLORS[priorityIndex]
                : ColorConstants.BLACK;

        return new Cell()
                .add(new Paragraph(text)
                        .setFont(boldFont)
                        .setFontSize(10)
                        .setFontColor(textColor))
                .setPadding(5);
    }

    // Açıklama hücresi, çok uzunsa kısaltıyoruz
    Cell createDescriptionCell(String text) {
        String displayText = text.length() > 150 ? text.substring(0, 150) + "..." : text;

        return new Cell()
                .add(new Paragraph(displayText)
                        .setFont(normalFont)
                        .setFontSize(9))
                .setPadding(6)
                .setTextAlignment(TextAlignment.LEFT);
    }

    // Öncelik hücresi, yazı tipi standart
    Cell createPriorityCell(TaskPriority priority) {
        return new Cell()
                .add(new Paragraph(priority.toString())
                        .setFont(normalFont)
                        .setFontSize(10))
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(5);
    }

    // Tarih ve saat hücresi, boşsa "-" koyuyoruz
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

    // Duruma göre görev sayısını sayıyoruz
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

    // Duruma göre görevleri filtreliyoruz
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

    // Basit yüzde hesaplama fonksiyonu
    double calculatePercentage(long count, long total) {
        return total == 0 ? 0 : (count * 100.0 / total);
    }
}



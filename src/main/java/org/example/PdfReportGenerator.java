package org.example;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.kernel.colors.ColorConstants;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class PdfReportGenerator {

    private final PdfFont boldFont;
    private final PdfFont normalFont;

    public PdfReportGenerator() throws IOException {
        this.boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        this.normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
    }

    public void generatePdf(List<Task> tasks) throws IOException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new FileOutputStream("task_report.pdf")));
             Document document = new Document(pdfDoc, PageSize.A4)) {

            // Sayfa 1: Kapak
            createCoverPage(pdfDoc, document);

            // Sayfa 2: Görev Tablosu
            document.add(new AreaBreak());
            addTasksTable(document, tasks);

            // Sayfa 3: İstatistikler
            document.add(new AreaBreak());
            addStatisticsPage(document, tasks);

            System.out.println("PDF raporu başarıyla oluşturuldu (3 sayfa).");
        }
    }

    private void createCoverPage(PdfDocument pdfDoc, Document document) {
        document.add(new Paragraph("Task Management Report")
                .setFont(boldFont)
                .setFontSize(30)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(250));

        document.add(new Paragraph("Generated: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                .setFont(normalFont)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginTop(430));
    }

    private void addTasksTable(Document document, List<Task> tasks) {
        document.add(new Paragraph("Pending Tasks")
                .setFont(boldFont)
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

        Table table = new Table(new float[]{2, 4, 2, 2});
        float pageWidth = document.getPdfDocument().getDefaultPageSize().getWidth() - document.getLeftMargin() - document.getRightMargin();
        table.setWidth(pageWidth);


        // Başlıklar
        table.addHeaderCell(createHeaderCell("Task"));
        table.addHeaderCell(createHeaderCell("Description"));
        table.addHeaderCell(createHeaderCell("Due Date"));
        table.addHeaderCell(createHeaderCell("Status"));

        // Satırlar
        tasks.stream()
                .filter(t -> !"completed".equalsIgnoreCase(t.getStatus()))
                .forEach(task -> {
                    table.addCell(createCell(task.getName()));
                    table.addCell(createCell(task.getDetails()));
                    table.addCell(createCell(task.getDate()));
                    table.addCell(createCell(task.getStatus()));
                });

        document.add(table);
    }

    private void addStatisticsPage(Document document, List<Task> tasks) {
        document.add(new Paragraph("Task Statistics")
                .setFont(boldFont)
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

        // Statü listesi (düzeltilmiş)
        String[] statuses = {"completed", "pendıng", "delayed"};

        for (String status : statuses) {
            addStatusTable(document, tasks, status);
        }
    }

    private void addStatusTable(Document document, List<Task> tasks, String status) {
        List<Task> filtered = tasks.stream()
                .filter(t -> status.equalsIgnoreCase(t.getStatus()))
                .collect(Collectors.toList());

        if (filtered.isEmpty()) return;

        document.add(new Paragraph(status.toUpperCase())
                .setFont(boldFont)
                .setFontSize(14)
                .setMarginTop(15));

        Table table = new Table(new float[]{2, 4, 2});
        float pageWidth = document.getPdfDocument().getDefaultPageSize().getWidth() - document.getLeftMargin() - document.getRightMargin();
        table.setWidth(pageWidth);


        table.addHeaderCell(createHeaderCell("Task"));
        table.addHeaderCell(createHeaderCell("Description"));
        table.addHeaderCell(createHeaderCell("Due Date"));

        for (Task task : filtered) {
            table.addCell(createCell(task.getName()));
            table.addCell(createCell(task.getDetails()));
            table.addCell(createCell(task.getDate()));
        }

        document.add(table);
    }

    private Cell createHeaderCell(String text) {
        Cell cell = new Cell().add(new Paragraph(text).setFont(boldFont));
        cell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
        return cell;
    }

    private Cell createCell(String text) {
        return new Cell().add(new Paragraph(text)
                .setFont(normalFont)
                .setTextAlignment(TextAlignment.LEFT)
                .setMargin(2));
    }
}










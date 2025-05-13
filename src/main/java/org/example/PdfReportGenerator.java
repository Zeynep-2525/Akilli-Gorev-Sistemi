package org.example;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.io.font.constants.StandardFonts;

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
             Document document = new Document(pdfDoc)) {

            // Page 1: Cover Page
            createCoverPage(pdfDoc);

            // Page 2: Tasks Table
            document.add(new AreaBreak());
            addTasksTable(document, tasks);

            // Page 3: Statistics
            document.add(new AreaBreak());
            addStatisticsPage(document, tasks);

            System.out.println("PDF report generated successfully with 3 pages");
        }
    }

    private void createCoverPage(PdfDocument pdfDoc) {
        PdfPage page = pdfDoc.addNewPage(PageSize.A4);
        PdfCanvas canvas = new PdfCanvas(page);
        Rectangle pageSize = page.getPageSize();

        // Title
        canvas.beginText()
                .setFontAndSize(boldFont, 30)
                .moveText(pageSize.getWidth()/2 - 170, pageSize.getHeight()/2)
                .showText("Task Management Report")
                .endText();

        // Date
        canvas.beginText()
                .setFontAndSize(normalFont, 10)
                .moveText(pageSize.getWidth() - 150, 36)
                .showText("Generated: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                .endText();
    }

    private void addTasksTable(Document document, List<Task> tasks) {
        document.add(new Paragraph("Pending Tasks")
                .setFont(boldFont)
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

        Table table = new Table(4);
        table.setWidth(document.getPdfDocument().getDefaultPageSize().getWidth() - 72);

        // Headers
        table.addHeaderCell(createHeaderCell("Task"));
        table.addHeaderCell(createHeaderCell("Description"));
        table.addHeaderCell(createHeaderCell("Due Date"));
        table.addHeaderCell(createHeaderCell("Status"));

        // Rows
        tasks.stream()
                .filter(t -> !t.getStatus().equalsIgnoreCase("completed"))
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

        // Status summary tables
        String[] statuses = {"completed", "pendıng", "delayed"};
        for (String status : statuses) {
            addStatusTable(document, tasks, status);
        }
    }

    private void addStatusTable(Document document, List<Task> tasks, String status) {
        document.add(new Paragraph(status.toUpperCase())
                .setFont(boldFont)
                .setFontSize(14)
                .setMarginTop(15));

        Table table = new Table(3);
        table.setWidth(document.getPdfDocument().getDefaultPageSize().getWidth() - 72);

        // Headers
        table.addHeaderCell(createHeaderCell("Task"));
        table.addHeaderCell(createHeaderCell("Description"));
        table.addHeaderCell(createHeaderCell("Due Date"));

        // Rows
        tasks.stream()
                .filter(t -> t.getStatus().equalsIgnoreCase(status))
                .forEach(task -> {
                    table.addCell(createCell(task.getName()));
                    table.addCell(createCell(task.getDetails()));
                    table.addCell(createCell(task.getDate()));
                });

        document.add(table);
    }

    private Cell createHeaderCell(String text) {
        return new Cell().add(new Paragraph(text).setFont(boldFont));
    }

    private Cell createCell(String text) {
        return new Cell().add(new Paragraph(text).setFont(normalFont));
    }
}









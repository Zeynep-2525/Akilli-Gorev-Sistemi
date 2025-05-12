package org.example;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class PdfReportGenerator {

    public void generatePdf() {
        //Görev servisi oluşturulup görevler alındı.
        TaskServiceTest service = new TaskServiceTest();
        List<Task> allTasks = service.getTasks();

        try {
            //gorev_raporu adında yeni bir dosya oluşturuldu.
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("task_report.pdf"));
            document.open();

            //Metin fontları oluşturuldu.
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 11);

            //Birinci sayfadaki başlık ve zaman yazıldı, yerleri ayarlandi.
            Rectangle pageSize = document.getPageSize();
            float pageWidth = pageSize.getWidth();
            float pageHeight = pageSize.getHeight();

            PdfContentByte canvas = writer.getDirectContent();

            ColumnText.showTextAligned(
                    canvas,
                    Element.ALIGN_CENTER,
                    new Phrase("Task Management Report", new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD)),
                    pageWidth / 2,
                    pageHeight / 2,
                    0
            );

            ColumnText.showTextAligned(
                    canvas,
                    Element.ALIGN_RIGHT,
                    new Phrase("Creation Date: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                            new Font(Font.FontFamily.HELVETICA, 10)),
                    pageWidth - 36,
                    36,
                    0
            );

            document.newPage();

            /*İkinci sayfada yapılmayan görevler isimleri,açıklamaları,zamanları
             ve durumları yazılcak şekilde tablo olluşturuldu.*/
            Paragraph title = new Paragraph("Tasks to be Done", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2f, 4f, 3f, 2f});
            table.addCell(new PdfPCell(new Phrase("Task", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Details", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Date", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Status", headerFont)));

            List<Task> pendingTasks = allTasks.stream()
                    .filter(t -> !t.getStatus().equalsIgnoreCase("completed"))
                    .collect(Collectors.toList());

            for (Task t : pendingTasks) {
                table.addCell(new PdfPCell(new Phrase(t.getName(), normalFont)));
                table.addCell(new PdfPCell(new Phrase(t.getDescription(), normalFont)));
                table.addCell(new PdfPCell(new Phrase(t.getTime(), normalFont)));
                table.addCell(new PdfPCell(new Phrase(t.getStatus(), normalFont)));
            }

            document.add(table);

            /* üçüncü sayfada görevlerin istatistikleri çıkarıldı.
            Yapılan görevler,bekleyen görevler ve tamamlanmış görevler kendi aralarında sıralanarak tablo oluşturuldu.*/
            document.newPage();
            Paragraph statTitle = new Paragraph("Task Status Statistics", titleFont);
            statTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(statTitle);
            document.add(Chunk.NEWLINE);

            for (String status : new String[]{"completed", "pending", "delayed"}) {
                Paragraph statusTitle = new Paragraph(status.toUpperCase(), new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD));
                statusTitle.setSpacingBefore(10f);
                document.add(statusTitle);
                document.add(Chunk.NEWLINE);

                PdfPTable statusTable = new PdfPTable(3);
                statusTable.setWidthPercentage(100);
                statusTable.setWidths(new float[]{2f, 5f, 3f});
                statusTable.addCell(new PdfPCell(new Phrase("Task", headerFont)));
                statusTable.addCell(new PdfPCell(new Phrase("Details", headerFont)));
                statusTable.addCell(new PdfPCell(new Phrase("Date", headerFont)));

                List<Task> filtered = allTasks.stream()
                        .filter(t -> t.getStatus().equalsIgnoreCase(status))
                        .collect(Collectors.toList());

                for (Task t : filtered) {
                    statusTable.addCell(new PdfPCell(new Phrase(t.getName(), normalFont)));
                    statusTable.addCell(new PdfPCell(new Phrase(t.getDescription(), normalFont)));
                    statusTable.addCell(new PdfPCell(new Phrase(t.getTime(), normalFont)));
                }

                document.add(statusTable);
                document.add(Chunk.NEWLINE);
            }

            document.close();
            System.out.println("PDF created successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



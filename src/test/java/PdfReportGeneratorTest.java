import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import org.example.PdfReportGenerator;
import org.example.PriorityLevel;
import org.junit.jupiter.api.Test;
import org.example.Task;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PdfReportGeneratorTest {


    //PdfReportGenerator sınıfında oluşturulan pdf'in oluşup oluşmadığını ve doluluğunu test eder.
    @Test
    public void testPdfGeneration_createsFile() throws IOException {

        List<Task> tasks = Arrays.asList(
                new Task("Task 1", "Description 1", "2025-05-20", "pending", PriorityLevel.Medium),
                new Task("Task 2", "Description 2", "2025-05-21", "completed",PriorityLevel.Low),
                new Task("Task 3", "Description 3", "2025-05-22", "delayed",PriorityLevel.High)

                );


        PdfReportGenerator generator = new PdfReportGenerator();
        generator.generatePdf(tasks);


        File file = new File("reports/task_report.pdf");
        assertTrue(file.exists(), "PDF dosyası oluşturulmamış.");
        assertTrue(file.length() > 0, "PDF dosyası boş.");
    }

    //PdfReportGenerator sınıfında oluşturulan pdf'in doğru sayfa sayısı kadar oluşup oluşmadığını test eder.
    @Test
    public void testPdfHasThreePages() throws IOException {
        PdfReader reader = new PdfReader("reports/task_report.pdf");
        PdfDocument pdfDoc = new PdfDocument(reader);
        assertEquals(3, pdfDoc.getNumberOfPages(), "PDF 3 sayfa içermeli.");
        pdfDoc.close();
    }

    /* Yapılacaklar sayfasındaki pending ve delayed görevlerin başlık, açıklama ve tarih bilgilerinin PDF içeriğinde bulunduğu kontrol edilir.
      Completed görevlerin hiçbir bilgisinin başlık, açıklama, tarih PDF'in bu sayfasında yer almadığı kontrol edilir.*/
    @Test
    public void testOnlyPendingTasksAreIncludedInTable() throws IOException {
        List<Task> tasks = Arrays.asList(
                new Task("Meeting", "Weekly team sync", "10:00 - 15.06.2024", "pending",PriorityLevel.High),
                new Task("Presentation", "Product demo", "11:30 - 18.06.2024", "completed",PriorityLevel.High),
                new Task("Data Entry", "Customer records update", "09:00 - 14.06.2024", "delayed",PriorityLevel.High)
        );
        PdfReportGenerator generator = new PdfReportGenerator();
        generator.generatePdf(tasks);

        PdfReader reader = new PdfReader("reports/task_report.pdf");
        PdfDocument pdfDoc = new PdfDocument(reader);


        String content = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(2));


        assertTrue(content.contains("Meeting"), "Pending görev bulunamadı.");
        assertTrue(content.contains("Weekly team sync"), "Pending açıklama eksik veya hatalı.");
        assertTrue(content.contains("10:00 - 15.06.2024"), "Pending tarih eksik veya yanlış formatta.");

        assertTrue(content.contains("Data Entry"),"Delayed görev bulunamadı");
        assertTrue(content.contains("Customer records update"),"Delayed açıklama eksik veya hatalı.");
        assertTrue(content.contains("09:00 - 14.06.2024"),"Delayed tarih eksik veya yanlış formatta.");

        assertFalse(content.contains("Presentation"), "Completed görev tabloya dahil edilmiş.");
        assertFalse(content.contains("Product demo"),"Completed açıklama eksik veya hatalı." );
        assertFalse(content.contains("11:30 - 18.06.2024"), "Completed tarih eksik veya yanlış formatta.");



        pdfDoc.close();
    }


    //İstatistik sayfası kontrol edilir.
    @Test
    public void testStatisticsIncludeEachStatus() throws IOException {
        PdfReader reader = new PdfReader("reports/task_report.pdf");
        PdfDocument pdfDoc = new PdfDocument(reader);

        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= pdfDoc.getNumberOfPages(); i++) {
            sb.append(PdfTextExtractor.getTextFromPage(pdfDoc.getPage(i)));
        }
        String content = sb.toString();


        assertTrue(content.contains("COMPLETED"));
        assertTrue(content.contains("PENDING"));
        assertTrue(content.contains("DELAYED"));
        pdfDoc.close();
    }



}




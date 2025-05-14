

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import org.example.PdfReportGenerator;
import org.junit.jupiter.api.Test;
import org.example.Task;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PdfReportGeneratorTest {



    @Test
    public void testPdfGeneration_createsFile() throws IOException {
        // Hazırlık: örnek görevler
        List<Task> tasks = Arrays.asList(
                new Task("Task 1", "Description 1", "2025-05-20", "pending"),
                new Task("Task 2", "Description 2", "2025-05-21", "completed")
        );

        // Raporu oluştur
        PdfReportGenerator generator = new PdfReportGenerator();
        generator.generatePdf(tasks);

        // Dosya kontrolü
        File file = new File("reports/task_report.pdf");
        assertTrue(file.exists(), "PDF dosyası oluşturulmamış.");
        assertTrue(file.length() > 0, "PDF dosyası boş.");
    }

    @Test
    public void testPdfHasThreePages() throws IOException {
        PdfReader reader = new PdfReader("reports/task_report.pdf");
        PdfDocument pdfDoc = new PdfDocument(reader);
        assertEquals(3, pdfDoc.getNumberOfPages(), "PDF 3 sayfa içermeli.");
        pdfDoc.close();
    }

    @Test
    public void testOnlyPendingTasksAreIncludedInTable() throws IOException {
        List<Task> tasks = Arrays.asList(
                new Task("Pending Task", "Do this soon", "2025-05-20", "pending"),
                new Task("Completed Task", "Already done", "2025-05-10", "completed")
        );
        PdfReportGenerator generator = new PdfReportGenerator();
        generator.generatePdf(tasks);

        // 3. PDF içeriğini oku
        File file = new File("reports/task_report.pdf");
        assertTrue(file.exists(), "PDF dosyası oluşturulmamış.");
        assertTrue(file.length() > 0, "PDF dosyası boş.");

        PdfReader reader = new PdfReader(file);
        PdfDocument pdfDoc = new PdfDocument(reader);

        // 4. Sayfa 2 içeriğini oku (görev tablosu bu sayfada)
        String content = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(2));

        // 5. "pending" görev görünmeli
        assertTrue(content.contains("Pending Task"), "Pending görev bulunamadı.");

        // 6. "completed" görev görünmemeli
        assertFalse(content.contains("Completed Task"), "Completed görev tabloya dahil edilmiş.");

        pdfDoc.close();
    }

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
        assertTrue(content.contains("PENDING")); // Bu hata verebilir, çünkü yazım hatası var
        assertTrue(content.contains("DELAYED"));
        pdfDoc.close();
    }



}




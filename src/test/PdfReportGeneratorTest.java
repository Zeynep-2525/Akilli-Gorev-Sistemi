package com.odev.service;


import com.itextpdf.layout.element.Cell;
import com.odev.taskmanager.model.TaskPriority;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import com.odev.taskmanager.model.Task;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = PdfReportGenerator.class)
class PdfReportGeneratorTest {

    @Autowired
    private PdfReportGenerator pdfReportGenerator;

    @TempDir
    Path temporaryFolder;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm - dd.MM.yyyy");
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private List<Task> tasks;

    

    @BeforeEach
    void setUp() {
    	  tasks = Arrays.asList(
            new Task("Task 1", "Görev 1 açıklaması", TaskPriority.HIGH, "user1@example.com", LocalDateTime.parse("12:30 - 15.06.2024", formatter), false),
            new Task("Task 2", "Görev 2 açıklaması", TaskPriority.MEDIUM, "user2@example.com", LocalDateTime.parse("00:00 - 16.06.2024", formatter), true),
            new Task("Task 3", "Uzun açıklama metni içeren görev 3, PDF'de kısaltılmalı", TaskPriority.LOW, "user3@example.com", LocalDateTime.parse("10:00 - 10.06.2024", formatter), false),
            new Task("Task 4", "Tarihsiz görev", TaskPriority.MEDIUM, "user4@example.com", null, false),
            new Task("Task 5", "Tamamlanmış ve geçmiş tarihli görev", TaskPriority.HIGH, "user5@example.com", LocalDateTime.parse("00:00 - 01.01.2024", formatter), true)
        );
    }

    @Test
    void shouldCorrectlyIdentifyDelayedTasks() {
        // Tamamlanmış ve geçmiş tarihli görev gecikmiş sayılmamalı
        Task completedPastTask = new Task("Tamamlanmış Görev", "Açıklama", TaskPriority.HIGH, "user@example.com", LocalDateTime.parse("00:00 - 01.01.2020", formatter), true);
        assertFalse(pdfReportGenerator.isTaskDelayed(completedPastTask),
                "Tamamlanmış görevler geçmiş tarih olsa bile gecikmiş sayılmamalı.");

        // Gelecekteki tarihli görev gecikmiş sayılmamalı
        LocalDateTime futureDate = LocalDate.now().plusDays(1).atStartOfDay();
        Task futureTask = new Task("Gelecekteki Görev", "Açıklama", TaskPriority.MEDIUM, "user@example.com", futureDate, false);
        assertFalse(pdfReportGenerator.isTaskDelayed(futureTask),
                "Gelecekteki tarihli görevler gecikmiş sayılmamalı.");

        // Tamamlanmamış ve geçmiş tarihli görev gecikmiş sayılmalı
        LocalDateTime pastDate = LocalDate.now().minusDays(1).atStartOfDay();
        Task overdueTask = new Task("Gecikmiş Görev", "Açıklama", TaskPriority.LOW, "user@example.com", pastDate, false);
        assertTrue(pdfReportGenerator.isTaskDelayed(overdueTask),
                "Geçmiş tarihli ve tamamlanmamış görevler gecikmiş sayılmalı.");

        // Tarihi olmayan görev gecikmiş sayılmamalı
        Task taskWithoutDate = new Task("Tarihsiz Görev", "Açıklama", TaskPriority.HIGH, "user@example.com", null, false);
        assertFalse(pdfReportGenerator.isTaskDelayed(taskWithoutDate),
                "Tarihi olmayan görevler gecikmiş sayılmamalı.");
    }

    // Diğer test metotları aynı kalabilir




    @Test
    void shouldFormatDateTimeCorrectly() {
        // Tarih ve saat formatlama testi
        LocalDateTime dateTime = LocalDateTime.of(2024, 6, 15, 14, 30);
        String formattedDateTime = dateTime.format(PdfReportGenerator.DATE_TIME_FORMATTER);
        assertEquals("14:30 - 15.06.2024", formattedDateTime,
                "Tarih-saat formatı beklenen şekilde olmalı.");

        // Sadece tarih formatlama testi
        LocalDate date = LocalDate.of(2024, 6, 15);
        String formattedDate = date.format(PdfReportGenerator.DATE_FORMATTER);
        assertEquals("15.06.2024", formattedDate,
                "Tarih formatı beklenen şekilde olmalı.");
    }

    @Test
    void createStatusCell_shouldReturnNonNullCellsForAllStatusCombinations() {
        // Tamamlanmış görev için hücre oluşturulmalı
        Cell completedCell = pdfReportGenerator.createStatusCell(true, false);
        assertNotNull(completedCell, "Tamamlanmış görev için hücre null olmamalı.");

        // Gecikmiş görev için hücre oluşturulmalı
        Cell delayedCell = pdfReportGenerator.createStatusCell(false, true);
        assertNotNull(delayedCell, "Gecikmiş görev için hücre null olmamalı.");

        // Bekleyen görev için hücre oluşturulmalı
        Cell pendingCell = pdfReportGenerator.createStatusCell(false, false);
        assertNotNull(pendingCell, "Bekleyen görev için hücre null olmamalı.");
    }

    @Test
    void createTaskCell_shouldReturnStyledCellsBasedOnPriorityIndex() {
        // Yüksek öncelikli görev için hücre oluşturulmalı
        Cell highPriorityCell = pdfReportGenerator.createTaskCell("Yüksek öncelikli görev", 0);
        assertNotNull(highPriorityCell, "Yüksek öncelik hücresi null olmamalı.");

        // Orta öncelikli görev için hücre oluşturulmalı
        Cell mediumPriorityCell = pdfReportGenerator.createTaskCell("Orta öncelikli görev", 1);
        assertNotNull(mediumPriorityCell, "Orta öncelik hücresi null olmamalı.");

        // Düşük öncelikli görev için hücre oluşturulmalı
        Cell lowPriorityCell = pdfReportGenerator.createTaskCell("Düşük öncelikli görev", 2);
        assertNotNull(lowPriorityCell, "Düşük öncelik hücresi null olmamalı.");
    }

    @Test
    void createPriorityCell_shouldReturnCellsForAllPriorityLevels() {
        // Yüksek öncelik hücresi
        Cell highPriority = pdfReportGenerator.createPriorityCell(TaskPriority.HIGH);
        assertNotNull(highPriority, "Yüksek öncelik hücresi null olmamalı.");

        // Orta öncelik hücresi
        Cell mediumPriority = pdfReportGenerator.createPriorityCell(TaskPriority.MEDIUM);
        assertNotNull(mediumPriority, "Orta öncelik hücresi null olmamalı.");

        // Düşük öncelik hücresi
        Cell lowPriority = pdfReportGenerator.createPriorityCell(TaskPriority.LOW);
        assertNotNull(lowPriority, "Düşük öncelik hücresi null olmamalı.");
    }

    @Test
    void createDateTimeCell_shouldHandleNullAndValidDatesGracefully() {
        // Null tarih için hücre oluşturulmalı
        Cell nullDateCell = pdfReportGenerator.createDateTimeCell(null);
        assertNotNull(nullDateCell, "Null tarihle oluşturulan hücre null olmamalı.");

        // Geçerli tarih için hücre oluşturulmalı
        LocalDateTime now = LocalDateTime.now();
        Cell validDateCell = pdfReportGenerator.createDateTimeCell(now);
        assertNotNull(validDateCell, "Geçerli tarih ile oluşturulan hücre null olmamalı.");
    }

    @Test
    void calculatePercentage_shouldReturnCorrectValuesForVariousInputs() {
        // Yüzde hesaplamaların doğruluğunu test eder
        assertEquals(50.0, pdfReportGenerator.calculatePercentage(5, 10), 0.001, "5/10 yüzde 50 olmalı.");
        assertEquals(0.0, pdfReportGenerator.calculatePercentage(0, 10), 0.001, "0/10 yüzde 0 olmalı.");
        assertEquals(0.0, pdfReportGenerator.calculatePercentage(5, 0), 0.001, "Bölen 0 ise yüzde 0 döndürmeli.");
        assertEquals(33.333, pdfReportGenerator.calculatePercentage(1, 3), 0.001, "1/3 yaklaşık yüzde 33.333 olmalı.");
    }
}

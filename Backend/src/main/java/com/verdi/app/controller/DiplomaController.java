package com.verdi.app;

import org.apache.poi.ss.usermodel.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Разрешаем фронтенду слать запросы
public class DiplomaController {

    @PostMapping("/upload")
    public String handleFileUpload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("universityName") String universityName,
            @RequestParam("innCode") long innCode) {

        try {
            System.out.println("=== НОВЫЙ ЗАПРОС ПРИНЯТ ===");
            System.out.println("ВУЗ: " + universityName);
            System.out.println("ИНН: " + innCode);
            System.out.println("Файл: " + file.getOriginalFilename());

            // Читаем файл прямо из потока (MultipartFile)
            try (InputStream is = file.getInputStream()) {
                parseExcelOrCSV(is, file.getOriginalFilename(), universityName, innCode);
            }

            return "Данные успешно получены и выведены в консоль Java!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка при обработке файла: " + e.getMessage();
        }
    }

    private void parseExcelOrCSV(InputStream is, String fileName, String universityName, long innCode) throws Exception {
        if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter df = new DataFormatter();

            // Твоя логика парсинга строк
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                String fio = df.formatCellValue(row.getCell(0)); // Допустим, 0 - ФИО
                String dip = df.formatCellValue(row.getCell(1)); // 1 - Номер диплома

                System.out.printf("[EXCEL] ФИО: %s | Диплом: %s | ВУЗ: %s%n", fio, dip, universityName);
            }
            workbook.close();
        } else {
            // Логика для CSV аналогично через InputStreamReader
            System.out.println("Обработка CSV файла...");
        }
    }
}
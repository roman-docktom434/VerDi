package com.verdi.app;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Scanner;

public class ParsingFromExcelCSVToDataBase {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

        System.out.println("=== Система импорта дипломов ===");

        System.out.print("Введите название учебного заведения: ");
        String universityName = scanner.nextLine();

        // Обработка ИНН (long вместо int)
        long innCode = 0;
        while (true) {
            System.out.print("Введите ИНН вуза (10 или 12 цифр): ");
            String input = scanner.nextLine();

            // Проверка: только цифры и длина 10 или 12
            if (input.matches("\\d{10}|\\d{12}")) {
                innCode = Long.parseLong(input);
                break; // Выход из цикла, если всё ок
            } else {
                System.err.println("Ошибка: ИНН должен состоять из 10 или 12 цифр!");
            }
        }

        String fileName = "Дипломы.xlsx";
        File file = new File(fileName);

        if (file.exists()) {
            parseFile(fileName, universityName, innCode);
        } else {
            System.err.println("Файл " + fileName + " не найден!");
        }
    }

    // Теперь передаем long innCode
    public static void parseFile(String path, String universityName, long innCode) {
        if (path.endsWith(".csv")) {
            parseCSV(path, universityName, innCode);
        } else {
            parseExcel(path, universityName, innCode);
        }
    }

    private static void parseExcel(String path, String universityName, long innCode) {
        try (Workbook workbook = WorkbookFactory.create(new File(path))) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter df = new DataFormatter();

            Row headerRow = sheet.getRow(0);
            int idxFio = -1, idxDip = -1;

            for (Cell cell : headerRow) {
                String val = df.formatCellValue(cell).trim();
                if (val.equalsIgnoreCase("ФИО")) idxFio = cell.getColumnIndex();
                if (val.equalsIgnoreCase("Номер диплома")) idxDip = cell.getColumnIndex();
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String fio = df.formatCellValue(row.getCell(idxFio));
                String dip = df.formatCellValue(row.getCell(idxDip));

                // Хэшируем с учетом ИНН
                String rowHash = generateHash(fio + dip + universityName + innCode);

                System.out.printf("[%s | ИНН: %d] | ФИО: %s | Номер диплома: %s | Hash: %s%n",
                        universityName, innCode, fio, dip, rowHash);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void parseCSV(String path, String universityName, long innCode) {
        try (FileReader reader = new FileReader(path, StandardCharsets.UTF_8)) {
            CSVParser parser = CSVFormat.DEFAULT
                    .withDelimiter(';')
                    .withFirstRecordAsHeader()
                    .parse(reader);

            for (CSVRecord record : parser) {
                String fio = record.get("ФИО");
                String dip = record.get("Номер диплома");

                String rowHash = generateHash(fio + dip + universityName + innCode);

                System.out.printf("[%s | ИНН: %d] | ФИО: %s | Номер диплома: %s | Hash: %s%n",
                        universityName, innCode, fio, dip, rowHash);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String generateHash(String data) {
        try {
            String salt = "MySecretVibeKey2026";
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest((data + salt).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(encodedhash);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка хэширования", e);
        }
    }
}
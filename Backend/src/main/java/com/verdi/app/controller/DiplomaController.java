package com.verdi.app.controller;

import com.verdi.app.entity.Diplom;
import com.verdi.app.repository.DiplomRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class DiplomaController {

    @Autowired
    private DiplomRepository diplomRepository;

    private static final String SALT = "MySecretVibeKey2026";
    private static final long DEFAULT_HS_CODE = 1231231231L;

    // --- НОВЫЙ МЕТОД ДЛЯ ОТОБРАЖЕНИЯ В ТАБЛИЦЕ ---
    @GetMapping("/diploms/all")
    public ResponseEntity<List<Diplom>> getAllDiplomas() {
        List<Diplom> diploms = diplomRepository.findAll();
        return ResponseEntity.ok(diploms);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("universityName") String universityName,
            @RequestParam("innCode") String innStr,
            @RequestParam("cancelled") Integer cancelledStatus) {

        if (file.isEmpty()) return ResponseEntity.badRequest().body("Файл пуст");

        String fileName = file.getOriginalFilename();

        try (InputStream is = file.getInputStream()) {
            List<Map<String, Object>> records;

            if (fileName != null && (fileName.endsWith(".xlsx") || fileName.endsWith(".xls"))) {
                records = parseExcel(is, universityName, innStr, cancelledStatus);
            } else if (fileName != null && fileName.endsWith(".csv")) {
                records = parseCSV(is, universityName, innStr, cancelledStatus);
            } else {
                return ResponseEntity.badRequest().body("Неподдерживаемый формат файла");
            }

            List<Diplom> diplomas = records.stream().map(data -> {
                Diplom d = new Diplom();

                // БЕЗОПАСНОЕ ПРЕОБРАЗОВАНИЕ НОМЕРА ДИПЛОМА
                Object dipObj = data.get("Diplom_number");
                if (dipObj != null) {
                    // Превращаем в строку, убираем лишние пробелы и парсим в Integer
                    String dipStr = String.valueOf(dipObj).replaceAll("\\D", "");
                    if (!dipStr.isEmpty()) {
                        d.setDiplomNumber(Integer.parseInt(dipStr));
                    }
                }

                // Остальные поля (убедитесь, что типы в Entity совпадают)
                d.setYear((Integer) data.get("Year"));
                d.setFaculty((String) data.get("Faculty"));
                d.setCancelled((Integer) data.get("Cancelled"));
                d.setFullName((String) data.get("full_name"));

                // Если в Entity есть HashCode и HsCode, раскомментируйте:
                // d.setHashCode((String) data.get("Hash_code"));
                // d.setHsCode(String.valueOf(data.get("HS_code")));

                return d;
            }).toList();

            diplomRepository.saveAll(diplomas);
            return ResponseEntity.ok("В базу успешно записано: " + diplomas.size());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Ошибка при обработке: " + e.getMessage());
        }
    }

    private List<Map<String, Object>> parseExcel(InputStream is, String uni, String inn, Integer cancelled) throws Exception {
        List<Map<String, Object>> list = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(is);
        Sheet sheet = workbook.getSheetAt(0);
        DataFormatter df = new DataFormatter();

        Row headerRow = sheet.getRow(0);
        if (headerRow == null) throw new Exception("Файл пуст");

        int iFio = -1, iDip = -1, iYear = -1, iFaculty = -1;
        for (Cell cell : headerRow) {
            String val = df.formatCellValue(cell).trim();
            if (val.equalsIgnoreCase("ФИО")) iFio = cell.getColumnIndex();
            if (val.equalsIgnoreCase("Номер диплома")) iDip = cell.getColumnIndex();
            if (val.equalsIgnoreCase("Год")) iYear = cell.getColumnIndex();
            if (val.equalsIgnoreCase("Специальность")) iFaculty = cell.getColumnIndex();
        }

        if (iFio == -1 || iDip == -1) throw new Exception("Колонки ФИО или Номер диплома не найдены");

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            String fio = df.formatCellValue(row.getCell(iFio));
            String dipStr = df.formatCellValue(row.getCell(iDip));
            String yearStr = iYear != -1 ? df.formatCellValue(row.getCell(iYear)) : "2026";
            String faculty = iFaculty != -1 ? df.formatCellValue(row.getCell(iFaculty)) : "Не указано";

            if (fio.isEmpty() || dipStr.isEmpty()) continue;

            list.add(createDataMap(fio, dipStr, yearStr, faculty, uni, inn, cancelled));
        }
        workbook.close();
        return list;
    }

    private List<Map<String, Object>> parseCSV(InputStream is, String uni, String inn, Integer cancelled) throws Exception {
        List<Map<String, Object>> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            CSVParser parser = CSVFormat.DEFAULT
                    .withDelimiter(';')
                    .withFirstRecordAsHeader()
                    .parse(reader);

            for (CSVRecord record : parser) {
                String fio = record.get("ФИО");
                String dipStr = record.get("Номер диплома");
                String yearStr = record.isMapped("Год") ? record.get("Год") : "2026";
                String faculty = record.isMapped("Специальность") ? record.get("Специальность") : "Не указано";

                list.add(createDataMap(fio, dipStr, yearStr, faculty, uni, inn, cancelled));
            }
        }
        return list;
    }

    private Map<String, Object> createDataMap(String fio, String dip, String year, String fac, String uni, String inn, Integer cancelled) {
        Map<String, Object> map = new HashMap<>();

        // Оставляем очистку от лишних символов
        String cleanDip = dip.replaceAll("\\D", "");
        int cleanYear = (year == null || year.isEmpty()) ? 2026 : Integer.parseInt(year.replaceAll("\\D", ""));

        map.put("full_name", fio);
        map.put("Diplom_number", cleanDip);
        map.put("Year", cleanYear);
        map.put("Faculty", fac);
        map.put("HS_code", DEFAULT_HS_CODE);
        map.put("Cancelled", cancelled);
        map.put("Hash_code", generateHash(fio + dip + uni + inn));

        return map;
    }

    private String generateHash(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest((data + SALT).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(encodedhash);
        } catch (Exception e) {
            return "error";
        }
    }
}
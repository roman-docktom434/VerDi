package com.verdi.app.controller;

import com.verdi.app.entity.HR;
import com.verdi.app.entity.Highschool;
import com.verdi.app.entity.Student;
import com.verdi.app.repository.HRRepository;
import com.verdi.app.repository.HighschoolRepository;
import com.verdi.app.repository.StudentRepository;
import com.verdi.app.util.HashUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private HighschoolRepository highschoolRepository;
    @Autowired
    private HRRepository hrRepository;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Map<String, String> data) {
        String type = data.get("type");
        try {
            switch (type) {
                case "student": return saveStudent(data);
                case "university": return saveUniversity(data);
                case "employer": return saveEmployer(data);
                default: return ResponseEntity.badRequest().body("Неизвестный тип аккаунта");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Системная ошибка: " + e.getMessage());
        }
    }

    private ResponseEntity<String> saveStudent(Map<String, String> data) {
        String fio = data.get("fio");
        String rawDiplom = data.get("diplomCode");
        String rawPassword = data.get("password");

        if (fio == null || rawDiplom == null || rawPassword == null) {
            return ResponseEntity.badRequest().body("Ошибка: ФИО, номер диплома и пароль обязательны");
        }

        String[] parts = fio.trim().split("\\s+");
        String s = parts.length >= 1 ? parts[0] : "";
        String n = parts.length >= 2 ? parts[1] : "";
        String m = parts.length >= 3 ? parts[2] : "";
        String hashedDiplom = HashUtil.hashSHA256(rawDiplom);

        // ПРОВЕРКА УНИКАЛЬНОСТИ
        if (studentRepository.existsBySernameAndNameAndMiddleNameAndDiplomCode(s, n, m, hashedDiplom)) {
            return ResponseEntity.badRequest().body("Ошибка: Студент с таким ФИО и дипломом уже зарегистрирован");
        }

        Student student = new Student();
        student.setSername(s);
        student.setName(n);
        student.setMiddleName(m);
        student.setDiplomCode(hashedDiplom);
        student.setPassword(HashUtil.hashSHA256(rawPassword));

        if (data.containsKey("hsCode")) {
            student.setHsCode(HashUtil.hashSHA256(data.get("hsCode")));
        }

        studentRepository.save(student);
        return ResponseEntity.ok("Студент успешно зарегистрирован");
    }

    private ResponseEntity<String> saveUniversity(Map<String, String> data) {
        String name = data.get("name"); // Убедись, что в HTML name="name"
        String rawCode = data.get("hsCode");
        String rawPassword = data.get("password");

        if (name == null || rawCode == null || rawPassword == null) {
            return ResponseEntity.badRequest().body("Ошибка: Название, код и пароль обязательны");
        }

        String hashedCode = HashUtil.hashSHA256(rawCode);

        // ПРОВЕРКА УНИКАЛЬНОСТИ (по @Id)
        if (highschoolRepository.existsById(hashedCode)) {
            return ResponseEntity.badRequest().body("Ошибка: ВУЗ с таким кодом уже зарегистрирован");
        }

        Highschool hs = new Highschool();
        hs.setHsCode(hashedCode);
        hs.setName(name);
        hs.setPassword(HashUtil.hashSHA256(rawPassword));

        highschoolRepository.save(hs);
        return ResponseEntity.ok("ВУЗ успешно зарегистрирован!");
    }

    private ResponseEntity<String> saveEmployer(Map<String, String> data) {
        String name = data.get("name");
        String email = data.get("email");
        String password = data.get("password");

        if (name == null || email == null || password == null) {
            return ResponseEntity.badRequest().body("Ошибка: Все поля обязательны");
        }

        String hashedEmail = HashUtil.hashSHA256(email);

        // 1. Проверка по почте (ID)
        if (hrRepository.existsById(hashedEmail)) {
            return ResponseEntity.badRequest().body("Ошибка: Эта почта уже зарегистрирована");
        }

        // 2. Проверка по названию организации (Unique Field)
        if (hrRepository.existsByName(name)) {
            return ResponseEntity.badRequest().body("Ошибка: Организация с таким названием уже зарегистрирована");
        }

        HR hr = new HR();
        hr.setEmail(hashedEmail);
        hr.setName(name);
        hr.setPassword(HashUtil.hashSHA256(password));

        hrRepository.save(hr);
        return ResponseEntity.ok("Работодатель успешно зарегистрирован!");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> data) {
        String type = data.get("type");
        String password = data.get("password");

        if ("university".equals(type)) {
            String identifier = data.get("identifier");
            Highschool hs = null;

            // Сначала ищем по хэшированному коду
            hs = highschoolRepository.findById(HashUtil.hashSHA256(identifier)).orElse(null);

            // Если не нашли, ищем по открытому имени
            if (hs == null) {
                hs = highschoolRepository.findByName(identifier);
            }

            if (hs != null && hs.getPassword().equals(HashUtil.hashSHA256(password))) {
                return ResponseEntity.ok("Вход выполнен (ВУЗ)");
            }
        }
        else if ("student".equals(type)) {
            String fio = data.get("fio");
            String diplom = data.get("diplomCode");

            String hashedDiplom = HashUtil.hashSHA256(diplom);
            String hashedPassword = HashUtil.hashSHA256(password);

            String[] parts = fio.trim().split("\\s+");
            String sn = parts.length >= 1 ? parts[0] : "";
            String n = parts.length >= 2 ? parts[1] : "";
            String mn = parts.length >= 3 ? parts[2] : "";

            Optional<Student> studentOpt = studentRepository.findBySernameAndNameAndMiddleNameAndDiplomCode(sn, n, mn, hashedDiplom);

            if (studentOpt.isPresent() && studentOpt.get().getPassword().equals(hashedPassword)) {
                return ResponseEntity.ok("Вход выполнен (Студент)");
            }
        }
        else if ("employer".equals(type)) {
            String hashedEmail = HashUtil.hashSHA256(data.get("identifier"));
            Optional<HR> hrOpt = hrRepository.findById(hashedEmail);

            if (hrOpt.isPresent() && hrOpt.get().getPassword().equals(HashUtil.hashSHA256(password))) {
                return ResponseEntity.ok("Вход выполнен (Работодатель)");
            }
        }

        return ResponseEntity.status(401).body("Неверные данные");
    }
}
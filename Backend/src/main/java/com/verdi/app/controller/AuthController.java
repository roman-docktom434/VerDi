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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
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
        String diplomCode = data.get("identifier");

        if (diplomCode == null) {
            return ResponseEntity.badRequest().body("Ошибка: Номер диплома (identifier) не передан!");
        }

        if (studentRepository.existsById(diplomCode)) {
            return ResponseEntity.badRequest().body("Студент уже зарегистрирован!");
        }

        Student s = new Student();
        s.setDiplomCode(diplomCode);
        s.setSername(data.get("surname"));
        s.setName(data.get("name"));
        s.setMiddleName(data.get("middle_name"));
        s.setPassword(HashUtil.hashSHA256(data.get("password")));

        if (data.containsKey("hs_code")) {
            s.setHsCode(HashUtil.hashSHA256(data.get("hs_code")));
        }

        studentRepository.save(s);
        return ResponseEntity.ok("Студент успешно зарегистрирован");
    }

    private ResponseEntity<String> saveUniversity(Map<String, String> data) {
        String hsName = data.get("name");
        String hsCodeRaw = data.get("identifier");
        if (hsCodeRaw == null || hsCodeRaw.isEmpty()) {
            return ResponseEntity.badRequest().body("Ошибка: Код ВУЗа не передан (identifier is null)");
        }
        if (highschoolRepository.existsById(HashUtil.hashSHA256(hsCodeRaw))) {
            return ResponseEntity.badRequest().body("ВУЗ с таким кодом уже существует!");
        }

        Highschool hs = new Highschool();
        hs.setHsCode(HashUtil.hashSHA256(hsCodeRaw));
        hs.setName(hsName);
        hs.setPassword(HashUtil.hashSHA256(data.get("password")));

        highschoolRepository.save(hs);
        return ResponseEntity.ok("ВУЗ успешно зарегистрирован");
    }

    private ResponseEntity<String> saveEmployer(Map<String, String> data) {
        String emailRaw = data.get("identifier");
        String companyName = data.get("name");

        if (hrRepository.existsById(HashUtil.hashSHA256(emailRaw))) {
            return ResponseEntity.badRequest().body("Пользователь с таким e-mail уже существует!");
        }
        if (hrRepository.existsByName(companyName)) {
            return ResponseEntity.badRequest().body("Компания с таким названием уже зарегистрирована!");
        }

        HR hr = new HR();
        hr.setEmail(HashUtil.hashSHA256(emailRaw));
        hr.setName(companyName);
        hr.setPassword(HashUtil.hashSHA256(data.get("password")));

        hrRepository.save(hr);
        return ResponseEntity.ok("Работодатель успешно зарегистрирован");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> data) {
        String type = data.get("type");
        String password = data.get("password");
        String hashedPassword = HashUtil.hashSHA256(password);

        Map<String, String> responseBody = new HashMap<>();

        if ("university".equals(type)) {
            String identifier = data.get("identifier");
            String hashedId = HashUtil.hashSHA256(identifier);
            Highschool hs = highschoolRepository.findById(hashedId).orElse(null);
            if (hs == null) hs = highschoolRepository.findByName(identifier);

            if (hs != null && hs.getPassword().equals(hashedPassword)) {
                responseBody.put("name", hs.getName());
                responseBody.put("type", "university");
                return ResponseEntity.ok(responseBody);
            }
        }
        else if ("student".equals(type)) {
            // Достаем ФИО и Диплом из Map data
            String fio = data.get("fio");
            String diplom = data.get("identifier"); // В JS мы договорились слать под ключом identifier

            if (fio == null || diplom == null) {
                return ResponseEntity.badRequest().body("ФИО или номер диплома не переданы");
            }

            // Разбиваем ФИО на части
            String[] parts = fio.trim().split("\\s+");

            // ОБЪЯВЛЯЕМ переменные sn, n, mn прямо здесь
            String sn = parts.length >= 1 ? parts[0] : "";
            String n = parts.length >= 2 ? parts[1] : "";
            String mn = parts.length >= 3 ? parts[2] : "";

            Optional<Student> studentOpt = studentRepository.findBySernameAndNameAndMiddleNameAndDiplomCode(sn, n, mn, diplom);

            if (studentOpt.isPresent() && studentOpt.get().getPassword().equals(hashedPassword)) {
                Student s = studentOpt.get();

                responseBody.put("name", s.getSername() + " " + s.getName());
                responseBody.put("type", "student");
                responseBody.put("status", "success");

                return ResponseEntity.ok(responseBody);
            }
        }
        else if ("employer".equals(type)) {
            String hashedEmail = HashUtil.hashSHA256(data.get("identifier"));
            Optional<HR> hrOpt = hrRepository.findById(hashedEmail);

            if (hrOpt.isPresent() && hrOpt.get().getPassword().equals(hashedPassword)) {
                HR hr = hrOpt.get();
                // КЛАДЕМ ДАННЫЕ В MAP
                responseBody.put("name", hr.getName());
                responseBody.put("type", "employer");
                return ResponseEntity.ok(responseBody);
            }
        }

        return ResponseEntity.status(401).body("Неверные данные");
    }
}
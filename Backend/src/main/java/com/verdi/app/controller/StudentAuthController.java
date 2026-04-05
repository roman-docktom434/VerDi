package com.verdi.app.controller;

import com.verdi.app.entity.Diplom;
import com.verdi.app.entity.Student;
import com.verdi.app.repository.DiplomRepository;
import com.verdi.app.repository.StudentRepository;
import com.verdi.app.util.HashUtil; // Убедись, что путь к HashUtil верный
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class StudentAuthController {

    @Autowired
    private DiplomRepository diplomRepository;

    @Autowired
    private StudentRepository studentRepository;

    @PostMapping("/students/activate")
    public ResponseEntity<?> activateStudent(@RequestBody Map<String, String> request) {
        String fullName = request.get("fullName");
        String rawPassword = request.get("password");

        if (fullName == null || rawPassword == null) {
            return ResponseEntity.badRequest().body("Данные не заполнены");
        }

        // 1. Ищем диплом по ФИО в таблице diplom
        Optional<Diplom> diplomOpt = diplomRepository.findByFullName(fullName);

        if (diplomOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Студент с таким ФИО не найден в реестре дипломов");
        }

        Diplom diplom = diplomOpt.get();

        // 2. Разбиваем ФИО для записи в таблицу Student
        String[] parts = fullName.trim().split("\\s+");
        String sername = parts.length >= 1 ? parts[0] : "";
        String name = parts.length >= 2 ? parts[1] : "";
        String middleName = parts.length >= 3 ? parts[2] : "";

        // 3. Создаем объект Student для таблицы student
        Student student = new Student();

        // Убедись, что типы данных совпадают (String или Integer)
        student.setDiplomCode(String.valueOf(diplom.getDiplomNumber()));
        student.setSername(sername);
        student.setName(name);
        student.setMiddleName(middleName);

        // Хэшируем пароль перед сохранением
        student.setPassword(HashUtil.hashSHA256(rawPassword));

        // Ставим дефолтный или берем из диплома
        student.setHsCode("1231231231");

        try {
            studentRepository.save(student);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ошибка при сохранении: аккаунт уже может быть активирован.");
        }

        // 4. Возвращаем данные для фронтенда
        Map<String, String> response = new HashMap<>();
        response.put("name", fullName);
        response.put("diploma", String.valueOf(diplom.getDiplomNumber()));
        response.put("password", rawPassword);

        return ResponseEntity.ok(response);
    }
}
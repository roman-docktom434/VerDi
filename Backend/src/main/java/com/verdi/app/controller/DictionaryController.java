package com.verdi.app.controller;

import com.verdi.app.entity.UniversityDict;
import com.verdi.app.repository.UniversityDictRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable; // ПРОВЕРЬ ЭТОТ ИМПОРТ
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dict")
@CrossOrigin(origins = "*")
public class DictionaryController {

    @Autowired
    private UniversityDictRepository dictRepository;

    @GetMapping("/universities")
    public List<String> getSuggestions(@RequestParam String query) {
        if (query == null || query.length() < 3) return List.of();

        // PageRequest.of() возвращает объект, который реализует org.springframework.data.domain.Pageable
        Pageable limit = PageRequest.of(0, 10);

        return dictRepository.findByFullNameContainingIgnoreCase(query, limit)
                .stream()
                .map(UniversityDict::getFullName)
                .collect(Collectors.toList());
    }
}
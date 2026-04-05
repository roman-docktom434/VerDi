package com.verdi.app.repository;

import com.verdi.app.entity.Highschool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
// Если твой Primary Key (hs_code) — это String (хэш), замени Long на String
// Было Long, должно быть String, так как hs_code — varchar(64)
public interface HighschoolRepository extends JpaRepository<Highschool, String> {
    Highschool findByName(String name);
    boolean existsByName(String name);
}
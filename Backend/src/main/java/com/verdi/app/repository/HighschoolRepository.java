package com.verdi.app.repository;

import com.verdi.app.entity.Highschool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HighschoolRepository extends JpaRepository<Highschool, String> {
    Highschool findByName(String name);
}
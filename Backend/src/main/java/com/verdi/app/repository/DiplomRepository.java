package com.verdi.app.repository;

import com.verdi.app.entity.Diplom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiplomRepository extends JpaRepository<Diplom, String> {
    Optional<Diplom> findByFullName(String fullName);
}
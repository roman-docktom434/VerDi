package com.verdi.app.repository;
import com.verdi.app.entity.UniversityDict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
@Repository
public interface UniversityDictRepository extends JpaRepository<UniversityDict, Long> {
    List<UniversityDict> findByFullNameContainingIgnoreCase(String query, Pageable pageable);
}
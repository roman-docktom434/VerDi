package com.verdi.app.repository;

import com.verdi.app.entity.HR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface HRRepository extends JpaRepository<HR, String> {

    // 1. Для поиска при логине (по хэшу почты)
    Optional<HR> findByEmail(String email);

    // 2. ДЛЯ УНИКАЛЬНОСТИ: проверка, существует ли уже компания с таким именем
    // Этот метод будет использоваться в AuthController перед сохранением
    boolean existsByName(String name);
}
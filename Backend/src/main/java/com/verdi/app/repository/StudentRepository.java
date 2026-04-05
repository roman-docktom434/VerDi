package com.verdi.app.repository;

import com.verdi.app.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface StudentRepository extends JpaRepository<Student, String> {

    // Этот метод теперь правильный, так как все аргументы String
    boolean existsBySernameAndNameAndMiddleNameAndDiplomCode(
            String sername, String name, String middleName, String diplomCode
    );

    // Добавь этот метод, если он используется в AuthController для логина
    Optional<Student> findBySernameAndNameAndMiddleNameAndDiplomCode(
            String sername, String name, String middleName, String diplomCode
    );
}
package com.verdi.app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "student", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"sername", "name", "middle_name", "diplom_code"})
})
public class Student {

    @Id
    @Column(name = "diplom_code", length = 256, nullable = false)
    private String diplomCode;

    @Column(nullable = false)
    private String sername;

    @Column(nullable = false)
    private String name;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "hs_code", length = 64)
    private String hsCode; // Хэш SHA-256 кода ВУЗа

    @Column(nullable = false, length = 64)
    private String password; // Хэш SHA-256 пароля

    // --- Геттеры и Сеттеры ---

    public String getDiplomCode() { return diplomCode; }
    public void setDiplomCode(String diplomCode) { this.diplomCode = diplomCode; }

    public String getSername() { return sername; }
    public void setSername(String sername) { this.sername = sername; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public String getHsCode() { return hsCode; }
    public void setHsCode(String hsCode) { this.hsCode = hsCode; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
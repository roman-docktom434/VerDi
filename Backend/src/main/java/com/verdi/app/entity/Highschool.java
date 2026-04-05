package com.verdi.app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "highschool")
public class Highschool {


    @Id
    @Column(name = "hs_code", length = 64)
    private String hsCode; // Хэш SHA-256 кода ВУЗа

    @Column(nullable = false, unique = true)
    private String name; // Открытое название (для автодополнения)

    @Column(nullable = false, length = 64)
    private String password; // Хэш SHA-256 пароля

    // --- Геттеры и Сеттеры ---

    public String getHsCode() { return hsCode; }
    public void setHsCode(String hsCode) { this.hsCode = hsCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
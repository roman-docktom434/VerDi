package com.verdi.app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "hr")
public class HR {
    @Id
    @Column(name = "email_hash", length = 64)
    private String email;

    @Column(nullable = false, unique = true) // Добавляем unique = true
    private String name; // Открытое имя компании или ФИО рекрутера

    @Column(nullable = false, length = 64)
    private String password; // Хэш SHA-256 пароля

    // --- Геттеры и Сеттеры ---

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
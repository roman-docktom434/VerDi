package com.verdi.app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "universities_dictionary")
public class UniversityDict {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    private String shortName;

    // Геттеры
    public String getFullName() { return fullName; }
}
package com.verdi.app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "diplom")
public class Diplom {

    @Id
    @Column(name = "Diplom_number")
    private Integer diplomNumber;

    @Column(name = "Hash_code", length = 64)
    private String hashCode;

    @Column(name = "HS_code")
    private Long hsCode;

    @Column(name = "Year")
    private Integer year;

    @Column(name = "Faculty", columnDefinition = "TEXT")
    private String faculty;

    @Column(name = "Cancelled")
    private Integer cancelled; // 0 - верифицирован (активен), 1 - аннулирован

    public Integer getCancelled() {
        return cancelled;
    }

    public void setCancelled(Integer cancelled) {
        this.cancelled = cancelled;
    }

    @Column(name = "full_name")
    private String fullName;

    // --- ГЕТТЕРЫ И СЕТТЕРЫ ---

    public Integer getDiplomNumber() {
        return diplomNumber;
    }

    public void setDiplomNumber(Integer diplomNumber) {
        this.diplomNumber = diplomNumber;
    }

    public String getHashCode() {
        return hashCode;
    }

    public void setHashCode(String hashCode) {
        this.hashCode = hashCode;
    }

    public Long getHsCode() {
        return hsCode;
    }

    public void setHsCode(Long hsCode) {
        this.hsCode = hsCode;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
CREATE DATABASE IF NOT EXISTS diploma_system;
USE diploma_system;

-- Таблица дипломов
CREATE TABLE Diplom (
    Diplom_number INT NOT NULL,
    Hash_code INT NOT NULL UNIQUE,
    HS_code INT NOT NULL,
    Year INT NOT NULL,
    Faculty VARCHAR(255) NOT NULL,
    Cancelled BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (Diplom_number)
);

-- Таблица ВУЗов
CREATE TABLE HighSchool (
    HS_code INT AUTO_INCREMENT,
    Name VARCHAR(255) NOT NULL UNIQUE,
    Password VARCHAR(255) NOT NULL,
    UNN INT NOT NULL UNIQUE,
    PRIMARY KEY (HS_code)
);

-- Таблица работодателей
CREATE TABLE HR (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(255) NOT NULL UNIQUE,
    Email VARCHAR(255) NOT NULL UNIQUE,
    Password VARCHAR(255) NOT NULL
);

-- Таблица студентов
CREATE TABLE Student (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(100) NOT NULL,
    Sername VARCHAR(100) NOT NULL,
    MiddleName VARCHAR(100),
    HS_code INT,
    Diplom_code INT,
    Password VARCHAR(255) NOT NULL,

    FOREIGN KEY (HS_code) REFERENCES HighSchool(HS_code) ON DELETE SET NULL,
    FOREIGN KEY (Diplom_code) REFERENCES Diplom(Diplom_number) ON DELETE SET NULL
);
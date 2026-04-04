<?php
require 'db.php';

$type = $_POST['type'];

try {

    // ================= HR =================
    if ($type == "hr") {
        $name = $_POST['name'];
        $email = $_POST['email'];
        $password = password_hash($_POST['password'], PASSWORD_DEFAULT);

        $check = $db->prepare("SELECT * FROM HR WHERE Name = ? OR Email = ?");
        $check->execute([$name, $email]);

        if ($check->fetch()) {
            die("Аккаунт уже существует");
        }

        $stmt = $db->prepare("INSERT INTO HR (Name, Email, Password) VALUES (?, ?, ?)");
        $stmt->execute([$name, $email, $password]);
    }

    // ================= ВУЗ =================
    if ($type == "hs") {
        $name = $_POST['name'];
        $code = $_POST['code'];
        $unn = $_POST['unn'];
        $password = password_hash($_POST['password'], PASSWORD_DEFAULT);

        $check = $db->prepare("SELECT * FROM HighSchool WHERE Name = ? OR HS_code = ?");
        $check->execute([$name, $code]);

        if ($check->fetch()) {
            die("ВУЗ уже существует");
        }

        $stmt = $db->prepare("INSERT INTO HighSchool (Name, HS_code, Password, UNN) VALUES (?, ?, ?, ?)");
        $stmt->execute([$name, $code, $password, $unn]);
    }

    // ================= Студент =================
    if ($type == "student") {
        $name = $_POST['name'];
        $sername = $_POST['sername'];
        $middle = $_POST['middle'];
        $hs = $_POST['hs_code'];
        $diplom = $_POST['diplom'];
        $password = password_hash($_POST['password'], PASSWORD_DEFAULT);

        // Проверка диплома
        $diplomCheck = $db->prepare("SELECT * FROM Diplom WHERE Diplom_number = ?");
        $diplomCheck->execute([$diplom]);

        if (!$diplomCheck->fetch()) {
            die("Диплом не найден");
        }

        $stmt = $db->prepare("
            INSERT INTO Student (Name, Sername, MiddleName, HS_code, Diplom_code, Password)
            VALUES (?, ?, ?, ?, ?, ?)
        ");
        $stmt->execute([$name, $sername, $middle, $hs, $diplom, $password]);
    }

    // Редирект (ВАЖНО — путь!)
    header("Location: ../Frontend/index.html");
    exit;

} catch (PDOException $e) {
    die("Ошибка: " . $e->getMessage());
}
<?php
require 'db.php';
session_start();

$email = $_POST['email'];
$password = $_POST['password'];

try {

    $stmt = $db->prepare("SELECT * FROM HR WHERE Email = ?");
    $stmt->execute([$email]);
    $user = $stmt->fetch();

    if ($user && password_verify($password, $user['Password'])) {
        $_SESSION['user'] = $user['ID'];
        header("Location: ../Frontend/index.html");
        exit;
    }

    die("Неверный логин или пароль");

} catch (PDOException $e) {
    die("Ошибка: " . $e->getMessage());
}
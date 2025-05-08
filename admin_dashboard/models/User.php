<?php
require_once __DIR__ . '/../config/db.php';

class User {
    private $conn;

    public function __construct() {
        $db = new Database();
        $this->conn = $db->conn;
    }

    public function getAll() {
        $sql = "SELECT * FROM users";
        $result = $this->conn->query($sql);
        return $result->fetch_all(MYSQLI_ASSOC);
    }

    public function search($query) {
        $q = "%$query%";
        $stmt = $this->conn->prepare("SELECT * FROM users WHERE name LIKE ? OR email LIKE ? OR id = ?");
        $stmt->bind_param("ssi", $q, $q, $query);
        $stmt->execute();
        return $stmt->get_result()->fetch_all(MYSQLI_ASSOC);
    }

    public function suspend($id) {
        $stmt = $this->conn->prepare("UPDATE users SET flag = 1 WHERE id = ?");
        $stmt->bind_param("i", $id);
        return $stmt->execute();
    }

    public function unsuspend($id) {
        $stmt = $this->conn->prepare("UPDATE users SET flag = 0 WHERE id = ?");
        $stmt->bind_param("i", $id);
        return $stmt->execute();
    }

    public function deleteUser($id) {
        $stmt = $this->conn->prepare("DELETE FROM users WHERE id = ?");
        $stmt->bind_param("i", $id);
        return $stmt->execute();
    }
} 
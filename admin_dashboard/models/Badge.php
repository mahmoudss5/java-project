<?php
require_once __DIR__ . '/../config/db.php';

class Badge {
    private $conn;

    public function __construct() {
        $db = new Database();
        $this->conn = $db->conn;
    }

    public function getAll() {
        $sql = "SELECT * FROM badges";
        $result = $this->conn->query($sql);
        return $result->fetch_all(MYSQLI_ASSOC);
    }

    public function getById($id) {
        $stmt = $this->conn->prepare("SELECT * FROM badges WHERE badge_id = ?");
        $stmt->bind_param("i", $id);
        $stmt->execute();
        return $stmt->get_result()->fetch_assoc();
    }

    public function add($name, $desc, $priv, $points) {
        $stmt = $this->conn->prepare("INSERT INTO badges (badge_name, badge_description, privilege, points) VALUES (?, ?, ?, ?)");
        $stmt->bind_param("sssi", $name, $desc, $priv, $points);
        return $stmt->execute();
    }

    public function update($id, $name, $desc, $priv, $points) {
        $stmt = $this->conn->prepare("UPDATE badges SET badge_name=?, badge_description=?, privilege=?, points=? WHERE badge_id=?");
        $stmt->bind_param("sssii", $name, $desc, $priv, $points, $id);
        return $stmt->execute();
    }

    public function delete($id) {
        $stmt = $this->conn->prepare("DELETE FROM badges WHERE badge_id = ?");
        $stmt->bind_param("i", $id);
        return $stmt->execute();
    }

    public function grantBadgeToUser($user_id, $badge_id) {
        $stmt = $this->conn->prepare("INSERT IGNORE INTO user_badges (user_id, badge_id) VALUES (?, ?)");
        $stmt->bind_param("ii", $user_id, $badge_id);
        return $stmt->execute();
    }

    public function revokeBadgeFromUser($user_id, $badge_id) {
        $stmt = $this->conn->prepare("DELETE FROM user_badges WHERE user_id = ? AND badge_id = ?");
        $stmt->bind_param("ii", $user_id, $badge_id);
        return $stmt->execute();
    }

    public function getUserBadges($user_id) {
        $stmt = $this->conn->prepare("SELECT b.* FROM badges b JOIN user_badges ub ON b.badge_id = ub.badge_id WHERE ub.user_id = ?");
        $stmt->bind_param("i", $user_id);
        $stmt->execute();
        return $stmt->get_result()->fetch_all(MYSQLI_ASSOC);
    }
} 
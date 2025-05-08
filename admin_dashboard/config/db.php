<?php
class Database {
    private $host = "127.0.0.1";
    private $user = "newuser";
    private $pass = "password";
    private $db = "project";
    public $conn;

    public function __construct() {
        $this->conn = new mysqli($this->host, $this->user, $this->pass, $this->db);
        if ($this->conn->connect_error) {
            die("Connection failed: " . $this->conn->connect_error);
        }
        $this->conn->set_charset("utf8mb4");
    }
} 
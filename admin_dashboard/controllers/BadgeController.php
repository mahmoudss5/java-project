<?php
require_once __DIR__ . '/../models/Badge.php';

class BadgeController {
    private $badgeModel;

    public function __construct() {
        $this->badgeModel = new Badge();
    }

    public function index() {
        $badges = $this->badgeModel->getAll();
        include __DIR__ . '/../views/badges/index.php';
    }

    public function add() {
        if ($_SERVER['REQUEST_METHOD'] === 'POST') {
            $name = $_POST['badge_name'];
            $desc = $_POST['badge_description'];
            $priv = $_POST['privilege'];
            $points = $_POST['points'];
            $this->badgeModel->add($name, $desc, $priv, $points);
            header('Location: ?action=index');
            exit;
        }
        include __DIR__ . '/../views/badges/add.php';
    }

    public function edit() {
        $id = $_GET['id'];
        $badge = $this->badgeModel->getById($id);
        if ($_SERVER['REQUEST_METHOD'] === 'POST') {
            $name = $_POST['badge_name'];
            $desc = $_POST['badge_description'];
            $priv = $_POST['privilege'];
            $points = $_POST['points'];
            $this->badgeModel->update($id, $name, $desc, $priv, $points);
            header('Location: ?action=index');
            exit;
        }
        include __DIR__ . '/../views/badges/edit.php';
    }

    public function delete() {
        $id = $_GET['id'];
        $this->badgeModel->delete($id);
        header('Location: ?action=index');
        exit;
    }
} 
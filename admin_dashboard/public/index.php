<?php
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

require_once __DIR__ . '/../controllers/AdminController.php';

$controller = new AdminController();
$action = $_GET['action'] ?? 'index';

switch ($action) {
    default:
        $controller->index();
        break;
} 
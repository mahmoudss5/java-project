<?php
require_once __DIR__ . '/../models/User.php';
require_once __DIR__ . '/../models/Badge.php';

class AdminController {
    private $userModel;
    private $badgeModel;

    public function __construct() {
        $this->userModel = new User();
        $this->badgeModel = new Badge();
    }

    public function index() {
            if (isset($_POST['add_badge'])) {
            $name = $_POST['badge_name'];
            $desc = $_POST['badge_description'];
            $priv = $_POST['privilege'];
            $points = $_POST['points'];
            $this->badgeModel->add($name, $desc, $priv, $points);
            header('Location: index.php');
            exit;
        }
    
        if (isset($_POST['delete_badge'])) {
            $id = $_POST['badge_id'];
            $this->badgeModel->delete($id);
            header('Location: index.php');
            exit;
        }
       
        if (isset($_POST['edit_badge'])) {
            $id = $_POST['badge_id'];
            $name = $_POST['badge_name'];
            $desc = $_POST['badge_description'];
            $priv = $_POST['privilege'];
            $points = $_POST['points'];
            $this->badgeModel->update($id, $name, $desc, $priv, $points);
            header('Location: index.php');
            exit;
        }
       
        if (isset($_POST['suspend_user'])) {
            $this->userModel->suspend($_POST['user_id']);
            header('Location: index.php');
            exit;
        }
    
        if (isset($_POST['unsuspend_user'])) {
            $this->userModel->unsuspend($_POST['user_id']);
            header('Location: index.php');
            exit;
        }
       
        if (isset($_POST['delete_user'])) {
            $this->userModel->deleteUser($_POST['user_id']);
            header('Location: index.php');
            exit;
        }
       
        if (isset($_POST['show_grant_badge'])) {
            $grantUserId = $_POST['user_id'];
        }
        if (isset($_POST['grant_badge_to_user'])) {
            $this->badgeModel->grantBadgeToUser($_POST['user_id'], $_POST['badge_id']);
            header('Location: index.php');
            exit;
        }
   
        if (isset($_POST['show_revoke_badge'])) {
            $revokeUserId = $_POST['user_id'];
        }
        if (isset($_POST['revoke_badge_from_user'])) {
            $this->badgeModel->revokeBadgeFromUser($_POST['user_id'], $_POST['badge_id']);
            header('Location: index.php');
            exit;
        }
        $editBadge = null;
        if (isset($_POST['show_edit_badge'])) {
            $editBadge = $this->badgeModel->getById($_POST['badge_id']);
        }
        $users = [];
        if (isset($_POST['search_users']) && !empty($_POST['search_query'])) {
            $users = $this->userModel->search($_POST['search_query']);
        } else {
            $users = $this->userModel->getAll();
        }
        $badges = $this->badgeModel->getAll();
        $userBadges = [];
        if (isset($revokeUserId)) {
            $userBadges = $this->badgeModel->getUserBadges($revokeUserId);
        }
        include __DIR__ . '/../views/admin/index.php';
    }
} 
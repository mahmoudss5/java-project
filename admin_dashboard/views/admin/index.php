<?php
// تحديد اللغة
session_start();
if (isset($_GET['lang'])) {
    $_SESSION['lang'] = $_GET['lang'];
}
$lang = $_SESSION['lang'] ?? 'en';

// نصوص الواجهة
$texts = [
    'en' => [
        'dashboard' => 'Admin Dashboard',
        'logout' => 'Logout',
        'search_users' => 'Search Users',
        'search_placeholder' => 'Enter name, email, or user ID',
        'search' => 'Search',
        'user_actions' => 'User Actions',
        'id' => 'ID',
        'name' => 'Name',
        'email' => 'Email',
        'actions' => 'Actions',
        'suspend' => 'Suspend',
        'flag' => 'Flag',
        'delete' => 'Delete',
        'revoke_badge' => 'Revoke Badge',
        'grant_badge' => 'Grant Badge',
        'badges' => 'Badges',
        'add_badge' => 'Add Badge',
        'description' => 'Description',
        'required_points' => 'Required Points',
        'privileges' => 'Privileges',
        'edit' => 'Edit',
        'cancel' => 'Cancel',
    ],
    'ar' => [
        'dashboard' => 'لوحة التحكم',
        'logout' => 'تسجيل الخروج',
        'search_users' => 'بحث المستخدمين',
        'search_placeholder' => 'ادخل الاسم أو البريد أو رقم المستخدم',
        'search' => 'بحث',
        'user_actions' => 'إجراءات المستخدم',
        'id' => 'المعرف',
        'name' => 'الاسم',
        'email' => 'البريد الإلكتروني',
        'actions' => 'الإجراءات',
        'suspend' => 'تعليق',
        'flag' => 'إبلاغ',
        'delete' => 'حذف',
        'revoke_badge' => 'سحب الوسام',
        'grant_badge' => 'منح وسام',
        'badges' => 'الأوسمة',
        'add_badge' => 'إضافة وسام',
        'description' => 'الوصف',
        'required_points' => 'النقاط المطلوبة',
        'privileges' => 'الصلاحيات',
        'edit' => 'تعديل',
        'cancel' => 'إلغاء',
    ]
];
$t = $texts[$lang];
?>
<!DOCTYPE html>
<html lang="<?= $lang ?>" dir="<?= $lang == 'ar' ? 'rtl' : 'ltr' ?>">
<head>
    <meta charset="UTF-8">
    <title><?= $t['dashboard'] ?></title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <style>
        body { background: #ecf0f3; }
        .card { border-radius: 12px; box-shadow: 0 2px 8px #0001; }
        .btn-custom { background: #6c63ff; color: #fff; }
        .btn-custom:hover { background: #554ee2; }
    </style>
</head>
<body>
<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2><?= $t['dashboard'] ?></h2>
        <div>
            <a href="?lang=en" class="btn btn-outline-primary btn-sm<?= $lang=='en'?' active':'' ?>">English</a>
            <a href="?lang=ar" class="btn btn-outline-primary btn-sm<?= $lang=='ar'?' active':'' ?>">العربية</a>
            <button class="btn btn-outline-danger ms-2"> <?= $t['logout'] ?> </button>
        </div>
    </div>
    <div class="card p-4 mb-4">
        <h5><?= $t['search_users'] ?></h5>
        <form method="post" class="row g-2 align-items-center mb-2">
            <div class="col-auto flex-grow-1">
                <input type="text" name="search_query" class="form-control" placeholder="<?= $t['search_placeholder'] ?>">
            </div>
            <div class="col-auto">
                <button type="submit" name="search_users" class="btn btn-custom"> <?= $t['search'] ?> </button>
            </div>
        </form>
    </div>
    <div class="card p-4 mb-4">
        <h5><?= $t['user_actions'] ?></h5>
        <table class="table table-bordered bg-white">
            <thead>
                <tr>
                    <th><?= $t['id'] ?></th>
                    <th><?= $t['name'] ?></th>
                    <th><?= $t['email'] ?></th>
                    <th>Status</th>
                    <th>Badges</th>
                    <th><?= $t['actions'] ?></th>
                </tr>
            </thead>
            <tbody>
            <?php foreach ($users as $user): ?>
                <tr>
                    <td><?= $user['id'] ?></td>
                    <td><?= htmlspecialchars($user['name']) ?></td>
                    <td><?= htmlspecialchars($user['email']) ?></td>
                    <td>
                        <?php if ($user['flag']): ?>
                            <span class="badge bg-danger">Flagged</span>
                        <?php else: ?>
                            <span class="badge bg-success">Active</span>
                        <?php endif; ?>
                    </td>
                    <td>
                        <?php 
                        $userBadges = $this->badgeModel->getUserBadges($user['id']);
                        foreach ($userBadges as $badge): 
                            $color = 'bg-secondary';
                            if ($badge['points'] < 100) $color = 'bg-success';
                            elseif ($badge['points'] < 500) $color = 'bg-primary';
                            elseif ($badge['points'] < 1000) $color = 'bg-warning text-dark';
                            else $color = 'bg-danger';
                        ?>
                            <span class="badge <?= $color ?> me-1 mb-1" title="<?= htmlspecialchars($badge['badge_description']) ?>">
                                <?= htmlspecialchars($badge['badge_name']) ?> (<?= $badge['points'] ?>)
                            </span>
                        <?php endforeach; ?>
                    </td>
                    <td>
                        <form method="post" style="display:inline;">
                            <input type="hidden" name="user_id" value="<?= $user['id'] ?>">
                            <?php if ($user['flag']): ?>
                                <button type="submit" name="unsuspend_user" class="btn btn-success btn-sm"> <?= $t['suspend'] ?> &#10003;</button>
                            <?php else: ?>
                                <button type="submit" name="suspend_user" class="btn btn-warning btn-sm"> <?= $t['suspend'] ?> </button>
                            <?php endif; ?>
                        </form>
                        <form method="post" style="display:inline;">
                            <input type="hidden" name="user_id" value="<?= $user['id'] ?>">
                            <button type="submit" name="delete_user" class="btn btn-danger btn-sm" onclick="return confirm('Are you sure?')"> <?= $t['delete'] ?> </button>
                        </form>
                        <form method="post" style="display:inline;">
                            <input type="hidden" name="user_id" value="<?= $user['id'] ?>">
                            <button type="submit" name="show_grant_badge" class="btn btn-success btn-sm"> <?= $t['grant_badge'] ?> </button>
                        </form>
                        <form method="post" style="display:inline;">
                            <input type="hidden" name="user_id" value="<?= $user['id'] ?>">
                            <button type="submit" name="show_revoke_badge" class="btn btn-outline-dark btn-sm"> <?= $t['revoke_badge'] ?> </button>
                        </form>
                    </td>
                </tr>
            <?php endforeach; ?>
            </tbody>
        </table>
    </div>
    <div class="card p-4 mb-4">
        <div class="d-flex justify-content-between align-items-center mb-2">
            <h5 class="mb-0"> <?= $t['badges'] ?> </h5>
            <button class="btn btn-custom" data-bs-toggle="modal" data-bs-target="#addBadgeModal"> <?= $t['add_badge'] ?> </button>
        </div>
        <table class="table table-bordered bg-white">
            <thead>
                <tr>
                    <th><?= $t['name'] ?></th>
                    <th><?= $t['description'] ?></th>
                    <th><?= $t['required_points'] ?></th>
                    <th><?= $t['privileges'] ?></th>
                    <th><?= $t['actions'] ?></th>
                </tr>
            </thead>
            <tbody>
            <?php foreach ($badges as $badge): ?>
                <tr>
                    <td><?= htmlspecialchars($badge['badge_name']) ?></td>
                    <td><?= htmlspecialchars($badge['badge_description']) ?></td>
                    <td><?= htmlspecialchars($badge['points']) ?></td>
                    <td><?= htmlspecialchars($badge['privilege']) ?></td>
                    <td>
                        <form method="post" style="display:inline;">
                            <input type="hidden" name="badge_id" value="<?= $badge['badge_id'] ?>">
                            <button type="submit" name="delete_badge" class="btn btn-danger btn-sm" onclick="return confirm('Are you sure?')"> <?= $t['delete'] ?> </button>
                        </form>
                        <form method="post" style="display:inline;">
                            <input type="hidden" name="badge_id" value="<?= $badge['badge_id'] ?>">
                            <button type="submit" name="show_edit_badge" class="btn btn-warning btn-sm"> <?= $t['edit'] ?> </button>
                        </form>
                    </td>
                </tr>
            <?php endforeach; ?>
            </tbody>
        </table>
    </div>
    <!-- Modal for Add Badge -->
    <div class="modal fade" id="addBadgeModal" tabindex="-1" aria-labelledby="addBadgeModalLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <form method="post" action="">
            <div class="modal-header">
              <h5 class="modal-title" id="addBadgeModalLabel"> <?= $t['add_badge'] ?> </h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <div class="mb-3">
                    <label class="form-label"> <?= $t['name'] ?> </label>
                    <input type="text" name="badge_name" class="form-control" required>
                </div>
                <div class="mb-3">
                    <label class="form-label"> <?= $t['description'] ?> </label>
                    <input type="text" name="badge_description" class="form-control" required>
                </div>
                <div class="mb-3">
                    <label class="form-label"> <?= $t['privileges'] ?> </label>
                    <input type="text" name="privilege" class="form-control" required>
                </div>
                <div class="mb-3">
                    <label class="form-label"> <?= $t['required_points'] ?> </label>
                    <input type="number" name="points" class="form-control" required>
                </div>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal"> <?= $t['cancel'] ?> </button>
              <button type="submit" name="add_badge" class="btn btn-success"> <?= $t['add_badge'] ?> </button>
            </div>
          </form>
        </div>
      </div>
    </div>
    <!-- Modal for Edit Badge -->
    <?php if (isset($editBadge) && $editBadge): ?>
    <div class="modal fade show" id="editBadgeModal" tabindex="-1" aria-labelledby="editBadgeModalLabel" aria-modal="true" style="display:block;background:#0003;">
      <div class="modal-dialog">
        <div class="modal-content">
          <form method="post" action="">
            <div class="modal-header">
              <h5 class="modal-title" id="editBadgeModalLabel"> <?= $t['edit'] . ' ' . $t['badges'] ?> </h5>
              <a href="index.php" class="btn-close"></a>
            </div>
            <div class="modal-body">
                <input type="hidden" name="badge_id" value="<?= $editBadge['badge_id'] ?>">
                <div class="mb-3">
                    <label class="form-label"> <?= $t['name'] ?> </label>
                    <input type="text" name="badge_name" class="form-control" value="<?= htmlspecialchars($editBadge['badge_name']) ?>" required>
                </div>
                <div class="mb-3">
                    <label class="form-label"> <?= $t['description'] ?> </label>
                    <input type="text" name="badge_description" class="form-control" value="<?= htmlspecialchars($editBadge['badge_description']) ?>" required>
                </div>
                <div class="mb-3">
                    <label class="form-label"> <?= $t['privileges'] ?> </label>
                    <input type="text" name="privilege" class="form-control" value="<?= htmlspecialchars($editBadge['privilege']) ?>" required>
                </div>
                <div class="mb-3">
                    <label class="form-label"> <?= $t['required_points'] ?> </label>
                    <input type="number" name="points" class="form-control" value="<?= htmlspecialchars($editBadge['points']) ?>" required>
                </div>
            </div>
            <div class="modal-footer">
              <a href="index.php" class="btn btn-secondary"> <?= $t['cancel'] ?> </a>
              <button type="submit" name="edit_badge" class="btn btn-success"> <?= $t['edit'] ?> </button>
            </div>
          </form>
        </div>
      </div>
    </div>
    <script>document.body.classList.add('modal-open');</script>
    <?php endif; ?>
    <!-- مودال منح بادج -->
    <?php if (isset($grantUserId)): ?>
    <div class="modal fade show" id="grantBadgeModal" tabindex="-1" aria-modal="true" style="display:block;background:#0003;">
      <div class="modal-dialog">
        <div class="modal-content">
          <form method="post" action="">
            <div class="modal-header">
              <h5 class="modal-title"> <?= $t['grant_badge'] ?> </h5>
              <a href="index.php" class="btn-close"></a>
            </div>
            <div class="modal-body">
                <input type="hidden" name="user_id" value="<?= $grantUserId ?>">
                <div class="mb-3">
                    <label class="form-label"> <?= $t['badges'] ?> </label>
                    <select name="badge_id" class="form-control" required>
                        <?php foreach ($badges as $badge): ?>
                            <option value="<?= $badge['badge_id'] ?>"> <?= htmlspecialchars($badge['badge_name']) ?> </option>
                        <?php endforeach; ?>
                    </select>
                </div>
            </div>
            <div class="modal-footer">
              <a href="index.php" class="btn btn-secondary"> <?= $t['cancel'] ?> </a>
              <button type="submit" name="grant_badge_to_user" class="btn btn-success"> <?= $t['grant_badge'] ?> </button>
            </div>
          </form>
        </div>
      </div>
    </div>
    <script>document.body.classList.add('modal-open');</script>
    <?php endif; ?>
    <!-- مودال سحب بادج -->
    <?php if (isset($revokeUserId)): ?>
    <div class="modal fade show" id="revokeBadgeModal" tabindex="-1" aria-modal="true" style="display:block;background:#0003;">
      <div class="modal-dialog">
        <div class="modal-content">
          <form method="post" action="">
            <div class="modal-header">
              <h5 class="modal-title"> <?= $t['revoke_badge'] ?> </h5>
              <a href="index.php" class="btn-close"></a>
            </div>
            <div class="modal-body">
                <input type="hidden" name="user_id" value="<?= $revokeUserId ?>">
                <div class="mb-3">
                    <label class="form-label"> <?= $t['badges'] ?> </label>
                    <select name="badge_id" class="form-control" required>
                        <?php foreach ($userBadges as $badge): ?>
                            <option value="<?= $badge['badge_id'] ?>"> <?= htmlspecialchars($badge['badge_name']) ?> </option>
                        <?php endforeach; ?>
                    </select>
                </div>
            </div>
            <div class="modal-footer">
              <a href="index.php" class="btn btn-secondary"> <?= $t['cancel'] ?> </a>
              <button type="submit" name="revoke_badge_from_user" class="btn btn-danger"> <?= $t['revoke_badge'] ?> </button>
            </div>
          </form>
        </div>
      </div>
    </div>
    <script>document.body.classList.add('modal-open');</script>
    <?php endif; ?>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 
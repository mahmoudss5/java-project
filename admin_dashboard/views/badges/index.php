<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Badge Management</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
</head>
<body class="bg-light">
<div class="container mt-5">
    <h2>Badges</h2>
    <a href="?action=add" class="btn btn-primary mb-3">Add Badge</a>
    <table class="table table-bordered bg-white">
        <thead>
            <tr>
                <th>Name</th>
                <th>Description</th>
                <th>Required Points</th>
                <th>Privileges</th>
                <th>Actions</th>
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
                    <a href="?action=edit&id=<?= $badge['badge_id'] ?>" class="btn btn-warning btn-sm">Edit</a>
                    <a href="?action=delete&id=<?= $badge['badge_id'] ?>" class="btn btn-danger btn-sm" onclick="return confirm('Are you sure?')">Delete</a>
                </td>
            </tr>
        <?php endforeach; ?>
        </tbody>
    </table>
</div>
</body>
</html> 
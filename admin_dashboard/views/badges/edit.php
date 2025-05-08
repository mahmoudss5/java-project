<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit Badge</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
</head>
<body class="bg-light">
<div class="container mt-5">
    <h2>Edit Badge</h2>
    <form method="post">
        <div class="mb-3">
            <label class="form-label">Name</label>
            <input type="text" name="badge_name" class="form-control" value="<?= htmlspecialchars($badge['badge_name']) ?>" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Description</label>
            <input type="text" name="badge_description" class="form-control" value="<?= htmlspecialchars($badge['badge_description']) ?>" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Privileges</label>
            <input type="text" name="privilege" class="form-control" value="<?= htmlspecialchars($badge['privilege']) ?>" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Required Points</label>
            <input type="number" name="points" class="form-control" value="<?= htmlspecialchars($badge['points']) ?>" required>
        </div>
        <button type="submit" class="btn btn-success">Update</button>
        <a href="?action=index" class="btn btn-secondary">Cancel</a>
    </form>
</div>
</body>
</html> 
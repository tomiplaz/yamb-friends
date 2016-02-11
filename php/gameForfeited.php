<?php
	require_once 'config.php';
	require_once 'library.php';

	$username = $_POST['username'];

	if ($username != null) {
		$user = mysqli_fetch_assoc(db_query("SELECT games_forfeited FROM users WHERE username='$username'"));

		$gamesForfeitedNew = $user['games_forfeited'] + 1;

		db_query("UPDATE users SET games_forfeited=$gamesForfeitedNew WHERE username='$username'");
	}
?>
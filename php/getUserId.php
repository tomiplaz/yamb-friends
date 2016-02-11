<?php
	require_once 'config.php';
	require_once 'library.php';

	$username = $_GET['username'];

	$user = mysqli_fetch_assoc(db_query("SELECT id FROM users WHERE username='$username'"));

	if ($user == null) {
		echo "User not found.";
	} else {
		echo $user['id'];
	}
?>
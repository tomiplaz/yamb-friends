<?php
	require_once 'config.php';
	require_once 'library.php';

	$username = $_POST['username'];
	$password = $_POST['password'];

	// Check validity of values on frontend

	if (mysqli_fetch_array(db_query("SELECT * FROM users WHERE username='$username'"))) {
		echo "Username not available.";
	} else {
		$hashedPassword = sha1($password);
		db_query("INSERT INTO users (username, password) VALUES ('$username', '$hashedPassword')");
		echo "Account created.";
	}
?>
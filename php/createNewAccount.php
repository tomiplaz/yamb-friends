<?php
	require_once 'config.php';
	require_once 'library.php';

	$username = $_POST['username'];
	$password = $_POST['password'];

	if (mysqli_fetch_assoc(db_query("SELECT id FROM users WHERE username='$username'")) != null) {
		echo "Username not available";
	} else {
		$hashedPassword = sha1($password);
		$timeRegistered = date("Y-m-d H:i:s");

		db_query("INSERT INTO users (username, password, time_registered) VALUES ('$username', '$hashedPassword', '$timeRegistered')");
		echo "Account created";
	}
?>
<?php
	require_once 'config.php';
	require_once 'library.php';

	$username = $_POST['username'];
	$password = $_POST['password'];

	$user = mysqli_fetch_assoc(db_query("SELECT password FROM users WHERE username='$username'"));

	if ($user == null) {
		echo "User not found";
	} elseif ($user['password'] != sha1($password)) {
		echo "Invalid password";
	} else {
		echo "Signed in";
	}
?>
<?php
	require_once 'config.php';
	require_once 'library.php';

	$username = $_POST['username'];
	$image = $_POST['image'];

	$user = mysqli_fetch_array(db_query("SELECT * FROM users WHERE username='$username'"));
	$userId = $user['id'];
	$path = "images/$userId.jpeg";

	if (file_put_contents($path, base64_decode($image)) === false) {
		echo "Image upload failed.";
	} else {
		echo "Image uploaded.";
	}
?>
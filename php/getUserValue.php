<?php
	require_once 'config.php';
	require_once 'library.php';

	$username = $_GET['username'];
	$field = $_GET['field'];

	$user = mysqli_fetch_assoc(db_query("SELECT $field FROM users WHERE username='$username'"));

	echo $user[$field];
?>
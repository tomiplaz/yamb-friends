<?php
	require_once 'config.php';
	require_once 'library.php';

	$users = db_query("SELECT id FROM users");

	echo mysqli_num_rows($users);
?>
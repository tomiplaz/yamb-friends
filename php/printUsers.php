<?php
	require_once 'config.php';
	require_once 'library.php';

	header('Content-Type: text/html; charset=utf-8');
	$users = db_query("SELECT username FROM users");
	while ($result = mysqli_fetch_array($users)) {
		echo $result['username'] . "</br>";
	}
?>
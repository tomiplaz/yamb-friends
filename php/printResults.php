<?php
	require_once 'config.php';
	require_once 'library.php';

	header('Content-Type: text/html; charset=utf-8');
	$results = db_query("SELECT username, type, result FROM games");
	while ($result = mysqli_fetch_array($results)) {
		echo $result['username'] . ", " . $result['type'] . ", " . $result['result'] . "</br>";
	}
?>
<?php
	require_once 'config.php';
	require_once 'library.php';

	$type = $_GET['type'];
	$number = $_GET['number'] - 1;

	$query = db_query("SELECT username, result FROM games WHERE type='$type'
		AND username!='' ORDER BY result DESC LIMIT 10");

	$games = array();
	while ($result = mysqli_fetch_array($query)) {
		array_push($games, $result);
	}

	if (isset($games[$number])) {
		$game = $games[$number];
		header('Content-Type: text/html; charset=utf-8');
		echo $game['username'] . " (" . $game['result'] . ")";
	} else {
		echo "Result not found";
	}
?>
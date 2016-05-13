<?php
	require_once 'config.php';
	require_once 'library.php';

	header('Content-Type: application/json; charset=utf-8');
	$results = db_query("SELECT username, type, result FROM games");
	$data = array();
	while ($result = mysqli_fetch_array($results)) {
		$data[] = array(
			"username" => $result['username'],
			"type" => $result['type'],
			"result" => $result['result']
		);
		//echo $result['username'] . ", " . $result['type'] . ", " . $result['result'] . "</br>";
	}
	echo json_encode($data);
?>
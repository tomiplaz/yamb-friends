<?php
	require_once 'config.php';
	require_once 'library.php';

	$username = $_POST['username'];
	$type = $_POST['type'];
	$game = $_POST['game'];
	$result = $_POST['result'];
	$duration = $_POST['duration'];
	$latitude = $_POST['latitude'];
	$longitude = $_POST['longitude'];

	$timeFinished = date("Y-m-d H:i:s");

	db_query("INSERT INTO games (username, type, game, result, duration, time_finished) 
		VALUES ('$username', '$type', '$game', '$result', '$duration', '$timeFinished')");

	if ($username != null) {
		$user = mysqli_fetch_assoc(db_query("SELECT * FROM users WHERE username='$username'"));

		$typePlayedField = $type . '_played';
		$typeBestField = $type . '_best';
		$typeAverageField = $type . '_average';

		$latitudeOld = $user['latitude'];
		$longitudeOld = $user['longitude'];
		$gamesPlayedOld = $user['games_played'];
		$totalTimePlayedOld = $user['total_time_played'];
		$averageGameDurationOld = $user['average_game_duration'];
		$typePlayedOld = $user[$typePlayedField];
		$typeBestOld = $user[$typeBestField];
		$typeAverageOld = $user[$typeAverageField];

		$latitudeNew = ($latitude == 0) ? $latitudeOld : $latitude;
		$longitudeNew = ($longitude == 0) ? $longitudeOld : $longitude;
		$gamesPlayedNew = $gamesPlayedOld + 1;
		$totalTimePlayedNew = $totalTimePlayedOld + $duration;
		$averageGameDurationNew = round(($averageGameDurationOld * $gamesPlayedOld + $duration) / $gamesPlayedNew);
		$typePlayedNew = $typePlayedOld + 1;
		$typeBestNew = ($result > $typeBestOld) ? $result : $typeBestOld;
		$typeAverageNew = number_format(($typeAverageOld * $typePlayedOld + $result) / $typePlayedNew, 2, ".", "");

		db_query("UPDATE users SET latitude=$latitudeNew, longitude=$longitudeNew, games_played=$gamesPlayedNew,
			total_time_played=$totalTimePlayedNew, average_game_duration=$averageGameDurationNew,
			$typePlayedField=$typePlayedNew, $typeBestField=$typeBestNew, $typeAverageField=$typeAverageNew
			WHERE username='$username'");
	}
?>
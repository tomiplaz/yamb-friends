<?php
	require_once 'config.php';
	require_once 'library.php';

	$username = $_GET['username'];

	$user = mysqli_fetch_assoc(db_query("SELECT latitude, longitude FROM users WHERE username='$username'"));

	$query = db_query("SELECT username, latitude, longitude FROM users WHERE username!='$username'");

	$otherUsers = array();
	while ($result = mysqli_fetch_array($query)) {
		array_push($otherUsers, $result);
	}

	if (count($otherUsers) == 0 || ($user['latitude'] == 0 && $user['longitude'] == 0)) {
		echo "No neighbour.";
	} else {
		$pk = 180.0 / pi();
		$a1 = $user['latitude'] / $pk;
		$a2 = $user['longitude'] / $pk;
		$auxDistance = null;
		$auxNeighbour = null;

		foreach ($otherUsers as $otherUser) {
			if ($otherUser['latitude'] == 0 && $otherUser['longitude'] == 0) {
				continue;
			} else {
				$b1 = $otherUser['latitude'] / $pk;
				$b2 = $otherUser['longitude'] / $pk;

				$t1 = cos($a1) * cos($a2) * cos($b1) * cos($b2);
				$t2 = cos($a1) * sin($a2) * cos($b1) * sin($b2);
				$t3 = sin($a1) * sin($b1);
				$tt = acos($t1 + $t2 + $t3);

				$d = 6366000 * $tt;

				if ($auxNeighbour == null) {
					$auxDistance = $d;
					$auxNeighbour = $otherUser['username'];
				} elseif ($d < $auxDistance) {
					$auxDistance = $d;
					$auxNeighbour = $otherUser['username'];
				}
			}
		}
	}

	if ($auxNeighbour != null) {
		echo $auxNeighbour;
	} else {
		echo "No neighbour.";
	}
?>
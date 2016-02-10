<?php	
	function escape($string, $conn){
		$string = stripslashes($string);
		$string = mysqli_real_escape_string($conn, $string);
		return $string;
	}

	function escapePOST($conn){
		$keys = array_keys($_POST);
		$counter = 0;
		foreach($_POST as $value){
			$key = $keys[$counter];
			$_POST[$key] = escape($_POST[$key], $conn);
			$counter++;
		}
	}

	function db_query($query){
		$conn = mysqli_connect(DB_HOST, DB_USER, DB_PASS, DB_NAME) or die('Error while connecting to MySQL database!');
		mysqli_query($conn, "SET NAMES 'UTF8'");
		mysqli_query($conn, "SET character_set_client='UTF8'");
		mysqli_query($conn, "SET CHARACTER SET utf8");
		mysqli_query($conn, "SET COLLATION_CONNECTION='utf8_general_ci'");
		escapePOST($conn);
		$rs = mysqli_query($conn, $query) or die(mysqli_error($conn));
		mysqli_close($conn);
		return $rs;
	}
?>
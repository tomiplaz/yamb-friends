<?php
    require_once 'config.php';
    require_once 'library.php';

    header('Content-Type: application/json; charset=utf-8');

    $query = db_query("SELECT username, result FROM games WHERE type='an1d5'
            AND username IS NOT NULL ORDER BY result DESC LIMIT 10");

    $games = array();
    while ($result = mysqli_fetch_array($query)) {
        $games[] = $result;
    }

    echo json_encode($games);
?>
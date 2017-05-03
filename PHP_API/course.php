<?php
require_once 'include/DB_Functions.php';
$db = new DB_Functions();
$courses = $db->getCourses();
$response = array();

foreach( $courses as $course) {
    $response[] = array("title" => $course[0], "description" => $course[1]);
}

echo json_encode(array('error'=> false, 'data' => $response));
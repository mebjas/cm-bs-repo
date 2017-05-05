<?php
require_once 'include/DB_Functions.php';
$db = new DB_Functions();

if (isset($_POST["get"]) && isset($_POST["uid"]) && isset($_POST["email"])) {
    $courses = $db->getCourses();
    $response = array();
    foreach( $courses as $course) {
        $response[] = array(
            "id" => $course[0],
            "title" => $course[1], 
            "description" => $course[2],
            "isFav" => false);
    }

    $uid = $_POST["uid"];
    $email = $_POST["email"];
    $myFav = $db->getMyFavs($uid, $email);

    if ($myFav != null) {
        foreach($myFav as $favCourseID) {
            foreach( $response as $index => $obj) {
                if ($obj["id"] == $favCourseID) {
                    $response[$index]["isFav"] = true;
                }
            }
        }
    }
    echo json_encode(array('error'=> false, 'data' => $response));
} else if (
    isset($_POST["set"]) 
    && isset($_POST["uid"]) 
    && isset($_POST["email"]) 
    && isset($_POST['currentState'])) {
    $uid = $_POST["uid"];
    $cid = $_POST["set"];
    $email = $_POST["email"];
    $currentState = $_POST["currentState"];

    if ($currentState == "set") {
        $db->unsetFav($uid, $cid);
    } else {
        $db->setFav($uid, $cid);
    }
    echo json_encode(array('error' => false));
} else {
    echo json_encode(array('error' => true, 'err_msg' => 'Invalid request'));
}


<?php

require_once 'include/DB_Functions.php';
$db = new DB_Functions();

// json response array
$response = array("error" => FALSE);

if (isset($_POST['email']) && isset($_POST['password'])) {
    
    // receiving the post params
    $email = $_POST['email'];
    $password = $_POST['password'];

    if($db->deleteProfile($email, $password)) {
        // deleted
        $response['message'] = "deleted successfully";
        echo json_encode($response);
        exit;
    }

    // required post params is missing
    $response['error'] = TRUE;
    $response['error_msg'] = "unable to delete";
    echo json_encode($response);

} else {
    // required post params is missing
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameters email or password is missing!";
    echo json_encode($response);
}
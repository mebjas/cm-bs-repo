<?php

require_once 'include/DB_Functions.php';
$db = new DB_Functions();

// json response array
$response = array("error" => FALSE);

if (isset($_POST['email']) && isset($_POST['password'])
    && isset($_POST['name']) && isset($_POST['newpassword'])) {
    
    // receiving the post params
    $name = $_POST['name'];
    $email = $_POST['email'];
    $password = $_POST['password'];
    $newpassword = $_POST['newpassword'];

    $user = $db->updateProfile($email, $name, $password, $newpassword);
    if ($user == NULL) {
        $response['error'] = TRUE;
        $response["error_msg"] = "Unable to update user information, wrong password";
        echo json_encode($response);
        exit;
    }

    // required post params is missing
    $response['message'] = "Account details updated";
    echo json_encode($response);

} else {
    // required post params is missing
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameters email or password is missing!";
    echo json_encode($response);
}
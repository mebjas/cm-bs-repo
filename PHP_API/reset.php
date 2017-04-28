<?php

/**
 * @author Ravi Tamada
 * @link http://www.androidhive.info/2012/01/android-login-and-registration-with-php-mysql-and-sqlite/ Complete tutorial
 */

require_once 'include/DB_Functions.php';
$db = new DB_Functions();

// json response array
$response = array("error" => FALSE);

if (isset($_POST['code']) && isset($_POST['password']) && isset($_POST['email'])) {

    // receiving the post params
    $email = $_POST['email'];
    $password = $_POST['password'];
    $code = $_POST['code'];
    $code = $db->setNewPassword($email, $password, $code);

    // todo - send a mail and stuff
    // return success
    $response['error'] = FALSE;
    echo json_encode($response);
} else if (isset($_POST['email'])) {

    // receiving the post params
    $email = $_POST['email'];
    $code = $db->resetPassword($email);

    // todo - send a mail and stuff
    // return success
    $response['error'] = FALSE;
    $response['code'] = $code;
    echo json_encode($response);
} else {
    // required post params is missing
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameters email is missing!";
    echo json_encode($response);
} 
?>


<?php

/**
 * @author Ravi Tamada
 * @link http://www.androidhive.info/2012/01/android-login-and-registration-with-php-mysql-and-sqlite/ Complete tutorial
 */

class DB_Functions {

    private $conn;

    // constructor
    function __construct() {
        require_once 'DB_Connect.php';
        // connecting to database
        $db = new Db_Connect();
        $this->conn = $db->connect();
    }

    // destructor
    function __destruct() {
    }

    public function getCourses() {
        $stmt = $this->conn->prepare("SELECT title, description FROM Courses");
        if ($stmt->execute()) {
            $courses = $stmt->get_result()->fetch_all();
            $stmt->close();
            return $courses;
        }
        return null;
    }

    /**
     * Storing new user
     * returns user details
     */
    public function storeUser($name, $email, $password) {
        $uuid = uniqid('', true);
        $hash = $this->hashSSHA($password);
        $encrypted_password = $hash["encrypted"]; // encrypted password
        $salt = $hash["salt"]; // salt

        $stmt = $this->conn->prepare("INSERT INTO users(unique_id, name, email, encrypted_password, salt, created_at) VALUES(?, ?, ?, ?, ?, NOW())");
        $stmt->bind_param("sssss", $uuid, $name, $email, $encrypted_password, $salt);
        $result = $stmt->execute();
        $stmt->close();

        // check for successful store
        if ($result) {
            $stmt = $this->conn->prepare("SELECT * FROM users WHERE email = ?");
            $stmt->bind_param("s", $email);
            $stmt->execute();
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();

            return $user;
        } else {
            return false;
        }
    }

    /**
     * Get user by email and password
     */
    public function getUserByEmailAndPassword($email, $password) {

        $stmt = $this->conn->prepare("SELECT * FROM users WHERE email = ?");

        $stmt->bind_param("s", $email);

        if ($stmt->execute()) {
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();

            // verifying user password
            $salt = $user['salt'];
            $encrypted_password = $user['encrypted_password'];
            $hash = $this->checkhashSSHA($salt, $password);
            // check for password equality
            if ($encrypted_password == $hash) {
                // user authentication details are correct

                // if ($user['session_id'] == null) {
                //     $user['session_id'] = $this->createSession($user);
                // }
                return $user;
            }
        } else {
            return NULL;
        }
    }

    public function resetPassword($email) {
        $code = substr(md5(uniqid()), 0, 4);
        $stmt = $this->conn->prepare("UPDATE users SET reset_code = ? WHERE email = ?");
        $stmt->bind_param("ss", $code, $email);
        $stmt->execute();
        return $code;
    }

    public function setNewPassword($email, $password, $code) {
        $stmt = $this->conn->prepare("SELECT * FROM `users` WHERE `email` = ? AND `reset_code` = ?");

        $stmt->bind_param("ss", $email, $code);

        if ($stmt->execute()) {
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();

            $hash = $this->hashSSHA($password);
            $encrypted_password = $hash["encrypted"]; // encrypted password
            $salt = $hash["salt"]; // salt
            $stmt = $this->conn->prepare("UPDATE `users` SET encrypted_password = ? , salt = ? WHERE `email` = ?");
            $stmt->bind_param("sss", $encrypted_password, $salt, $email);
            if ($stmt->execute()) {
                $stmt->close();
                return true;
            }
        }
        return false;
    }

    public function deleteProfile($email, $password) {
        $stmt = $this->conn->prepare("SELECT * FROM users WHERE email = ?");
        $stmt->bind_param("s", $email);

        if ($stmt->execute()) {
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();

            // verifying user password
            $id = $user['id'];
            $salt = $user['salt'];
            $encrypted_password = $user['encrypted_password'];
            $hash = $this->checkhashSSHA($salt, $password);
            // check for password equality
            if ($encrypted_password == $hash) {
                // delete
                return $this->_deleteAccount($email);
            }
        } else {
            return FALSE;
        }
    }

    private function _deleteAccount($email) {
        $stmt = $this->conn->prepare("DELETE FROM users WHERE email = ?");
        $stmt->bind_param("s", $email);
        if ($stmt->execute()) {
            $stmt->close();
            return true;
        }
        return false;
    }

    public function updateProfile($email, $name, $password, $newpassword) {
        $stmt = $this->conn->prepare("SELECT * FROM users WHERE email = ?");
        $stmt->bind_param("s", $email);

        if ($stmt->execute()) {
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();

            // verifying user password
            $id = $user['id'];
            $salt = $user['salt'];
            $encrypted_password = $user['encrypted_password'];
            $hash = $this->checkhashSSHA($salt, $password);
            // check for password equality
            if ($encrypted_password == $hash) {
                // correct password
                if (!$this->_updateUser($email, $name, $id)) return NULL;
                if (strlen(trim($newpassword))) {
                    if (!$this->_updateUserPassword($newpassword, $id)) return NULL;
                }
                return $user;
            }
        } else {
            return NULL;
        }
    }

    private function _updateUser($email, $name, $id) {
        $stmt = $this->conn->prepare("UPDATE `users` SET name = ? , email = ? WHERE `id` = ?");
        $stmt->bind_param("sss", $name, $email, $id);
        if ($stmt->execute()) {
            $stmt->close();
            return true;
        }
        return false;
    }

    private function _updateUserPassword($newpassword, $id) {
        $hash = $this->hashSSHA($newpassword);
        $encrypted_password = $hash["encrypted"]; // encrypted password
        $salt = $hash["salt"]; // salt
        $stmt = $this->conn->prepare("UPDATE `users` SET encrypted_password = ? , salt = ? WHERE `id` = ?");
        $stmt->bind_param("sss", $encrypted_password, $salt, $id);
        if ($stmt->execute()) {
            $stmt->close();
            return true;
        }
        return false;
    }

    /**
     * Get user by email and password
     */
    public function getUserBySessionId($session_id) {

        $stmt = $this->conn->prepare("SELECT *, sessions.* FROM users 
            LEFT JOIN sessions on sessions.user_id = users.id
            WHERE sessions.session_id = ?");

        $stmt->bind_param("s", $session_id);

        if ($stmt->execute()) {
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();

            return $user;
        } else {
            return NULL;
        }
    }

    /**
     * Check user is existed or not
     */
    public function isUserExisted($email) {
        $stmt = $this->conn->prepare("SELECT email from users WHERE email = ?");

        $stmt->bind_param("s", $email);

        $stmt->execute();

        $stmt->store_result();

        if ($stmt->num_rows > 0) {
            // user existed 
            $stmt->close();
            return true;
        } else {
            // user not existed
            $stmt->close();
            return false;
        }
    }

    /**
     * Create a session if not exists
     */
    public function createSession($user) {
        $id = $user["id"];
        $session_id = $this->createSessionID();

        $stmt2 = $this->conn->prepare("INSERT INTO `sessions`(`user_id`, `session_id`) VALUES (?, ?)");
        $stmt2->bind_param("is", $id, $session_id);
        $stmt2->execute();
        $stmt2->close();
        return $session_id;
     }

    /**
     * Get session ID for customer
     */
     private function createSessionID() {
         return md5(uniqid());
     }

    /**
     * Encrypting password
     * @param password
     * returns salt and encrypted password
     */
    public function hashSSHA($password) {

        $salt = sha1(rand());
        $salt = substr($salt, 0, 10);
        $encrypted = base64_encode(sha1($password . $salt, true) . $salt);
        $hash = array("salt" => $salt, "encrypted" => $encrypted);
        return $hash;
    }

    /**
     * Decrypting password
     * @param salt, password
     * returns hash string
     */
    public function checkhashSSHA($salt, $password) {

        $hash = base64_encode(sha1($password . $salt, true) . $salt);

        return $hash;
    }

}

?>

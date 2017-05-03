package todofy.cm.app;

import java.net.URL;

/**
 * Created by minhazv on 4/28/2017.
 */

public class AppConfig {
    public static String URL_BASE = "http://192.168.1.10:9999/PHP_API/";

    public static String URL_LOGIN() {
        return URL_BASE + "login.php";
    }

    // Server user register url
    public static String URL_REGISTER() { return  URL_BASE + "register.php"; }

    // Server user update url
    public static String URL_UPDATE() { return URL_BASE + "update.php"; }

    // Server user delete url
    public static String URL_DELETE() {return URL_BASE + "delete.php"; }

    // Server user register url
    public static String URL_RESET() {return URL_BASE + "reset.php"; }

    // Get courses
    public static String URL_COURSES() {return URL_BASE + "course.php";}
}

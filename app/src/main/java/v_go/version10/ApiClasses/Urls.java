package v_go.version10.ApiClasses;


public class URLs {

    private static final String SERVER_ROOT_URL = "http://ec2-52-40-59-253.us-west-2.compute.amazonaws.com:8080";
    private static final String LOCALHOST_ROOT_URL = "http://192.168.1.70:8080";

    // current target root url
    public static final String TARGET_ROOT_URL = SERVER_ROOT_URL;

    // server URLs
    public static final String LOGIN_URL = TARGET_ROOT_URL + "/login";
    public static final String LOGOUT_URL = TARGET_ROOT_URL + "/logout";
    public static final String UPLOAD_AVATAR_URL = TARGET_ROOT_URL + "/upload_avatar";
    public static final String DOWNLOAD_AVATAR_URL = TARGET_ROOT_URL + "/get_avatar";
    public static final String SEND_CODE_URL = TARGET_ROOT_URL + "/account/sendcode";
    public static final String VERIFY_CODE_URL = TARGET_ROOT_URL + "/account/verify";
    public static final String REGISTER_URL = TARGET_ROOT_URL + "/account/register";
    public static final String GET_USER_INFO_URL = TARGET_ROOT_URL + "/account/getUserInfo";
    public static final String GET_CHAT_HISTORY_URL = TARGET_ROOT_URL + "/chat_history";
    public static final String GET_CONTACT_LIST_URL = TARGET_ROOT_URL + "/get_contact_list";
    public static final String ADD_CONTACT_URL_ = TARGET_ROOT_URL + "/add_contact";

    // localhost API test URLs
    public static final String LOCAL_HOST_URL = "http://127.0.0.1:8080";
    public static final String LOCAL_LOGIN_URL = "http://127.0.0.1:8080/login";
    public static final String LOCAL_LOGOUT_URL = "http://127.0.0.1:8080/logout";
    public static final String LOCAL_REGISTER_URL = "http://127.0.0.1:8080/register";
}

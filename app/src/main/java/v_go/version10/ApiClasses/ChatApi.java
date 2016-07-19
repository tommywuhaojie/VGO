package v_go.version10.ApiClasses;

import org.json.JSONArray;
import org.json.JSONObject;

public class ChatApi {

    public static JSONObject AddNewContact(String other_user_id){
        String data="other_user_id="+other_user_id;
        return REST.PerformPostRequestForJSONObject(Urls.ADD_CONTACT_URL, data);
    }

    public static JSONObject GetSingleContact(String other_user_id){
        String data="other_user_id="+other_user_id;
        return REST.PerformPostRequestForJSONObject(Urls.GET_SINGLE_CONTACT_URL, data);
    }

    public static JSONObject ReadAllMessage(String other_user_id){
        String data="other_user_id="+other_user_id;
        return REST.PerformPostRequestForJSONObject(Urls.READ_ALL_MESSAGE_URL, data);
    }

    public static JSONArray GetContactList(){
        return REST.PerformPostRequestForJSONArray(Urls.GET_CONTACT_LIST_URL, null);
    }

    public static JSONArray GetChatHistory(String other_user_id, int number_of_message){
        String data = "other_user_id="+other_user_id+"&number_of_message="+number_of_message;
        return REST.PerformPostRequestForJSONArray(Urls.GET_CHAT_HISTORY_URL, data);
    }

}

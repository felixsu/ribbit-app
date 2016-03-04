package felix.com.ribbit.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by rrsari on 01/03/2016.
 */
@ParseClassName("Message")
public class Message extends ParseObject {
    public static final String USER_ID_KEY = "user_id";
    public static final String BODY_KEY = "body";
    public static final String CLIENT_ID="recipient_id";

    public String getUserId() {
        return getString(USER_ID_KEY);
    }

    public String getBody() {
        return getString(BODY_KEY);
    }

    public void setUserId(String userId) {
        put(USER_ID_KEY, userId);
    }

    public void setBody(String body) {
        put(BODY_KEY, body);
    }

    public String getClientId(){
        return getString(CLIENT_ID);
    }

    public void setClientId(String client){
       put(CLIENT_ID,client);
    }
}


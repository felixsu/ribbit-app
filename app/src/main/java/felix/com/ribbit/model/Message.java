package felix.com.ribbit.model;

/**
 * Created by rrsari on 01/03/2016.
 */

public class Message {
    public static final String USER_ID_KEY = "user_id";
    public static final String BODY_KEY = "body";
    public static final String CLIENT_ID = "recipient_id";

    public String getUserId() {
        return (USER_ID_KEY);
    }

    public void setUserId(String userId) {
    }

    public String getBody() {
        return (BODY_KEY);
    }

    public void setBody(String body) {
    }

    public String getClientId() {
        return (CLIENT_ID);
    }

    public void setClientId(String client) {

    }
}


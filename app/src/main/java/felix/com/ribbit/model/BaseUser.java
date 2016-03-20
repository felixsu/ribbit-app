package felix.com.ribbit.model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by fsoewito on 3/19/2016.
 */
public class BaseUser extends HashMap<String, Object> implements Serializable {

    private static final String KEY_PASSWORD = "key-password";
    private static final String KEY_EMAIL = "key-email";

    public BaseUser() {
    }

    public String getPassword() {
        if (containsKey(KEY_PASSWORD)) {
            return (String) get(KEY_PASSWORD);
        } else {
            throw new IllegalStateException("password null");
        }
    }

    public void setPassword(String password) {
        password = password.trim();
        put(KEY_PASSWORD, password);
    }

    public String getEmail() {
        if (containsKey(KEY_EMAIL)) {
            return (String) get(KEY_EMAIL);
        } else {
            throw new IllegalStateException("email null");
        }
    }

    public void setEmail(String email) {
        email = email.trim();
        put(KEY_EMAIL, email);
    }


}

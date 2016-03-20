package felix.com.ribbit.model;

import com.firebase.client.Firebase;

/**
 * Created by fsoewito on 3/19/2016.
 */
public class RibbitBase {
    protected static final String RIBBIT_ROOT = "https://ribbit-app-firebase.firebaseio.com";
    protected static final String RIBBIT_DATA = RIBBIT_ROOT + "/data";
    protected static final String RIBBIT_USERS = RIBBIT_DATA + "/users";

    protected static final Firebase ROOT = new Firebase(RIBBIT_ROOT);
    protected static final Firebase USERS = new Firebase(RIBBIT_USERS);

    public static Firebase getRoot() {
        return ROOT;
    }

    public static Firebase getUsers() {
        return USERS;
    }

}

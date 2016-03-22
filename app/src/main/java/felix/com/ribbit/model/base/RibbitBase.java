package felix.com.ribbit.model.base;

import com.firebase.client.Firebase;

/**
 * Created by fsoewito on 3/19/2016.
 */
public class RibbitBase {
    protected static final String RIBBIT_ROOT = "https://ribbit-app-firebase.firebaseio.com";
    protected static final String RIBBIT_DATA = RIBBIT_ROOT + "/data";

    protected static final Firebase ROOT = new Firebase(RIBBIT_ROOT);

    public static Firebase getRoot() {
        return ROOT;
    }



}

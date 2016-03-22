package felix.com.ribbit.model.ribbit;

import com.firebase.client.Firebase;

import felix.com.ribbit.model.base.RibbitBase;

/**
 * Created by fsoewito on 3/22/2016.
 */
public class RibbitFriend extends RibbitBase {

    protected static final Firebase FRIENDS = new Firebase(RIBBIT_DATA + "/friends");
}

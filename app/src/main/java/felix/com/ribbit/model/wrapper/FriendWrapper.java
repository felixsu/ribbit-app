package felix.com.ribbit.model.wrapper;

import com.firebase.client.Firebase;

import felix.com.ribbit.model.firebase.FriendData;
import felix.com.ribbit.model.ribbit.RibbitFriend;

/**
 * Created by fsoewito on 3/22/2016.
 */
public class FriendWrapper extends RibbitWrapper<FriendData> {

    @Override
    public Firebase getFirebase() {
        return RibbitFriend.getFirebaseFriend();
    }
}

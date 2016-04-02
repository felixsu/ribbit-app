package felix.com.ribbit.model.wrapper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.firebase.client.Firebase;

import felix.com.ribbit.model.firebase.FriendData;
import felix.com.ribbit.model.firebase.UserData;
import felix.com.ribbit.model.ribbit.RibbitFriend;

/**
 * Created by fsoewito on 3/22/2016.
 */
public class FriendWrapper extends RibbitWrapper<FriendData> {

    public FriendWrapper() {
    }

    public FriendWrapper(UserWrapper user){
        mId = user.getId();
        mData = new FriendData(user.getData());
    }

    @Override
    @JsonIgnore()
    public Firebase getFirebase() {
        return RibbitFriend.getFirebaseFriend();
    }
}

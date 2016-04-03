package felix.com.ribbit.model.wrapper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import felix.com.ribbit.listener.RibbitResultListener;
import felix.com.ribbit.model.firebase.FriendData;
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

    public void store(String userUid, RibbitResultListener resultListener) {
        Firebase firebaseFriend = getFirebase().child("/" + userUid).child("/" + mId);
        firebaseFriend.setValue(mData, new FriendStoreListener(resultListener));
    }

    private class FriendStoreListener implements Firebase.CompletionListener {

        private final RibbitResultListener mResultListener;

        private FriendStoreListener(RibbitResultListener resultListener) {
            mResultListener = resultListener;
        }

        @Override
        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
            mResultListener.onFinish();
            if (firebaseError == null) {
                mResultListener.onSuccess();
            } else {
                mResultListener.onError(firebaseError.toException(), firebaseError.getMessage());
            }
        }
    }
}

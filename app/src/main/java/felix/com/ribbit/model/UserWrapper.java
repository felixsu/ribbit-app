package felix.com.ribbit.model;

import android.util.Log;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;

import felix.com.ribbit.listener.RibbitListener;
import felix.com.ribbit.util.Util;

/**
 * Created by fsoewito on 3/20/2016.
 */
public class UserWrapper extends RibbitWrapper<UserData> {

    private static final String TAG = UserWrapper.class.getName();

    private static final String KEY_CREATED_AT = "key-created-at";
    private static final String KEY_UPDATED_AT = "key-updated-at";

    public UserWrapper(String uid, UserData data) {
        mUid = uid;
        mData = data;
    }

    public UserWrapper() {
        if (mData == null) {
            mData = new UserData();
        }
    }

    public void signUp(final RibbitListener listener) {
        Firebase root = Ribbit.getRoot();

        String email = mData.getEmail();
        String password = mData.getPassword();
        root.createUser(email, password, new SignUpListener(this, listener));
    }

    public void update(final RibbitListener listener) {
        Firebase usersPath = Ribbit.getUsers().child("/" + mUid);
        mData.updateDate();
        HashMap m = Util.getMapperInstance().convertValue(mData, HashMap.class);
        usersPath.updateChildren(m);
    }

    private class SignUpListener implements Firebase.ValueResultHandler<Map<String, Object>> {
        final UserWrapper mUserWrapper;
        final RibbitListener mListener;

        public SignUpListener(UserWrapper userWrapper, RibbitListener listener) {
            mUserWrapper = userWrapper;
            mListener = listener;
        }

        @Override
        public void onSuccess(Map<String, Object> result) {
            final String uid = (String) result.get("uid");
            Firebase firebaseUsers = Ribbit.getUsers().child("/" + uid);

            mUserWrapper.setUid(uid);
            final UserData userData = mUserWrapper.getData();
            userData.updateDate();
            firebaseUsers.setValue(userData, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError e, Firebase firebase) {
                    if (e == null) {
                        Log.d(TAG, "storing userData data completed successfully " + uid);
                        Ribbit.login(mUserWrapper, mListener);
                        Ribbit.setCurrentUser(mUserWrapper);
                    } else {
                        mListener.onFinish();
                        Log.e(TAG, "storing userData data failed");
                        mListener.onError(e.toException());
                    }
                }
            });
        }

        @Override
        public void onError(FirebaseError e) {
            mListener.onFinish();
            Log.e(TAG, e.getMessage(), e.toException());
        }
    }
}

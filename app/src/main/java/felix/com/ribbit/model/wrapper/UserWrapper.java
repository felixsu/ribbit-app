package felix.com.ribbit.model.wrapper;

import android.util.Log;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;

import felix.com.ribbit.listener.RibbitResultListener;
import felix.com.ribbit.model.base.RibbitBase;
import felix.com.ribbit.model.firebase.UserData;
import felix.com.ribbit.model.ribbit.RibbitUser;
import felix.com.ribbit.util.Util;

/**
 * Created by fsoewito on 3/20/2016.
 */
public class UserWrapper extends RibbitWrapper<UserData> implements Cloneable{

    private static final String TAG = UserWrapper.class.getName();

    public UserWrapper(String uid, UserData data) {
        mId = uid;
        mData = data;
    }

    public UserWrapper() {
        if (mData == null) {
            mData = new UserData();
        }
    }

    public void signUp(final RibbitResultListener resultListener) {
        Firebase root = RibbitBase.getRoot();

        String email = mData.getEmail();
        String password = mData.getPassword();
        root.createUser(email, password, new SignUpListener(this, resultListener));
    }

    public void store(final RibbitResultListener resultListener){
        Firebase firebaseUser = RibbitUser.getFirebaseUsers().child("/" + mId);
        mData.prepareForSignUp();

        firebaseUser.setValue(mData, new StoreResultListener(resultListener));

    }

    /* INNER CLASS */
    private class SignUpListener implements Firebase.ValueResultHandler<Map<String, Object>> {
        private final UserWrapper mUserWrapper;
        private final RibbitResultListener mResultListener;

        public SignUpListener(UserWrapper userWrapper, RibbitResultListener resultListener) {
            mUserWrapper = userWrapper;
            mResultListener = resultListener;
        }

        private void logInUser(UserWrapper userWrapper, RibbitResultListener listener) {
            final UserData userData = userWrapper.getData();

            RibbitUser.firstLogin(userData.getEmail(), userData.getPassword(), listener);
        }

        @Override
        public void onSuccess(Map<String, Object> result) {
            final String uid = (String) result.get("uid");
            mUserWrapper.setId(uid);

            logInUser(mUserWrapper, mResultListener);
        }

        @Override
        public void onError(FirebaseError e) {
            Log.d(TAG, "Firebase error with error code" + e.getCode());
            Log.e(TAG, e.getMessage(), e.toException());
            mResultListener.onFinish();
            mResultListener.onError(e.toException(), e.getMessage());
        }
    }

    private class StoreResultListener implements Firebase.CompletionListener {

        final private RibbitResultListener mResultListener;

        public StoreResultListener(RibbitResultListener resultListener) {
            mResultListener = resultListener;
        }

        @Override
        public void onComplete(FirebaseError e, Firebase firebase) {
            mResultListener.onFinish();
            if (e == null){
                mResultListener.onSuccess();
            }else{
                mResultListener.onError(e.toException(), e.getMessage());
            }
        }
    }
}
